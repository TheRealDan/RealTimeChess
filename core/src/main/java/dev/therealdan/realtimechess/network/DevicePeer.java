package dev.therealdan.realtimechess.network;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Disposable;

public interface DevicePeer extends Disposable {

    void send(Packet packet);

    Socket getSocket();

    String getName();
}
