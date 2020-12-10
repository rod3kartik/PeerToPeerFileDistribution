import java.util.*;
import java.io.*;

public class Controller extends Thread {

    public void run() {

        
        Thread.currentThread();
        if (Thread.interrupted() | Constants.isShutDownMessageReceived) {
            return;
        }
       System.out.println("going out of controller");

       
    }
}
