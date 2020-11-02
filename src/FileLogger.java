import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.FileHandler;

public class FileLogger {
    static String peerID;
    Logger loggerObj;

//    public static void main(String[] args) {
//
//
//    }

    // creating log files for each peer
    public FileLogger(String peerID) throws Exception{
        this.peerID = peerID;
        //String str = Integer.toString(peerID);
        FileHandler logFile = new FileHandler("log_peer_"+ peerID, true);
        loggerObj = Logger.getLogger("P2P.Log.File");
        loggerObj.addHandler(logFile);
    }

    // logging TCP connection between source peer (with peer ID "peerID") 
    // and destination peer (with peer ID "destPeerID") once it is established
    public void setTCPConnectionLog(int destPeerID) {
        loggerObj.info( "Peer " + peerID + "is connected from Peer " + destPeerID);
    }

    // logging the updated preferred neighbours list for peer 
    // with peer ID "peerID" after a certain unchoking interval
    public void updateNeighboursLog(List<Integer> neighboursList) {
        String neighbours = "";
        for (int i = 0; i < neighboursList.size(); i++) {
            neighbours += Integer.toString(neighboursList.get(i)) + (i != neighboursList.size()-1 ? "," : "");
        }

        loggerObj.info("Peer" + peerID + "has the preferred neighbours " + neighbours);
    }

    // logging the change in optimistically unchoked neighbour by peer with peer ID "peerID"
    public void changeOptUnchokedNeighbourLog(int optUnchokedPeerID) {
        loggerObj.info("Peer " + peerID + "has the optimistically unchoked neighbour "+ optUnchokedPeerID);
    }

    // logging the event of unchoking of a peer with peer ID "peerID" when 
    // it receives an "unchoked" message by a peer with peer id "destPeerID" 
    public void unchokedLog(int destPeerID) {
        loggerObj.info("Peer "+ peerID + " is unchoked by " + destPeerID);
    }

    // logging the event of unchoking of a peer with peer ID "peerID" when 
    // it receives an "choked" message by a peer with peer id "destPeerID"  
    public void chockedLog(int destPeerID) {
        loggerObj.info("Peer "+ peerID + " is choked by " + destPeerID);
    }

    // logging the event of receiving a "have" message from peer with peer ID 
    // "destPeerID" by peer with "peerID" where the "pieceIndex" is also tracked
    public void receivedHaveMessageLog(int destPeerID, int pieceIndex) {
        loggerObj.info("Peer "+ peerID + " received the 'have' message from " + destPeerID
                + " for the piece " + pieceIndex);
    }

    // logging the event of receiving "interested" message by peer with "peerID" 
    // from peer with "destPeerId"
    public void receivedInterestedMessageLog(int destPeerId) {
        loggerObj.info("Peer " + peerID + " received the 'interested' message from "+ destPeerId);
    }

    // logging the event of receiving "uninterested" message by peer with "peerID" 
    // from peer with "destPeerId"
    public void receivedNotInterestedMessageLog(int destPeerId) {
        loggerObj.info("Peer " + peerID + " received the 'not interested' message from "+ destPeerId);
    }

    // logging the event of peer with "peerID" downloading a piece with "pieceIndex" 
    // from peer with "destpeerId"
    public void downloadingPieceLog(int destPeerId, int pieceIndex) {
        loggerObj.info("Peer " + peerID + " has downloaded the piece "+ pieceIndex + " from " + destPeerId);
    }

    // logging the event of completion of download of the piece by peer with "peerID"
    public void downloadCompleteLog() {
        loggerObj.info("Peer " + peerID + " has downloaded the complete file");
    }
}
