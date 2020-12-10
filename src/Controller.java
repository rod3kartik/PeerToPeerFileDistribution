import java.util.*;
import java.io.*;

public class Controller extends Thread {

    public void run() {

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
                    Constants.isShutDownMessageReceived = true;
                    utilities.broadcastShutdownMessage();
                    utilities.shutdownAllThreads();
                    try {
                        Constants.selfServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    timer.cancel();
                    timer.purge();
                    System.out.println("timer cancel nhi hua");
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
                }
                
                List<RemotePeerInfo> preferredNeighbors = utilities.getKPreferredNeighbors();
                if(preferredNeighbors.size() > 0){
                    Constants.setListOfPreferredNeighbours(preferredNeighbors);
                    System.out.println("List of pref neighours " + preferredNeighbors.size());
                    Constants.printListOfPeers(preferredNeighbors);
                    for(RemotePeerInfo rpI: Constants.listOfAllPeers){
                        if (Constants.selfPeerInfo.equals(rpI)) continue;

                        if(Constants.preferredNeighbors.contains(rpI) ){
                            Message unchokeMsg = new Message(1, 1, null);
                            unchokeMsg.sendUnchokeMessage(rpI.out);
                            rpI.isUnchoked = true; 
                        }
                        else{
                            Message chokeMsg = new Message(1, 0, null);
                            chokeMsg.sendChokeMessage(rpI.out);
                            rpI.isUnchoked = false;
                        }
                    }
                }
            }
            }, begin, timeInterval);
            System.out.println("going out of controller");

            Timer optimisticUnchokingUnchokedTimer = new Timer();
            int unchokeTimeInterval = Constants.OptimisticUnchokingInterval * 1000;
            optimisticUnchokingUnchokedTimer.schedule(new TimerTask() {

                @Override
                public void run(){
                    System.out.println("******************");
                    if(Constants.isShutDownMessageReceived | Thread.currentThread().interrupted()){
                        try {
                            Constants.selfServerSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        utilities.shutdownAllThreads();
                        optimisticUnchokingUnchokedTimer.cancel();
                        optimisticUnchokingUnchokedTimer.purge();
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
                        peer.isUnchoked = true; 
                    }
                   
                }
                
            }, unchokeTimeInterval);

    }
}
