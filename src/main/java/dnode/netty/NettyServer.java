package dnode.netty;

import dnode.DNode;
import dnode.Server;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class NettyServer implements Server {
    private final int port;
    private Channel channel;

    public NettyServer(int port) {
        this.port = port;
    }

    @Override
    public void listen(DNode dnode) throws IOException {
        ChannelFactory factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        ServerBootstrap bootstrap = new ServerBootstrap(factory);
        bootstrap.setPipelineFactory(new DNodePipelineFactory(dnode));
        channel = bootstrap.bind(new InetSocketAddress(port));
    }

    @Override
    public void shutdown() throws IOException {
        if(channel != null) {
            channel.close();
        }
    }
}
