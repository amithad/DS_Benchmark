//package CMS.Util;

//import CMS.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Scanner;

/**
 * Created by amitha on 2/3/17.
 */
public class CLI extends Thread {

    Node node;

    public CLI(Node node) {
        this.node = node;
    }

    @Override
    public void run() {
        boolean CLIRunning = true;
        CSVUtils.writeSearchHeader(); //create file and write header
        CSVUtils.writeNodeHeaderCSV();
        while (CLIRunning) {
            node.screen(null);
            node.screen("Enter command/ query to search");

            Scanner scanner = new Scanner(System.in);
            String fileName = scanner.nextLine();
            if (fileName.equals("")) {

            } else if (fileName.equals("exit 0")) {
                printNodeStats();
                System.exit(0);
            } else if (fileName.equals("exit 1")) {
                printNodeStats();
                CSVUtils.writeBreakCSV();
                resetStats();
            } else if (fileName.equals("leave 1")) {
                try {
                    node.leaveDS();
                    printNodeStats();
                    resetStats();
                } catch (InterruptedException | NotBoundException | IOException e) {
                    e.printStackTrace();
                }
            } else if (fileName.equals("simulate -a")) {
                try {
                    processQueries("queries");
                } catch (IOException | NotBoundException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    node.searchFile(fileName);
                } catch (IOException | InterruptedException | NotBoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void printNodeStats() {
        node.screen("========================================================");
        node.screen("Node degree: " + node.getNeighbourCount());
        node.screen("Total query msgs received: " + node.queriesReceived);
        node.screen("Total query msgs forwarded: " + node.queriesForwarded);
        node.screen("Total query msgs answered: " + node.queriesAnswered);
        node.screen("========================================================");
        CSVUtils.writeResults(new CSVUtils.NodeResults(node.queriesReceived, node.queriesForwarded, node.queriesAnswered, node.getNeighbourCount()));
    }

    public void resetStats() {
        node.queriesReceived = 0;
        node.queriesForwarded = 0;
        node.queriesAnswered = 0;
    }

    public void processQueries(String fileRepo) throws IOException, NotBoundException, InterruptedException {
        node.screen("Initializing query processor..");
        FileReader fr = new FileReader(fileRepo);
        BufferedReader br = new BufferedReader(fr);

        String filename;
        while ((filename = br.readLine()) != null) {
            node.searchFile(filename);
        }
        node.searchFile("exit 1");
        node.screen("Iteration finished.");
    }

}

