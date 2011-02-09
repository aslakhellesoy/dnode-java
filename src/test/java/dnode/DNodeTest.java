package dnode;

import dnode.nio.NIOServer;
import dnode.webbit.WebbitServer;
import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import webbit.WebServer;
import webbit.WebServers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import static org.junit.Assert.assertEquals;

public class DNodeTest {
    private DNode dNode;
    private Server server;

    public static class Mooer {
        private final int moo;

        public Mooer(int moo) {
            this.moo = moo;
        }

        public void moo(Callback cb) {
            cb.call(moo);
        }

        public void boo(Callback cb) {
            cb.call(moo * 10);
        }
    }

    @After
    public void shutdownServer() throws IOException {
        server.shutdown();
    }

    private final Object signals = new Object();

    @Test
    public void shouldTalk() throws IOException, InterruptedException {
        dNode = new DNode(new Mooer(100));
        runServer(dNode);
        assertEquals("100\n", runClient("moo"));
    }

    @Test
    public void shouldUseDataInInstance() throws IOException, InterruptedException {
        dNode = new DNode(new Mooer(200));
        runServer(dNode);
        assertEquals("200\n", runClient("moo"));
    }

    @Test
    public void shouldCallRightMethod() throws IOException, InterruptedException {
        dNode = new DNode(new Mooer(300));
        runServer(dNode);
        assertEquals("3000\n", runClient("boo"));
    }

    @Test
    @Ignore
    public void shouldTalkUsingWebbit() throws IOException, InterruptedException {
        dNode = new DNode(new Mooer(100));
        runWebbitServer(dNode);
//        assertEquals("100\n", runClient("moo"));
        // TODO: Run HTMLUnit here.
    }

    private void runServer(final DNode dNode) throws InterruptedException {
        server = new NIOServer(6060);
        
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

        BufferedReader clientStdOut = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = clientStdOut.readLine()) != null) {
            result.append(line).append("\n");
        }
        int exit = client.waitFor();
        if (exit != 0)
            throw new AssertionFailedError("Exit value from external process was " + exit +
                                           " (with stdout/stderr: " + result + ")");
        return result.toString();
    }
}
