package dev.therealdan.realtimechess.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dev.therealdan.realtimechess.screens.MainMenuScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class RealTimeChessApp extends Game {

    public FontManager font;

    public ShapeRenderer shapeRenderer;
    public SpriteBatch batch;

    @Override
    public void create() {
        font = new FontManager();

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        font.dispose();

        shapeRenderer.dispose();
        batch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        getScreen().resize(width, height);
    }
}
