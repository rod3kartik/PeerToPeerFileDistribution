import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class Peer {
    private static int sPort;   //The server will be listening on this port number
    static int NumberOfPreferredNeighbors;
    static int UnchokingInterval;
    static int OptimisticUnchokingInterval;
    static String FileName;
    static int FileSize;
    static int PieceSize;

    //Mapping of message type to value
    public Map<String, Integer> messageTypeToVal = new HashMap<>() {{
        put("choke", 0);
        put("unchoke", 1);
        put("interested", 2);
        put("not interested", 3);
        put("have", 4);
        put("bitfield", 5);
        put("request", 6);
        put("piece", 7)
    }};

    //mapping of value to corresponding message type
    public Map<Integer, String> valToMessageType = new HashMap<>() {{
        put(0,"choke");
        put(1, "unchoke");
        put(2, "interested");
        put(3, "not interested");
        put(4, "have");
        put(5, "bitfield");
        put(6, "request");
        put(7, "piece");
    }};

    public static void main(String[] args) throws Exception {

        String peerFromCommandLine = args[0];

        //Setting configuration variables
        NumberOfPreferredNeighbors = CommonFileReader.getNumberOfPreferredNeighbours();
        UnchokingInterval = CommonFileReader.getUnchokingInterval();
        OptimisticUnchokingInterval = CommonFileReader.getOptimisticUnchokingInterval();
        FileName = CommonFileReader.getFileName();
        FileSize = CommonFileReader.getFileSize();
        PieceSize = CommonFileReader.getPieceSize();

        FileLogger fl = new FileLogger(peerFromCommandLine);
        fl.downloadCompleteLog();
        Connection.fileReader();

        List<RemotePeerInfo> allBeforePeerInfo = Connection.getPeerInfo(peerFromCommandLine);
        RemotePeerInfo selfInfo = allBeforePeerInfo.get(allBeforePeerInfo.size()-1);
        allBeforePeerInfo.remove(allBeforePeerInfo.size()-1);
        sPort = Integer.parseInt(selfInfo.peerPort);
        System.out.println("Server has started");
        ServerSocket listener = new ServerSocket(sPort);
        //int clientNum = 1;
        HashSet<Integer> connectedClients = new HashSet();
        try {
            System.out.println("Client to be started");
            for(int i = 0; i < allBeforePeerInfo.size();i++){
                new Client("localhost",allBeforePeerInfo.get(i).peerPort).start();
            }
            System.out.println("server to be started");
            new Server(listener.accept()).start();
        }
        catch(Exception e){
        }
        System.out.println("Client to be started");
//        try{
//            for(int i = 0; i < allBeforePeerInfo.size()-1;i++){
//                new ClientHandler("localhost",allBeforePeerInfo.get(i).peerPort).start();
//            }
//        }
//        catch(Exception e){
//
//        }
    }
}


