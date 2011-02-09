package dnode.webbit;

import java.util.LinkedList;
import java.io.IOException;

import dnode.Connection;
import webbit.*;

public class WebbitConnection implements Connection {
    private final WebSocketConnection webSocketConnection;
    private final LinkedList<String> incoming = new LinkedList<String>();

    public WebbitConnection(WebSocketConnection webSocketConnection) {
        if (webSocketConnection == null) {
            throw new NullPointerException("Where is the connection?");
        }
        this.webSocketConnection = webSocketConnection;
    }

    public void send(String data) {
        webSocketConnection.send(data);
    }

    public void addMessage(String msg) {
        synchronized (incoming) {
            incoming.add(msg);
            incoming.notifyAll();
        }
    }

    public String read() throws IOException {
        synchronized (incoming) {
            while (incoming.size() == 0) {
                try {
                    incoming.wait();
                } catch (InterruptedException ignored) {
                }
            }
            return incoming.removeFirst();
        }
    }

    public void close() throws IOException {
        webSocketConnection.close();
    }
}
