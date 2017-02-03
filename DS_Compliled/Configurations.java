//package CMS.Util;

//import CMS.Daemon;
//import jdk.nashorn.internal.runtime.linker.Bootstrap;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created by Chamil Prabodha on 03/02/2017.
 */
public class Configurations {

    private static Properties properties;
    private static InputStream inputStream;

    public static String BOOTSTRAPIP;
    public static int BOOTSTRAPPORT;
    public static String USERNAME;
    public static String NODEIP;
    public static int NODEPORT;
    public static String FILEREPO;
    public static int MAXHOPS;
    public static int TTL;
    public static String QUERIES;

    static {

        try {
            properties = new Properties();
            String filename = Daemon.class.getProtectionDomain().getCodeSource().getLocation().toURI().getSchemeSpecificPart() + "config.properties";
            inputStream = new FileInputStream(filename);

            if (inputStream != null) {
                properties.load(inputStream);
                BOOTSTRAPIP = properties.getProperty("BOOTSTRAPIP");
                BOOTSTRAPPORT = Integer.parseInt(properties.getProperty("BOOTSTRAPPORT"));
                USERNAME = properties.getProperty("USERNAME");
                NODEIP = properties.getProperty("NODEIP");
                NODEPORT = Integer.parseInt(properties.getProperty("NODEPORT"));
                FILEREPO = properties.getProperty("FILEREPO");
                MAXHOPS = Integer.parseInt(properties.getProperty("MAXHOPS"));
                TTL = Integer.parseInt(properties.getProperty("TTL"));
                QUERIES = properties.getProperty("QUERIES");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }



    }
}
