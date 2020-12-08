import java.util.*;
import java.io.*;


public class Controller extends Thread{

    public void run(){

        Timer timer = new Timer();
        int begin = 0;
        int timeInterval = Constants.UnchokingInterval*1000;
        timer.schedule(new TimerTask() {
        @Override
        public void run() {
            //call the method
            if(Constants.selfPeerInfo.fileAvailable.equals("1") && utilities.isDownloadComplete()){
                utilities.broadcastShutdownMessage();
                timer.cancel();
                timer.purge();
                return;
            }
            if(Constants.isShutDownMessageReceived){
                utilities.mergeFileChunks();
                timer.cancel();
                timer.purge();
                return; 
            }
            System.out.println("Running controller again");
            List<RemotePeerInfo> preferredNeighbors = utilities.getKPreferredNeighbors();
            System.out.println("cleared");
            Constants.setListOfPreferredNeighbours(preferredNeighbors);
            System.out.println("List of pref neighours " + preferredNeighbors.size());
            Constants.printListOfPeers(preferredNeighbors);
            for(RemotePeerInfo rpI: Constants.listOfAllPeers){
                if (Constants.selfPeerInfo.equals(rpI)) continue;

                if(Constants.preferredNeighbors.contains(rpI) ){
                    Message unchokeMsg = new Message(4, 1, null);
                    System.out.println("Sending unchoke message: "+ rpI.peerID);
                    if (rpI.out == null){
                        System.out.println("Unchoke null ");
                    }
                    unchokeMsg.sendUnchokeMessage(rpI.out);
                    rpI.isUnchoked = true; 
                }
                else{
                    Message chokeMsg = new Message(4, 0, null);
                    System.out.println("Sending choke message: " + rpI.peerID);
                    if (rpI.out == null){
                        System.out.println("THis is null though");
                    }
                    chokeMsg.sendChokeMessage(rpI.out);
                    rpI.isUnchoked = false;
                }
            }
        }
        }, begin, timeInterval);
    }
}
