import java.util.*;
import java.io.*;
import java.net.Socket;

public class Controller extends Thread {

    public void run() {

        while (true) {
            //System.out.println(Constants.isShutDownMessageReceived);
            if (Constants.isShutDownMessageReceived) {
                try {
                    Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                    for (Thread thread : threadSet) {
                    System.out.println("Alive thread: " + thread);
                    //thread.interrupt();
                    }
                    System.out.println("********************** In the final shutdown **********************");
                    Thread.currentThread();
                    Thread.sleep(5 * 1000);
                    for (Socket socket : Constants.listOfAllSockets) {
                        socket.close();
                    }
                    Constants.selfServerSocket.close();
                    // System.exit(0);
                break; 
                } catch (Exception e) {
                    e.printStackTrace();
                }
               
            }
        }
       System.out.println("going out of controller");

       
    }
}
