package dnode.webbit;

import com.google.gson.JsonElement;
import dnode.Connection;
import webbit.WebSocketConnection;

public class WebbitConnection implements Connection {
    private final WebSocketConnection webSocketConnection;

    public WebbitConnection(WebSocketConnection webSocketConnection) {
        if (webSocketConnection == null) {
            throw new NullPointerException("Where is the connection?");
        }
        this.webSocketConnection = webSocketConnection;
    }

    @Override
    public void write(JsonElement data) {
        webSocketConnection.send(data.toString() + "\n");
    }

    @Override
    public void close() {
        webSocketConnection.close();
    }
}
