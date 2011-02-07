package dnode;

import com.google.gson.*;

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

    private final DNodeObject instance;
    private SocketChannel sc;
    private Map<String,Callback> callbacks = new HashMap<String, Callback>();
    private ServerSocketChannel ssc;

    public DNode(Object instance) {
        this.instance = new DNodeObject(instance);
    }

    public void listen(int port) throws IOException {
        sc = connect(port);
        send(methods());
        String clientMethods = read();
        String invocation = read();

        JsonArray args = new JsonArray();
        args.add(new JsonPrimitive(100));
        //send("{\"method\":0,\"arguments\":[100],\"callbacks\":{},\"links\":[]}");
        send(responseString(0, args, new JsonObject(), new JsonArray()));
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

    private String methods() {
        JsonArray arguments = new JsonArray();
        arguments.add(instance.toJson());
        JsonObject callbacks = new JsonObject();
        JsonArray callbackArray = new JsonArray();
        callbackArray.add(new JsonPrimitive("0"));
        callbackArray.add(new JsonPrimitive("moo"));
        callbacks.add("0", callbackArray);
        return responseString("methods", arguments, callbacks, new JsonArray());
    }

    private String responseString(int method, JsonArray arguments, JsonObject callbacks, JsonArray links) {
        return responseString(new JsonPrimitive(method), arguments, callbacks, links);
    }
        
    private String responseString(String method, JsonArray arguments, JsonObject callbacks, JsonArray links) {
        return responseString(new JsonPrimitive(method), arguments, callbacks, links);
    }

    private String responseString(JsonPrimitive method, JsonArray arguments, JsonObject callbacks, JsonArray links) {
        JsonObject response = new JsonObject();
        response.add("method", method);
        response.add("arguments", arguments);
        response.add("callbacks", callbacks);
        response.add("links", links);
        return response.toString();
    }

    public void on(String event, Callback callback) {
        callbacks.put(event, callback);
    }
}
