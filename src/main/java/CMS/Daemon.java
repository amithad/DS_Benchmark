package CMS;

import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Daemon {

    public static final String BSAddress = "127.0.0.1";
    public static final int BSPort = 55556;

    private static Map<Integer, String> RPCMap;

    public static void main(String[] args) throws IOException {

        RPCMap = new HashMap<>();
        RPCMap.put(0, "sum");
        RPCMap.put(1, "multiply");
        RPCMap.put(2, "sqrt");
        RPCMap.put(3, "allCaps");
        RPCMap.put(4, "square");

        TestNode n0 = new TestNode("N0", 5000, 10000, "user0");
        TestNode n1 = new TestNode("N1", 5001, 10001, "user1");
        TestNode n2 = new TestNode("N2", 5002, 10002, "user2");
        TestNode n3 = new TestNode("N3", 5003, 10003, "user3");
        TestNode n4 = new TestNode("N4", 5004, 10004, "user4");

        n0.start();
        n1.start();
        n2.start();
        n3.start();
        n4.start();
    }

    public static class TestNode extends Thread {

        private Node myNode;

        public TestNode(String nodeID, int UDPServerPort, int RPCServerPort, String myUsername) throws IOException {
            this.myNode = new Node(BSAddress, BSPort);
            myNode.initialize(nodeID, UDPServerPort, myUsername);
            myNode.initializeRPC(RPCServerPort);

            ///add RPC functionality
            int tempID = UDPServerPort - 5000;
            for (int i = 0; i <= tempID; i++) {
                myNode.invokeRPC(RPCMap.get(i));
            }
        }

        @Override
        public void run() {
            try {
                myNode.start();
                if(myNode.getNodeID().equals("N0")){
                    sleep(2000);
                    //call the RPC here to execute.
                    myNode.getRPCaller().multiply(45,20);
                }
            } catch (IOException | InterruptedException | XmlRpcException e) {
                e.printStackTrace();
            }
        }
    }
}

