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
    public static HashMap<String, Boolean> handshakedPeers = new HashMap<>();
    public static int selfPeerIndex = 0;
    public static Piece[] fileChunks;
    public static BitSet selfBitfield;
    public static BitSet chunksLeft;
    public static HashMap<String, Boolean> chokeUnchokeMap = new HashMap<>();
    public static HashSet<RemotePeerInfo> interestedNeighbors = new HashSet();
    public static List<RemotePeerInfo> preferredNeighbors = new ArrayList<>();
    // public static 
    //Mapping of message type to value
    public static Map<String, Integer> messageTypeToVal = new HashMap(){{
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

    public static Map<String, BitSet> peerIDToBitfield = new HashMap<>();

    Constants(){
        //Setting configuration variables
        CommonFileReader.confReader();
        NumberOfPreferredNeighbors = CommonFileReader.getNumberOfPreferredNeighbours();
        UnchokingInterval = CommonFileReader.getUnchokingInterval();
        OptimisticUnchokingInterval = CommonFileReader.getOptimisticUnchokingInterval();
        FileName = CommonFileReader.getFileName();
        FileSize = CommonFileReader.getFileSize();
        PieceSize = CommonFileReader.getPieceSize();
        fileChunks = utilities.readFileIntoChunks();
        listOfAllPeers = Connection.fileReader();
        System.out.println(listOfAllPeers.length);
        printListOfAllPairs();
    }

    // public void generateMapOfSocketToPeerID() {
    //     for (RemotePeerInfo peer : listOfAllPeers) {
    //         System.out.println(peer.peerAddress + peer.peerID);
    //         socketToPeerID.put(peer.peerAddress + ":" + peer.peerPort, Integer.parseInt(peer.peerID));
    //     }
    // }

    public void printListOfAllPairs() {
        for (RemotePeerInfo peer : listOfAllPeers) {
            System.out.println(peer.peerID + peer.bitfield);
        }
    }

    public static void setSelfBit() {
        selfBitfield = listOfAllPeers[selfPeerIndex].bitfield;
    }

    public static void setChunksLeft() {
        if (selfBitfield.length() ==0){
            chunksLeft = new BitSet(Constants.fileChunks.length);
            chunksLeft.set(0,Constants.fileChunks.length);
            //this.chunksLeft = chunksLeft;

        }
        else {
            chunksLeft = (BitSet) selfBitfield.clone();
            chunksLeft.flip(0, chunksLeft.length());
        }
    }

    //Setting list of preffered neighbors
    public static synchronized void setListOfPreferredNeighbours(List<RemotePeerInfo> peers){
        preferredNeighbors = peers;
    }
}
