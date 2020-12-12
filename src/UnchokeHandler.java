import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class UnchokeHandler extends Thread {
    private long currentTime;
    UnchokeHandler(){
        this.currentTime = System.currentTimeMillis() + Constants.UnchokingInterval;
    }

    public void run() {
        System.out.println("Running controller again");
        while(!Constants.isShutDownMessageReceived){
            if(this.currentTime == System.currentTimeMillis()){

                if (Constants.isShutDownMessageReceived | Thread.currentThread().isInterrupted()) {
                    utilities.mergeFileChunks();
                    // Runtime.getRuntime().exit(0);
                    for (Socket socket : Constants.listOfAllSockets) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("returning from timer");
                }
                if (Constants.selfPeerInfo.fileAvailable.equals("1") && utilities.isDownloadComplete()) {
                    System.out.println("Shutting down controller");
                    
                    for(Map.Entry<String, BitSet> setEntry : Constants.peerIDToBitfield.entrySet()){
                        System.out.println("Final bitfields are: " + setEntry.getKey() + setEntry.getValue());
                    }
                    utilities.broadcastShutdownMessage();
                    Constants.isShutDownMessageReceived = true;
                    System.out.println("timer cancel nhi hua" + Constants.isShutDownMessageReceived);
                    return;
                }
                
                List<RemotePeerInfo> preferredNeighbors = utilities.getKPreferredNeighbors();
                System.out.println("Hey there");
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
                this.currentTime += Constants.UnchokingInterval;
            }
        }
    }

}
