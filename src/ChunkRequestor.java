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


//A thread class dedicated to request chunks whenver unchoked
public class ChunkRequestor extends Thread{
    RemotePeerInfo peer;
    int requestedPieceIndex;
    ChunkRequestor(RemotePeerInfo p){
        peer = p;
    }
    public void run(){
   
        while (this.peer.isUnchoked & !Constants.isShutDownMessageReceived) {
            if(Constants.requestedPieceIndexes.contains(requestedPieceIndex)){
                continue;
            }
            
            BitSet commonPiecesBitSet = (BitSet)Constants.chunksLeft.clone();

            //Getting common pieces that peer has still left to download
            commonPiecesBitSet.intersects(this.peer.bitfield);
            List<Integer> indexes = utilities.getIndexListFromBitset(commonPiecesBitSet);
            Random rand = new Random();
            if(indexes.size() == 0) break;
            int pieceIndex = indexes.get(0);

            //randomly choosing one piece to request
            while(true){
                if(indexes.size() == 0) break;
                int randomInt = rand.nextInt(indexes.size());
                pieceIndex = indexes.get(randomInt);
                if(!Constants.requestedPieceIndexes.contains(pieceIndex)){
                    break;
                }
                indexes.remove(Integer.valueOf(pieceIndex));
            }

            //sending request message with random selected index
            Message.sendRequestMessage(pieceIndex, this.peer);
            Constants.updateRequestedPieceIndexes(pieceIndex, true);
            requestedPieceIndex = pieceIndex;
        }

        if(Constants.requestedPieceIndexes.contains(requestedPieceIndex)){
            Constants.updateRequestedPieceIndexes(requestedPieceIndex, false);
        }

    }
}
