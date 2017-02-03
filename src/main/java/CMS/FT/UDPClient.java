package CMS.FT;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by amitha on 1/9/17.
 */
public class UDPClient {

    private DatagramSocket socket;
    private InetAddress BSIP;
    private int BSPort;
    private FTMan ftMan;

    public UDPClient(String BSIP, int BSPort, FTMan ftMan) throws IOException {
        this.BSIP = InetAddress.getByName(BSIP);
        this.BSPort = BSPort;
        this.ftMan = ftMan;
        connectBS();
    }

    private void connectBS() throws SocketException {

        //create UDP socket connection
        socket = new DatagramSocket();
    }

    private DatagramSocket getSocket() throws SocketException {
        if (socket.isClosed())
            connectBS();
        return socket;
    }

    public void disconnectBS() {
        socket.close();
    }

    public String sendBSMsg(String NodeID,String sendMessage, boolean expectResponse) throws IOException {
        sendMessage = String.format("%04d", sendMessage.length() + 5) + " " + sendMessage;
        byte[] outData = sendMessage.getBytes();
        //send packets
        DatagramPacket sendPacket = new DatagramPacket(outData, outData.length, BSIP, BSPort);
        getSocket().send(sendPacket);
        if (expectResponse)
            return receiveFTMsg(NodeID);
        return null;
    }

    private String receiveFTMsg(String NodeID) throws IOException { //receive packets
        byte[] inData = new byte[65536];
        DatagramPacket receivePacket = new DatagramPacket(inData, inData.length);
        getSocket().receive(receivePacket);

        String receivedMessage = new String(inData, 0, receivePacket.getLength());
        ftMan.echo("Reply from Server: " + receivedMessage);
        return receivedMessage;
    }
}
