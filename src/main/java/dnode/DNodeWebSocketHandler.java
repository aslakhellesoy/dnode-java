package dnode;

import java.util.Map;
import java.util.HashMap;

import webbit.*;

public class DNodeWebSocketHandler implements WebSocketHandler {
    private final DNode dnode;

    public DNodeWebSocketHandler(DNode dnode) {
        this.dnode = dnode;
    }

    // This looks horrible, but it seems to be OK... =)
    private Map<WebSocketConnection, WebbitConnection> connections = new HashMap<WebSocketConnection, WebbitConnection>();

    public void onOpen(WebSocketConnection connection) throws Exception {
        WebbitConnection c = getFor(connection);
        dnode.handle(c);
    }

    public void onMessage(WebSocketConnection connection, String msg) throws Exception {
        WebbitConnection c = getFor(connection);
        c.addMessage(msg);
    }

    public void onClose(WebSocketConnection connection) throws Exception {
        WebbitConnection c = getFor(connection);
        connections.remove(connection);
    }

    private WebbitConnection getFor(WebSocketConnection connection) {
        WebbitConnection wc = connections.get(connection);
        if(wc == null) {
            wc = new WebbitConnection(connection);
            connections.put(connection, wc);
        }
        return wc;
    }
}


