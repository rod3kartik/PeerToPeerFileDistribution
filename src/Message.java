import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class Message {
//    public static enum Type {
//        CHOKE, UNCHOKE, INTERESTED, NOTINTERESTED, HAVE, BITFIELD, REQUEST, PIECE, HANDSHAKE;
//    }

    private byte[] messageType;
    private byte[] messageLength;
    private byte[] messagePayload;
    private FileLogger fl;
    private RemotePeerInfo peerObject;


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

    public Message(byte[] receivedMessage){
        messageLength = Arrays.copyOfRange(receivedMessage, 0, 4);
        messageType = Arrays.copyOfRange(receivedMessage, 4, 5);
        messagePayload = Arrays.copyOfRange(receivedMessage, 5, receivedMessage.length);
    }

    public void extractMessage(){
        String message = Arrays.toString(this.messageType);
        switch (message){
            case "0":
                updatePeerChokeList(Integer.parseInt(peerObject.peerID),0);
                break;
            case "1":
                updatePeerChokeList(Integer.parseInt(peerObject.peerID),1);
                break;
            case "2":
                interested();
                break;

            case "4":
                notInterested();
                break;

            case "5":
                initBitField(messagePayload);
                break;

            case "6":
                sendRequestedMessage(messagePayload);
                break;

            case "7":
                downloadPiece(messagePayload);
        }

    }

    public byte[] createMessage(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try {
            outputStream.write(messageLength);
            outputStream.write(messageType);
            outputStream.write(messagePayload);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }
    
    private void updatePeerChokeList(int peerId, int mType) {
        new Peer().setPeerChokeMap(peerId,mType);
    }

    private void interested(){
        fl.receivedInterestedMessageLog(1002);
    }

    private void notInterested(){

    }

    private void initBitField(byte[] newBitField){
        // peerObject.setBitfield(newBitField);
    }

    private void sendRequestedMessage(byte[] messageIndex){
        //Send file to the peer with requested message index
    }

    private void downloadPiece(byte[] piece) {
        //download and merge incoming piece

        //Need to update according to received packet
        int pieceIndex = 3;
        updateBitField(pieceIndex);
    }

    private void updateBitField(int pieceIndex){
        peerObject.updateBitField(pieceIndex);
    }

 
}
