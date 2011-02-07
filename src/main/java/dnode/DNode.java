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
        try {
            invoke(invocation, new Callback() {
                public void call(Object... args) {
                    JsonArray jsonArgs = transform(args);
                    send(responseString(0, jsonArgs, new JsonObject(), new JsonArray()));
                    try {
                        shutdown();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            });
        } catch (Throwable throwable) {
            // TODO write back exception result
            throwable.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void invoke(String invocation, Callback callback) throws Throwable {
        instance.invoke(callback);
    }

    private JsonArray transform(Object[] args) {
        JsonArray result = new JsonArray();
        for (Object arg : args) {
            result.add(toJson(arg));
        }
        return result;
    }

    private JsonElement toJson(Object o) {
        JsonElement e;
        if(o instanceof String) {
            e = new JsonPrimitive((String) o);
        } else if(o instanceof Number) {
            e = new JsonPrimitive((Number) o);
        } else {
            throw new RuntimeException("Unsupported type: " + o.getClass());
        }
        return e;
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

    private void send(String data) {
        try {
            sc.write(encoder.encode(CharBuffer.wrap(data + "\r\n")));
        } catch (IOException e) {
            try {
                sc.close();
            } catch (IOException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
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
