import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Connection {
    public static void startConnection() {
        try {
            RemotePeerInfo[] array = new RemotePeerInfo[4];
            File peerInfoConfigFile = new File("peerInfo.cfg");
            int line = 0;
            BufferedReader br = new BufferedReader(new FileReader(peerInfoConfigFile));
            String st;
            while ((st = br.readLine()) != null) {
                String[] rows = st.split(" ");
                array[line] = new RemotePeerInfo(rows[0],rows[1],rows[2],rows[3]);
                line++;
            }
            System.out.println(array[0].peerId);
        }
        catch(Exception e){
            System.out.println("exception");
        }
    }
    public static void main(String[] args){
        startConnection();
    }
}
