package dnode;

import com.google.gson.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DNode {
    private final DNodeObject instance;
    private Map<String, Callback> callbacks = new HashMap<String, Callback>();

    public DNode(Object instance) {
        this.instance = new DNodeObject(instance);
    }

    public void listen(Server server) throws IOException {
        server.listen(this);
    }

    public void handle(final Connection connection) {
        try {
            connection.send(methods());
            String clientMethods = connection.read();
            String invocation = connection.read();
            invoke(invocation, new Callback() {
                public void call(Object... args) {
                    JsonArray jsonArgs = transform(args);
                    connection.send(responseString(0, jsonArgs, new JsonObject(), new JsonArray()));
                    try {
                        connection.close();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            });
        } catch (Throwable throwable) {
            // TODO write back exception result
            throwable.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void invoke(String invocation, Callback callback) throws Throwable {
        JsonObject invocationJson = (JsonObject) new JsonParser().parse(invocation);
        instance.invoke(invocationJson, callback);
    }

    private JsonArray transform(Object[] args) {
        JsonArray result = new JsonArray();
        for (Object arg : args) {
            result.add(toJson(arg));
        }
        return result;
    }

    private JsonElement toJson(Object o) {
        JsonElement e;
        if (o instanceof String) {
            e = new JsonPrimitive((String) o);
        } else if (o instanceof Number) {
            e = new JsonPrimitive((Number) o);
        } else {
            throw new RuntimeException("Unsupported type: " + o.getClass());
        }
        return e;
    }

    public void emit(String event, Object... args) {
        Callback callback = callbacks.get(event);
        if(callback != null) { // TODO: Loop over a list
            callback.call(args);
        }
    }

    private String methods() {
        JsonArray arguments = new JsonArray();
        arguments.add(instance.getSignature());
        return responseString("methods", arguments, instance.getCallbacks(), new JsonArray());
    }

    private String responseString(int method, JsonArray arguments, JsonElement callbacks, JsonArray links) {
        return responseString(new JsonPrimitive(method), arguments, callbacks, links);
    }

    private String responseString(String method, JsonArray arguments, JsonElement callbacks, JsonArray links) {
        return responseString(new JsonPrimitive(method), arguments, callbacks, links);
    }

    private String responseString(JsonElement method, JsonArray arguments, JsonElement callbacks, JsonArray links) {
        JsonObject response = new JsonObject();
        response.add("method", method);
        response.add("arguments", arguments);
        response.add("callbacks", callbacks);
        response.add("links", links);
        return response.toString();
    }

    public void on(String event, Callback callback) {
        callbacks.put(event, callback);
    }
}
