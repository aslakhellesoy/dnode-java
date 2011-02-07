package dnode;

import junit.framework.AssertionFailedError;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class DNodeTest {
    public static class Mooer {
        private final Object moo;

        public Mooer(Object moo) {
            this.moo = moo;
        }

        public void moo(Callback cb) {
            cb.call(moo);
        }
    }

    private final Object signals = new Object();

    @Test
    public void shouldTalk() throws IOException, InterruptedException {
        final DNode dNode = new DNode(new Mooer(100));
        runServer(dNode);
        assertEquals("100\n", runClient());
    }

    @Test
    public void shouldUSeDataInInstance() throws IOException, InterruptedException {
        final DNode dNode = new DNode(new Mooer(200));
        runServer(dNode);
        assertEquals("200\n", runClient());
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

    private String runClient() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/node", "/Users/ahellesoy/scm/dnode-java/dnode/client.js");
        pb.redirectErrorStream(true);
        Process client = pb.start();

        BufferedReader clientStdOut = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = clientStdOut.readLine()) != null) {
            result.append(line).append("\n");
        }
        int exit = client.waitFor();
        if(exit != 0)
            throw new AssertionFailedError("Exit value from external process was " + exit + 
                    " (with stdout/stderr: " + result + ")");
        return result.toString();
    }

}
