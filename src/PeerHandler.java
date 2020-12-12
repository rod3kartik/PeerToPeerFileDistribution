import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
            System.out.println(socket.getRemoteSocketAddress().toString().substring(1));
            peer = Constants.listOfAllPeers[peerIndex];
            // peerId = Constants.socketToPeerID.get(socket.getre.toString().substring(1));
            // System.out.println("PeerID " + peer.peerID);
        } catch (Exception e) {
            System.out.println("Cought an exception in PeerHandler");
            e.printStackTrace();
        }

    }

    public void run() {
        // System.out.println("From handler");
        // new Controller().start();
        Handshake h = new Handshake();
        byte[] incomingMessage;
        try {
            while (!Thread.currentThread().isInterrupted() & !Constants.isShutDownMessageReceived) {
                if (Constants.isShutDownMessageReceived) {
                    System.out.println("In shut down condition of PeerHandler " + peer.peerID);
                    //utilities.shutdownAllThreads();
                    return;
                }
                if (firstTime) {
                    if (!Constants.handshakedPeers.containsKey(peer.peerID)) {
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
                        // System.out.println("header received " + tempHeader);
                        if (tempHeader.equals(Constants.headerHandshake)) {
                            h.handleHandShakeMessage(Arrays.copyOfRange(incomingMessage, 28, 32));
                        }

                        System.out.println(Constants.selfBitfield + " length: " + Constants.selfBitfield.length());
                        if (Constants.selfBitfield.length() != 0) {
                            byte[] bitFieldToByteArray = Constants.selfBitfield.toByteArray();
                            System.out.println("byte array: " + bitFieldToByteArray);
                            Message msg = new Message(bitFieldToByteArray.length + 1, 5, bitFieldToByteArray);

                            byte[] bitFieldMessage = msg.createMessage();
                            utilities.writeToOutputStream(out, bitFieldMessage);
                            Constants.fl.setTCPConnectiontoLog(peer.peerID, Calendar.getInstance());
                        }

                    } catch (Exception e) {
                        System.out.println("exception in disconnection");
                        e.printStackTrace();
                    }
                    firstTime = false;
                } else {

                    try {
                        byte[] messageLength = in.readNBytes(4);
                        if(messageLength.length > 0){
                            int msgLength = (int) utilities.fromByteArrayToLong(messageLength);
                            // System.out.println("Received message length: " + msgLength);
                            incomingMessage = new byte[msgLength];
                            incomingMessage = in.readNBytes(msgLength);
                            ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
                            // System.out.println("hr");
                            try {
                                outputBuffer.write(messageLength);
                                outputBuffer.write(incomingMessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // System.out.println("Peer: " + this.peer.peerID);
                            Message messageObj = new Message(outputBuffer.toByteArray(), this.peer, out);
                            messageObj.extractMessage();
                        }
                        // System.out.println("Map is " + Constants.peerIDToBitfield);
                    } catch (Exception e) {
                        // e.printStackTrace();
                        System.out.println("Peer Socket has been closed gracefully");
                        peerSocket.close();
                        Runtime.getRuntime().exit(0);
                        return;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
