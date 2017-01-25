package CMS.Util;

import CMS.Node;

import java.util.StringTokenizer;

/**
 * Created by amitha on 1/25/17.
 */

/**
 * New Msg formats:
 * <p>
 * RPCREQ <RPCall> <hops> <SenderIP> <SenderPort>  - request for RPC call
 * <p>
 * RPCOK <RPCall> <SenderIP> <SenderPort> - acknowledge the availability of functionality
 */

public class RPCServiceMsgDecoder {
    public static String DecodeRPCMsg(Node node, String msg, String senderIP) {
        StringTokenizer st = new StringTokenizer(msg, " ");
        String length = st.nextToken();
        String responseType = st.nextToken();

        RPCServiceMsgType t = RPCServiceMsgType.valueOf(responseType);
        switch (t) {
            case RPCREQ: {
                String rpcRequest = st.nextToken();
                int hops = Integer.parseInt(st.nextToken());
                String senderip = st.nextToken();
                int senderPort = Integer.parseInt(st.nextToken());
                if (node.RPCIsSupported(rpcRequest)) {
                    node.sendRPCOK(rpcRequest, senderip, senderPort);
                } else if (hops != 0) {
                    hops--;

                }
                break;
            }
            case RPCOK: {
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
