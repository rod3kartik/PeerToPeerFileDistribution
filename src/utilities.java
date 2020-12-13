import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.*;
import java.io.*;
import java.util.*;


public class utilities {
    
    //Utility function to convert byte array to long integer value
    public static long fromByteArrayToLong(byte[] value) 
    {   
        
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        try {
            final byte val = (byte) (value[0] < 0 ? 0xFF : 0);
    
            for(int i = value.length; i < Long.BYTES; i++)
                buffer.put(val);
        
            buffer.put(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.getLong(0);
    }

    //Synchronized outputstream writing function 
    public static synchronized void writeToOutputStream(ObjectOutputStream out, byte[] message){
        if(out == null){
            return;
        }
        try {
            out.write(message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }

    //Function to get k preferred neighbors 
    public static List<RemotePeerInfo> getKPreferredNeighbors(){
        List<RemotePeerInfo> kPreferredNeighbors = new ArrayList();
        int k = Constants.NumberOfPreferredNeighbors;
      

        RemotePeerInfo[] peers = new RemotePeerInfo[Constants.interestedNeighbors.size()];
        int index = 0;
        for (RemotePeerInfo remotePeerInfo : Constants.interestedNeighbors) {
            if(index >= peers.length) break;
            peers[index] = remotePeerInfo;
            index++;
        }
        if(peers.length < 1){
            return kPreferredNeighbors;
        }
        if(Constants.selfPeerInfo.fileAvailable.equals("1")){
           int[] randomNumbers = new Random().ints(0, peers.length).distinct().limit(Math.min(k, peers.length)).toArray();
           for (int i : randomNumbers) {
               kPreferredNeighbors.add(peers[i]);
           }
        } else {

            Arrays.sort(peers, Comparator.comparing(RemotePeerInfo::getDownloadRate));
            for (RemotePeerInfo remotePeerInfo : peers) {
                if(k == 0) break;
                if(Constants.interestedNeighbors.contains(remotePeerInfo)){
                    kPreferredNeighbors.add(remotePeerInfo);
                    k -= 1;
                }
            }
        }
        return kPreferredNeighbors;
    }

    //Utility function to remove object from an array by index
    public static RemotePeerInfo[] removeObjectByIndexFromArray(RemotePeerInfo[] peers, int index){
        RemotePeerInfo[] returnPeers = new RemotePeerInfo[peers.length - 1];
        int count = 0;
        for (int i = 0; i < peers.length; i++) {
            if(i == index){
                continue;
            }
            returnPeers[count] = peers[i];
            count += 1;
        }
        return returnPeers;
    }
    

    //broadcasting 'have' message to all it's neighbors
    public static void broadcastHaveMessage(String peerID, byte[] pieceIndex){
        for (RemotePeerInfo peer : Constants.listOfAllPeers) {
            if(!peer.peerID.equals(peerID)){
                try {
                    Message msg = new Message(1 + pieceIndex.length, 4, pieceIndex);
                    byte[] msgByteArray = msg.createMessage();
                    utilities.writeToOutputStream(peer.out, msgByteArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //getting already received indexes of a input bitfield
    public static List<Integer> getIndexListFromBitset(BitSet bitSet) {
        List<Integer> indexes = new ArrayList<>(); 
        for (int i = bitSet.nextSetBit(0); i != -1; i = bitSet.nextSetBit(i + 1)) {
            indexes.add(i);
        }
        return indexes;
    }

    //function to check if the complete file is downloaded by all the peers
    public static boolean isDownloadComplete(){
        if((Constants.peerIDToBitfield.size() == Constants.listOfAllPeers.length - 1)){
            for(Map.Entry<String, BitSet> setEntry : Constants.peerIDToBitfield.entrySet()){
                if(setEntry.getValue().cardinality() != Constants.selfBitfield.cardinality()){
                    return false;
                }
            }
            Constants.isShutDownMessageReceived = true;
            return true;
        }
        return false;
    }

    //Utility function to read the given initial file 
    //to break and read into chunks
   public static Piece[] readFileIntoChunks() {
        Piece[] fileChunks = new Piece[Constants.numberOfChunks];
       try {
        byte[] buffer = Files.readAllBytes(Paths.get("./theFile"));
        // System.out.println("Buffer length "+ buffer.length);
        int offset = 0;
        int fileChunkIndex = 0;
        
        while(offset<buffer.length){
            fileChunks[fileChunkIndex++] = new Piece(Arrays.copyOfRange(buffer, offset, Math.min(offset + Constants.PieceSize, buffer.length)));
            offset += Constants.PieceSize;
        }
       } catch (Exception e) {
           System.out.println("Error in the file reading operation");
           e.printStackTrace();
       }
        
       return fileChunks;
   }

   //Broadcasting shutdown message to all peers once the shutdown is complete
    public static void broadcastShutdownMessage() {
        for (RemotePeerInfo peer : Constants.listOfAllPeers) {
            if(!(peer.peerID.equals(Constants.selfPeerInfo.peerID))){
                byte[] msg = new Message(1, -1, null).createMessage();
                utilities.writeToOutputStream(peer.out, msg);
            }
        }
    }


    //Meging all the chunks and writing back into file
    public static synchronized void mergeFileChunks(){
        String path = "../peer_" + Constants.selfPeerInfo.peerID + "/file.txt";
        
        File file = new File(path);
        if(!file.exists()){
            
                file.getParentFile().mkdirs(); 
                try{
                file.createNewFile();
                System.out.println("File and Folder has been created");
                }
                catch(Exception e){
                    System.out.println("Error in creating file");
                    e.printStackTrace();
                }

        }
        Constants.fl.downloadCompleteLog(Calendar.getInstance());

        try{
            FileOutputStream stream = new FileOutputStream(file);
            for(Piece piece : Constants.fileChunks){
                stream.write(piece.getPieceContent());
            }
            stream.flush();
            stream.close();
        }
        catch(Exception e ){
            e.printStackTrace();
        }
    }

}
