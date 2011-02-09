package dnode.socketio;

import webbit.HttpRequest;
import webbit.WebSocketConnection;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

class SocketIOConnection implements WebSocketConnection {
    private final WebSocketConnection connection;
    private final SocketIOCodec codec;

    public SocketIOConnection(WebSocketConnection connection, SocketIOCodec codec) {
        this.connection = connection;
        this.codec = codec;
        // TODO: See dnode/conn.js about how to calculate a unique id
        System.out.println("connection = " + connection);
        connection.send(codec.encode("5938544741366059"));
    }

    @Override
    public HttpRequest httpRequest() {
        return connection.httpRequest();
    }

    @Override
    public WebSocketConnection send(String message) {
        String encodedMessage = codec.encode(message);
        System.out.println("SEND " + encodedMessage);
        connection.send(encodedMessage);
        return this;
    }

    @Override
    public WebSocketConnection close() {
        connection.close();
        return this;
    }

    @Override
    public Map<String, Object> data() {
        return connection.data();
    }

    @Override
    public Object data(String key) {
        return connection.data(key);
    }

    @Override
    public WebSocketConnection data(String key, Object value) {
        return connection.data(key, value);
    }

    @Override
    public Set<String> dataKeys() {
        return connection.dataKeys();
    }

    @Override
    public Executor handlerExecutor() {
        return connection.handlerExecutor();
    }

    @Override
    public void execute(Runnable runnable) {
        connection.execute(runnable);
    }
}
