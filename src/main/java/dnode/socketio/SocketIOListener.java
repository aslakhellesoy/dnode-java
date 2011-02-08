package dnode.socketio;

import com.google.gson.JsonElement;

public interface SocketIOListener {
    void onClientMessage(JsonElement json, SocketIOHandler socketIOHandler);

    void onClientMessage(String message, SocketIOHandler socketIOHandler);
}
