import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.io.FileNotFoundException;
public class FileLogger {
    public static String peerID;
    //Logger loggerObj;
    private static PrintWriter logfile;
    private SimpleDateFormat dateFormatter;
    //private FileHandler handler;

    // creating log files for each peer
    public FileLogger(String peerID) {
        try {
            this.peerID = peerID;
            logfile = new PrintWriter("../log_peer_" + peerID + ".log");

        } catch (FileNotFoundException e) {
            System.out.println("Not able to create log writer");
        }
    }

    // logging TCP connection between source peer (with peer ID "peerID")
    // and destination peer (with peer ID "destPeerID") once it is established
    public  void setTCPConnectionfromLog(String destPeerID, Calendar currentTime) {
        //String msg = dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + "is connected from Peer " + destPeerID;
        //writeToLogger(msg);
        try {
            logfile.println(currentTime.getTime() + "Peer " + peerID + " is connected from Peer " + destPeerID);
            logfile.println();
            logfile.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void setTCPConnectiontoLog(String destPeerID, Calendar currentTime) {
        //String msg = dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + "is connected to Peer " + destPeerID;
        //loggerObj.info( dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + "is connected to Peer " + destPeerID);
        //writeToLogger(msg);

        try {
            logfile.println(currentTime.getTime() + "Peer " + peerID + " is connected to Peer " + destPeerID);
            logfile.println();

            logfile.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // logging the updated preferred neighbours list for peer
    // with peer ID "peerID" after a certain unchoking interval

    public  void updateNeighboursLog(String[] neighboursList, Calendar currentTime) {
        String neighbours = "";
        for (int i = 0; i < neighboursList.length; i++) {
            neighbours += neighboursList[i] + (i != neighboursList.length-1 ? "," : "");
        }

        try {
            logfile.println(currentTime.getTime() + "Peer " + peerID + " has the preferred neighbors " + neighbours);
            logfile.println();

            logfile.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // private synchronized void writeToLogger(String msg) {
    //     loggerObj.info(msg);


    // }

    // logging the change in optimistically unchoked neighbour by peer with peer ID "peerID"
    public void changeOptUnchokedNeighbourLog(String optUnchokedPeerID, Calendar currentTime) {
        try {
            logfile.println(currentTime.getTime() + "Peer " + peerID + " has the optimistically unchocked neighbor " + optUnchokedPeerID);
            logfile.println();

            logfile.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // logging the event of unchoking of a peer with peer ID "peerID" when
    // it receives an "unchoked" message by a peer with peer id "destPeerID"
    public void unchokedLog(String destPeerID, Calendar currentTime) {
        try {
            logfile.println(currentTime.getTime() + "Peer " + peerID + " is unchoked by " + destPeerID);
            logfile.println();

            logfile.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // logging the event of unchoking of a peer with peer ID "peerID" when
    // it receives an "choked" message by a peer with peer id "destPeerID"
    public void chockedLog(String destPeerID, Calendar currentTime) {
        try {
            logfile.println(currentTime.getTime() + "Peer " + peerID + " is choked by " + destPeerID);
            logfile.println();

            logfile.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //loggerObj.info(dateFormatter.format(currentTime.getTime()) + "Peer "+ destPeerID + " is choked by " + peerID);
    }

    // logging the event of receiving a "have" message from peer with peer ID
    // "destPeerID" by peer with "peerID" where the "pieceIndex" is also tracked
    public void receivedHaveMessageLog(String destPeerID, int pieceIndex,Calendar currentTime) {
        try {
            logfile.println(currentTime.getTime() + "Peer " + peerID + " received the 'have' message from " + destPeerID + " for the piece " + pieceIndex);
            logfile.println();

            logfile.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //loggerObj.info( dateFormatter.format(currentTime.getTime()) + "Peer "+ peerID + " received the 'have' message from " + destPeerID
        //       + " for the piece " + pieceIndex);
    }

    // logging the event of receiving "interested" message by peer with "peerID"
    // from peer with "destPeerId"
    public void receivedInterestedMessageLog(String destPeerId, Calendar currentTime) {
        try {
            logfile.println(currentTime.getTime() + "Peer " + peerID + " received the 'interested' message from " + destPeerId);
            logfile.println();

            logfile.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //loggerObj.info( dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + " received the 'interested' message from "+ destPeerId);
    }

    // logging the event of receiving "uninterested" message by peer with "peerID"
    // from peer with "destPeerId"
    public void receivedNotInterestedMessageLog(String destPeerId, Calendar currentTime) {

        try {
            logfile.println(currentTime.getTime() + "Peer " + peerID + " received the 'not interested' message from " + destPeerId);
            logfile.println();

            logfile.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //loggerObj.info(dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + " received the 'not interested' message from "+ destPeerId);
    }

    // logging the event of peer with "peerID" downloading a piece with "pieceIndex"
    // from peer with "destpeerId"
    public void downloadingPieceLog(String destPeerId, int pieceIndex, Calendar currentTime) {
        try {
            logfile.println(currentTime.getTime() + "Peer " + peerID + " has downloaded the piece " + pieceIndex + " from " + destPeerId);
            logfile.println();

            logfile.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //loggerObj.info(dateFormatter.format(currentTime.getTime()) + "Peer " + peerID + " has downloaded the piece "+ pieceIndex + " from " + destPeerId);
    }

    // logging the event of completion of download of the piece by peer with "peerID"
    public void downloadCompleteLog(Calendar currentTime) {
        try {
            logfile.println(currentTime.getTime() + "Peer " + peerID + " has downloaded the complete file.");
            logfile.println();

            logfile.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
