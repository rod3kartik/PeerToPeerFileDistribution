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


    public byte[] getMessageType() {
        return MessageType;
    }

    public byte[] getMessageLength() {
        return messageLength;
    }

    public byte[] getMessagePayload() {
        return messagePayload;
    }


    public Message(byte[] message, FileLogger fl, RemotePeerInfo peer){
        this.messageLength = Arrays.copyOfRange(message,0,4);
        this.MessageType = Arrays.copyOfRange(message,4,5);
        int length = Integer.parseInt(messageLength.toString()) - 1;
        this.messagePayload = Arrays.copyOfRange(message,5,message.length);
        this.fl = fl;
        this.peerObject = peer;
    }

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

    private void updatePeerChokeList(int peerId, int mType) {
        new Peer().setPeerChokeMap(peerId,mType);
    }

    private void interested(){
        fl.receivedInterestedMessageLog(1002);
    }

    private void notInterested(){

    }

    private void initBitField(byte[] newBitField){
        peerObject.setBitfield(newBitField);
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
