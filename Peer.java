public class Peer{
    private int peerID;
    private String bitfield; 
    private HashSet<String> preferredNeighbours = new HashSet();
    private HashMap<int, String> peerIDToBitfields = new HashMap();
    //choking unchoking read from cfg file

    public String createPeerFolder(){

    }


    public String getBitfield() {
        return bitfield;
    }


    public boolean sendInterestedMssg(){
        
    }
    
    public boolean sendNotInterestedMssg(){

    }

    public boolean sendHaveMssg(){

    }

    public boolean checkBitfield(){
        //comparing peer bitfield to incoming bitfield piece

    }
    public void updateBitfield(){
        
    }

    /*public void chokingUnchoking(){

    }*/

    public void request(){

    }


}