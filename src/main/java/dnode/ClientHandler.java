package dnode;

public interface ClientHandler<T> {
    void onConnect(T client);
}
