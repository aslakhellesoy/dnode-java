package dnode;

import dnode.netty.NettyServer;
import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import webbit.WebServer;
import webbit.WebServers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

public class DNodeTest {
    private DNode dNode;
    private final Server server = new NettyServer(6060);

    public class Mooer {
        private final int moo;
        public DNode dNode;

        public Mooer(int moo) {
            this.moo = moo;
        }

        public void moo(Callback cb) throws IOException {
            cb.call(moo);
            dNode.closeAllConnections();
            server.shutdown();
        }

        public void boo(Callback cb) throws IOException {
            cb.call(moo * 10);
            dNode.closeAllConnections();
            server.shutdown();
        }
    }

    @After
    public void shutdownServer() throws IOException {
        server.shutdown();
    }

    private final Object signals = new Object();

    @Test
    public void shouldTalk() throws IOException, InterruptedException {
        createDnode(100);
        runServer(dNode);
        assertEquals("100\n", runClient("moo"));
    }

    @Test
    public void shouldUseDataInInstance() throws IOException, InterruptedException {
        createDnode(200);
        runServer(dNode);
        assertEquals("200\n", runClient("moo"));
    }

    private void createDnode(int moo) {
        Mooer instance = new Mooer(moo);
        dNode = new DNode(instance);
        instance.dNode = dNode;
    }

    @Test
    public void shouldCallRightMethod() throws IOException, InterruptedException {
        createDnode(300);
        runServer(dNode);
        assertEquals("3000\n", runClient("boo"));
    }

    @Test
    @Ignore
    public void shouldTalkUsingWebbit() throws IOException, InterruptedException {
        createDnode(100);
        runWebbitServer(dNode);
//        assertEquals("100\n", runClient("moo"));
        // TODO: Run HTMLUnit here.
    }

    private void runServer(final DNode dNode) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    dNode.on("ready", new Callback() {
                        public void call(Object... args) {
                            synchronized (signals) {
                                signals.notifyAll();
                            }
                        }
                    });
                    dNode.listen(server);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        synchronized (signals) {
            thread.start();
            signals.wait();
        }
    }

    private void runWebbitServer(final DNode dNode) throws InterruptedException {
        WebServer webServer = WebServers.createWebServer(6060);
//        server = new WebbitServer(webServer, "/websocket");
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    dNode.on("ready", new Callback() {
                        public void call(Object... args) {
                            synchronized (signals) {
                                signals.notifyAll();
                            }
                        }
                    });
                    dNode.listen(server);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        synchronized (signals) {
            thread.start();
            signals.wait();
        }
    }

    private String runClient(String method) throws IOException, InterruptedException {
        String node = System.getProperty("node", "/usr/local/bin/node");
        String clientScript = System.getProperty("client", "client.js");
        ProcessBuilder pb = new ProcessBuilder(node, clientScript, method);
        pb.redirectErrorStream(true);
        Process client = pb.start();

        Reader clientStdOut = new InputStreamReader(client.getInputStream(), "UTF-8");
        StringBuilder result = new StringBuilder();
        int c;
        while ((c = clientStdOut.read()) != -1) {
            result.append((char) c);
        }
        int exit = client.waitFor();
        if (exit != 0)
            throw new AssertionFailedError("Exit value from external process was " + exit +
                    " (with stdout/stderr: " + result + ")");
        return result.toString();
    }
}
