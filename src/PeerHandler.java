import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class PeerHandler extends Thread{
    private Socket peerSocket;
    private ObjectInputStream in;	//stream read from the socket
    private ObjectOutputStream out;    //stream write to the socket
    private int peerId;

    PeerHandler(Socket socket) {
        this.peerSocket = socket;
    
        try {
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
        } catch (Exception e) {
            System.out.println("Cought an exception in PeerHandler");
            e.printStackTrace();
        }
    }

    //Constructor for sending the message
    PeerHandler(Socket socket, int peerId) {
        this.peerSocket = socket;
        try {
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
        } catch (Exception e) {
            System.out.println("Cought an exception in PeerHandler");
            e.printStackTrace();
        }
        this.peerId = peerId;
    }


    public void run(){
        System.out.println("From handler");
        try {
            String b = (String)in.readObject();
            System.out.println(b);
        } catch (Exception e) {
            //TODO: handle exception
        }
        Handshake h = new Handshake(peerId);

    }

}
