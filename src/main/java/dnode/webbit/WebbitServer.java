package dnode.webbit;

import dnode.DNode;
import dnode.Server;
import dnode.socketio.SocketIOWebSocketHandler;
import webbit.WebServer;

import java.io.IOException;

public class WebbitServer implements Server {
    private final WebServer server;

    public WebbitServer(WebServer server) {
        this.server = server;
    }

    public void listen(DNode dnode) throws IOException {
        server.add("/socket.io/websocket", new SocketIOWebSocketHandler(new DNodeWebSocketHandler(dnode)));
    }

    public void shutdown() throws IOException {
        server.stop();
    }
}

