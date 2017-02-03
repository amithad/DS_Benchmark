package CMS.FT;

import CMS.Node;
import CMS.Util.Neighbour;
import CMS.Util.PCHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
    protected int RPCServerPort;
    private String myUsername;
    private String BSIP;
    private int BSPort;

    //=========================Measurements=====================

    public int queriesReceived = 0;
    public int queriesForwarded = 0;
    public int queriesAnswered = 0;

    //==========================================================

    private UDPClient o_UDPClient;
    protected List<Neighbour> neighbourList; //can be modified by Node class.

    private boolean initialized = false;

    public FTMan(String BSIP, int BSPort) throws IOException {
        this.BSIP = BSIP;
        this.BSPort = BSPort;
        o_UDPClient = new UDPClient(BSIP, BSPort, this);
        neighbourList = new ArrayList<>();
    }

    protected void startFT() throws IOException, InterruptedException, NotBoundException {
        if (initialized) {
            echo("Connecting to BootstrapServer at " + BSIP + ":" + BSPort + "...");
            sendREG();
        } else
            echo("Initialize the node before starting!");
    }

    protected void startHB() {
        //heartbeat monitor
    }

    public void initialize(String nodeID, String myUsername) throws UnknownHostException {
        this.nodeID = nodeID;
        this.myIP = discoverMyIP();
        this.myUsername = myUsername;
        initialized = true;
    }

    public String sendREG() throws IOException, InterruptedException, NotBoundException {
        //handle registration loop. keep registration attempt count.
        if (false) this.wait(2000); //wait accordingly

        String regMsg = "REG";
        regMsg += " " + myIP.getHostAddress() + " " + RPCServerPort + " " + myUsername;
        String response = sendBSMsg(regMsg, true);
        FTMsgDecoder.decodeFTMsg(this, response, BSIP);
        return response;
    }

    public String sendUNREG() throws IOException, InterruptedException, NotBoundException {
        //handle unreg loop. Keep unreg attempt count
        String unregMsg = "UNREG";
        unregMsg += " " + myIP.getHostAddress() + " " + RPCServerPort + " " + myUsername;
        String response = sendBSMsg(unregMsg, true);
        FTMsgDecoder.decodeFTMsg(this, response, BSIP);
        return response;
    }

    public String leaveDS() throws InterruptedException, NotBoundException, IOException {
        String response = sendUNREG();
        Iterator<Neighbour> i = neighbourList.iterator();
        while (i.hasNext()) {
            Neighbour temp = i.next();
            sendRPCLEAVE(temp.getNeighbourIP(), temp.getNeighbourPort());
        }
        return response;
    }

    public String sendRPCLEAVE(String sendIP, int sendPort) throws InterruptedException, NotBoundException, IOException {
        String leaveMsg = "LEAVE";
        leaveMsg += " " + getIPAddress() + " " + RPCServerPort;
        String response = sendDSRPCComm(leaveMsg, sendIP, sendPort);
        return response;
    }

    public void sendRPCJOIN(String IP, String sendIP, int sendPort) throws IOException, NotBoundException, InterruptedException {
        String joinMsg = "JOIN";
        joinMsg += " " + myIP.getHostAddress() + " " + RPCServerPort;
        sendDSRPCComm(joinMsg, sendIP, sendPort);
    }

    public void sendRPCJOINOK(String sendIP, int sendPort, int responseFlag) throws IOException, InterruptedException, NotBoundException {
        String joinOkMsg = "JOINOK";
        joinOkMsg += " " + responseFlag;
        sendDSRPCComm(joinOkMsg, sendIP, sendPort);
    }

    public void sendSEROK(int noOfFiles, String sendIP, int sendPort, int hops, String fileNames) throws InterruptedException, NotBoundException, IOException {
        String serOkMsg = "SEROK";
        serOkMsg += " " + noOfFiles;
        serOkMsg += " " + getIPAddress() + " " + RPCServerPort;
        serOkMsg += " " + hops + " " + fileNames;
        sendDSRPCComm(serOkMsg, sendIP, sendPort);
    }

    public String sendBSMsg(String message, boolean expectResponse) throws IOException {
        return o_UDPClient.sendBSMsg(this.nodeID, message, expectResponse);
    }

    public String sendDSRPCComm(String msg, String sendIP, int sendPort) throws IOException, NotBoundException, InterruptedException {
        Registry r1 = LocateRegistry.getRegistry(sendIP, sendPort);
        PCHandler stub = (PCHandler) r1.lookup(Node.sID);
        msg = String.format("%04d", msg.length() + 5) + " " + msg;
        String response = stub.DSCommMsg(msg, myIP.getHostName());
        return response;
    }

    public boolean addNeighbour(Neighbour nbr) throws IOException, NotBoundException, InterruptedException {
        if (nbr != null) {
            boolean neighbourExists = false;
            Iterator<Neighbour> i = neighbourList.iterator();
            while (i.hasNext()) {
                Neighbour temp = i.next();
                if (nbr.getNeighbourIP().equals(temp.getNeighbourIP()) && nbr.getNeighbourPort() == temp.getNeighbourPort()) {
                    neighbourExists = true;
                    break;
                }
            }
            if (!neighbourExists) {
                neighbourList.add(nbr);
                echo("New neighbour " + nbr.getNeighbourIP() + ":" + nbr.getNeighbourPort() + " was added successfully.");
                sendRPCJOIN(myIP.getHostName(), nbr.getNeighbourIP(), nbr.getNeighbourPort());
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

    public void floodNeighbours(String msg) throws IOException, InterruptedException, NotBoundException {
        Iterator<Neighbour> i = neighbourList.iterator();
        while (i.hasNext()) {
            Neighbour n = i.next();
            sendDSRPCComm(msg, n.getNeighbourIP(), n.getNeighbourPort());
        }
    }

    private InetAddress discoverMyIP() throws UnknownHostException {
        //replace with IP discovery code
        InetAddress myIP = InetAddress.getByName("127.0.0.1");
        return myIP;
    }

    public void disconnectBS() throws IOException, InterruptedException, NotBoundException {
        echo("Disconnecting...");
        sendUNREG();
        o_UDPClient.disconnectBS();
    }

    public String getNodeID() {
        return nodeID;
    }

    public void echo(String msg) {
        String prefix = new Date().toString() + ": ";
        prefix += this.getNodeID() + ": ";
        //System.out.println(prefix + msg);
    }

    public void screen(String msg) {
        if (msg == null) {
            System.out.println();
        } else {
            String prefix = new Date().toString() + ": ";
            prefix += this.getNodeID() + ": ";
            System.out.println(prefix + msg);
        }
    }

    public int getNeighbourCount(){
        return neighbourList.size();
    }

    public String getIPAddress() {
        return myIP.getHostAddress();
    }

    public boolean fileIsAvailable(String fileName) {
        return false;
    }

    public void retrieveResult(String fileName, String fileTarget, int targetPort, int hops) {

    }

}