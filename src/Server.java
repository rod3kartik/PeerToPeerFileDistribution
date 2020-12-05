import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server extends Thread {

//    private static final int sPort = 8000;   //The server will be listening on this port number
//
//    public static void main(String[] args) throws Exception {
//        System.out.println("The server is running.");
//        ServerSocket listener = new ServerSocket(sPort);
//        int clientNum = 1;
//        try {
//            while(true) {
//                new Handler(listener.accept(),clientNum).start();
//                System.out.println("Client "  + clientNum + " is connected!");
//                clientNum++;
//            }
//        } finally {
//            listener.close();
//        }
//
//    }

    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client's requests.
     */
        private byte[] message;    //message received from the client
        private byte[] MESSAGE;    //uppercase message send to the client
        private int serverPort;
        private ObjectInputStream in;	//stream read from the socket
        private ObjectOutputStream out;    //stream write to the socket
        private int no;		//The index number of the client
        private FileLogger serverLog;

        public Server(int port, FileLogger fl) {
            this.serverPort = port;
//            System.out.println("connection" + connection);
            this.serverLog = fl;
        }

        public void run() {
            try{
                //initialize Input and Output streams
            	
            	Socket inputSocket = new Socket("localhost", this.serverPort);
                out = new ObjectOutputStream(inputSocket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(inputSocket.getInputStream());
                try{
                    while(true)
                    {   
                        System.out.println("Hey there");
                        //receive the message sent from the client
                        message = in.readAllBytes();

                        System.out.println("Receive message: " + message + " from client " + no);

                        //Build a message object
                        //Need to update the peerObject*
//                        Message messageObj = new Message(message, serverLog, null);
//                        messageObj.extractMessage();
                        //Capitalize all letters in the message
//                        MESSAGE = message.toUpperCase();
                        //send MESSAGE back to the client
                        String temp = new String(message);
                        temp += " from server";
                        System.out.println("Message created in server " + temp);
                        sendMessage(temp);
                    }
                }
                catch(Exception e){
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
                    // connection.close();
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


