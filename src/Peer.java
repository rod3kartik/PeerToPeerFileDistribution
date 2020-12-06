import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

        new Constants();

        System.out.println(Constants.fileChunks);
        FileLogger fl = new FileLogger(peerFromCommandLine);

        // connecting to other peers and starting Client and Server
        List<RemotePeerInfo> allBeforePeerInfo = Connection.getPeerInfo(peerFromCommandLine);
        System.out.println(allBeforePeerInfo);
        RemotePeerInfo selfInfo = allBeforePeerInfo.get(allBeforePeerInfo.size()-1);

        //Global self peer index
        Constants.selfPeerIndex = allBeforePeerInfo.size()-1;
        try {
            allBeforePeerInfo.remove(allBeforePeerInfo.size()-1);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        sPort = Integer.parseInt(selfInfo.peerPort);
        
        for (int outgoingPeer = 0; outgoingPeer < Constants.selfPeerIndex; outgoingPeer++) {
            System.out.println("Outgoing peer " + allBeforePeerInfo.get(outgoingPeer));
            Socket neighborPeer = new Socket(allBeforePeerInfo.get(outgoingPeer).peerAddress, Integer.parseInt(allBeforePeerInfo.get(outgoingPeer).peerPort));
            new PeerHandler(neighborPeer, outgoingPeer).start();
        }
        
        ServerSocket serverSocket = new ServerSocket(sPort);
        for(int incomingPeers = Constants.selfPeerIndex + 1; incomingPeers< Constants.listOfAllPeers.length; incomingPeers++){
            Socket peerSocket = serverSocket.accept();
            new PeerHandler(peerSocket, incomingPeers).start();
        }
        
        HashSet<Integer> connectedClients = new HashSet();


    }
}


