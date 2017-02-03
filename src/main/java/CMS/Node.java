package CMS; /**
 * Created by amitha on 1/8/17.
 */

import CMS.FT.FTMan;
import CMS.Util.CLI;
import CMS.Util.RPCServer;
import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Node extends FTMan {

    public static final int hopCount = 5;
    public static final String sID = "SIdentifier";

    private Vector myFiles = new Vector<>();
    private RPCServer o_RPCServer;
    private boolean RPCInitialized = false;
    private CLI userInterface;

    private Map<String, Boolean> responses = new HashMap<>();
    private Map<String, Long> startTime = new HashMap<>();

    public Node(String BSIP, int BSPort) throws IOException {
        super(BSIP, BSPort);
    }

    public void startUI(){
        userInterface = new CLI(this);
        userInterface.start();
    }

    public void start() throws IOException, InterruptedException, XmlRpcException, NotBoundException {
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

    public void initializeRPC(int RPCServerPort) {
        this.RPCServerPort = RPCServerPort;
        o_RPCServer = new RPCServer(this.RPCServerPort, this);
        RPCInitialized = true;
    }

    @Override
    public boolean fileIsAvailable(String fileName) {
        for (int i = 0; i < myFiles.size(); i++) {
            if (fileName.equals(myFiles.get(i))) {
                return true;
            }
        }
        return false;
    }

    public void searchFile(String fileName) throws IOException, InterruptedException, XmlRpcException, NotBoundException {

        responses.put(fileName,true);
        String SerMsg = "SER";
        SerMsg += " " + getIPAddress() + " " + getRPCServerPort() + " " + fileName + " " + hopCount;
        startTime.put(fileName,System.currentTimeMillis());
        floodNeighbours(SerMsg);
    }

    @Override
    public void displayResult(String fileName, String fileTarget, int targetPort, int hops) {
        if(responses.get(fileName)){
            long diffTime = System.currentTimeMillis() - startTime.get(fileName);
            responses.put(fileName,false);
            screen("========================================================");
            screen("Requested file(s) are available in the system.");
            screen("File name - " + fileName);
            screen("File target - " + fileTarget + ":" + targetPort);
            screen("Latency (ms) - " + diffTime);
            screen("Hops to reach the target - " + hops);
            screen("========================================================");
        }
    }

    public int getRPCServerPort() {
        return RPCServerPort;
    }
}
