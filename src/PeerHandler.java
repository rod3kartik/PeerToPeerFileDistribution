import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


//Handler class for each peer socket
public class PeerHandler extends Thread {

    private ObjectInputStream in; // stream read from the socket
    private ObjectOutputStream out; // stream write to the socket
    private RemotePeerInfo peer;
    private boolean firstTime = true;
    private Socket peerSocket;

    PeerHandler(Socket socket, int peerIndex, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        // System.out.println(peerIndex + " " + Constants.listOfAllPeers.length);

        try {
            this.peerSocket = socket;
            this.out = outputStream;
            this.in = inputStream;
            
            peer = Constants.listOfAllPeers[peerIndex];
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() {
        Handshake h = new Handshake();
        byte[] incomingMessage;
        try {
            while (!Thread.currentThread().isInterrupted() & !Constants.isShutDownMessageReceived) {
                if (Constants.isShutDownMessageReceived) {
                    break;
                }

                //For the first time, we will follow the process of handshake
                if (firstTime) {
                    if (!Constants.handshakedPeers.containsKey(peer.peerID)) {
                        Constants.handshakedPeers.put(peer.peerID, false);
                        h.sendHandshake(out);
                        System.out.println(Constants.handshakedPeers.get(peer.peerID));
                    }

                    try {
                        incomingMessage = new byte[32];

                        in.read(incomingMessage);

                        //System.out.println("incoming message received " + incomingMessage);
                        byte[] isHandShakeHeader = Arrays.copyOfRange(incomingMessage, 0, 18);
                        String tempHeader = new String(isHandShakeHeader, StandardCharsets.UTF_8);
                        
                        if (tempHeader.equals(Constants.headerHandshake)) {
                            h.handleHandShakeMessage(Arrays.copyOfRange(incomingMessage, 28, 32));
                        }

                        if (Constants.selfBitfield.length() != 0) {
                            byte[] bitFieldToByteArray = Constants.selfBitfield.toByteArray();
                            Message msg = new Message(bitFieldToByteArray.length + 1, 5, bitFieldToByteArray);

                            byte[] bitFieldMessage = msg.createMessage();
                            utilities.writeToOutputStream(out, bitFieldMessage);
                            Constants.fl.setTCPConnectionfromLog(peer.peerID, Calendar.getInstance());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    firstTime = false;
                } else {

                    //Actual message transfer begins by reading and writing through input and output streams of socket
                    try {
                        byte[] messageLength = in.readNBytes(4);
                        if(messageLength.length > 0){
                            int msgLength = (int) utilities.fromByteArrayToLong(messageLength);
                            incomingMessage = new byte[msgLength];
                            incomingMessage = in.readNBytes(msgLength);
                            ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
                            try {
                                outputBuffer.write(messageLength);
                                outputBuffer.write(incomingMessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Message messageObj = new Message(outputBuffer.toByteArray(), this.peer, out);
                            messageObj.extractMessage();
                        }
                    } catch (Exception e) {
                        System.out.println("Peer Socket has been closed gracefully");
                        peerSocket.close();
                        return;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
