import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.BitSet;

public class Message {
//    public static enum Type {
//        CHOKE, UNCHOKE, INTERESTED, NOTINTERESTED, HAVE, BITFIELD, REQUEST, PIECE, HANDSHAKE;
//    }

    private byte[] messageType;
    private byte[] messageLength;
    private byte[] messagePayload;
    private FileLogger fl;
    private RemotePeerInfo peer;
    private String remotePeerID;
    private ObjectOutputStream outputStream;

    // getter methods
    public byte[] getMessageType() {
        return messageType;
    }

    public byte[] getMessageLength() {
        return messageLength;
    }

    public byte[] getMessagePayload() {
        return messagePayload;
    }


    public Message(int msgLength, int type, byte[] payload){
        this.messageLength = ByteBuffer.allocate(4).putInt(msgLength).array();
        this.messageType = ByteBuffer.allocate(4).putInt(type).array();
        this.messagePayload = payload;
        System.out.println("Message payload is " + messagePayload);
    }

    public Message(byte[] receivedMessage, String peerID,ObjectOutputStream opstream){
        messageLength = Arrays.copyOfRange(receivedMessage, 0, 4);
        messageType = Arrays.copyOfRange(receivedMessage, 4, 8);
        messagePayload = Arrays.copyOfRange(receivedMessage, 8, receivedMessage.length);
        remotePeerID = peerID;
        this.outputStream = opstream;
    }

    // extracts the received message, determines the type of the message, 
    // and sends it for the next process as per its type
    public void extractMessage(){
        // String message = Arrays.toString(this.messageType);
        int msgType = (int)utilities.fromByteArrayToInteger(this.messageType);
        System.out.println("message type: "+ msgType);
        switch (msgType){
            case 0:
                //updatePeerChokeList(Integer.parseInt(peerObject.peerID),0);
                break;
            case 1:
                // updatePeerChokeList(Integer.parseInt(peerObject.peerID),1);
                break;
            case 2:
                System.out.println("In case for handling intreseted");
                //handleInterested();
                break;
            case 3:
                //handleNotInterested();
                break;
            case 4:
                //handleHaveMessage();
                break;

            case 5:
                initBitField(this.messagePayload, this.outputStream);
                if(compareBitField(Constants.peerIDToBitfield.get(remotePeerID) )){
                    sendInterested(this.outputStream);
                }
                else{
                    sendNotInterested(this.outputStream);

                }

                break;

            case 6:
                sendRequestedMessage(messagePayload);
                break;

            case 7:
                downloadPiece(messagePayload);
        }

    }

    public byte[] createMessage(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(this.messageLength);
            outputStream.write(this.messageType);
            if(this.messagePayload != null) {
                outputStream.write(this.messagePayload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    private void updatePeerChokeList(int peerId, int mType) {
        new Peer().setPeerChokeMap(peerId,mType);
    }

    // logging when interested message in the corresponding peerID log file 
    // is received from a peer with a certain peerID
    private void interested(){
        fl.receivedInterestedMessageLog(1002);
        //compareBitField(Constants.peerIDToBitfield.get(remotePeerID));
        //Check if the one who sent interested signal is choked or unchoked
    }

    // method for when not interested message is sent

    // initializes the bitfield using the setter method
    private void initBitField(byte[] newBitField, ObjectOutputStream outputStream){
        BitSet payload = BitSet.valueOf(newBitField);
        Constants.peerIDToBitfield.put(remotePeerID, payload);
        System.out.println("Remote peerID is" + remotePeerID);

    }

    // sends request message to the peer with the piece that is required
    private void sendRequestedMessage(byte[] messageIndex){
        //Send file to the peer with requested message index
    }

    private boolean compareBitField(BitSet remoteBitfield){
        BitSet selfChunksLeft = Constants.chunksLeft;

        selfChunksLeft.and(remoteBitfield);
        if(selfChunksLeft.length() > 0){
            //send intereseted
            return true;
        }
        return false;
        //System.out.println("After anding " + selfChunksLeft);

    }

    // downloads the piece from the message received
    private void downloadPiece(byte[] piece) {
        //download and merge incoming piece

        //Need to update according to received packet
        int pieceIndex = 3;
        updateBitField(pieceIndex);
    }

    // updates the bitfiled with the recent piece that has been downloaded
    private void updateBitField(int pieceIndex){
        // peerObject.updateBitField(pieceIndex);
    }

    private void sendInterested(ObjectOutputStream outputStream){
        try {
            System.out.println("In send Interested method");
            Message msg = new Message( 4, 2, null);
            byte[] interestedMessage = msg.createMessage();
            outputStream.write(interestedMessage);
            outputStream.flush();
            //outputStream.write(this.messageType);
            //outputStream.write(this.messagePayload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotInterested(ObjectOutputStream outputStream){
        try {
            Message msg = new Message( 4, 3, null);
            byte[] interestedMessage = msg.createMessage();
            outputStream.write(interestedMessage);
            outputStream.flush();
            //outputStream.write(this.messageType);
            //outputStream.write(this.messagePayload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
