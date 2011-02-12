package dnode.netty;

import com.google.gson.JsonElement;
import dnode.Connection;
import org.jboss.netty.channel.Channel;

public class NettyConnection implements Connection {
    private final Channel channel;

    public NettyConnection(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public void write(JsonElement data) {
        channel.write(data.toString() + "\n");
    }
}
