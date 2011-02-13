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

    public void emit(String event, Object... args) {
        Callback callback = callbacks.get(event);
        if (callback != null) { // TODO: Loop over a list
            callback.call(args);
        }
    }

    private JsonElement methods() {
        JsonArray arguments = new JsonArray();
        arguments.add(instance.getSignature());
        return response("methods", arguments, instance.getCallbacks(), new JsonArray());
    }

    private JsonElement response(String method, JsonArray arguments, JsonElement callbacks, JsonArray links) {
        return response(new JsonPrimitive(method), arguments, callbacks, links);
    }

    public JsonElement response(int method, JsonArray arguments, JsonElement callbacks, JsonArray links) {
        return response(new JsonPrimitive(method), arguments, callbacks, links);
    }

    private JsonElement response(JsonElement method, JsonArray arguments, JsonElement callbacks, JsonArray links) {
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
            instance.invoke(this, json, connection);
        }
    }

    private void handleMethods(JsonObject methods) {

    }

    public void closeAllConnections() {
        for (Connection connection : connections) {
            connection.close();
        }
    }
}
