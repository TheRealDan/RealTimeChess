package dev.therealdan.realtimechess.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Client implements DevicePeer {

    private Socket socket;

    public Client(String host, int port) {
        this.socket = Gdx.net.newClientSocket(Net.Protocol.TCP, host, port, new SocketHints());
    }

    @Override
    public void send(Packet packet) {
        if (getSocket() == null) return;

        try {
            getSocket().getOutputStream().write(packet.getBytes());
        } catch (IOException e) {
            Gdx.app.error(getName(), "Error", e);
        }
    }

    public InputStream getInputStream() {
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() {
        return socket.getOutputStream();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    @Override
    public void dispose() {
        socket.dispose();
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public Type getType() {
        return Type.CLIENT;
    }
}
