import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.*;

//Class to read common.cfg and set relevant variables
public class CommonFileReader {
    // static int NumberOfPreferredNeighbors;
    // static int UnchokingInterval;
    // static int OptimisticUnchokingInterval;
    // static String FileName;
    // static int FileSize;
    // static int PieceSize;

    //A map to keep all the configuration variables
    public static Map<String, String> configurationVariables = new HashMap<>();

    //FileReader function
    public static void confReader() {
        try {

            File peerInfoConfigFile = new File("common.cfg");
           
            BufferedReader br = new BufferedReader(new FileReader(peerInfoConfigFile));
            String st;
            while ((st = br.readLine()) != null) {
                String[] row = st.split(" ");
                configurationVariables.put(row[0], row[1]);
                // System.out.println("here2");
            }
            br.close();
            //System.out.println(configurationVariables.keySet());
            
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    //Getter functions for all the properties of configuration files
    public static int getNumberOfPreferredNeighbours() {
        return Integer.parseInt(configurationVariables.get("NumberOfPreferredNeighbors"));
    }

    public static int getUnchokingInterval() {
        return Integer.parseInt(configurationVariables.get("UnchokingInterval"));
    }

    public static int getOptimisticUnchokingInterval() {
        return Integer.parseInt(configurationVariables.get("OptimisticUnchokingInterval"));
    }

    public static String getFileName() {
        return configurationVariables.get("FileName");
    }

    public static int getFileSize() {
        return Integer.parseInt(configurationVariables.get("FileSize"));
    }

    public static int getPieceSize() {
        return Integer.parseInt(configurationVariables.get("PieceSize"));
    }


    // public static void main(String[] args) {
    //     confReader();
    // }
}
