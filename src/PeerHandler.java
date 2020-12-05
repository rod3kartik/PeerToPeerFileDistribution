import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PeerHandler extends Thread{
    private Socket peerSocket;
    private InputStream in;	//stream read from the socket
    private OutputStream out;    //stream write to the socket
    private RemotePeerInfo peer;

    PeerHandler(Socket socket, int peerIndex) {
        this.peerSocket = socket;
        System.out.println(peerIndex + " " + Constants.listOfAllPeers.length);

        try {
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
            System.out.println(socket.getRemoteSocketAddress().toString().substring(1));
            peer = Constants.listOfAllPeers[peerIndex];
            // peerId = Constants.socketToPeerID.get(socket.getre.toString().substring(1));
            System.out.println("PeerID " + peer.peerID);
        } catch (Exception e) {
            System.out.println("Cought an exception in PeerHandler");
            e.printStackTrace();
        }
        
    }

    public void run(){
        System.out.println("From handler");
        Handshake h = new Handshake();
        if(!Constants.handshakedPeers.containsKey(peer.peerID)){
            Constants.handshakedPeers.put(peer.peerID, false);
            h.sendHandshake(out);
            System.out.println(Constants.handshakedPeers.get(peer.peerID));
        } else if (!Constants.handshakedPeers.get(peer.peerID)) {
            Constants.handshakedPeers.put(peer.peerID, true);
            h.sendHandshake(out);
            System.out.println(Constants.handshakedPeers.get(peer.peerID));

        }
        
        try {
            
            while(true) {
                System.out.println( " IN the while");
                byte[] incomingMessage = new byte[32];
                in.read(incomingMessage);
                // System.out.println( " IN the while 2");
                
                System.out.println("incoming message received " + incomingMessage);
                byte[] isHandShakeHeader = Arrays.copyOfRange(incomingMessage, 0, 18);
                String tempHeader = new String(isHandShakeHeader, StandardCharsets.UTF_8);
                System.out.println("header received " + tempHeader);
                if(tempHeader.equals(Constants.headerHandshake)){
                    h.handleHandShakeMessage(Arrays.copyOfRange(incomingMessage, 28, 32));
                }
                break;

            }
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println( " exception in disconnection");

        }
    }

}
