package dev.therealdan.realtimechess.network;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Disposable;

public interface DevicePeer extends Disposable {

    void send(Packet packet);

    Socket getSocket();

    default String getName() {
        return getType().toString().substring(0, 1) + getType().toString().substring(1).toLowerCase();
    }

    Type getType();

    enum Type {
        CLIENT, SERVER,
        OFFLINE
    }
}
