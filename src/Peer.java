import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Peer {
    private static int sPort;   //The server will be listening on this port number

    // getter for chokeMap
    public Map<Integer,Integer> getPeerChokeList() {
        return peerChokeMap;
    }

    //Setting values to choke Map
    public void setPeerChokeMap(int nodeId, int value) {
        peerChokeMap.put(nodeId,value);
    }

    //static or non-static
    private static Map<Integer,Integer> peerChokeMap = new HashMap<>();
    

    public static void main(String[] args) throws Exception {

        String peerFromCommandLine = args[0];

        Constants constantVariables = new Constants();

        FileLogger fl = new FileLogger(peerFromCommandLine);

        List<RemotePeerInfo> allBeforePeerInfo = Connection.getPeerInfo(peerFromCommandLine);
        System.out.println(allBeforePeerInfo);
        RemotePeerInfo selfInfo = allBeforePeerInfo.get(allBeforePeerInfo.size()-1);
        try {
            allBeforePeerInfo.remove(allBeforePeerInfo.size()-1);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        sPort = Integer.parseInt(selfInfo.peerPort);
        
        
        for (RemotePeerInfo outgoingPeer : allBeforePeerInfo) {
            System.out.println(outgoingPeer);
            Socket neighborPeer = new Socket(outgoingPeer.peerAddress, Integer.parseInt(outgoingPeer.peerPort));
            ObjectOutputStream wt = new ObjectOutputStream(neighborPeer.getOutputStream());
            wt.writeObject("hello there");
            wt.flush();
            new PeerHandler(neighborPeer).start();
        }
        
        List<RemotePeerInfo> afterPeers = Connection.getAfterPeersInfo(peerFromCommandLine);
        ServerSocket serverSocket = new ServerSocket(sPort);
        for(int incomingPeers = 0; incomingPeers< 5 - allBeforePeerInfo.size(); incomingPeers++){
            // Server serverThread = new Server(sPort, fl);
            Socket peerSocket = serverSocket.accept();
            new PeerHandler(peerSocket).start();
        }
        
        HashSet<Integer> connectedClients = new HashSet();


    }
}


