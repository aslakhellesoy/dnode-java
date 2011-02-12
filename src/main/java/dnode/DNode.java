package dnode;

import com.google.gson.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DNode {
    private final DNodeObject instance;
    private Map<String, Callback> callbacks = new HashMap<String, Callback>();
    private List<Connection> connections = new ArrayList<Connection>();

    public DNode(Object instance) {
        this.instance = new DNodeObject(instance);
    }

    public void listen(Server server) throws IOException {
        server.listen(this);
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
        if (callback != null) { // TODO: Loop over a list
            callback.call(args);
        }
    }

    private JsonElement methods() {
        JsonArray arguments = new JsonArray();
        arguments.add(instance.getSignature());
        return responseString("methods", arguments, instance.getCallbacks(), new JsonArray());
    }

    private JsonElement responseString(int method, JsonArray arguments, JsonElement callbacks, JsonArray links) {
        return responseString(new JsonPrimitive(method), arguments, callbacks, links);
    }

    private JsonElement responseString(String method, JsonArray arguments, JsonElement callbacks, JsonArray links) {
        return responseString(new JsonPrimitive(method), arguments, callbacks, links);
    }

    private JsonElement responseString(JsonElement method, JsonArray arguments, JsonElement callbacks, JsonArray links) {
        JsonObject response = new JsonObject();
        response.add("method", method);
        response.add("arguments", arguments);
        response.add("callbacks", callbacks);
        response.add("links", links);
        return response;
    }

    public void on(String event, Callback callback) {
        callbacks.put(event, callback);
    }

    public void onOpen(Connection connection) {
        connections.add(connection);
        connection.write(methods());        
    }

    public void onMessage(Connection connection, String msg) {
        JsonObject json = new JsonParser().parse(msg).getAsJsonObject();
        JsonPrimitive method = json.getAsJsonPrimitive("method");
        if (method.isString() && method.getAsString().equals("methods")) {
            handleMethods(json.getAsJsonArray("arguments").get(0).getAsJsonObject());
        } else {
            handleInvocation(json, connection);
        }
    }

    private void handleMethods(JsonObject arguments) {
    }

    private void handleInvocation(JsonObject invocation, final Connection channel) {
        Callback callback = new Callback() {
            @Override
            public void call(Object... args) throws RuntimeException {
                JsonArray jsonArgs = transform(args);
                channel.write(responseString(0, jsonArgs, new JsonObject(), new JsonArray()));
            }
        };
        instance.invoke(invocation, callback);
    }

    public void closeAllConnections() {
        for (Connection connection : connections) {
            connection.close();
        }
    }
}
