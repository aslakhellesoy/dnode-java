package dnode.webbit;

import java.util.Map;
import java.util.HashMap;

import dnode.DNode;
import webbit.*;

public class DNodeWebSocketHandler implements WebSocketHandler {
    private final DNode dnode;

    // This looks horrible, but it seems to be OK... =)
    private final Map<WebSocketConnection, WebbitConnection> connections = new HashMap<WebSocketConnection, WebbitConnection>();

    public DNodeWebSocketHandler(DNode dnode) {
        this.dnode = dnode;
    }

    public void onOpen(WebSocketConnection connection) throws Exception {
        WebbitConnection c = getFor(connection);
        dnode.handle(c);
    }

    public void onMessage(WebSocketConnection connection, String msg) throws Exception {
        WebbitConnection c = getFor(connection);
        c.addMessage(msg);
    }

    public void onClose(WebSocketConnection connection) throws Exception {
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


