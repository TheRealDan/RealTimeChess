package dev.therealdan.realtimechess.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import dev.therealdan.realtimechess.main.RealTimeChessApp;

import java.util.HashMap;

public class Piece {

    private static HashMap<String, Texture> textures = new HashMap<>();

    private Type type;
    private Colour colour;
    private Position position;

    public Piece(Type type, Colour colour, Position position) {
        this.type = type;
        this.colour = colour;
        this.position = position;
    }

    public static void render(RealTimeChessApp app, float x, float y, float size, Type type, Color color) {
        float spacing = size * 0.1f;
        size -= spacing * 2f;
        app.batch.setColor(color);
        app.batch.draw(type.getTexture(), x + spacing, y + spacing, size, size);
    }

    public void render(RealTimeChessApp app, float x, float y, float cell) {
        render(app, x, y, cell, getColour().getColor());
    }

    public void render(RealTimeChessApp app, float x, float y, float cell, Color color) {
        float spacing = cell * 0.1f;
        render(app, x, y, cell, getType(), color);

        if (Gdx.input.isKeyPressed(Input.Keys.TAB))
            app.font.center(app.batch, getPosition().getNotation(), x + cell / 2f, y + spacing * 3f, (int) (10f * app.font.scale), getColour().opposite().getColor());
    }

    public boolean isStartPosition() {
        switch (getType()) {
            case PAWN:
                return getPosition().getNumber() == (getColour().equals(Colour.BLACK) ? 7 : 2);
        }
        return false;
    }

    public Type getType() {
        return type;
    }

    public Colour getColour() {
        return colour;
    }

    public Position getPosition() {
        return position;
    }

    public Piece copy() {
        return new Piece(type, colour, position.copy());
    }

    public enum Type {
        PAWN, ROOK, KNIGHT, BISHOP, KING, QUEEN;

        public String getNotation() {
            return toString().substring(0, 1);
        }

        public Texture getTexture() {
            if (!textures.containsKey(toString()))
                textures.put(toString(), new Texture("images/pieces/" + toString().toLowerCase() + ".png"));
            return textures.get(toString());
        }
    }

    public enum Colour {
        BLACK, WHITE;

        public Colour opposite() {
            return equals(BLACK) ? WHITE : BLACK;
        }

        public Color getColor() {
            return equals(BLACK) ? Color.BLACK : Color.WHITE;
        }

        public Texture getTexture() {
            if (!textures.containsKey(toString()))
                textures.put(toString(), new Texture("images/board/" + toString().toLowerCase() + ".png"));
            return textures.get(toString());
        }
    }
}
