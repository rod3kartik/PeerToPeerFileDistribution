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
            List<RemotePeerInfo> preferredNeighbors = utilities.getKPreferredNeighbors();
            Constants.setListOfPreferredNeighbours(preferredNeighbors);
        }
        }, begin, timeInterval);
    }
}
