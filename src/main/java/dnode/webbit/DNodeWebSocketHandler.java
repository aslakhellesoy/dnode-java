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
        System.out.println("ONOPEN");
        WebbitConnection c = getFor(connection);
        dnode.handle(c);
        System.out.println("ONOPEN DONE");
    }

    public void onMessage(WebSocketConnection connection, String msg) throws Exception {
        System.out.println("DNode RECV: " + msg);
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


