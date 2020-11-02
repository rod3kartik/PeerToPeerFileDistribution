import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Peer {
    private static int sPort;   //The server will be listening on this port number

    public static void main(String[] args) throws Exception {

        String peerFromCommandLine = args[0];

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
    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client's requests.
     */


}


