package CMS.Util;

import CMS.Node;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by amitha on 1/25/17.
 */

/**
 * New Msg formats:
 * <p>
 * RPCREQ <RPCall> <hops> <SenderIP> <SenderPort>  - request for RPC call
 * <p>
 * RPCOK <RPCall> <SenderIP> <RPCPort> - acknowledge the availability of functionality
 */

public class RPCServiceMsgDecoder {
    public static String DecodeRPCMsg(Node node, String msg, String senderIP) throws IOException {
        StringTokenizer st = new StringTokenizer(msg, " ");
        String length = st.nextToken();
        String responseType = st.nextToken();

        RPCServiceMsgType t = RPCServiceMsgType.valueOf(responseType);
        switch (t) {
            case RPCREQ: {
                node.echo("Incoming RPCRequest from " + senderIP);
                String rpcRequest = st.nextToken();
                int hops = Integer.parseInt(st.nextToken());
                String senderip = st.nextToken();
                int senderPort = Integer.parseInt(st.nextToken());
                if (node.RPCIsSupported(rpcRequest) && node.isRPCServerReady()) {
                    node.echo("Requested RPC found.");
                    node.sendRPCOK(rpcRequest, senderip, senderPort);
                } else if (hops != 0) {
                    hops--;
                    String msgToFlood = "RPCREQ " + rpcRequest + " " + hops + " " + senderip + " " + senderPort;
                    node.floodNeighbours(msgToFlood);
                    node.echo("Requested RPC not supported. Flooding the network.");
                }
                break;
            }
            case RPCOK: {
                node.echo("RPC request acknowledged by node " + senderIP);
                if (node.isWaitingToExecRPC()) {
                    node.echo("RPC response approved. Executing..");
                    String rpcReq = st.nextToken();
                    String senderip = st.nextToken();
                    int RPCServerPort = Integer.parseInt(st.nextToken());
                    node.echo("RPC received target - " + senderip + ":" + RPCServerPort);
                    node.setTargetVariables(senderip, RPCServerPort);
                    node.interruptWait();
                }
                break;
            }
            default: {
                node.echo("Unsupported message format.");
                break;
            }
        }
        return null;
    }
}
