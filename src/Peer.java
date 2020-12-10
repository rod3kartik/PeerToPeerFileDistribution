import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Peer {
    private static int sPort;   //The server will be listening on this port number

    // getter for chokeMap
    public Map<Integer,Integer> getPeerChokeList() {
        return peerChokeMap;
    }

    //Setting values to choke Map
    public void setPeerChokeMap(int nodeId, int value) {
        peerChokeMap.put(nodeId,value);
    }

    //static or non-static
    private static Map<Integer,Integer> peerChokeMap = new HashMap<>();
    

    public static void main(String[] args) throws Exception {

        String peerFromCommandLine = args[0];

        Constants c = new Constants();

        FileLogger fl = new FileLogger(peerFromCommandLine);

        // connecting to other peers and starting Client and Server
        List<RemotePeerInfo> allBeforePeerInfo = Connection.getPeerInfo(peerFromCommandLine);
        System.out.println(allBeforePeerInfo);
        RemotePeerInfo selfInfo = allBeforePeerInfo.get(allBeforePeerInfo.size()-1);

        //Global self peer index
        Constants.selfPeerIndex = allBeforePeerInfo.size()-1;
        Constants.setSelfBit();
        Constants.setChunksLeft();
        Constants.setSelfPeerInfo();
        Constants.setFileChunks();
        Constants.fl = new FileLogger(Constants.selfPeerInfo.peerID);

        for (RemotePeerInfo rp : Constants.listOfAllPeers){
            Constants.peerIDToPeerInfo.put(rp.peerID,rp);
        }

        try {
            allBeforePeerInfo.remove(allBeforePeerInfo.size()-1);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        sPort = Integer.parseInt(selfInfo.peerPort);
        // Controller controller = new Controller();
        // Constants.listOfThreads.add(controller);
        // controller.start();
        startTimerForUnchoking();
        startTimerForOptimisticallyUnchoking();
        for (int outgoingPeer = 0; outgoingPeer < Constants.selfPeerIndex; outgoingPeer++) {
            System.out.println("Outgoing peer " + allBeforePeerInfo.get(outgoingPeer));
            Socket neighborPeer = new Socket(allBeforePeerInfo.get(outgoingPeer).peerAddress, Integer.parseInt(allBeforePeerInfo.get(outgoingPeer).peerPort));
            Constants.listOfAllSockets.add(neighborPeer);
            ObjectOutputStream out = new ObjectOutputStream(neighborPeer.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(neighborPeer.getInputStream());
            Constants.listOfAllPeers[outgoingPeer].out = out;

            PeerHandler newP = new PeerHandler(neighborPeer, outgoingPeer, in, out);
            Constants.listOfThreads.add(newP);
            newP.start();
        }
        
        ServerSocket serverSocket = new ServerSocket(sPort);
        Constants.selfServerSocket = serverSocket;
        for(int incomingPeers = Constants.selfPeerIndex + 1; incomingPeers< Constants.listOfAllPeers.length; incomingPeers++){
            Socket peerSocket = serverSocket.accept();
            Constants.listOfAllSockets.add(peerSocket);
            ObjectOutputStream out = new ObjectOutputStream(peerSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(peerSocket.getInputStream());
            Constants.listOfAllPeers[incomingPeers].out = out;
            PeerHandler newP = new PeerHandler(peerSocket, incomingPeers, in , out);
            Constants.listOfThreads.add(newP);
            newP.start();
        }
       
        
        while(true){
            // System.out.println("hey");
            if(Constants.isShutDownMessageReceived){
                // Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                // for (Thread thread : threadSet) {
                //     System.out.println("Alive thread: " + thread);
                //     thread.interrupt();
                // }
                // serverSocket.close();
                // for (Socket socket : Constants.listOfAllSockets) {
                //     socket.close();
                // }
                System.out.println("********************** in the final shutdown");
                for (Socket socket : Constants.listOfAllSockets){
                    socket.close(); 
                }
                System.exit(0);
                break;
            }
        }
        //System.out.println("Compeleted Everything");
    }

    private static void startTimerForOptimisticallyUnchoking() {
        Timer optimisticUnchokingUnchokedTimer = new Timer();
        int unchokeTimeInterval = Constants.OptimisticUnchokingInterval * 1000;
        optimisticUnchokingUnchokedTimer.schedule(new TimerTask() {
            @Override
            public void run(){
                System.out.println("******************");
                Thread.currentThread();
                if (Constants.isShutDownMessageReceived | Thread.interrupted()) {
                    try {
                        Constants.selfServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //utilities.shutdownAllThreads();
                    optimisticUnchokingUnchokedTimer.cancel();
                    return;
                }
                List<RemotePeerInfo> interestedChokedNeighbors = new ArrayList<>();
                for(RemotePeerInfo rpI: Constants.interestedNeighbors){
                    if(!rpI.isUnchoked){
                        interestedChokedNeighbors.add(rpI);
                    }
                }
                if(interestedChokedNeighbors.size() > 0){
                    RemotePeerInfo peer = interestedChokedNeighbors.get(new Random().nextInt(interestedChokedNeighbors.size()));
                    Message unchokeMsg = new Message(1, 1, null);
                    unchokeMsg.sendUnchokeMessage(peer.out);
                    peer.setIsUnchoked(true);
                }
               
            }
            
        }, unchokeTimeInterval);

    }

    private static void startTimerForUnchoking() {
        Timer timer = new Timer();
        int begin = 0;
        int timeInterval = Constants.UnchokingInterval * 1000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // call the method
                System.out.println("Running controller again");
                
                if (Constants.selfPeerInfo.fileAvailable.equals("1") && utilities.isDownloadComplete()) {
                    System.out.println("Shutting down controller");
                    
                    for(Map.Entry<String, BitSet> setEntry : Constants.peerIDToBitfield.entrySet()){
                        System.out.println("Final bitfields are: " + setEntry.getKey() + setEntry.getValue());
                    }
                    utilities.broadcastShutdownMessage();
                    Constants.isShutDownMessageReceived = true;
                    //utilities.shutdownAllThreads();
                    try {
                        Constants.selfServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    timer.cancel();
                    timer.purge();
                    System.out.println("timer cancel nhi hua");
                    return;
                }
                if(Constants.isShutDownMessageReceived | Thread.currentThread().isInterrupted()){
                    utilities.mergeFileChunks();
                    try {
                        Constants.selfServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    timer.cancel();
                    timer.purge();
                    return;
                }
                
                List<RemotePeerInfo> preferredNeighbors = utilities.getKPreferredNeighbors();
                if(preferredNeighbors.size() > 0){
                    Constants.setListOfPreferredNeighbours(preferredNeighbors);
                    System.out.println("List of pref neighours " + preferredNeighbors.size());
                    // Constants.printListOfPeers(preferredNeighbors);
                    for(RemotePeerInfo rpI: Constants.listOfAllPeers){
                        if (Constants.selfPeerInfo.equals(rpI)) continue;

                        if(Constants.preferredNeighbors.contains(rpI) ){
                            Message unchokeMsg = new Message(1, 1, null);
                            unchokeMsg.sendUnchokeMessage(rpI.out);
                            rpI.setIsUnchoked(true);
                        }
                        else{
                            Message chokeMsg = new Message(1, 0, null);
                            chokeMsg.sendChokeMessage(rpI.out);
                            rpI.setIsUnchoked(false);
                        }
                    }
                }
            }
            }, begin, timeInterval);
            
    }
}


