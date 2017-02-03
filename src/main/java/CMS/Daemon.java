package CMS;

import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.HashMap;
import java.util.Map;

public class Daemon {

    public static final String BSAddress = "127.0.0.1"; //Bootstrap server IP
    public static final int BSPort = 55556; //Bootstrap server port

    private static Map<Integer, String> fileMap;

    public static void main(String[] args) throws IOException, InterruptedException {

        fileMap = new HashMap<>();
        fileMap.put(0, "Cinderella");
        fileMap.put(1, "Thumbelina ");
        fileMap.put(2, "Aurora");
        fileMap.put(3, "Belle");
        fileMap.put(4, "Tiana");

        TestNode n0 = new TestNode("N0", 10000, "user0");
        TestNode n1 = new TestNode("N1", 10001, "user1");
        TestNode n2 = new TestNode("N2", 10002, "user2");
        TestNode n3 = new TestNode("N3", 10003, "user3");
        TestNode n4 = new TestNode("N4", 10004, "user4");

        n0.start();
        n1.start();
        n2.start();
        n3.start();
        n4.start();
    }

    public static class TestNode extends Thread {

        private Node myNode;

        public TestNode(String nodeID, int RPCServerPort, String myUsername) throws IOException {
            this.myNode = new Node(BSAddress, BSPort);
            myNode.initialize(nodeID, myUsername);
            myNode.initializeRPC(RPCServerPort);

            ///add RPC functionality
            int tempID = RPCServerPort - 10000;
            for (int i = 0; i <= tempID; i++) {
                myNode.addFile(fileMap.get(i));
            }
        }

        @Override
        public void run() {
            try {
                myNode.start();
                Thread.sleep(2000);
                if (myNode.getNodeID().equals("N1")) {
                    //myNode.searchFile("Tiana");
                    myNode.startUI();
                }

            } catch (IOException | InterruptedException | NotBoundException | XmlRpcException e) {
                e.printStackTrace();
            }
        }
    }
}

