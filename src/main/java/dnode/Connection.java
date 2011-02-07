package dnode;

import java.io.IOException;

public interface Connection {
    void send(String data);

    String read() throws IOException;

    void close() throws IOException;
}
