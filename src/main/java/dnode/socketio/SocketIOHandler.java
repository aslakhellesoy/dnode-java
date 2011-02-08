package dnode.socketio;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import webbit.WebSocketConnection;
import webbit.WebSocketHandler;

import java.util.List;

public class SocketIOHandler implements WebSocketHandler {
    private static final SocketIOCodec codec = new SocketIOCodec();
    private final SocketIOListener listener;

    public SocketIOHandler(SocketIOListener listener) {
        this.listener = listener;
    }

    @Override
    public void onOpen(WebSocketConnection connection) throws Exception {
        System.out.println("open:connection = " + connection);
        // TODO: See dnode/conn.js about how to calculate a unique id
        connection.send(codec.encode("5938544741366059"));
    }

    @Override
    public void onMessage(WebSocketConnection connection, String msg) throws Exception {
        List<String> messages = codec.decode(msg);
        for (String message : messages) {
            String frame = message.substring(0, 3);
            if(frame.equals("~j~")) {
                // Heartbeat
                return;
            } else if(frame.equals("~j~")) {
                JsonElement json = new JsonParser().parse(message.substring(3));
                listener.onClientMessage(json, this);
            } else {
                listener.onClientMessage(message, this);
            }
            

            // if it looks like: ~m~53~m~{"method":"methods","arguments":[{}],"callbacks":{}}
            // then repoly with
            // {"method":"methods","arguments":[{"cat":"[Function]"}],"callbacks":{"0":["0","cat"]},"links":[]}

//            JsonElement method = jmsg.getAsJsonObject().get("method");
//            if (method.getAsString().equals("methods")) {
//                connection.send(codec.encode("{\"method\":\"methods\",\"arguments\":[{\"cat\":\"[Function]\"}],\"callbacks\":{\"0\":[\"0\",\"cat\"]},\"links\":[]}"));
//            } else if (method.getAsInt() == 0) {
//                connection.send(codec.encode("{\"method\":0,\"arguments\":[\"meow\"],\"callbacks\":{},\"links\":[]}"));
//            }
        }
    }

    @Override
    public void onClose(WebSocketConnection connection) throws Exception {
        System.out.println("close:connection = " + connection);
    }
}
