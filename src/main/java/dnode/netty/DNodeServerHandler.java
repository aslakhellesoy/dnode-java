package dnode.netty;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dnode.DNode;
import org.jboss.netty.channel.*;

public class DNodeServerHandler extends SimpleChannelUpstreamHandler {
    private final DNode dnode;

    public DNodeServerHandler(DNode dnode) {
        this.dnode = dnode;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        dnode.onConnection(e.getChannel());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String message = (String) e.getMessage();
        JsonElement json = new JsonParser().parse(message);
        dnode.handleRequest(json.getAsJsonObject(), e.getChannel());
    }
}
