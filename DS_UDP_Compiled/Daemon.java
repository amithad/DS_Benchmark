//package CMS;

//import CMS.Util.Configurations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Daemon {

    public static final String BSAddress = Configurations.BOOTSTRAPIP;
    public static final int BSPort = Configurations.BOOTSTRAPPORT;

    private static Map<Integer, String> fileMap;

    public static void main(String[] args) throws IOException {

        fileMap = new HashMap<>();
        /*fileMap.put(0, "Cinderella");
        fileMap.put(1, "Aurora");
        fileMap.put(2, "Belle");
        fileMap.put(3, "Thumbelina");
        fileMap.put(4, "Tiana");*/

        TestNode n0 = new TestNode("N0", Configurations.NODEPORT, Configurations.USERNAME);
        /*TestNode n1 = new TestNode("N1", 5001, "user1");
        TestNode n2 = new TestNode("N2", 5002, "user2");
        TestNode n3 = new TestNode("N3", 5003, "user3");
        TestNode n4 = new TestNode("N4", 5004, "user4");*/

        n0.start();
        /*n1.start();
        n2.start();
        n3.start();
        n4.start();*/
    }

    public static class TestNode extends Thread {

        private Node myNode;

        public TestNode(String nodeID, int UDPServerPort, String myUsername) throws IOException {
            this.myNode = new Node(BSAddress, BSPort);
            myNode.initialize(nodeID, UDPServerPort, myUsername);
            myNode.addFiles(Configurations.FILEREPO);
        }

        @Override
        public void run() {
            try {
                myNode.start();
                Thread.sleep(2000);
                if (myNode.getNodeID().equals("N0")) {
                    myNode.startUI();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

