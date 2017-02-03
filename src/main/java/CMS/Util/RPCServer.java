package CMS.Util;

import CMS.FT.FTMsgDecoder;
import CMS.Node;
import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by amitha on 1/25/17.
 */

public class RPCServer extends Thread implements PCHandler {

    private Node node;
    private int port;

    public RPCServer(int port, Node node) {
        this.port = port;
        this.node = node;
    }

    @Override
    public String DSCommMsg(String msg, String senderIP) throws InterruptedException, XmlRpcException, IOException, NotBoundException {
        //do the decoding here
        node.echo(msg);
        String result = FTMsgDecoder.decodeFTMsg(node, msg, senderIP);
        return result;
    }

    @Override
    public void run() {
        try {
            node.echo("Starting RMI registry...");
            Registry r1 = LocateRegistry.createRegistry(port);
            node.echo("RMI registry started.");
            node.echo("Attempting to start RPCServer...");
            PCHandler stub = (PCHandler) UnicastRemoteObject.exportObject(this, 0);
            r1.rebind(Node.sID, stub);
            node.echo("RPCServer up and running on " + node.getIPAddress() + ":" + port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

}
