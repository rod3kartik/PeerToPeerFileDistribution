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
        this.peerID = ByteBuffer.allocate(4).putInt(Integer.parseInt(Constants.selfPeerInfo.peerID)).array();
        
    }

    public byte[] generateByteArrayMessage(byte[] handshakeHeader, byte[] zeroBytes, byte[] peerId){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try {
            outputStream.write(handshakeHeader);
            outputStream.writeBytes(zeroBytes);
            outputStream.writeBytes(peerID);    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public void sendHandshake(ObjectOutputStream out){
        try {       
            byte[] finalMsg = generateByteArrayMessage(this.handshakeHeader, this.zeroBytes, this.peerID);
            //System.out.println("sent message: "+ new String(finalMsg));
            utilities.writeToOutputStream(out, finalMsg);
           
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void handleHandShakeMessage(byte[] bytePeerID){
        long peerID = utilities.fromByteArrayToLong(bytePeerID);
        Constants.handshakedPeers.put(Long.toString(peerID), true);
        System.out.println("PeerID received in handshake: " + peerID);
        // System.out.println(Constants.handshakedPeers);
    }
}
