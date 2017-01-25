package CMS.FT;

import CMS.Util.Neighbour;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by amitha on 1/8/17.
 */

public class FTMan {

    private String nodeID;
    private InetAddress myIP;
    private int UDPServerPort;
    private String myUsername;
    private String BSIP;
    private int BSPort;

    private UDPClient o_UDPClient;
    private UDPServer o_UDPServer;
    protected List<Neighbour> neighbourList; //can be modified by Node class.

    private boolean initialized = false;

    public FTMan(String BSIP, int BSPort) throws IOException {
        this.BSIP = BSIP;
        this.BSPort = BSPort;
        o_UDPClient = new UDPClient(BSIP, BSPort, this);
        neighbourList = new ArrayList<>();
    }

    protected void startFT() throws IOException, InterruptedException {
        if (initialized) {
            echo("Connecting to BootstrapServer at " + BSIP + ":" + BSPort + "...");
            sendREG();
        } else
            echo("Initialize the node before starting!");
    }

    protected void startServer() throws IOException {
        if (initialized) {
            o_UDPServer = new UDPServer(UDPServerPort, this);
            o_UDPServer.start();
        } else
            echo("Initialize the node before starting the server!");

    }

    protected void startHB() {
        //heartbeat monitor
    }

    public void initialize(String nodeID, int UDPServerPort, String myUsername) throws UnknownHostException {
        this.nodeID = nodeID;
        this.myIP = discoverMyIP();
        this.UDPServerPort = UDPServerPort;
        this.myUsername = myUsername;
        initialized = true;
    }

    public String sendREG() throws IOException, InterruptedException {
        //handle registration loop. keep registration attempt count.
        if (false) this.wait(2000); //wait accordingly

        String regMsg = "REG";
        regMsg += " " + myIP.getHostAddress() + " " + UDPServerPort + " " + myUsername;
        String response = sendBSMsg(regMsg, true);
        FTMsgDecoder.decodeFTMsg(this, response, BSIP);
        return response;
    }

    public String sendUNREG() throws IOException, InterruptedException {
        //handle unreg loop. Keep unreg attempt count
        String unregMsg = "UNREG";
        unregMsg += " " + myIP.getHostAddress() + " " + UDPServerPort + " " + myUsername;
        String response = sendBSMsg(unregMsg, true);
        FTMsgDecoder.decodeFTMsg(this, response, BSIP);
        return response;
    }

    public void sendJOIN(String IP, String sendIP, int sendPort) throws IOException {
        String joinMsg = "JOIN";
        joinMsg += " " + myIP.getHostAddress() + " " + UDPServerPort;
        sendDSCommMsg(joinMsg, sendIP, sendPort);
    }

    public void sendJOINOK(String sendIP, int sendPort, int responseFlag) throws IOException {
        String joinOkMsg = "JOINOK";
        joinOkMsg += " " + responseFlag;
        sendDSCommMsg(joinOkMsg, sendIP, sendPort);
    }

    public boolean addNeighbour(Neighbour nbr) throws IOException {
        if (nbr != null) {
            boolean neighbourExists = false;
            Iterator<Neighbour> i = neighbourList.iterator();
            while (i.hasNext()) {
                if (nbr.getNeighbourIP().equals(i.next().getNeighbourIP())) {
                    neighbourExists = true;
                    break;
                }
            }
            if (!neighbourExists) {
                neighbourList.add(nbr);
                echo("New neighbour " + nbr.getNeighbourIP() + ":" + nbr.getNeighbourPort() + " was added successfully.");
                sendJOIN(myIP.getHostName(), nbr.getNeighbourIP(), nbr.getNeighbourPort());
                return true;
            }
        }
        return false;
    }

    public boolean removeNeighbour(String neighbourIP) {
        Iterator<Neighbour> i = neighbourList.iterator();
        while (i.hasNext()) {
            Neighbour n = i.next();
            if (neighbourIP.equals(n.getNeighbourIP())) {
                neighbourList.remove(n);
                echo("Neighbour " + neighbourIP + " is leaving. Routing table updated.");
                return true;
            }
        }
        return false;
    }

    public int getNeighbourPort(String neighbourIP) {
        Iterator<Neighbour> i = neighbourList.iterator();
        while (i.hasNext()) {
            Neighbour n = i.next();
            if (neighbourIP.equals(n.getNeighbourIP())) {
                return n.getNeighbourPort();
            }
        }
        return 0;
    }

    public String sendBSMsg(String message, boolean expectResponse) throws IOException {
        return o_UDPClient.sendBSMsg(this.nodeID, message, expectResponse);
    }

    public void sendDSCommMsg(String msg, String sendIP, int sendPort) throws IOException {
        o_UDPClient.sendDSCommMsg(msg, sendIP, sendPort);
    }

    private InetAddress discoverMyIP() throws UnknownHostException {
        //replace with IP discovery code
        InetAddress myIP = InetAddress.getByName("127.0.0.1");
        return myIP;
    }

    public void disconnectBS() throws IOException, InterruptedException {
        echo("Disconnecting...");
        sendUNREG();
        o_UDPClient.disconnectBS();
    }

    protected void readDSMsg(String msg, String senderIP){
        //override this method to get access to custom msg types.
    }

    public String getNodeID() {
        return nodeID;
    }

    public void echo(String msg) {
        String prefix = new Date().toString() + ": ";
        prefix += this.getNodeID() + ": ";
        System.out.println(prefix + msg);
    }
}