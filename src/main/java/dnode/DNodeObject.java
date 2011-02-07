package dnode;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DNodeObject {
    private final Object instance;

    public DNodeObject(Object instance) {
        this.instance = instance;
    }

    public JsonElement toJson() {
        Class<?> klass = this.instance.getClass();
        JsonObject signature = new JsonObject();
        for(Method m : klass.getDeclaredMethods()) {
            signature.addProperty(m.getName(), "[Function]");
        }
        return signature;
    }

    public void invoke(Callback callback) {
        try {
            instance.getClass().getDeclaredMethods()[0].invoke(instance, callback);
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
