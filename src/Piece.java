public class Piece {
    private byte[] piece;

    Piece(byte[] inputBytes) {
        System.out.println("Checking each chunk" + new String(inputBytes));
        piece = inputBytes;
    }

    public int getPieceSize(){
        return this.piece.length;
    }

    public byte[] getPieceContent(){
        return this.piece;
    }
}
