// import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.Arrays;

public class Handshake {
    private byte[] handshakeHeader;
    private byte[] zeroBytes;
    private byte[] peerID;
    
    public Handshake(){
        this.handshakeHeader = Constants.headerHandshake.getBytes(StandardCharsets.UTF_8);
        this.zeroBytes = new byte[10];
        this.peerID = ByteBuffer.allocate(4).putInt(Integer.parseInt(Constants.listOfAllPeers[Constants.selfPeerIndex].peerID)).array();
    }

    public byte[] generateByteArrayMessage(byte[] handshakeHeader, byte[] zeroBytes, byte[] peerId){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try {
            outputStream.write(handshakeHeader);
            outputStream.writeBytes(zeroBytes);
            outputStream.writeBytes(peerId);    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public void sendHandshake(OutputStream out){
        try {       
            byte[] finalMsg = generateByteArrayMessage(this.handshakeHeader, this.zeroBytes, this.peerID);
            System.out.println("sent message: "+ finalMsg.toString());
            out.write(finalMsg);
            out.flush();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    // public int isHandshake(String msg) {
    //     try {
    //        ObjectInputStream in;
    //        in = new ObjectInputStream(socket.getInputStream());
    //        String incomingMessage = (String)in.readObject();
    //         if(msg.substring(0,18).contentEquals(headerHandshake)){
    //             try {
    //                 return Integer.parseInt(msg.substring(28));
    //             }
    //             catch(Exception ex){
    //                 System.out.println("PeerId Cannot be converted to Integer");
    //             }
    //         }
    //     }
    //     catch(Exception ex){
    //         System.out.println(ex.getMessage());
    //     }
    //     return 0;
    // }

    public void handleHandShakeMessage(byte[] bytePeerID){
        int peerID = utilities.fromFourByteArrayToInteger(bytePeerID);
        Constants.handshakedPeers.put(Integer.toString(peerID), true);
        System.out.println("PeerID received in handshake " + peerID);
        System.out.println(Constants.handshakedPeers);
    }
}
