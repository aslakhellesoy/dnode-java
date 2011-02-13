package dnode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

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

    public JsonObject getCallbacks() {
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

    public void invoke(final DNode dNode, JsonObject invocation, final Connection connection) {
        try {
            int methodIndex = invocation.get("method").getAsInt();
            Set<Map.Entry<String, JsonElement>> callbacks = invocation.get("callbacks").getAsJsonObject().entrySet();
            for (Map.Entry<String, JsonElement> callback : callbacks) {
                final int callbackId = Integer.parseInt(callback.getKey());
                JsonArray path = callback.getValue().getAsJsonArray();
                Callback cb = new Callback() {
                    @Override
                    public void call(Object... args) throws RuntimeException {
                        JsonArray jsonArgs = dNode.transform(args);
                        connection.write(dNode.response(callbackId, jsonArgs, new JsonObject(), new JsonArray()));
                    }
                };
                instance.getClass().getDeclaredMethods()[methodIndex].invoke(instance, cb);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
