import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Connection {
    static int line;
    static List<RemotePeerInfo> hosts = new ArrayList<>();
    // Connecting new peers to existing peers already in the network 
    public static List<RemotePeerInfo> getPeerInfo(String peer){
        List<RemotePeerInfo> allPeersBefore = new ArrayList<>();
        for(int i=0;i<line;i++){
           if (peer.equals(hosts.get(i).peerID)){
               System.out.println("Line "+ line);
               allPeersBefore.add(hosts.get(i));
                break;
           }
           allPeersBefore.add(hosts.get(i));
           }
        return allPeersBefore;
        }
    
    public static List<RemotePeerInfo> getAfterPeersInfo(String peer){
        List<RemotePeerInfo> allPeersAfter = new ArrayList<>();
        for(int i=line-1;i<-1;i++){
            if (peer.equals(hosts.get(i).peerID)){
                 break;
            }
            allPeersAfter.add(hosts.get(i));
            }
        return allPeersAfter;
        }

    public static RemotePeerInfo[] fileReader() {
        try {

            File peerInfoConfigFile =Paths.get("./" + "peerInfo.cfg").toFile();
            line = 0;
            BufferedReader br = new BufferedReader(new FileReader(peerInfoConfigFile));
            String st;

            // reading each line and sending the arguments to RemotePeerInfo
            while ((st = br.readLine()) != null) {
                System.out.println("in connection: "+ st);
                String[] rows = st.split(" ");
                hosts.add(new RemotePeerInfo(rows[0],rows[1],rows[2],rows[3]));
                line++;
                System.out.println(" loop Line number: "+ line);
            }
            br.close();
            System.out.println("Line number: "+ line);
            Constants.totalNumberOfPeers = line;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        RemotePeerInfo[] peerList = new RemotePeerInfo[line];
        for(int i = 0; i<line; i++){
            peerList[i] = hosts.get(i);
        }
        return peerList;
    }

}
