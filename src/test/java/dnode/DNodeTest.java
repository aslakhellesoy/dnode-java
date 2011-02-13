package dnode;

import dnode.netty.NettyServer;
import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.Test;

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

    @Test
    public void shouldTalk() throws IOException, InterruptedException {
        createDnode(100);
        dNode.listen(server);
        assertEquals("100\n", runClient("moo"));
    }

    @Test
    public void shouldUseDataInInstance() throws IOException, InterruptedException {
        createDnode(200);
        dNode.listen(server);
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
        dNode.listen(server);
        assertEquals("3000\n", runClient("boo"));
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
