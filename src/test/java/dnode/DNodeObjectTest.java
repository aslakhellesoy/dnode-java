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
    public void shouldDescribeItself() {
        DNodeObject dcat = new DNodeObject(new Cat());
        assertEquals("{\"say\":\"[Function]\",\"meow\":\"[Function]\"}", dcat.toJson().toString());
    }

}    