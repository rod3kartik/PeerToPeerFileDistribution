import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.*;
import java.io.*;
import java.util.*;

//import java.util.stream.Collector;

//import org.graalvm.util.CollectionsUtil;



public class utilities {
    
    
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

    // public static int fromFourByteArrayToInteger(byte[] bytes) {
    //     return ((bytes[0] & 0xFF) << 24) | 
    //            ((bytes[1] & 0xFF) << 16) | 
    //            ((bytes[2] & 0xFF) << 8 ) | 
    //            ((bytes[3] & 0xFF) << 0 );
    // }

    public static String[] convertToStrings(byte[][] byteStrings) {
        String[] data = new String[byteStrings.length];
        for (int i = 0; i < byteStrings.length; i++) {
            data[i] = new String(byteStrings[i], Charset.defaultCharset());
    
        }
        return data;
    }

    public static synchronized void writeToOutputStream(ObjectOutputStream out, byte[] message){
        try {
            out.write(message);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
    
    public static List<RemotePeerInfo> getKPreferredNeighbors(){
        List<RemotePeerInfo> kPreferredNeighbors = new ArrayList();
        int k = Constants.NumberOfPreferredNeighbors;
        System.out.println("value of K is " + k);

        RemotePeerInfo[] peers = new RemotePeerInfo[Constants.interestedNeighbors.size()];
        int index = 0;
        System.out.println("Interested neighhours are " + Constants.interestedNeighbors);
        for (RemotePeerInfo remotePeerInfo : Constants.interestedNeighbors) {
            if(index >= peers.length) break;
            peers[index] = remotePeerInfo;
            index++;
        }
        System.out.println("Peers array length is " + peers.length);

        if(Constants.selfPeerInfo.fileAvailable.equals("1")){
           int[] randomNumbers = new Random().ints(0, peers.length).distinct().limit(Math.min(k, peers.length)).toArray();
           for (int i : randomNumbers) {
               kPreferredNeighbors.add(peers[i]);
           }
        //    System.out.println("in equals 2");
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
    
    private static byte[][] convertToBytes(String[] strings) {
        byte[][] data = new byte[strings.length][];
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            data[i] = string.getBytes(Charset.defaultCharset()); // you can chose charset
        }
        return data;
    }

    //broadcasting have message to all it's neighbors
    public static void broadcastHaveMessage(String peerID, byte[] pieceIndex){
        for (RemotePeerInfo peer : Constants.listOfAllPeers) {
            if(!peer.peerID.equals(peerID)){
                try {
                    Message msg = new Message(4 + pieceIndex.length, 4, pieceIndex);
                    byte[] msgByteArray = msg.createMessage();
                    utilities.writeToOutputStream(peer.out, msgByteArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<Integer> getIndexListFromBitset(BitSet bitSet) {
        List<Integer> indexes = new ArrayList<>(); 
        for (int i = bitSet.nextSetBit(0); i != -1; i = bitSet.nextSetBit(i + 1)) {
            indexes.add(i);
        }
        return indexes;
    }

    public static boolean isDownloadComplete(){
        if((Constants.peerIDToBitfield.size() == Constants.listOfAllPeers.length - 1)){
            System.out.println("in isDownload");
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

   public static Piece[] readFileIntoChunks() {
        Piece[] fileChunks = new Piece[Constants.numberOfChunks];
       try {
        byte[] buffer = Files.readAllBytes(Paths.get("./" + Constants.FileName));
        System.out.println("Buffer length "+ buffer.length);
        int offset = 0;
        int fileChunkIndex = 0;
        
        while(offset<buffer.length){
            fileChunks[fileChunkIndex++] = new Piece(Arrays.copyOfRange(buffer, offset, offset + Constants.PieceSize));
            offset += Constants.PieceSize;
        }
       } catch (Exception e) {
           System.out.println("Couldn't read the file");
           e.printStackTrace();
       }
        
       return fileChunks;
   }

    public static void broadcastShutdownMessage() {
        for (RemotePeerInfo peer : Constants.listOfAllPeers) {
            if(!(peer.peerID.equals(Constants.selfPeerInfo.peerID))){
                byte[] msg = new Message(4, 8, null).createMessage();
                utilities.writeToOutputStream(peer.out, msg);
            }
        }
    }

    public static void mergeFileChunks(){
        String path = "../peer_" + Constants.selfPeerInfo.peerID + ".txt";
        File file = new File(path);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            FileOutputStream stream = new FileOutputStream(file);
            for(Piece piece : Constants.fileChunks){
                outputStream.write(piece.getPieceContent());
            }
            byte completefile[] = outputStream.toByteArray();
            stream.write(completefile);
            stream.flush();
        }
        catch(Exception e ){
            e.printStackTrace();
        }
    }
   
}
