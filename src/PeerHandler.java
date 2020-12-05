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
            System.out.println(socket.getRemoteSocketAddress().toString().substring(1));
            peerId = Constants.socketToPeerID.get(socket.getRemoteSocketAddress().toString().substring(1));
            System.out.println("PeerID " + peerId);
        } catch (Exception e) {
            System.out.println("Cought an exception in PeerHandler");
            e.printStackTrace();
        }
        
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
        if(!Constants.handshakedPeers.contains(peerId)){
            h.sendHandshake(peerSocket);
        }

        try {
            
            while(true) {
                byte[] incomingMessage = in.readAllBytes();
                byte[] isHandShakeHeader = Arrays.copyOfRange(incomingMessage, 0, 18);
                if(isHandShakeHeader.toString().equals(Constants.headerHandshake)){
                    h.handleHandShakeMessage(Arrays.copyOfRange(incomingMessage, 28, 32));
                }

            }
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

}
