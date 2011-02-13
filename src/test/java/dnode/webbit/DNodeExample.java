package dnode.webbit;

import dnode.Callback;
import dnode.DNode;
import webbit.WebServer;
import webbit.handler.EmbeddedResourceHandler;
import webbit.netty.NettyWebServer;

import java.io.IOException;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class DNodeExample {
    public static class Cat {
        public void cat(Callback cb) {
            cb.call("GROWL");
        }
    }

    public static void main(String[] args) throws IOException {
        WebServer server = new NettyWebServer(6061);
        new DNode(new Cat()).listen(new WebbitServer(server));
        server.add("/.*", new EmbeddedResourceHandler("dnode/js", newFixedThreadPool(4)));
        server.start();
    }
}
