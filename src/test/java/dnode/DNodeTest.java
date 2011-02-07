package dnode;

import junit.framework.AssertionFailedError;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class DNodeTest {
    public static class Calculator {
        public void plus(int a, int b, Callback cb) {
            cb.call(a + b);
        }
    }

    private final Object signals = new Object();

    @Test
    public void shouldTalk() throws IOException, InterruptedException {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    DNode dNode = new DNode(new Calculator());
                    dNode.on("ready", new Callback() {
                        public void call(Object... args) {
                            System.out.println("READY");
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

        assertEquals("100\n", runClient());
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
