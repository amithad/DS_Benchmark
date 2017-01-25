package CMS.Util;

import CMS.FT.FTMan;
import CMS.Node;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

/**
 * Created by amitha on 1/25/17.
 */

public class RPCServer extends Thread {

    private int port;
    private Node node;
    public RPCServer(int port, Node node){
        this.port = port;
        this.node = node;
    }

    @Override
    public void run() {

        try {
            if(!node.isRPCServerReady()){
                node.echo("Attempting to start XML-RPC Server...");
                WebServer webServer = new WebServer(port);
                XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
                PropertyHandlerMapping phm = new PropertyHandlerMapping();
                phm.addHandler("handler", PCHandler.class);

                XmlRpcServerConfigImpl cf = new XmlRpcServerConfigImpl();
                xmlRpcServer.setConfig(cf);
                xmlRpcServer.setHandlerMapping(phm);
                webServer.start();

                node.echo("RPC Server started successfully and running on port "+port+".");
                node.RPCServerReady(); //set the ready flag.
            }
            else
                node.echo("RPC Server is already running.");

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
