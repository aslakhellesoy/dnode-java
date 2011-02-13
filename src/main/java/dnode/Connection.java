package dnode;

import com.google.gson.JsonElement;

public interface Connection {
    void close();
    void write(JsonElement data);
}
