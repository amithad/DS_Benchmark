//package CMS.FT;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by amitha on 1/9/17.
 */
public class UDPServer extends Thread {

    private DatagramSocket socket;
    private int listenerPort;
    private FTMan ftMan;

    public UDPServer(int listenerPort, FTMan ftMan) throws IOException {
        this.listenerPort = listenerPort;
        this.ftMan = ftMan;
        socket = new DatagramSocket(listenerPort);
    }

    private DatagramSocket getSocket() throws SocketException {
        if (socket.isClosed())
            socket = new DatagramSocket(listenerPort);
        return socket;
    }

    public void startListener() throws IOException, InterruptedException {
        while (true) {
            ftMan.echo("Listening on " + listenerPort + "...");
            byte[] inData = new byte[65536];
            DatagramPacket receivePacket = new DatagramPacket(inData, inData.length);
            getSocket().receive(receivePacket);
            String receivedMessage = new String(inData, 0, receivePacket.getLength());
            ftMan.echo("Incoming msg from " + receivePacket.getAddress().getHostAddress() + " - " + receivedMessage);
            FTMsgDecoder.decodeFTMsg(ftMan, receivedMessage,receivePacket.getAddress().getHostAddress());
        }
    }

    @Override
    public void run() {
        try {
            startListener();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
