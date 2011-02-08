package dnode;

import java.util.LinkedList;
import java.io.IOException;
import webbit.*;

public class WebbitConnection implements Connection {
    private final WebSocketConnection c;
    private final LinkedList<String> incoming = new LinkedList<String>();

    public WebbitConnection(WebSocketConnection  conn) {
        this.c = conn;
    }

    public void send(String data) {
        c.send(data);
    }

    public void addMessage(String msg) {
        synchronized(incoming) {
            incoming.add(msg);
            incoming.notifyAll();
        }
    }

    public String read() throws IOException {
        synchronized(incoming) {
            while(incoming.size() == 0) {
                try {
                    incoming.wait();
                } catch(InterruptedException ignored) {
                }
            }
            return incoming.removeFirst();
        }
    }

    public void close() throws IOException {
        c.close();
    }
}
