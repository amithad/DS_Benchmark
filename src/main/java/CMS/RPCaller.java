package CMS;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

/**
 * Created by amitha on 1/25/17.
 */
public class RPCaller {

    private Node node;
    private boolean waitingToExecRPC = false;
    //RPC shared variables
    private String RPCTargetIP;
    private int RPCTargetPort;

    public RPCaller(Node node) {
        this.node = node;
    }

    public void square(int x) throws IOException, XmlRpcException {
        XmlRpcClient client = initRPCClient("square");
        Vector params = new Vector();
        params.addElement(x);
        Object result = client.execute("handler.square", params);
        int answer = ((Integer) result).intValue();
        node.echo("Square of " + x + " is " + answer + ".");
    }

    public void sum(int x, int y) throws IOException, XmlRpcException {
        XmlRpcClient client = initRPCClient("sum");
        Vector params = new Vector();
        params.addElement(x);
        params.addElement(y);
        Object result = client.execute("handler.sum", params);
        int answer = ((Integer) result).intValue();
        node.echo("Sum of " + x + " and " + y + " is " + answer + ".");
    }

    public void multiply(int x, int y) throws IOException, XmlRpcException {
        XmlRpcClient client = initRPCClient("multiply");
        Vector params = new Vector();
        params.addElement(x);
        params.addElement(y);
        Object result = client.execute("handler.multiply", params);
        int answer = ((Integer) result).intValue();
        node.echo("Multiplication of " + x + " and " + y + " is " + answer + ".");
    }

    public void sqrt(int x) throws IOException, XmlRpcException {
        XmlRpcClient client = initRPCClient("sqrt");
        Vector params = new Vector();
        params.addElement(x);
        Object result = client.execute("handler.sqrt", params);
        int answer = ((Integer) result).intValue();
        node.echo("Square root of " + x + " is " + answer + ".");
    }

    public void allCaps(String s) throws IOException, XmlRpcException {
        XmlRpcClient client = initRPCClient("allCaps");
        Vector params = new Vector();
        params.addElement(s);
        Object result = client.execute("handler.allCaps", params);
        String answer = result.toString();
        node.echo("Uppercase result of " + s + " is " + answer + ".");
    }


    public XmlRpcClient initRPCClient(String operation) throws IOException {
        String msg = "RPCREQ " + operation + " " + Node.hopCount + " " + node.getIPAddress() + " " + node.getUDPServerPort();
        node.floodNeighbours(msg);
        waitingToExecRPC = true;
        while (node.isWaitingToExecRPC()) {

        }
        XmlRpcClientConfigImpl cf = new XmlRpcClientConfigImpl();
        cf.setServerURL(new URL("http://" + RPCTargetIP + ":" + RPCTargetPort));
        cf.setConnectionTimeout(60000);
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(cf);

        return client;
    }

    public boolean isWaitingToExecRPC() {
        return waitingToExecRPC;
    }

    public void interruptWait() {
        waitingToExecRPC = false;
    }

    public void setTargetVariables(String RPCTargetIP, int RPCTargetPort) {
        this.RPCTargetIP = RPCTargetIP;
        this.RPCTargetPort = RPCTargetPort;
    }

}
