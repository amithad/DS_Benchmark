package CMS.Util;

import CMS.Node;

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
            node.screen(null);
            node.screen("Enter a file name to search:");

            Scanner scanner = new Scanner(System.in);
            String fileName = scanner.nextLine();

            if(fileName.equals("exit 0")){
                printNodeStats();
                System.exit(0);
            }

            try {
                node.searchFile(fileName);
            } catch (IOException | InterruptedException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void printNodeStats(){
        node.screen("========================================================");
        node.screen("Node degree: "+ node.getNeighbourCount());
        node.screen("Total query msgs received: "+ node.queriesReceived);
        node.screen("Total query msgs forwarded: "+ node.queriesForwarded);
        node.screen("Total query msgs answered: "+ node.queriesAnswered);
        node.screen("========================================================");
    }

}
