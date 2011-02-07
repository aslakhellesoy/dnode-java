package dnode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DNodeObjectTest {
    public static class Cat {
        public void say(Callback callback) {

        }

        public void meow(Callback callback) {

        }
    }

    @Test
    public void shouldReportSignature() {
        DNodeObject dcat = new DNodeObject(new Cat());
        assertEquals("{\"say\":\"[Function]\",\"meow\":\"[Function]\"}", dcat.getSignature().toString());
    }

    @Test
    public void shouldReportCallbacks() {
        DNodeObject dcat = new DNodeObject(new Cat());
        assertEquals("{\"0\":[\"0\",\"say\"],\"1\":[\"0\",\"meow\"]}", dcat.getCallbacks().toString());
    }
}    