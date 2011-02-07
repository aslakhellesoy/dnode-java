package dnode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

public class DNode {
    private static Charset charset = Charset.forName("UTF-8");
    private static CharsetEncoder encoder = charset.newEncoder();
    private static CharsetDecoder decoder = charset.newDecoder();

    private final Object instance;
    private SocketChannel sc;
    private Map<String,Callback> callbacks = new HashMap<String, Callback>();
    private ServerSocketChannel ssc;

    public DNode(Object instance) {
        this.instance = instance;
    }

    public void listen(int port) throws IOException {
        sc = connect(port);
        send(methods());
        String clientMethods = read();
        String invocation = read();
        System.out.println("invocation = " + invocation);
        send("{\"method\":0,\"arguments\":[100],\"callbacks\":{},\"links\":[]}");
        shutdown();
    }

    private void shutdown() throws IOException {
        ssc.close();
        sc.close();
    }

    private SocketChannel connect(int port) throws IOException {
        ssc = ServerSocketChannel.open();
        InetSocketAddress isa = new InetSocketAddress(InetAddress.getLocalHost(), port);
        ssc.socket().bind(isa);
        emit("ready");
        return ssc.accept();
    }

    private void emit(String event, Object... args) {
        callbacks.get(event).call(args);
    }

    private String read() throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(1024);
        sc.read(bb);
        bb.flip();
        CharBuffer response = decoder.decode(bb);
        return response.toString();
    }

    private void send(String data) throws IOException {
        sc.write(encoder.encode(CharBuffer.wrap(data + "\r\n")));
    }

    // {"method":"methods","arguments":[{"moo":"[Function]"}],"callbacks":{"0":["0","moo"]},"links":[]}

    private String methods() {
        return "{\"method\":\"methods\",\"arguments\":[{\"moo\":\"[Function]\"}],\"callbacks\":{\"0\":[\"0\",\"moo\"]},\"links\":[]}";
    }

    public void on(String event, Callback callback) {
        callbacks.put(event, callback);
    }
}
