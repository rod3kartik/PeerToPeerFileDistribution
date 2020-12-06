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
    public BitSet bitfield;
    public RemotePeerInfo(String pId, String pAddress, String pPort, String fileAvailable) {
        peerID = pId;
        peerAddress = pAddress;
        peerPort = pPort;
        this.fileAvailable = fileAvailable;
        if(fileAvailable == "1"){
            for(int i=0; i<Constants.fileChunks.length; i++){
                bitfield.set(i);
            }
        }
    }

    public void setBitfield(int bit) {
        bitfield.set(bit);
    }

    public void updateBitField(int pieceIndex) {
        bitfield.set(pieceIndex);
    }
}
