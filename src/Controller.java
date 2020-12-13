import java.util.*;
import java.io.*;
import java.net.Socket;

public class Controller extends Thread {

    public void run() {
        while (true) {
            //System.out.println(Constants.isShutDownMessageReceived);
            if (Constants.isShutDownMessageReceived) {
                try {
                    for (Socket socket : Constants.listOfAllSockets) {
                        socket.close();
                    }
                    Constants.selfServerSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();

                }
                break; 
            }
        }       
    }
}
