import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class ChunkRequestor extends Thread{
    RemotePeerInfo peer;
    ChunkRequestor(RemotePeerInfo p){
        peer = p;
    }
    public void run(){
        while(this.peer.isUnchoked){
            System.out.println("in chunk requestor");
            BitSet commonPiecesBitSet = (BitSet)Constants.chunksLeft.clone();
            commonPiecesBitSet.intersects(this.peer.bitfield);
            List<Integer> indexes = utilities.getIndexListFromBitset(commonPiecesBitSet);
            Random rand = new Random();
            if(indexes.size() == 0) break;
            int pieceIndex = indexes.get(0);
            while(true){
                if(indexes.size() == 0) break;
                int randomInt = rand.nextInt(indexes.size());
                pieceIndex = indexes.get(randomInt);
                if(!Constants.requestedPieceIndexes.contains(pieceIndex)){
                    break;
                }
                indexes.remove(Integer.valueOf(pieceIndex));
            }
            Message.sendRequestMessage(pieceIndex, this.peer);
            Constants.updateRequestedPieceIndexes(pieceIndex, true);
        }

    }
}
