package dev.therealdan.realtimechess.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dev.therealdan.realtimechess.game.*;
import dev.therealdan.realtimechess.main.RealTimeChessApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GameScreen implements Screen, InputProcessor {

    final RealTimeChessApp app;

    private ScreenViewport viewport;
    private OrthographicCamera camera;

    private GameInstance instance;

    public GameScreen(RealTimeChessApp app, Bot.Difficulty difficulty, Piece.Colour colour) {
        this(app);
        instance = new GameInstance(difficulty, null, null, colour);
    }

    public GameScreen(RealTimeChessApp app, int port, Piece.Colour colour) {
        this(app);
        ServerSocketHints hints = new ServerSocketHints();
        hints.acceptTimeout = 0;
        ServerSocket server = Gdx.net.newServerSocket(Net.Protocol.TCP, port, hints);
        instance = new GameInstance(null, server, null, colour);
    }

    public GameScreen(RealTimeChessApp app, String host, int port) {
        this(app);
        Socket client = Gdx.net.newClientSocket(Net.Protocol.TCP, host, port, new SocketHints());
        Piece.Colour colour;
        try {
            String incoming = new BufferedReader(new InputStreamReader(client.getInputStream())).readLine();
            colour = Piece.Colour.valueOf(incoming);
        } catch (IOException e) {
            Gdx.app.log("Client", "Error", e);
            return;
        }
        ;
        instance = new GameInstance(null, null, client, colour);
    }

    public GameScreen(RealTimeChessApp app) {
        this.app = app;
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        camera.update();
        app.shapeRenderer.setProjectionMatrix(camera.combined);
        app.batch.setProjectionMatrix(camera.combined);

        if (instance.getBot() != null) instance.getBot().think(instance.getBoard());
        instance.render(app);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        app.font.scale = Gdx.graphics.getWidth() / 1000f;
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        dispose();
    }

    @Override
    public void dispose() {
        if (instance.getServer() != null) instance.getServer().dispose();
        if (instance.getClient() != null) instance.getClient().dispose();
        if (instance.getConnected() != null) instance.getConnected().dispose();
    }

    @Override
    public boolean keyDown(int i) {
        switch (i) {
            case 111:
                app.setScreen(new MainMenuScreen(app));
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        if (instance.getBoard().getPromoting() != null && instance.getBoard().getPromoting().getColour().equals(instance.getColour())) {
            Notation notation = new Notation(instance.getBoard().getPromoting(), instance.getPromotion());
            instance.getBoard().promote(instance.getBoard().getPromoting(), instance.getPromotion());
            if (instance.getClient() != null || instance.getConnected() != null) {
                Socket socket = instance.getClient() != null ? instance.getClient() : instance.getConnected();
                try {
                    socket.getOutputStream().write((notation.getNotation() + "\n").getBytes());
                } catch (IOException e) {
                    Gdx.app.log(instance.getClient() != null ? "Client" : "Server", "Error", e);
                }
            }
            return false;
        }

        if (instance.getBoard().getHovering() != null) {
            Piece piece = instance.getBoard().byPosition(instance.getBoard().getHovering());
            if (piece != null && (piece.getColour().equals(instance.getColour()) || (instance.getBot() != null && instance.getBot().getDifficulty().equals(Bot.Difficulty.BRAINLESS)))) {
                if (piece.isOnCooldown()) return false;
                if (instance.getBoard().isChecked(instance.getColour()) && instance.getBoard().getPossibleMoves(piece).isEmpty()) return false;
                instance.getBoard().select(piece);
                instance.getBoard().setHolding(true);
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        if (instance.getBoard().isHolding()) {
            Piece piece = instance.getBoard().getSelected();
            Position position = instance.getBoard().getHovering();
            Notation notation = new Notation(piece, position);
            if (instance.getBoard().moveTo(piece, position)) {
                if (instance.getClient() != null || instance.getConnected() != null) {
                    Socket socket = instance.getClient() != null ? instance.getClient() : instance.getConnected();
                    try {
                        socket.getOutputStream().write((notation.getNotation() + "\n").getBytes());
                    } catch (IOException e) {
                        Gdx.app.log(instance.getClient() != null ? "Client" : "Server", "Error", e);
                    }
                }
            }
        }
        instance.getBoard().setHolding(false);
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}
