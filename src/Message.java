import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.io.ByteArrayInputStream;


public class Message {
//    public static enum Type {
//        CHOKE, UNCHOKE, INTERESTED, NOTINTERESTED, HAVE, BITFIELD, REQUEST, PIECE, HANDSHAKE;
//    }

    private byte[] MessageType;
    private byte[] messageLength;
    private byte[] messagePayload;
    private FileLogger fl;
    private RemotePeerInfo peerObject;

    // getter methods
    public byte[] getMessageType() {
        return MessageType;
    }

    public byte[] getMessageLength() {
        return messageLength;
    }

    public byte[] getMessagePayload() {
        return messagePayload;
    }

    // contructor
    public Message(byte[] message, FileLogger fl, RemotePeerInfo peer){
        this.messageLength = Arrays.copyOfRange(message,0,4);
        this.MessageType = Arrays.copyOfRange(message,4,5);
        int length = Integer.parseInt(messageLength.toString()) - 1;
        this.messagePayload = Arrays.copyOfRange(message,5,message.length);
        this.fl = fl;
        this.peerObject = peer;
    }

    // extracts the received message, determines the type of the message, 
    // and sends it for the next process as per its type
    public void extractMessage(){
        String message = Arrays.toString(this.MessageType);
        switch (message){
            case "0":
                updatePeerChokeList(Integer.parseInt(peerObject.peerId),0);
                break;
            case "1":
                updatePeerChokeList(Integer.parseInt(peerObject.peerId),1);
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

    // updates peer choke list using setter method 
    private void updatePeerChokeList(int peerId, int mType) {
        new Peer().setPeerChokeMap(peerId,mType);
    }

    // logging when interested message in the corresponding peerID log file 
    // is received from a peer with a certain peerID
    private void interested(){
        fl.receivedInterestedMessageLog(1002);
    }

    // method for when not interested message is sent
    private void notInterested(){

    }

    // initializes the bitfield using the setter method
    private void initBitField(byte[] newBitField){
        peerObject.setBitfield(newBitField);
    }

    // sends request message to the peer with the piece that is required
    private void sendRequestedMessage(byte[] messageIndex){
        //Send file to the peer with requested message index
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
        peerObject.updateBitField(pieceIndex);
    }
}
