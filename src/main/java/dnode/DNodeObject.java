package dnode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jboss.netty.channel.Channel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DNodeObject {
    private final Object instance;

    public DNodeObject(Object instance) {
        this.instance = instance;
    }

    public JsonElement getSignature() {
        Class<?> klass = this.instance.getClass();
        JsonObject signature = new JsonObject();
        for (Method m : klass.getDeclaredMethods()) {
            signature.addProperty(m.getName(), "[Function]");
        }
        return signature;
    }

    public JsonElement getCallbacks() {
        Class<?> klass = this.instance.getClass();
        JsonObject callbacks = new JsonObject();
        int index = 0;
        for (Method m : klass.getDeclaredMethods()) {
            Class<?>[] parameterTypes = m.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                if (Callback.class.isAssignableFrom(parameterType)) {
                    JsonArray path = new JsonArray();
                    path.add(new JsonPrimitive("0"));
                    path.add(new JsonPrimitive(m.getName()));
                    callbacks.add(String.valueOf(index++), path);
                }
            }
        }
        return callbacks;
    }

    public void invoke(JsonObject invocation, Callback callback) {
        try {
            instance.getClass().getDeclaredMethods()[invocation.get("method").getAsInt()].invoke(instance, callback);
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
