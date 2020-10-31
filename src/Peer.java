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
            while(true) {
                new ServerHandler(listener.accept()).start();
            }

        }
        catch(Exception e){

        }
        try{
            for(int i = 0; i < allBeforePeerInfo.size()-1;i++){
                new ClientHandler("localhost",allBeforePeerInfo.get(i).peerPort).start();
            }
        }
        catch(Exception e){

        }
    }
    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client's requests.
     */
    private static class ServerHandler extends Thread {
        private String message;    //message received from the client
        private String MESSAGE;    //uppercase message send to the client
        private Socket connection;
        private ObjectInputStream in;	//stream read from the socket
        private ObjectOutputStream out;    //stream write to the socket
        private int no;		//The index number of the client

        public ServerHandler(Socket connection) {
            this.connection = connection;

            //System.out.println("connection" + connection);
        }

        public void run() {
            try{
                //initialize Input and Output streams
                out = new ObjectOutputStream(connection.getOutputStream());
                out.flush();
                in = new ObjectInputStream(connection.getInputStream());
                try{
                    while(true)
                    {
                        //receive the message sent from the client
                        message = (String)in.readObject();
                        System.out.println("Receive message: " + message + " from client " + no);
                        //Capitalize all letters in the message
                        MESSAGE = message.toUpperCase();
                        //send MESSAGE back to the client
                        sendMessage(MESSAGE);
                    }
                }
              catch(ClassNotFoundException classnot){
                    System.err.println("Data received in unknown format");
                }
            }
            catch(IOException ioException){
                System.out.println("Disconnect with Client " + no);
            }
            finally{
                //Close connections
                try{
                    in.close();
                    out.close();
                    connection.close();
                }
                catch(IOException ioException){
                    System.out.println("Disconnect with Client " + no);
                }
            }
        }

        public void sendMessage(String msg)
        {
            try{
                out.writeObject(msg);
                out.flush();
                System.out.println("Send message: " + msg + " to Client " + no);
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }

    }
    private static class ClientHandler extends Thread {
        private String message;    //message received from the client
        private String MESSAGE;    //uppercase message send to the client
        private Socket requestSocket;
        private ObjectInputStream in;	//stream read from the socket
        private ObjectOutputStream out;    //stream write to the socket
        private int port;		//The index number of the client

        public ClientHandler(String connection, String port) throws Exception {
            this.requestSocket = new Socket("localhost",Integer.parseInt(port));
            this.port = Integer.parseInt(port);
            //System.out.println("connection" + connection);
        }

        public void run() {
            try{
                //initialize Input and Output streams
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(requestSocket.getInputStream());
                try{
                    while(true)
                    {
                        //receive the message sent from the client
                        message = (String)in.readObject();
                        System.out.println("Receive message: " + message  );
                        //Capitalize all letters in the message
                        MESSAGE = message.toUpperCase();
                        //send MESSAGE back to the client
                        sendMessage(MESSAGE);
                    }
                }
                catch(ClassNotFoundException classnot){
                    System.err.println("Data received in unknown format");
                }
            }
            catch(IOException ioException){
                System.out.println("Disconnect with Client " );
            }
            finally{
                //Close connections
                try{
                    in.close();
                    out.close();
                    requestSocket.close();
                }
                catch(IOException ioException){
                    System.out.println("Disconnect with Client " );
                }
            }
        }

        public void sendMessage(String msg)
        {
            try{
                out.writeObject(msg);
                out.flush();
                System.out.println("Send message: " + msg + " to Client " );
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }

    }
}

