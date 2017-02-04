//package CMS.Util;

/**
 * Created by amitha on 1/24/17.
 */
public class Neighbour {
    private String IP;
    private int port;

    public Neighbour(String ip, int port){
        this.IP  = ip;
        this.port = port;
    }

    public String getNeighbourIP(){
        return this.IP;
    }

    public int getNeighbourPort(){
        return this.port;
    }
}
