package dev.therealdan.realtimechess.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import dev.therealdan.realtimechess.game.Piece;

import java.util.HashMap;
import java.util.HashSet;

public class Settings {

    private HashSet<Setting> toggles = new HashSet<>();
    private HashMap<Setting, String> strings = new HashMap<>();

    private Setting hovering;
    private Setting editing;

    public Settings(Preferences preferences) {
        setToggle(Setting.PREFERENCE, preferences.getBoolean("settings.preference", true));
        setNumber(Setting.PORT, preferences.getLong("settings.port", 42000));
        setString(Setting.IP_ADDRESS, preferences.getString("settings.address", ""));
    }

    public void render(RealTimeChessApp app, float oy, float oheight) {
        float ospacing = Gdx.graphics.getHeight() / 25f, spacing = ospacing;
        float owidth = Gdx.graphics.getWidth() * 0.4f, width = owidth;
        float height = Math.min(oheight / Setting.values().length - spacing, 80f);
        float theight = (height + spacing) * Setting.values().length;
        float x = -width / 2f;
        float y = oy - oheight / 2f + theight / 2f - height;

        hovering = null;
        for (Setting setting : Setting.values()) {
            if (!setting.isSetting()) continue;
            switch (setting.getType()) {
                default:
                    app.batch.setColor(Color.WHITE);
                    app.batch.draw(app.textures.firebrick, x, y, width, height);
                    app.font.center(app.batch, setting.getName(), x + width / 2f, y + height / 2f, (int) (16f * app.font.scale), Color.WHITE);
                    break;
                case TOGGLE:
                    if (Mouse.containsMouse(x, y, width, height)) hovering = setting;
                    app.batch.setColor(Color.WHITE);
                    app.batch.draw(Mouse.containsMouse(x, y, width, height) ? app.textures.brown : app.textures.firebrick, x, y, width, height);
                    app.font.center(app.batch, setting.getName(), x + width / 2f, y + height / 2f, (int) (16f * app.font.scale), Color.WHITE);
                    Piece.render(app, x + width - height, y, height, Piece.Type.PAWN, (getToggle(setting) ? Piece.Colour.WHITE : Piece.Colour.BLACK).getColor());
                    break;
                case STRING:
                case NUMBER:
                    if (Mouse.containsMouse(x, y, width, height)) hovering = setting;
                    app.batch.setColor(Color.WHITE);
                    app.batch.draw(Mouse.containsMouse(x, y, width, height) ? app.textures.brown : app.textures.firebrick, x, y, width, height);
                    app.font.draw(app.batch, setting.getName(), x + height / 2f, y + height / 2f, (int) (16f * app.font.scale), Color.WHITE);
                    spacing = 5f;
                    width = owidth - height - app.font.getWidth(app.batch, setting.getName(), (int) (16f * app.font.scale));
                    app.batch.draw(app.textures.white, x + (owidth - width), y + spacing, width - spacing, height - spacing * 2f);
                    app.font.center(app.batch, getString(setting) + (setting.equals(editing) && System.currentTimeMillis() % 1500 > 750 ? "|" : ""), x + (owidth - width) + width / 2f, y + height / 2f, (int) (16f * app.font.scale), Color.BLACK);
                    spacing = ospacing;
                    width = owidth;
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

    public boolean keyDown(int i) {
        switch (i) {
            case 111:
                if (editing != null) {
                    editing = null;
                    break;
                }
                return true;
            case 66:
                editing = null;
                break;
        }

        if (editing != null) {
            String key = Input.Keys.toString(i);
            if (Input.Keys.BACKSPACE == i) {
                if (!getString(editing).isEmpty())
                    setString(editing, getString(editing).substring(0, getString(editing).length() - 1));
                return false;
            }
            if (editing.getType().equals(Type.NUMBER) && !"1234567890".contains(key)) return false;
            setString(editing, getString(editing) + key);
        }

        return false;
    }

    public boolean touchDown() {
        editing = null;
        if (hovering == null) return false;

        switch (hovering.getType()) {
            case TOGGLE:
                setToggle(hovering, !getToggle(hovering));
                break;
            case STRING:
            case NUMBER:
                editing = hovering;
                break;
            case BACK:
                return true;

        }
        return false;
    }

    public void save(Preferences preferences) {
        preferences.putBoolean("settings.preference", getToggle(Setting.PREFERENCE));
        preferences.putLong("settings.port", getNumber(Setting.PORT));
        preferences.putString("settings.address", getString(Setting.IP_ADDRESS));
        preferences.flush();
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

    public void setString(Setting setting, String value) {
        strings.put(setting, value);
    }

    public String getString(Setting setting) {
        return strings.get(setting);
    }

    private void setNumber(Setting setting, long value) {
        strings.put(setting, Long.toString(value));
    }

    public long getNumber(Setting setting) {
        return Long.parseLong(strings.get(setting));
    }

    public enum Setting {
        PREFERENCE, PORT, IP_ADDRESS,
        BACK;

        public Type getType() {
            switch (this) {
                default:
                    return Type.BACK;
                case PREFERENCE:
                    return Type.TOGGLE;
                case PORT:
                    return Type.NUMBER;
                case IP_ADDRESS:
                    return Type.STRING;
            }
        }

        public String getName() {
            return toString().replace("_", " ");
        }

        public boolean isSetting() {
            switch (this) {
                default:
                    return true;
                case IP_ADDRESS:
                    return false;
            }
        }
    }

    public enum Type {
        TOGGLE, STRING, NUMBER,
        BACK;
    }
}
