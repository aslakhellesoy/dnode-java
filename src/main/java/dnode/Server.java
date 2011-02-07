package dnode;

import java.io.IOException;

public interface Server {
    void listen(DNode dnode) throws IOException;

    void shutdown() throws IOException;
}
