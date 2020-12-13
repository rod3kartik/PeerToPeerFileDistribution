
//Class to designate each filechunk as a piece
public class Piece {
    private byte[] piece;

    Piece(byte[] inputBytes) {
        piece = inputBytes;
    }

    public int getPieceSize(){
        return this.piece.length;
    }

    public byte[] getPieceContent(){
        return this.piece;
    }
}
