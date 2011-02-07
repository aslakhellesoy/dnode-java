package dnode;

import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

public class DNodeTest {
    private DNode dNode;

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
        dNode.shutdown();
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
                    dNode.listen(6060);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        thread.start();
        synchronized (signals) {
            signals.wait();
        }
    }

    private String runClient(String method) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/node", "/Users/ahellesoy/scm/dnode-java/dnode/client.js", method);
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
