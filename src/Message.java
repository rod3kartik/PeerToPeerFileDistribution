import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class Message {
//    public static enum Type {
//        CHOKE, UNCHOKE, INTERESTED, NOTINTERESTED, HAVE, BITFIELD, REQUEST, PIECE, HANDSHAKE;
//    }

    private byte[] messageType;
    private byte[] messageLength;
    private byte[] messagePayload;
    private FileLogger fl;
    private RemotePeerInfo peer;
    //private String remotePeerID;
    private ObjectOutputStream outputStream;
    //private int chunkIndex;

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

    public Message(byte[] receivedMessage, RemotePeerInfo peer, ObjectOutputStream opstream){
        try {
            messageLength = Arrays.copyOfRange(receivedMessage, 0, 4);
            messageType = Arrays.copyOfRange(receivedMessage, 4, 8);
            messagePayload = Arrays.copyOfRange(receivedMessage, 8, receivedMessage.length);
            this.peer = peer;
            //this.remotePeerID = this.peer.peerID;
            this.outputStream = opstream;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // extracts the received message, determines the type of the message, 
    // and sends it for the next process as per its type
    public void extractMessage(){
        // String message = Arrays.toString(this.messageType);
        int msgType = (int)utilities.fromByteArrayToLong(this.messageType);
        System.out.println("message type: "+ msgType);
        switch (msgType){
            case 0:
                System.out.println("*** Choke Peer **");
                handleChokeMessage(this.peer);
                break;
            case 1:
                //setting that peer's unchoked status 
                handleUnchokeMessage(this.peer);
                new ChunkRequestor(this.peer).start();
                break;
            case 2:
                System.out.println("In case for handling intreseted");
                handleInterested();
                //Write in logger
                break;
            case 3:
                handleNotInterested();
                //Write in logger
                break;
            case 4:
                this.handleHaveMessage();
                break;

            case 5:
                initBitField(this.messagePayload, this.outputStream);
                if(compareBitField(Constants.peerIDToBitfield.get(peer.peerID) )){
                    sendInterestedMessage(this.outputStream);
                }
                else{
                    sendNotInterested(this.outputStream);
                }
                break;
            case 6:
                handleRequestMessage(this.messagePayload);
                break;

            case 7:
                byte[] pieceIndexByteArray = handleDownloadPiece(this.messagePayload);
                this.peer.setDownloadDataSize(this.messagePayload.length);
                this.peer.setDownloadSpeed();
                utilities.broadcastHaveMessage(Constants.listOfAllPeers[Constants.selfPeerIndex].peerID, pieceIndexByteArray);
                for (RemotePeerInfo rpi : Constants.listOfAllPeers) {
                    if(rpi.peerID.equals(Constants.selfPeerInfo.peerID)) continue;
                    if(!compareBitField(rpi.bitfield)) sendNotInterested(rpi.out);
                }

        }

    }

  



    private void handleHaveMessage() {
        int pieceIndex = (int) utilities.fromByteArrayToLong(this.messagePayload);
        
        if(Constants.peerIDToBitfield.containsKey(this.peer.peerID)){
            BitSet tempBitSet = Constants.peerIDToBitfield.get(this.peer.peerID);
            tempBitSet.set(pieceIndex);
            Constants.peerIDToBitfield.put(this.peer.peerID, tempBitSet);
            this.peer.bitfield = tempBitSet;
        } else{
            BitSet tempBitSet = new BitSet();
            tempBitSet.set(pieceIndex);
            Constants.peerIDToBitfield.put(this.peer.peerID, tempBitSet);
            this.peer.bitfield = tempBitSet;
        }

        if(compareBitField(this.peer.bitfield)){
            sendInterestedMessage(this.outputStream);
        }

    }

    private void handleChokeMessage(RemotePeerInfo remotePeer) {
        System.out.println("Handle choke message " + remotePeer.peerID);

        remotePeer.isUnchoked = false;
    }

    private void handleUnchokeMessage(RemotePeerInfo peer2) {
        peer2.isUnchoked = true;
        System.out.println("Handle unchoke message " + peer2.peerID);

    }

    public static void sendRequestMessage(int index, RemotePeerInfo peer2) {
        try {
            byte[] pload = ByteBuffer.allocate(4).putInt(index).array();
            byte[] msg = new Message(8, 6, pload).createMessage();
            peer2.out.write(msg);
            peer2.out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
      

    }

    public byte[] createMessage() {
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

    public void sendUnchokeMessage(ObjectOutputStream out){
        byte[] unchokeMsg = this.createMessage();
        try{
            out.write(unchokeMsg);
            out.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        
    }
    public void sendChokeMessage(ObjectOutputStream out){
        byte[] chokeMsg = this.createMessage();
        try{
            out.write(chokeMsg);
            out.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleInterested(){
        //System.out.println("Remote peerID is + " + remotePeerID);
        System.out.println("Remote peerID is + " + this.peer.peerID);
        Constants.interestedNeighbors.add(Constants.peerIDToPeerInfo.get(this.peer.peerID));

        System.out.println("Intrested neighoours set " + Constants.interestedNeighbors);
    }

    private void handleNotInterested(){
        System.out.println("@@@@@@@@@@@@@@@@Received not interested: " + this.peer.peerID);
       if(Constants.interestedNeighbors.contains((Constants.peerIDToPeerInfo.get(this.peer.peerID)))) {
           Constants.interestedNeighbors.remove(Constants.peerIDToPeerInfo.get(this.peer.peerID));
       }
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
        Constants.peerIDToBitfield.put(peer.peerID, payload);
        //System.out.println("Remote peerID is" + peer.peerID);

    }

    // sends request message to the peer with the piece that is required
    private void handleRequestMessage(byte[] messageIndex){
        //Send file to the peer with requested message index

        try {
            // byte[] temp = Arrays.copyOfRange(messageIndex, 0, 4);
            int pieceIndex = (int)utilities.fromByteArrayToLong(messageIndex);

            if(peer.isUnchoked && Constants.selfBitfield.get(pieceIndex)){
                System.out.println("***************************** " + pieceIndex);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                
                outputStream.write(messageIndex);
                outputStream.write(Constants.fileChunks[pieceIndex].getPieceContent());
            
                Message msg = new Message(outputStream.toByteArray().length + 4, 7, outputStream.toByteArray());
                byte[] msgByteArray = msg.createMessage();
                this.outputStream.write(msgByteArray);
                this.outputStream.flush();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean compareBitField(BitSet remoteBitfield){
        BitSet selfChunksLeft = Constants.chunksLeft;

        selfChunksLeft.intersects(remoteBitfield);
        if(selfChunksLeft.length() > 0){
            //send intereseted
            return true;
        }
        return false;
        //System.out.println("After anding " + selfChunksLeft);

    }

     
    // downloads the piece from the message received
    private byte[] handleDownloadPiece(byte[] piece) {
        //download and merge incoming piece
        //Need to update according to received packet
        System.out.println("$$$$$$$$$$$$$$$$$In handle download piece handle");
        byte[] temp= Arrays.copyOfRange(piece, 0, 4);
        int pieceIndex = (int)utilities.fromByteArrayToLong(temp);

        Piece newPiece = new Piece(temp);
        Constants.fileChunks[pieceIndex] = newPiece;

        //int pieceIndex = 3;
        updateBitField(pieceIndex);
        Constants.updateRequestedPieceIndexes(pieceIndex, false);
        return temp;
        //Broadcast have message with argument piece Index as byte array
    }
    // updates the bitfiled with the recent piece that has been downloaded
    private void updateBitField(int pieceIndex){
        Constants.selfBitfield.set(pieceIndex);
        Constants.chunksLeft.set(pieceIndex,false);
        // peerObject.updateBitField(pieceIndex);
    }

    private void sendInterestedMessage(ObjectOutputStream outputStream){
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
