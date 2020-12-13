import java.util.*;
import java.io.*;
import java.net.Socket;

//A controller class to check and close all the sockets whenever shudown message is received
public class Controller extends Thread {

    public void run() {
        while (true) {
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
