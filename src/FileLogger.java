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

    public FileLogger(String peerID) throws Exception{
        this.peerID = peerID;
        //String str = Integer.toString(peerID);
        FileHandler logFile = new FileHandler("log_peer_"+ peerID, true);
        loggerObj = Logger.getLogger("P2P.Log.File");
        loggerObj.addHandler(logFile);
    }

    public void setTCPConnectionLog(int destPeerID) {
        loggerObj.info( "Peer " + peerID + "is connected from Peer " + destPeerID);
    }

    public void updateNeighboursLog(List<Integer> neighboursList) {
        String neighbours = "";
        for (int i = 0; i < neighboursList.size(); i++) {
            neighbours += Integer.toString(neighboursList.get(i)) + (i != neighboursList.size()-1 ? "," : "");
        }

        loggerObj.info("Peer" + peerID + "has the preferred neighbours " + neighbours);
    }

    public void changeOptUnchokedNeighbourLog(int optUnchokedPeerID) {
        loggerObj.info("Peer " + peerID + "has the optimistically unchoked neighbour "+ optUnchokedPeerID);
    }

    public void unchokedLog(int destPeerID) {
        loggerObj.info("Peer "+ peerID + " is unchoked by " + destPeerID);
    }

    public void chockedLog(int destPeerID) {
        loggerObj.info("Peer "+ peerID + " is choked by " + destPeerID);
    }

    public void receivedHaveMessageLog(int destPeerID, int pieceIndex) {
        loggerObj.info("Peer "+ peerID + " received the 'have' message from " + destPeerID
                + " for the piece " + pieceIndex);
    }

    public void receivedInterestedMessageLog(int destPeerId) {
        loggerObj.info("Peer " + peerID + " received the 'interested' message from "+ destPeerId);
    }

    public void receivedNotInterestedMessageLog(int destPeerId) {
        loggerObj.info("Peer " + peerID + " received the 'not interested' message from "+ destPeerId);
    }

    public void downloadingPieceLog(int destPeerId) {
        loggerObj.info("Peer " + peerID + " received the 'interested' message from "+ destPeerId);
    }

    public void downloadCompleteLog() {
        loggerObj.info("Peer " + peerID + " has downloaded the complete file");
    }
}
