package dev.therealdan.realtimechess.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import dev.therealdan.realtimechess.screens.MainMenuScreen;

public class RealTimeChessApp extends Game {

    public Preferences preferences;
    public Settings settings;
    public FontManager font;
    public Textures textures;

    public ShapeRenderer shapeRenderer;
    public SpriteBatch batch;

    @Override
    public void create() {
        preferences = Gdx.app.getPreferences("realtimechess");
        settings = new Settings(preferences);
        font = new FontManager();
        textures = new Textures();

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0.2f, 0.1f, 1);
        batch.begin();
        super.render();
        batch.end();
    }

    @Override
    public void dispose() {
        font.dispose();

        shapeRenderer.dispose();
        batch.dispose();

        settings.save(preferences);
    }

    @Override
    public void resize(int width, int height) {
        shapeRenderer.dispose();
        batch.dispose();

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        getScreen().resize(width, height);
    }
}
