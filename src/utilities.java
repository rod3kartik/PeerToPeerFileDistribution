import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class utilities {
    

    public static long fromByteArrayToInteger(byte[] value) 
    {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        final byte val = (byte) (value[0] < 0 ? 0xFF : 0);
    
        for(int i = value.length; i < Long.BYTES; i++)
            buffer.put(val);
    
        buffer.put(value);
        return buffer.getLong(0);
    
    }

    public static int fromFourByteArrayToInteger(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) | 
               ((bytes[1] & 0xFF) << 16) | 
               ((bytes[2] & 0xFF) << 8 ) | 
               ((bytes[3] & 0xFF) << 0 );
    }

    public static String[] convertToStrings(byte[][] byteStrings) {
        String[] data = new String[byteStrings.length];
        for (int i = 0; i < byteStrings.length; i++) {
            data[i] = new String(byteStrings[i], Charset.defaultCharset());
    
        }
        return data;
    }
    
    
    private static byte[][] convertToBytes(String[] strings) {
        byte[][] data = new byte[strings.length][];
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            data[i] = string.getBytes(Charset.defaultCharset()); // you can chose charset
        }
        return data;
    }

   public static Piece[] readFileIntoChunks() {
        Piece[] fileChunks = new Piece[Constants.FileSize/Constants.PieceSize];
       try {
        byte[] buffer = Files.readAllBytes(Paths.get("./" + Constants.FileName));
        System.out.println("Buffer length "+ buffer.length);
        int offset = 0;
        int fileChunkIndex = 0;
        
        while(offset<buffer.length){
            fileChunks[fileChunkIndex] = new Piece(Arrays.copyOfRange(buffer, offset, offset + Constants.PieceSize));
            offset += Constants.PieceSize;
            // System.out.println(offset);
        }
       } catch (Exception e) {
           //TODO: handle exception
           System.out.println("Couldn't read the file");
           e.printStackTrace();
       }
        
       return fileChunks;
   }
   
}
