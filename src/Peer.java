import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Peer {
    private static int sPort; // The server will be listening on this port number

    // getter for chokeMap
    public Map<Integer, Integer> getPeerChokeList() {
        return peerChokeMap;
    }

    // Setting values to choke Map
    public void setPeerChokeMap(int nodeId, int value) {
        peerChokeMap.put(nodeId, value);
    }

    // static or non-static
    private static Map<Integer, Integer> peerChokeMap = new HashMap<>();

    public static void main(String[] args) throws Exception {

        String peerFromCommandLine = args[0];

        Constants c = new Constants();

        FileLogger fl = new FileLogger(peerFromCommandLine);

        // connecting to other peers and starting Client and Server
        List<RemotePeerInfo> allBeforePeerInfo = Connection.getPeerInfo(peerFromCommandLine);
        System.out.println(allBeforePeerInfo);
        RemotePeerInfo selfInfo = allBeforePeerInfo.get(allBeforePeerInfo.size() - 1);

        // Global self peer index
        Constants.selfPeerIndex = allBeforePeerInfo.size() - 1;
        Constants.setSelfBit();
        Constants.setChunksLeft();
        Constants.setSelfPeerInfo();
        Constants.setFileChunks();
        Constants.fl = new FileLogger(Constants.selfPeerInfo.peerID);

        for (RemotePeerInfo rp : Constants.listOfAllPeers) {
            Constants.peerIDToPeerInfo.put(rp.peerID, rp);
        }

        try {
            allBeforePeerInfo.remove(allBeforePeerInfo.size() - 1);
        } catch (Exception e) {
            System.out.println(e);
        }

        sPort = Integer.parseInt(selfInfo.peerPort);
        Controller controller = new Controller();
        Constants.listOfThreads.add(controller);
        controller.start();
        new UnchokeHandler().start();
        new OptimisticUnchokeHandler().start();
        ServerSocket serverSocket = new ServerSocket(sPort);
        Constants.selfServerSocket = serverSocket;
        for (int outgoingPeer = 0; outgoingPeer < Constants.selfPeerIndex; outgoingPeer++) {
            System.out.println("Outgoing peer " + allBeforePeerInfo.get(outgoingPeer));
            Socket neighborPeer = new Socket(allBeforePeerInfo.get(outgoingPeer).peerAddress,
                    Integer.parseInt(allBeforePeerInfo.get(outgoingPeer).peerPort));
            Constants.listOfAllSockets.add(neighborPeer);
            ObjectOutputStream out = new ObjectOutputStream(neighborPeer.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(neighborPeer.getInputStream());
            Constants.listOfAllPeers[outgoingPeer].out = out;

            PeerHandler newP = new PeerHandler(neighborPeer, outgoingPeer, in, out);
            Constants.listOfThreads.add(newP);
            newP.start();
        }
      
        for (int incomingPeers = Constants.selfPeerIndex
                + 1; incomingPeers < Constants.listOfAllPeers.length; incomingPeers++) {
            try {
                Socket peerSocket = serverSocket.accept();
                Constants.listOfAllSockets.add(peerSocket);
                ObjectOutputStream out = new ObjectOutputStream(peerSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(peerSocket.getInputStream());
                Constants.listOfAllPeers[incomingPeers].out = out;
                PeerHandler newP = new PeerHandler(peerSocket, incomingPeers, in, out);
                Constants.listOfThreads.add(newP);
                newP.start();
            } catch (SocketException e) {
                System.out.println("received an exception server");
                // serverSocket.close();
            }
        }

        controller.join();
        System.out.println("Compeleted Everything");
    }
 
}


