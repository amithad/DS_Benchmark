package CMS; /**
 * Created by amitha on 1/8/17.
 */

import CMS.FT.FTMan;
import CMS.Util.CLI;
import CMS.Util.CSVUtils;
import CMS.Util.Configurations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import static java.lang.System.currentTimeMillis;

public class Node extends FTMan {

    public static final int waitTimeout = Configurations.TTL;
    public static final int hopCount = Configurations.MAXHOPS;

    private boolean fileRequested = false;
    private long startTime = 0;
    private CLI userInterface;

    private Vector<String> myFiles = new Vector<>();

    public Node(String BSIP, int BSPort) throws IOException {
        super(BSIP, BSPort);
    }

    public void start() throws IOException, InterruptedException {
        startFT();
        startServer();
        if (!myFiles.isEmpty()) {
            String out = "Available Files:";
            for (int i = 0; i < myFiles.size(); i++) {
                out += " " + myFiles.get(i);
            }
            echo(out);
        }
        Thread.sleep(2000);
        startHB();
    }

    public void startUI() {
        userInterface = new CLI(this);
        userInterface.start();
    }


    public void addFileString(String fileName) { //adds files to the list
        myFiles.addElement(fileName);
    }

    public void addFiles(String fileRepo) throws IOException {
        FileReader fr = new FileReader(fileRepo);
        BufferedReader br = new BufferedReader(fr);

        String filename;
        while ((filename = br.readLine()) != null) {
            addFileString(filename);
        }
        br.close();
        fr.close();
    }

    @Override
    public String findFile(String fileName) {
        fileName = fileName.replace('@', ' ');
        String result = "";

        fileName = fileName.trim();
        for (int i = 0; i < myFiles.size(); i++) {
            if (fileName.equalsIgnoreCase(myFiles.get(i))) {
                result = myFiles.get(i).replace(' ', '@');
                return result;
            } else {
                for (String partialWord : myFiles.get(i).split(" ")) {
                    if (partialWord.equalsIgnoreCase(fileName)) {
                        if (result.equalsIgnoreCase("")) {

                            result = result + myFiles.get(i).replace(' ', '@');
                        } else {

                            result = result + "," + myFiles.get(i).replace(' ', '@');
                        }
                    }
                }
            }
        }
        return result;
    }

    public void searchFile(String fileName) throws IOException, InterruptedException {
        startTime = currentTimeMillis();
        String localResponse = findFile(fileName);
        if (!localResponse.equalsIgnoreCase("")) {
            requestFile();
            retrieveResult(localResponse, getIPAddress(), getUDPServerPort(), 0);
        } else {
            requestFile();
            String SerMsg = "SER";
            SerMsg += " " + getIPAddress() + " " + getUDPServerPort() + " " + fileName.replace(' ', '@') + " " + hopCount;
            floodNeighbours(SerMsg);
            while (fileRequested) {
                if (currentTimeMillis() - startTime > waitTimeout) {
                    fileRequested = false;
                    break;
                }
            } //block the next call
        }
    }

    @Override
    public void retrieveResult(String fileName, String fileTarget, int targetPort, int hops) {
        if (fileRequested) {
            fileRequested = false;
            long diffTime = currentTimeMillis() - startTime;
            dropRequest();
            screen("========================================================");
            screen("Requested file(s) are available in the system.");
            screen("File name - " + fileName.replace('@', ' '));
            screen("File target - " + fileTarget + ":" + targetPort);
            screen("Latency (ms) - " + diffTime);
            screen("Hops to reach the target - " + hops);
            screen("========================================================");
            CSVUtils.writeSearchResults(new CSVUtils.SearchNodeResults(fileName, diffTime, hops));
        }
    }

    public void requestFile() {
        fileRequested = true;
    }

    public void dropRequest() {
        fileRequested = false;
    }
}