package dnode;

import org.junit.Test;

import java.io.*;

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

        runClient();
    }

    private void runClient() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/node", "/Users/ahellesoy/scm/dnode-java/dnode/client.js");
        pb.redirectErrorStream(true);
        Process client = pb.start();

        final BufferedReader clientStdOut = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
        Thread pumper = new Thread(new Runnable() {
            public void run() {
                try {
                    String line;
                    System.out.println("READING...");
                    while ((line = clientStdOut.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        pumper.start();
        int exit = client.waitFor();
        System.out.println("exit = " + exit);
    }

}
