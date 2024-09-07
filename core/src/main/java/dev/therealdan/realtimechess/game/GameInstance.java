package dev.therealdan.realtimechess.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import dev.therealdan.realtimechess.main.Mouse;
import dev.therealdan.realtimechess.main.RealTimeChessApp;

public class GameInstance {

    private static Texture navy = new Texture("images/navy.png");
    private static Texture white = new Texture("images/white.png");

    private Board board;
    private Bot bot;
    private Piece.Colour colour;

    private Piece.Type promotion = Piece.Type.QUEEN;

    public GameInstance(Bot.Difficulty difficulty) {
        board = Board.standardBoard();
        bot = new Bot(difficulty, Piece.Colour.BLACK);
        colour = Piece.Colour.WHITE;
    }

    public void render(RealTimeChessApp app) {
        float width = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.8f, height = width;
        float x = -width / 2f;
        float y = -height / 2f;
        getBoard().render(app, x, y, width, height);
        getBot().think(getBoard());

        Piece piece = getBoard().getPromoting();
        if (piece != null && piece.getColour().equals(getColour())) {
            float cell = width / 8;
            x += (piece.getPosition().getX() - 1) * cell;
            y += (piece.getPosition().getY() - 1) * cell;
            app.batch.setColor(getColour().getColor());
            app.batch.draw(white, x, y - cell * 3f, cell, cell * 4);

            Piece.Type[] types = {Piece.Type.QUEEN, Piece.Type.KNIGHT, Piece.Type.ROOK, Piece.Type.BISHOP};
            for (Piece.Type type : types) {
                if (Mouse.containsMouse(x, y, cell, cell) || type.equals(promotion)) {
                    promotion = type;
                    app.batch.setColor(Color.WHITE);
                    app.batch.draw(navy, x, y, cell, cell);
                }
                Piece.render(app, x, y, cell, type, type.equals(promotion) ? getColour().getColor() : Color.NAVY);
                y -= cell;
            }
        }
    }

    public Board getBoard() {
        return board;
    }

    public Bot getBot() {
        return bot;
    }

    public Piece.Colour getColour() {
        return colour;
    }

    public Piece.Type getPromotion() {
        return promotion;
    }
}
