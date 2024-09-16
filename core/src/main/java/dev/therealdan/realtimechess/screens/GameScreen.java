package dev.therealdan.realtimechess.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dev.therealdan.realtimechess.game.Bot;
import dev.therealdan.realtimechess.game.GameInstance;
import dev.therealdan.realtimechess.game.Piece;
import dev.therealdan.realtimechess.game.Position;
import dev.therealdan.realtimechess.main.RealTimeChessApp;
import dev.therealdan.realtimechess.network.Client;
import dev.therealdan.realtimechess.network.Server;
import dev.therealdan.realtimechess.network.packets.PromotionPacket;

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
        instance = new GameInstance(null, new Server(port), null, colour);
    }

    public GameScreen(RealTimeChessApp app, String host, int port, Piece.Colour preference) {
        this(app);
        instance = new GameInstance(null, null, new Client(host, port), preference);
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
        if (instance.hasGameStarted()) {
            if (instance.getBoard().getPromoting() != null && instance.getBoard().getPromoting().getColour().equals(instance.getColour())) {
                if (instance.getDevicePeer() != null)
                    instance.getDevicePeer().send(new PromotionPacket(instance.getBoard().getPromoting().getPosition(), instance.getPromotion()));
                instance.getBoard().promote(instance.getBoard().getPromoting(), instance.getPromotion());
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
        }
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        if (instance.getBoard().isHolding()) {
            Piece piece = instance.getBoard().getSelected();
            Position position = instance.getBoard().getHovering();
            instance.moveTo(piece, position);
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
