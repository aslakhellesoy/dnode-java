package dnode.socketio;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SocketIOCodecTest {
    private final SocketIOCodec codec = new SocketIOCodec();

    @Test
    public void shouldEncodeOneElement() {
        assertEncoding(Arrays.asList("Hello"));
    }

    @Test
    public void shouldEncodeTwoElements() {
        assertEncoding(Arrays.asList("Hello", "World"));
    }

    private void assertEncoding(List m) {
        String encoded = codec.encode(m);
        assertEquals(m, codec.decode(encoded));
    }
}
