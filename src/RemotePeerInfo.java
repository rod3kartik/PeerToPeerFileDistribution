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

 //This class is used to store all attributes of each Peer
public class RemotePeerInfo {
    public String peerID;
    public String peerAddress;
    public String peerPort;
    public String fileAvailable;
    public BitSet bitfield = new BitSet(Constants.numberOfChunks);
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

        bitfield.clear(0, Constants.numberOfChunks);
        if(fileAvailable.equals("1")){
            bitfield.set(0, Constants.numberOfChunks);
        }
    }

    public RemotePeerInfo(String pId, String pAddress, String pPort) {
        peerID = pId;
        peerAddress = pAddress;
        peerPort = pPort;
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

    public synchronized void setIsUnchoked(boolean flag){
        this.isUnchoked = flag;
    }

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
