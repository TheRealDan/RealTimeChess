package dev.therealdan.realtimechess.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dev.therealdan.realtimechess.game.Bot;
import dev.therealdan.realtimechess.main.Mouse;
import dev.therealdan.realtimechess.main.RealTimeChessApp;

public class MainMenuScreen implements Screen, InputProcessor {

    final RealTimeChessApp app;

    private ScreenViewport viewport;
    private OrthographicCamera camera;

    private Bot.Difficulty difficulty = null;

    public MainMenuScreen(RealTimeChessApp app) {
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
        ScreenUtils.clear(0, 0.2f, 0.1f, 1);

        camera.update();
        app.shapeRenderer.setProjectionMatrix(camera.combined);
        app.batch.setProjectionMatrix(camera.combined);

        app.batch.begin();
        app.font.center(app.batch, "Real Time Chess", 0, Gdx.graphics.getHeight() / 2f - Gdx.graphics.getHeight() * 0.1f, (int) (40f * app.font.scale), Color.WHITE);
        app.batch.end();

        float spacing = Gdx.graphics.getHeight() / 25f;
        float width = Gdx.graphics.getWidth() * 0.4f;
        float height = ((Gdx.graphics.getHeight() * 0.8f - spacing * Bot.Difficulty.values().length) / Bot.Difficulty.values().length) - spacing;
        float x = -width / 2f;
        float y = Gdx.graphics.getHeight() * 0.3f - height - spacing;

        this.difficulty = null;
        for (Bot.Difficulty difficulty : Bot.Difficulty.values()) {
            boolean hovering = Mouse.containsMouse(x, y, width, height);
            if (hovering)
                this.difficulty = difficulty;

            app.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            app.shapeRenderer.setColor(hovering ? Color.BROWN : Color.FIREBRICK);
            app.shapeRenderer.rect(x, y, width, height);
            app.shapeRenderer.end();

            app.batch.begin();
            app.font.center(app.batch, difficulty.toString(), x + width / 2f, y + height * 0.6f, (int) (16f * app.font.scale), Color.WHITE);
            app.batch.end();

            y -= height + spacing;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        app.font.scale = Gdx.graphics.getWidth() / 1000f;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int i) {
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
        if (difficulty != null) {
            app.setScreen(new GameScreen(app, difficulty));
        }
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
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
