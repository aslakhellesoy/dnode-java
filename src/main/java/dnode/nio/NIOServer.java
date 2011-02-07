package dnode.nio;

import dnode.Connection;
import dnode.DNode;
import dnode.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NIOServer implements Server {
    private ServerSocketChannel ssc;
    private final int port;

    public NIOServer(int port) {
        this.port = port;
    }

    public void listen(DNode dnode) throws IOException {
        ssc = ServerSocketChannel.open();
        InetSocketAddress isa = new InetSocketAddress(InetAddress.getLocalHost(), port);
        ssc.socket().bind(isa);
        dnode.emit("ready");
        SocketChannel channel = ssc.accept();

        Connection conn = new NIOConnection(channel);
        dnode.handle(conn);
    }

    public void shutdown() throws IOException {
        ssc.close();
    }

}
