package dev.therealdan.realtimechess.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dev.therealdan.realtimechess.game.Bot;
import dev.therealdan.realtimechess.game.Piece;
import dev.therealdan.realtimechess.main.Mouse;
import dev.therealdan.realtimechess.main.RealTimeChessApp;
import dev.therealdan.realtimechess.main.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainMenuScreen implements Screen, InputProcessor {

    final RealTimeChessApp app;

    private ScreenViewport viewport;
    private OrthographicCamera camera;

    private Option hovering = null;
    private Option menu = Option.MAIN;
    private Bot.Difficulty previousDifficulty = null;
    private Bot.Difficulty difficulty = null;
    private float textureXOffset = 0;
    private String host = "";
    private boolean editHost = false;

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
        camera.update();
        app.shapeRenderer.setProjectionMatrix(camera.combined);
        app.batch.setProjectionMatrix(camera.combined);

        float oheight = Gdx.graphics.getHeight() * 0.8f;
        float height = Gdx.graphics.getHeight() - oheight;
        float y = Gdx.graphics.getHeight() / 2f;

        app.font.center(app.batch, (menu != null ? menu : Option.MAIN).getTitle(), 0, y - height / 2f, (int) (40f * app.font.scale), Color.WHITE);

        y -= height;

        switch (menu) {
            default:
                renderMenu(y, oheight);
                break;
            case BOTS:
                renderBots(delta, y, oheight);
                break;
            case SETTINGS:
                app.settings.render(app, y, oheight);
                break;
        }
    }

    private void renderMenu(float oy, float oheight) {
        List<Option> options = menu.getOptions();

        float ospacing = Gdx.graphics.getHeight() / 25f, spacing = ospacing;
        float owidth = Gdx.graphics.getWidth() * 0.4f, width = owidth;
        float height = Math.min(oheight / options.size() - spacing, 80f);
        float theight = (height + spacing) * options.size();
        float x = -width / 2f;
        float y = oy - oheight / 2f + theight / 2f - height;

        hovering = null;
        for (Option option : options) {
            if (Mouse.containsMouse(x, y, width, height)) hovering = option;
            app.batch.setColor(Color.WHITE);
            app.batch.draw(option.equals(hovering) ? app.textures.brown : app.textures.firebrick, x, y, width, height);

            if (option.equals(Option.HOSTNAME)) {
                app.font.draw(app.batch, "IP ADDRESS", x + height / 2f, y + height / 2f, (int) (16f * app.font.scale), Color.WHITE);
                spacing = 5f;
                width = owidth - height - app.font.getWidth(app.batch, "IP ADDRESS", (int) (16f * app.font.scale));
                app.batch.draw(app.textures.white, x + (owidth - width), y + spacing, width - spacing, height - spacing * 2f);
                app.font.center(app.batch, host + (editHost && System.currentTimeMillis() % 1500 > 750 ? "|" : ""), x + (owidth - width) + width / 2f, y + height / 2f, (int) (16f * app.font.scale), Color.BLACK);
                spacing = ospacing;
                width = owidth;
            } else {
                app.font.center(app.batch, option.getName(), x + width / 2f, y + height / 2f, (int) (16f * app.font.scale), Color.WHITE);
            }
            y -= height + spacing;
        }
    }

    private void renderBots(float delta, float oy, float oheight) {
        if (difficulty == null) difficulty = Bot.Difficulty.values()[0];
        if (previousDifficulty == null) previousDifficulty = difficulty;
        hovering = null;

        float spacing = Gdx.graphics.getHeight() / 25f;
        float height = oheight * 0.8f;
        float width = Math.min((Gdx.graphics.getWidth() - spacing * 3f) / 2f, height);
        height = width;

        float buttonWidth = width * 0.4f;
        float buttonHeight = Math.min(buttonWidth / 2f, 80f);

        float x = -spacing / 2f - width;
        float y = oy - height;

        if (difficulty != previousDifficulty) {
            textureXOffset += delta * width * 3f;
            if (textureXOffset >= width)
                previousDifficulty = difficulty;
        } else if (textureXOffset > 0) {
            textureXOffset -= delta * width * 3f;
        }

        app.batch.setColor(Color.WHITE);
        app.batch.draw(previousDifficulty.getTexture(), x + textureXOffset, y, width, height);
        x += width + spacing;
        app.batch.draw(app.textures.black, x, y, width, height);

        float ox = x;
        float buttonX = ox + width / 2f - buttonWidth / 2f;
        float buttonY = y + spacing;
        int buttonFontSize = (int) (12f * app.font.scale);

        x += spacing / 2f;
        y += height - spacing / 2f;
        app.font.draw(app.batch, difficulty.getName() + " " + difficulty.getDifficulty(), x, y, (int) (20f * app.font.scale), Color.WHITE);
        y -= spacing * 2f;
        app.font.draw(app.batch, difficulty.getDescription(), x, y, width - spacing, (int) (12f * app.font.scale), Color.WHITE);

        if (Mouse.containsMouse(buttonX, buttonY, buttonWidth, buttonHeight)) hovering = Option.PLAY;
        app.batch.draw(Mouse.containsMouse(buttonX, buttonY, buttonWidth, buttonHeight) ? app.textures.brown : app.textures.firebrick, buttonX, buttonY, buttonWidth, buttonHeight);
        app.font.center(app.batch, Option.PLAY.getName(), buttonX + buttonWidth / 2f, buttonY + buttonHeight / 2f, buttonFontSize, Color.WHITE);
        buttonWidth /= 2f;
        buttonX = ox + spacing;
        if (Mouse.containsMouse(buttonX, buttonY, buttonWidth, buttonHeight)) hovering = Option.PREVIOUS;
        app.batch.draw(Mouse.containsMouse(buttonX, buttonY, buttonWidth, buttonHeight) ? app.textures.brown : app.textures.firebrick, buttonX, buttonY, buttonWidth, buttonHeight);
        app.font.center(app.batch, Option.PREVIOUS.getName(), buttonX + buttonWidth / 2f, buttonY + buttonHeight / 2f, buttonFontSize, Color.WHITE);
        buttonX = ox + width - spacing - buttonWidth;
        if (Mouse.containsMouse(buttonX, buttonY, buttonWidth, buttonHeight)) hovering = Option.NEXT;
        app.batch.draw(Mouse.containsMouse(buttonX, buttonY, buttonWidth, buttonHeight) ? app.textures.brown : app.textures.firebrick, buttonX, buttonY, buttonWidth, buttonHeight);
        app.font.center(app.batch, Option.NEXT.getName(), buttonX + buttonWidth / 2f, buttonY + buttonHeight / 2f, buttonFontSize, Color.WHITE);
    }

    private void next() {
        if (Option.BOTS.equals(menu))
            difficulty = difficulty.equals(Bot.Difficulty.values()[Bot.Difficulty.values().length - 1]) ? Bot.Difficulty.values()[0] : Bot.Difficulty.values()[Arrays.stream(Bot.Difficulty.values()).collect(Collectors.toList()).indexOf(difficulty) + 1];
    }

    private void previous() {
        if (Option.BOTS.equals(menu))
            difficulty = difficulty.equals(Bot.Difficulty.values()[0]) ? Bot.Difficulty.values()[Bot.Difficulty.values().length - 1] : Bot.Difficulty.values()[Arrays.stream(Bot.Difficulty.values()).collect(Collectors.toList()).indexOf(difficulty) - 1];
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
        if (Option.SETTINGS.equals(menu)) {
            if (app.settings.keyDown(i)) {
                menu = Option.MAIN;
            }
            return false;
        } else if (Option.JOIN.equals(menu)) {
            switch (i) {
                default:
                    String key = Input.Keys.toString(i);
                    if (!"1234567890.".contains(key)) return false;
                    host += key;
                    return false;
                case 111:
                    if (editHost) {
                        editHost = false;
                        return false;
                    }
                    menu = Option.ONLINE;
                    return false;
                case 66:
                    editHost = false;
                    return false;
                case 67:
                    if (!host.isEmpty())
                        host = host.substring(0, host.length() - 1);
                    return false;
            }
        }

        switch (i) {
            case 111:
                hovering = null;
                menu = Option.MAIN;
                previousDifficulty = null;
                difficulty = null;
                break;
            case 32:
            case 22:
                next();
                break;
            case 29:
            case 21:
                previous();
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
        editHost = false;
        if (Option.SETTINGS.equals(menu)) {
            if (app.settings.touchDown()) {
                menu = Option.MAIN;
            }
            return false;
        }

        if (hovering != null) {
            switch (hovering) {
                default:
                    menu = hovering;
                    return false;
                case BACK:
                    menu = menu.getPrevious();
                    return false;
                case PLAY:
                    app.setScreen(new GameScreen(app, difficulty, app.settings.getToggle(Settings.Setting.PREFERENCE) ? Piece.Colour.WHITE : Piece.Colour.BLACK));
                    return false;
                case HOST:
                    app.setScreen(new GameScreen(app, (int) app.settings.getNumber(Settings.Setting.PORT), app.settings.getToggle(Settings.Setting.PREFERENCE) ? Piece.Colour.WHITE : Piece.Colour.BLACK));
                    return false;
                case HOSTNAME:
                    editHost = true;
                    return false;
                case JOIN_START:
                    app.setScreen(new GameScreen(app, host, (int) app.settings.getNumber(Settings.Setting.PORT)));
                    return false;
                case PREVIOUS:
                    previous();
                    return false;
                case NEXT:
                    next();
                    return false;
                case QUIT:
                    Gdx.app.exit();
                    return false;
            }
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

    public enum Option {
        MAIN,
        BOTS, ONLINE, SETTINGS, QUIT,
        BACK, PLAY, PREVIOUS, NEXT,
        HOSTNAME, HOST, JOIN, JOIN_START;

        public String getName() {
            switch (this) {
                default:
                    return toString();
                case BOTS:
                    return "VERSUS BOTS";
                case ONLINE:
                    return "MULTIPLAYER";
                case JOIN_START:
                    return "JOIN";
            }
        }

        public String getTitle() {
            switch (this) {
                default:
                    return "Real Time Chess";
                case BOTS:
                    return "Choose your opponent";
                case ONLINE:
                    return "Online Multiplayer";
                case JOIN:
                    return "Enter host IP address to connect to";
                case SETTINGS:
                    return "Settings";
            }
        }

        public Option getPrevious() {
            switch (this) {
                default:
                    return MAIN;
                case JOIN:
                    return ONLINE;
            }
        }

        public List<Option> getOptions() {
            switch (this) {
                default:
                    return new ArrayList<>();
                case MAIN:
                    return Arrays.stream(new Option[]{BOTS, ONLINE, SETTINGS, QUIT}).collect(Collectors.toList());
                case ONLINE:
                    return Arrays.stream(new Option[]{HOST, JOIN, BACK}).collect(Collectors.toList());
                case JOIN:
                    return Arrays.stream(new Option[]{HOSTNAME, JOIN_START, BACK}).collect(Collectors.toList());
            }
        }
    }
}
