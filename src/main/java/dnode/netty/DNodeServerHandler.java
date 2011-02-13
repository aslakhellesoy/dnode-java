package dnode.netty;

import dnode.Connection;
import dnode.DNode;
import org.jboss.netty.channel.*;

import java.util.HashMap;
import java.util.Map;

public class DNodeServerHandler extends SimpleChannelUpstreamHandler {
    private final DNode dnode;
    private Map<Channel, Connection> connections = new HashMap<Channel, Connection>();

    public DNodeServerHandler(DNode dnode) {
        this.dnode = dnode;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Connection connection = new NettyConnection(e.getChannel());
        connections.put(e.getChannel(), connection);
        dnode.onOpen(connection);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String message = (String) e.getMessage();
        dnode.onMessage(connections.get(e.getChannel()), message);
    }
}
