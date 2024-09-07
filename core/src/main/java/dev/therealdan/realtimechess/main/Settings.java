package dev.therealdan.realtimechess.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import dev.therealdan.realtimechess.game.Piece;

import java.util.HashSet;

public class Settings {

    private HashSet<Setting> toggles = new HashSet<>();

    private Setting hovering;

    public Settings(Preferences preferences) {
        setToggle(Setting.PREFERENCE, preferences.getBoolean("settings.preference", true));
    }

    public void render(RealTimeChessApp app, float oy, float oheight) {
        float spacing = Gdx.graphics.getHeight() / 25f;
        float width = Gdx.graphics.getWidth() * 0.4f;
        float height = Math.min(oheight / Setting.values().length - spacing, 80f);
        float theight = (height + spacing) * Setting.values().length;
        float x = -width / 2f;
        float y = oy - oheight / 2f + theight / 2f - height;

        hovering = null;
        for (Setting setting : Setting.values()) {
            switch (setting.getType()) {
                default:
                    app.batch.setColor(Color.WHITE);
                    app.batch.draw(Mouse.containsMouse(x, y, width, height) ? app.textures.brown : app.textures.firebrick, x, y, width, height);
                    app.font.center(app.batch, setting.getName(), x + width / 2f, y + height / 2f, (int) (16f * app.font.scale), Color.WHITE);
                    break;
                case TOGGLE:
                    if (Mouse.containsMouse(x, y, width, height)) hovering = setting;
                    app.batch.setColor(Color.WHITE);
                    app.batch.draw(Mouse.containsMouse(x, y, width, height) ? app.textures.brown : app.textures.firebrick, x, y, width, height);
                    app.font.center(app.batch, setting.getName(), x + width / 2f, y + height / 2f, (int) (16f * app.font.scale), Color.WHITE);
                    Piece.render(app, x + width - height, y, height, Piece.Type.PAWN, (getToggle(setting) ? Piece.Colour.WHITE : Piece.Colour.BLACK).getColor());
                    break;
                case BACK:
                    if (Mouse.containsMouse(x, y, width, height)) hovering = setting;
                    app.batch.setColor(Color.WHITE);
                    app.batch.draw(Mouse.containsMouse(x, y, width, height) ? app.textures.brown : app.textures.firebrick, x, y, width, height);
                    app.font.center(app.batch, setting.getName(), x + width / 2f, y + height / 2f, (int) (16f * app.font.scale), Color.WHITE);
                    break;

            }

            y -= height + spacing;
        }
    }

    public boolean touchDown() {
        if (hovering == null) return false;

        switch (hovering.getType()) {
            case TOGGLE:
                setToggle(hovering, !getToggle(hovering));
                break;
            case BACK:
                return true;

        }
        return false;
    }

    public void save(Preferences preferences) {
        preferences.putBoolean("settings.preference", getToggle(Setting.PREFERENCE));
    }

    private void setToggle(Setting setting, boolean enabled) {
        if (enabled) {
            toggles.add(setting);
        } else {
            toggles.remove(setting);
        }
    }

    public boolean getToggle(Setting setting) {
        return toggles.contains(setting);
    }

    public enum Setting {
        PREFERENCE,
        BACK;

        public Type getType() {
            switch (this) {
                default:
                    return Type.BACK;
                case PREFERENCE:
                    return Type.TOGGLE;
            }
        }

        public String getName() {
            return toString();
        }
    }

    public enum Type {
        TOGGLE,
        BACK;
    }
}
