package dev.therealdan.realtimechess.screens.game;

import com.badlogic.gdx.Gdx;
import dev.therealdan.realtimechess.game.Board;
import dev.therealdan.realtimechess.game.Piece;
import dev.therealdan.realtimechess.main.RealTimeChessApp;
import dev.therealdan.realtimechess.main.Settings;
import dev.therealdan.realtimechess.network.Client;
import dev.therealdan.realtimechess.network.DevicePeer;
import dev.therealdan.realtimechess.network.packets.AssignmentPacket;
import dev.therealdan.realtimechess.network.packets.BoardPacket;
import dev.therealdan.realtimechess.screens.GameScreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientScreen extends GameScreen {

    private Client client;

    public ClientScreen(RealTimeChessApp app) {
        super(app);

        client = new Client(app.settings.getString(Settings.Setting.IP_ADDRESS), (int) app.settings.getNumber(Settings.Setting.PORT));

        Piece.Colour preference = app.settings.getToggle(Settings.Setting.PREFERENCE) ? Piece.Colour.WHITE : Piece.Colour.BLACK;
        connectToServer(preference);
    }

    private void connectToServer(Piece.Colour preference) {
        new Thread(() -> {
            while (true) {
                try {
                    incoming(new BufferedReader(new InputStreamReader(getClient().getInputStream())).readLine());
                } catch (IOException e) {
                    Gdx.app.log(getDevicePeer().getName(), "Error", e);
                }
            }
        }).start();
        new Thread(() -> {
            long checkInterval = 200;
            long lastCheck = System.currentTimeMillis() - checkInterval;
            while (getColour() == null || board == null) {
                if (System.currentTimeMillis() - lastCheck < checkInterval) continue;
                lastCheck = System.currentTimeMillis();
                getDevicePeer().send(new AssignmentPacket(preference));
                getDevicePeer().send(new BoardPacket(new Board()));
            }
        }).start();
    }

    @Override
    public void dispose() {
        getClient().dispose();
    }

    public Client getClient() {
        return client;
    }

    @Override
    public boolean hasGameStarted() {
        return getClient().isConnected() && getColour() != null;
    }

    @Override
    public DevicePeer getDevicePeer() {
        return getClient();
    }
}
