import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Connection {
    static int line;
    static RemotePeerInfo[] hosts = new RemotePeerInfo[4];
    public static List<RemotePeerInfo> getPeerInfo(String peer){
        List<RemotePeerInfo> allPeersBefore = new ArrayList<>();
        for(int i=0;i<line;i++){
           if (peer.equals(hosts[i].peerId)){
               allPeersBefore.add(hosts[i]);
                break;
           }
           allPeersBefore.add(hosts[i]);
           }
        return allPeersBefore;
        }

    public static void fileReader() {
        try {

            File peerInfoConfigFile = new File("C:\\Users\\kartz\\IdeaProjects\\CN\\src\\peerInfo.cfg");
            //int line = 0;
            BufferedReader br = new BufferedReader(new FileReader(peerInfoConfigFile));
            String st;
            while ((st = br.readLine()) != null) {
                String[] rows = st.split(" ");
                hosts[line] = new RemotePeerInfo(rows[0],rows[1],rows[2],rows[3]);
                line++;
            }
            br.close();
            //System.out.println(array[0].peerId);
        }
        catch(Exception e){
            System.out.println("exception");
        }
    }

}
