// import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;

import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;
import java.util.Arrays;

public class Handshake {
    private byte[] handshakeHeader;
    private byte[] zeroBytes;
    private byte[] peerId;
    
    public Handshake(int peerId){
        this.handshakeHeader = Constants.headerHandshake.getBytes();
        this.zeroBytes = new byte[10];
        this.peerId = ByteBuffer.allocate(4).putInt(peerId).array();
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

    public void sendHandshake(Socket socket){
        try {
            ObjectOutputStream out;
            out = new ObjectOutputStream(socket.getOutputStream());
            byte[] finalMsg = generateByteArrayMessage(this.handshakeHeader, this.zeroBytes, this.peerId);
            System.out.println(finalMsg.toString());
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
        Constants.handshakedPeers.add(peerID);
        System.out.println("PeerID received in handshake " + peerID);
    }
}
