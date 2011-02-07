package dnode.nio;

import dnode.Connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class NIOConnection implements Connection {
    private static Charset charset = Charset.forName("UTF-8");
    private static CharsetEncoder encoder = charset.newEncoder();
    private static CharsetDecoder decoder = charset.newDecoder();

    private final SocketChannel channel;

    public NIOConnection(SocketChannel channel) {
        this.channel = channel;
    }

    public void send(String data) {
        try {
            channel.write(encoder.encode(CharBuffer.wrap(data + "\r\n")));
        } catch (IOException e) {
            try {
                close();
            } catch (IOException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public String read() throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(1024);
        channel.read(bb);
        bb.flip();
        CharBuffer response = decoder.decode(bb);
        return response.toString();
    }

    public void close() throws IOException {
        channel.close();
    }
}
