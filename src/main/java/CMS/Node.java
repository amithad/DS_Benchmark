package CMS; /**
 * Created by amitha on 1/8/17.
 */

import CMS.FT.FTMan;
import CMS.Util.CLI;
import CMS.Util.CSVUtils;
import CMS.Util.RPCServer;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Vector;

public class Node extends FTMan {

    public static final int hopCount = 5;
    public static final String sID = "SIdentifier";

    private Vector<String> myFiles = new Vector<>();
    private RPCServer o_RPCServer;
    private boolean RPCInitialized = false;
    private CLI userInterface;

    private boolean fileRequested = false;
    private long startTime = 0;

    public Node(String BSIP, int BSPort) throws IOException {
        super(BSIP, BSPort);
    }

    public void startUI() {
        userInterface = new CLI(this);
        userInterface.start();
    }

    public void start() throws IOException, InterruptedException, NotBoundException {
        startRPC();
        Thread.sleep(2000);
        startFT();
        if (!myFiles.isEmpty()) {
            String out = "Available files:";
            for (int i = 0; i < myFiles.size(); i++) {
                out += " " + myFiles.get(i);
            }
            echo(out);
        }
        startHB();
    }

    public void startRPC() {
        if (RPCInitialized) {
            o_RPCServer.start();
        } else {
            echo("Configure RPC before starting.");
        }
    }

    public void addFile(String fileName) { //adds RPCs to the list.
        myFiles.addElement(fileName);
    }

    public void addFiles(String fileRepo) throws IOException {
        FileReader fr = new FileReader(fileRepo);
        BufferedReader br = new BufferedReader(fr);

        String filename;
        while((filename=br.readLine())!=null){
            addFile(filename);
        }
    }
    
    public void initializeRPC(int RPCServerPort) {
        this.RPCServerPort = RPCServerPort;
        o_RPCServer = new RPCServer(this.RPCServerPort, this);
        RPCInitialized = true;
    }

    @Override
    public String findFile(String fileName) {

        String result = "";

        fileName = fileName.trim();
        for (int i = 0; i < myFiles.size(); i++) {
            //screen(myFiles.get(i));
            if (fileName.equalsIgnoreCase(myFiles.get(i))) {
                result = myFiles.get(i).replace(' ', '@');
                return result;
            } else {
                for (String partialWord : myFiles.get(i).split(" ")) {
                    if (partialWord.equalsIgnoreCase(fileName)) {
                        if (result.equalsIgnoreCase("")) {

                            result = result + myFiles.get(i).replace(' ', '@');
                        } else {

                            result = result + "," + myFiles.get(i).replace(' ', '@');
                        }
                    }
                }
            }
        }
        //screen(result);
        return result;
    }

    public void searchFile(String fileName) throws IOException, InterruptedException, NotBoundException {
        requestFile();
        String SerMsg = "SER";
        SerMsg += " " + getIPAddress() + " " + getRPCServerPort() + " " + fileName + " " + hopCount;
        startTime = System.currentTimeMillis();
        floodNeighbours(SerMsg);
        while (fileRequested) {
        } //block the next call
    }

    @Override
    public void retrieveResult(String fileName, String fileTarget, int targetPort, int hops) {
        if (fileRequested) {
            fileRequested = false;
            long diffTime = System.currentTimeMillis() - startTime;
            dropRequest();
            screen("========================================================");
            screen("Requested file(s) are available in the system.");
            screen("File name - " + fileName.replace('@',' '));
            screen("File target - " + fileTarget + ":" + targetPort);
            screen("Latency (ms) - " + diffTime);
            screen("Hops to reach the target - " + hops);
            screen("========================================================");
            CSVUtils.writeSearchResults(new CSVUtils.SearchNodeResults(fileName, diffTime, hops));
        }
    }

    public int getRPCServerPort() {
        return RPCServerPort;
    }

    public void requestFile() {
        fileRequested = true;
    }

    public void dropRequest() {
        fileRequested = false;
    }
}
