import java.io.ObjectOutputStream;
import java.util.BitSet;
import java.util.HashSet;


/*
 *                     CEN5501C Project2
 * This is the program starting remote processes.
 * This program was only tested on CISE SunOS environment.
 * If you use another environment, for example, linux environment in CISE
 * or other environments not in CISE, it is not guaranteed to work properly.
 * It is your responsibility to adapt this program to your running environment.
 */

public class RemotePeerInfo {
    public String peerID;
    public String peerAddress;
    public String peerPort;
    public String fileAvailable;
    public BitSet bitfield = new BitSet(Constants.FileSize/Constants.PieceSize);
    public float downloadRate = 0;
    public ObjectOutputStream out;
    public boolean isUnchoked = false;
    public float downloadDataSize = 0;
    public long downloadStartTime = 0;

    public RemotePeerInfo(String pId, String pAddress, String pPort, String fileAvailable) {
        peerID = pId;
        peerAddress = pAddress;
        peerPort = pPort;
        this.fileAvailable = fileAvailable;

        bitfield.clear(0, Constants.FileSize/Constants.PieceSize);
        if(fileAvailable.equals("1")){
            bitfield.set(0, Constants.FileSize/Constants.PieceSize);
        }
        System.out.println(bitfield.length() + " " + fileAvailable + " "+ bitfield + " " + peerID);
    }

    public void setBitfield(int bit) {
        bitfield.set(bit);
        
    }

    public float getDownloadRate(){
        return downloadRate;
    }
    // updates bitfield after downloading a piece
    public void updateBitField(int pieceIndex) {
        bitfield.set(pieceIndex);
    }

    // @Override
    // public int compareTo(RemotePeerInfo p1) {
    //     return this.getDownloadRate().compareTo(p1.getDownloadRate());
    // }

    public void setDownloadSpeed(){
		long timePeriod = System.currentTimeMillis() - downloadStartTime;
		if(timePeriod != 0){
			downloadRate = downloadDataSize /timePeriod;
		}else{
			downloadRate = 0;
		} 
    }
    
    public void setDownloadDataSize(int size){
        downloadDataSize += size;
    }
}
