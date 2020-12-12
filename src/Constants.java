import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.util.*;
public class Constants {

    
    public static int NumberOfPreferredNeighbors;
    public static int UnchokingInterval;
    public static int OptimisticUnchokingInterval;
    public static String FileName;
    public static int FileSize;
    public static int numberOfChunks;
    public static int PieceSize;
    public static RemotePeerInfo[] listOfAllPeers;
    public static String headerHandshake ="P2PFILESHARINGPROJ";
    public static int totalNumberOfPeers;
    public static HashMap<String, Boolean> handshakedPeers = new HashMap<>();
    public static int selfPeerIndex = 0;
    public static RemotePeerInfo selfPeerInfo;
    public static Piece[] fileChunks;
    public static BitSet selfBitfield;
    public static BitSet chunksLeft;
    public static HashMap<String, Boolean> chokeUnchokeMap = new HashMap<>();
    public static HashSet<RemotePeerInfo> interestedNeighbors = new HashSet();
    public static List<RemotePeerInfo> preferredNeighbors = new ArrayList<>();
    public static List<Integer> requestedPieceIndexes = new ArrayList<>();
    public static FileLogger fl;
    public static boolean isShutDownMessageReceived = false;
    public static ServerSocket selfServerSocket;
    public static List<Thread> listOfThreads = new ArrayList<>();
    public static List<Socket> listOfAllSockets = new ArrayList<>();
    // public static 
    //Mapping of message type to value
    public static Map<String,RemotePeerInfo> peerIDToPeerInfo = new HashMap<>();
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
        UnchokingInterval = CommonFileReader.getUnchokingInterval()*1000;
        OptimisticUnchokingInterval = CommonFileReader.getOptimisticUnchokingInterval()*1000;
        FileName = CommonFileReader.getFileName();
        FileSize = CommonFileReader.getFileSize();

        PieceSize = CommonFileReader.getPieceSize();
        System.out.println(FileSize + " " + PieceSize + " " + Math.ceil(FileSize/PieceSize) + " " + (int)Math.ceil(FileSize*1.0/PieceSize*1.0));
        try {
            numberOfChunks = (int)Math.ceil((FileSize * 1.0)/(PieceSize*1.0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        listOfAllPeers = Connection.fileReader();
        //System.out.println("list of all peers "+ listOfAllPeers[listOfAllPeers.length-2].peerID);
         printArrayOfPeers(listOfAllPeers);
    }

    // public void generateMapOfSocketToPeerID() {
    //     for (RemotePeerInfo peer : listOfAllPeers) {
    //         System.out.println(peer.peerAddress + peer.peerID);
    //         socketToPeerID.put(peer.peerAddress + ":" + peer.peerPort, Integer.parseInt(peer.peerID));
    //     }
    // }

    public static void printArrayOfPeers(RemotePeerInfo[] listOfAllPeers) {
        for (RemotePeerInfo peer : listOfAllPeers) {
            System.out.println(peer.peerID + peer.bitfield);
        }
    }

    public static void printListOfPeers(List<RemotePeerInfo> listOfAllPeers) {
        for (RemotePeerInfo peer : listOfAllPeers) {

            System.out.println("From printListOfPeers " + peer.peerID + peer.bitfield);
        }
    }

    public static void setSelfBit() {
        selfBitfield = listOfAllPeers[selfPeerIndex].bitfield;
    }

    public static void setChunksLeft() {
        if (selfBitfield.length() ==0){
            chunksLeft = new BitSet(Constants.numberOfChunks);
            chunksLeft.set(0,Constants.numberOfChunks);
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
        String[] prefNeighours = new String[peers.size()];
        int index = 0;
        for(RemotePeerInfo peer : peers){
            prefNeighours[index++] = peer.peerID;
        }
        fl.updateNeighboursLog(prefNeighours,Calendar.getInstance());
    }

    //set selfPeerInfo
    public static void setSelfPeerInfo(){
        selfPeerInfo = listOfAllPeers[selfPeerIndex];
    }

    public static void setFileChunks(){
        if(selfPeerInfo.fileAvailable.equals("1")){
            fileChunks = utilities.readFileIntoChunks();
            // printFileChunks(fileChunks);
        } else {
            fileChunks = new Piece[Constants.numberOfChunks];
    }
    }

    private static void printFileChunks(Piece[] fileChunks2) {

        for(Piece piece : Constants.fileChunks){
            //System.out.println("2 . Final chunks stored are: " + new String(piece.getPieceContent()));
        }
    }


    public static synchronized void updateRequestedPieceIndexes(int index, boolean add) {
        if(add){
            requestedPieceIndexes.add(index);
        } else {
            requestedPieceIndexes.remove(Integer.valueOf(index));
        }
    }
}
