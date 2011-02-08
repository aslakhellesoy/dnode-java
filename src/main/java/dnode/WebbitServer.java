package dnode;

import java.io.IOException;
import webbit.WebServer;

public class WebbitServer implements Server {
    private final WebServer server;
    private final String path;
    public WebbitServer(WebServer server, String path) {
        this.server = server;
        this.path = path;
    }

    public void listen(DNode dnode) throws IOException {
        this.server.add(path, new DNodeWebSocketHandler(dnode));
        this.server.start();
        dnode.emit("ready");
    }

    public void shutdown() throws IOException {
        this.server.stop();
    }
}

