package dev.therealdan.realtimechess.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

import java.io.IOException;

public class Server implements DevicePeer {

    private ServerSocket serverSocket;
    private Socket socket;

    public Server(int port) {
        ServerSocketHints hints = new ServerSocketHints();
        hints.acceptTimeout = 0;
        this.serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, port, hints);
    }

    public void accept() {
        socket = serverSocket.accept(new SocketHints());
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

    @Override
    public void dispose() {
        serverSocket.dispose();
        socket.dispose();
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public Type getType() {
        return Type.SERVER;
    }
}
