/*
 *                     CEN5501C Project2
 * This is the program starting remote processes.
 * This program was only tested on CISE SunOS environment.
 * If you use another environment, for example, linux environment in CISE
 * or other environments not in CISE, it is not guaranteed to work properly.
 * It is your responsibility to adapt this program to your running environment.
 */

public class RemotePeerInfo {
    public String peerId;
    public String peerAddress;
    public String peerPort;
    public String fileAvailable;
    public byte[] bitfield;
    public RemotePeerInfo(String pId, String pAddress, String pPort, String fileAvailable) {
        peerId = pId;
        peerAddress = pAddress;
        peerPort = pPort;
        this.fileAvailable = fileAvailable;
        bitfield = new byte[Constants.FileSize/Constants.PieceSize];
    }

    // sets bitfield of a peer
    public void setBitfield(byte[] bitfield) {
        this.bitfield = bitfield;
    }

    // updates bitfield after downloading a piece
    public void updateBitField(int pieceIndex) {
        this.bitfield[pieceIndex] = 1;
    }
}
