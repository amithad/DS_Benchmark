package CMS.Util;

import CMS.Node;
import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Scanner;

/**
 * Created by amitha on 2/3/17.
 */
public class CLI extends Thread {

    Node node;

    public CLI(Node node){
        this.node = node;
    }

    @Override
    public void run(){
        boolean CLIRunning = true;
        while (CLIRunning){
            System.out.println();
            System.out.println("Enter a file name to search:");
            Scanner scanner = new Scanner(System.in);
            String fileName = scanner.nextLine();
            try {
                node.searchFile(fileName);
            } catch (IOException | InterruptedException | XmlRpcException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

}
