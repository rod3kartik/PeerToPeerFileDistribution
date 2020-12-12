import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class OptimisticUnchokeHandler extends Thread {
    private long currentTime;

    OptimisticUnchokeHandler(){
        this.currentTime = System.currentTimeMillis() + Constants.UnchokingInterval;
    }

    public void run(){
        while(!Constants.isShutDownMessageReceived){
            if(this.currentTime == System.currentTimeMillis()){
                System.out.println("****************** " + Constants.isShutDownMessageReceived );
                System.out.println("After the condition check");
                List<RemotePeerInfo> interestedChokedNeighbors = new ArrayList<>();
                for (RemotePeerInfo rpI : Constants.interestedNeighbors) {
                    if (!rpI.isUnchoked) {
                        interestedChokedNeighbors.add(rpI);
                    }
                }
                if (interestedChokedNeighbors.size() > 0) {
                    RemotePeerInfo peer = interestedChokedNeighbors
                            .get(new Random().nextInt(interestedChokedNeighbors.size()));
                    Message unchokeMsg = new Message(1, 1, null);
                    unchokeMsg.sendUnchokeMessage(peer.out);
                    peer.setIsUnchoked(true);
                }
                this.currentTime += Constants.OptimisticUnchokingInterval;
            }
        }
        System.out.println("startTimerForOptimisticallyUnchoking Closed");
    }

}
