package dnode.socketio;

import com.google.gson.JsonElement;

public interface SocketIOListener {
    void onClientMessage(JsonElement json, SocketIOWebSocketHandler socketIOWebSocketHandler);

    void onClientMessage(String message, SocketIOWebSocketHandler socketIOWebSocketHandler);
}
