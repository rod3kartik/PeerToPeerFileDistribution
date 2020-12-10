import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class FileLogger {
    static String peerID;
    Logger loggerObj;
    private SimpleDateFormat dateFormatter;
    private FileHandler handler;
//    public static void main(String[] args) {
//
//
//    }

    // creating log files for each peer
    public FileLogger(String peerID) {
        try {
            this.peerID = peerID;
            //String str = Integer.toString(peerID);
            dateFormatter = new SimpleDateFormat("HH:mm:ss");
            FileHandler handler = new FileHandler("../log_peer_" + peerID, true);
            SimpleFormatter format = new SimpleFormatter();
            handler.setFormatter(format);
            loggerObj = Logger.getLogger("peerLogger_" + peerID);
            loggerObj.addHandler(handler);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // logging TCP connection between source peer (with peer ID "peerID") 
    // and destination peer (with peer ID "destPeerID") once it is established
    public  void setTCPConnectionfromLog(String destPeerID, Calendar currentTime) {
        String msg = dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + "is connected from Peer " + destPeerID;
        writeToLogger(msg);

        //loggerObj.info( dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + "is connected from Peer " + destPeerID);
    }

    public  void setTCPConnectiontoLog(String destPeerID, Calendar currentTime) {
        String msg = dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + "is connected to Peer " + destPeerID;
        //loggerObj.info( dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + "is connected to Peer " + destPeerID);
        writeToLogger(msg);

    }
    // logging the updated preferred neighbours list for peer 
    // with peer ID "peerID" after a certain unchoking interval

    public  void updateNeighboursLog(String[] neighboursList, Calendar currentTime) {
        String neighbours = "";
        for (int i = 0; i < neighboursList.length; i++) {
            neighbours += neighboursList[i] + (i != neighboursList.length-1 ? "," : "");
        }
        String msg = dateFormatter.format(currentTime.getTime()) + "Peer" + peerID + "has the preferred neighbours " + neighbours;
        //loggerObj.info(dateFormatter.format(currentTime.getTime()) + "Peer" + peerID + "has the preferred neighbours " + neighbours);
        writeToLogger(msg);
    }

    private synchronized void writeToLogger(String msg) {
        loggerObj.info(msg);
    }

    // logging the change in optimistically unchoked neighbour by peer with peer ID "peerID"
    public void changeOptUnchokedNeighbourLog(String optUnchokedPeerID, Calendar currentTime) {
        String msg = dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + "has the optimistically unchoked neighbour "+ optUnchokedPeerID;
        //loggerObj.info(dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + "has the optimistically unchoked neighbour "+ optUnchokedPeerID);
        writeToLogger(msg);

    }

    // logging the event of unchoking of a peer with peer ID "peerID" when 
    // it receives an "unchoked" message by a peer with peer id "destPeerID" 
    public void unchokedLog(String destPeerID, Calendar currentTime) {
        String msg = dateFormatter.format(currentTime.getTime()) + "Peer "+ destPeerID + " is choked by " + peerID;
        //loggerObj.info(dateFormatter.format(currentTime.getTime()) + "Peer "+ destPeerID + " is unchoked by " + peerID);
        writeToLogger(msg);

    }

    // logging the event of unchoking of a peer with peer ID "peerID" when 
    // it receives an "choked" message by a peer with peer id "destPeerID"  
    public void chockedLog(String destPeerID, Calendar currentTime) {
        String msg = dateFormatter.format(currentTime.getTime()) + "Peer "+ destPeerID + " is choked by " + peerID;
        writeToLogger(msg);

        //loggerObj.info(dateFormatter.format(currentTime.getTime()) + "Peer "+ destPeerID + " is choked by " + peerID);
    }

    // logging the event of receiving a "have" message from peer with peer ID 
    // "destPeerID" by peer with "peerID" where the "pieceIndex" is also tracked
    public void receivedHaveMessageLog(String destPeerID, int pieceIndex,Calendar currentTime) {
        String msg = dateFormatter.format(currentTime.getTime()) + "Peer "+ peerID + " received the 'have' message from " + destPeerID
                + " for the piece " + pieceIndex;
        writeToLogger(msg);

        //loggerObj.info( dateFormatter.format(currentTime.getTime()) + "Peer "+ peerID + " received the 'have' message from " + destPeerID
         //       + " for the piece " + pieceIndex);
    }

    // logging the event of receiving "interested" message by peer with "peerID" 
    // from peer with "destPeerId"
    public void receivedInterestedMessageLog(String destPeerId, Calendar currentTime) {
        String msg = dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + " received the 'interested' message from "+ destPeerId;
        writeToLogger(msg);

        //loggerObj.info( dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + " received the 'interested' message from "+ destPeerId);
    }

    // logging the event of receiving "uninterested" message by peer with "peerID" 
    // from peer with "destPeerId"
    public void receivedNotInterestedMessageLog(String destPeerId, Calendar currentTime) {
        String msg = dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + " received the 'not interested' message from "+ destPeerId;
        writeToLogger(msg);

        //loggerObj.info(dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + " received the 'not interested' message from "+ destPeerId);
    }

    // logging the event of peer with "peerID" downloading a piece with "pieceIndex" 
    // from peer with "destpeerId"
    public void downloadingPieceLog(String destPeerId, int pieceIndex, Calendar currentTime) {
        String msg = dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + " has downloaded the piece "+ pieceIndex + " from " + destPeerId;
        writeToLogger(msg);

        //loggerObj.info(dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + " has downloaded the piece "+ pieceIndex + " from " + destPeerId);
    }

    // logging the event of completion of download of the piece by peer with "peerID"
    public void downloadCompleteLog() {
        String msg = "Peer " + peerID + " has downloaded the complete file";
        //loggerObj.info("Peer " + peerID + " has downloaded the complete file");
        writeToLogger(msg);

    }
}
