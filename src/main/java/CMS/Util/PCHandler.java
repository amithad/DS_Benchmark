package CMS.Util;

import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;

/**
 * Created by amitha on 1/25/17.
 */
public interface PCHandler extends Remote {

    String DSCommMsg(String msg, String senderIP) throws InterruptedException, XmlRpcException, IOException, NotBoundException;
}


