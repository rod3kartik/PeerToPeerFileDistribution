import java.io.*;
import java.util.*;
public class Constants {

    
    public static int NumberOfPreferredNeighbors;
    public static int UnchokingInterval;
    public static int OptimisticUnchokingInterval;
    public static String FileName;
    public static int FileSize;
    public static int PieceSize;
    public static RemotePeerInfo[] listOfAllPeers = new RemotePeerInfo[4];
    public static String headerHandshake ="P2PFILESHARINGPROJ";
    public static HashSet<Integer> handshakedPeers = new HashSet<>();
    //Mapping of message type to value
    public static Map<String, Integer> messageTypeToVal = new HashMap() {{
        put("choke", 0);
        put("unchoke", 1);
        put("interested", 2);
        put("not interested", 3);
        put("have", 4);
        put("bitfield", 5);
        put("request", 6);
        put("piece", 7);
    }};

    //mapping of value to corresponding message type
    public static Map<Integer, String> valToMessageType = new HashMap() {{
        put(0,"choke");
        put(1, "unchoke");
        put(2, "interested");
        put(3, "not interested");
        put(4, "have");
        put(5, "bitfield");
        put(6, "request");
        put(7, "piece");
    }};

    public static Map<String, Integer> socketToPeerID = new HashMap<>();

    Constants(){
        //Setting configuration variables
        CommonFileReader.confReader();
        NumberOfPreferredNeighbors = CommonFileReader.getNumberOfPreferredNeighbours();
        UnchokingInterval = CommonFileReader.getUnchokingInterval();
        OptimisticUnchokingInterval = CommonFileReader.getOptimisticUnchokingInterval();
        FileName = CommonFileReader.getFileName();
        FileSize = CommonFileReader.getFileSize();
        PieceSize = CommonFileReader.getPieceSize();
        listOfAllPeers = Connection.fileReader();
        generateMapOfSocketToPeerID();
    }

    public void generateMapOfSocketToPeerID() {
        for (RemotePeerInfo peer : listOfAllPeers) {
            System.out.println(peer.peerAddress + peer.peerId);
            socketToPeerID.put(peer.peerAddress + ":" + peer.peerPort, Integer.parseInt(peer.peerId));
        }
    }
}
