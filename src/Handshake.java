import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;

import java.net.*;
import java.io.*;
import java.util.Arrays;

public class Handshake {
    private String handshakeHeader;
    private byte[] zeroBytes;
    private int peerId;
    public String headerHandshake ="P2PFILESHARINGPROJ";

    // contructor
    public Handshake(int peerId){
        this.handshakeHeader = headerHandshake;
        this.zeroBytes = new byte[10];
        this.peerId = peerId;
    }

    // generates handshake message with the proper format
    public String generateMessage(String handshakeHeader,byte[] zeroBytes, int peerId){
        return handshakeHeader + zeroBytes.toString() + peerId;
    }

    // send handshake message through the socket
    public void sendHandshake(Socket socket){
        try {
            ObjectOutputStream out;
            out = new ObjectOutputStream(socket.getOutputStream());
            String finalMsg = generateMessage(this.handshakeHeader, this.zeroBytes, this.peerId);
            out.writeObject(finalMsg);
            out.flush();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    // checks if the received messahe is a handshake message
    public int isHandshake(String msg) {
        try {
//            ObjectInputStream in;
//            in = new ObjectInputStream(socket.getInputStream());
//            String incomingMessage = (String)in.readObject();
            if(msg.substring(0,18).contentEquals(headerHandshake)){
                try {
                    return Integer.parseInt(msg.substring(28));
                }
                catch(Exception ex){
                    System.out.println("PeerId Cannot be converted to Integer");
                }
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        return 0;
    }
}
