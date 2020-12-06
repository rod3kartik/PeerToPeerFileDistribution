import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PeerHandler extends Thread{
    private Socket peerSocket;
    private ObjectInputStream in;	//stream read from the socket
    private ObjectOutputStream out;    //stream write to the socket
    private RemotePeerInfo peer;
    private boolean firstTime = true;

    PeerHandler(Socket socket, int peerIndex) {
        this.peerSocket = socket;
        System.out.println(peerIndex + " " + Constants.listOfAllPeers.length);

        try {
            
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
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
        byte[] incomingMessage;
        try {
            while(true){
                if(firstTime){
                    if(!Constants.handshakedPeers.containsKey(peer.peerID)){
                        Constants.handshakedPeers.put(peer.peerID, false);
                        h.sendHandshake(out);
                        System.out.println(Constants.handshakedPeers.get(peer.peerID));
                    }
        
                    try {
                        incomingMessage = new byte[32];
                        in.read(incomingMessage);
                        // System.out.println( " IN the while 2");
                        
                        System.out.println("incoming message received " + incomingMessage);
                        byte[] isHandShakeHeader = Arrays.copyOfRange(incomingMessage, 0, 18);
                        String tempHeader = new String(isHandShakeHeader, StandardCharsets.UTF_8);
                        System.out.println("header received " + tempHeader);
                        if(tempHeader.equals(Constants.headerHandshake)){
                            h.handleHandShakeMessage(Arrays.copyOfRange(incomingMessage, 28, 32));
                        }
                        
                        System.out.println(Constants.selfBitfield + " length: " + Constants.selfBitfield.length());
                        if(Constants.selfBitfield.length() != 0){
                            byte[] bitFieldTooByteArray = Constants.selfBitfield.toByteArray();
                            System.out.println("byte array: "+ bitFieldTooByteArray);
                            Message msg = new Message(bitFieldTooByteArray.length + 4, 5, bitFieldTooByteArray);
                            byte[] bitFieldMessage = msg.createMessage();
                            out.write(bitFieldMessage);
                            out.flush();
                        }     
        
                    } catch (Exception e) {
                        System.out.println( "exception in disconnection");
                        e.printStackTrace();
                    }
                    firstTime = false;
                }
                else {
                    
                    try {
                        byte[] messageLength = in.readNBytes(4);
                        int msgLength = (int)utilities.fromByteArrayToInteger(messageLength);
                        System.out.println("Received message length: " + msgLength);
                        incomingMessage = new byte[msgLength];
                        incomingMessage = in.readNBytes(msgLength);
                        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
                        System.out.println("hr");
                        try {
                            outputBuffer.write(messageLength);
                            outputBuffer.write(incomingMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(this.peer + this.peer.peerID);
                        Message messageObj = new Message(outputBuffer.toByteArray(), this.peer.peerID);
                        messageObj.extractMessage();
                        System.out.println("Map is " + Constants.peerIDToBitfield);
                    } catch (Exception e) {
                        System.out.println( " exception in handler");
                        e.printStackTrace();
                    }
                }
        
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
