package dnode.socketio;

import webbit.WebSocketConnection;
import webbit.WebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler that wraps/unwraps messages sent with a SocketIO client.
 */
public class SocketIOWebSocketHandler implements WebSocketHandler {
    private static final SocketIOCodec codec = new SocketIOCodec();
    private final WebSocketHandler handler;
    private Map<WebSocketConnection,SocketIOConnection> socketIOConnections = new HashMap<WebSocketConnection,SocketIOConnection>();

    public SocketIOWebSocketHandler(WebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onOpen(WebSocketConnection connection) throws Exception {
        SocketIOConnection socketIOConnection = new SocketIOConnection(connection, codec);
        socketIOConnections.put(connection, socketIOConnection);
        handler.onOpen(socketIOConnection);
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
                // TODO: Should we parse into JSON here? We also seem to get JSON that is *not* prefixed with ~j~ (??)
                message = message.substring(3);
            }
            handler.onMessage(socketIOConnections.get(connection), message);
        }
    }

    @Override
    public void onClose(WebSocketConnection connection) throws Exception {
        handler.onClose(socketIOConnections.remove(connection));
    }

}
