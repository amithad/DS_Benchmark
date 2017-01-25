package CMS; /**
 * Created by amitha on 1/8/17.
 */

import CMS.FT.FTMan;
import CMS.Util.RPCServer;
import CMS.Util.RPCServiceMsgDecoder;

import java.io.IOException;
import java.util.Vector;

public class Node extends FTMan {

    private Vector myRPCs = new Vector<>();
    private RPCServer o_RPCServer;
    private int RPCServerPort;
    private boolean RPCInitialized = false;
    private boolean RPCServerReady = false;//accessed by RPCServer

    public Node(String BSIP, int BSPort) throws IOException {
        super(BSIP, BSPort);
    }

    public void start() throws IOException, InterruptedException {
        startFT();
        startServer();
        if (!myRPCs.isEmpty()) {
            String out = "Supporting RPCs:";
            for (int i = 0; i < myRPCs.size(); i++) {
                out += " " + myRPCs.get(i);
            }
            echo(out);
        }
        startHB();
        startRPC();
    }

    public void invokeRPC(String rpc) { //adds RPCs to the list.
        myRPCs.addElement(rpc);
    }

    public void initializeRPC(int RPCServerPort) {
        this.RPCServerPort = RPCServerPort;
        o_RPCServer = new RPCServer(this.RPCServerPort, this);
        RPCInitialized = true;
    }

    public boolean RPCIsSupported(String rpc){
        for(int i = 0; i < myRPCs.size(); i++){
            if(rpc.equals(myRPCs.get(i))){
                return true;
            }
        }
        return false;
    }

    public void sendRPCOK(String RPCall, String senderIP, int senderPort){

    }

    public void startRPC() {
        if (RPCInitialized) {
            o_RPCServer.start();
        }
        else{
            echo("Configure RPC before starting.");
        }
    }

    @Override
    protected void readDSMsg(String msg, String senderIP){
        RPCServiceMsgDecoder.DecodeRPCMsg(this,msg,senderIP);
    }

    public void RPCServerReady() {
        RPCServerReady = true;
    }

    public boolean isRPCServerReady() {
        return RPCServerReady;
    }

}
