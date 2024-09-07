package dev.therealdan.realtimechess.game;

import com.badlogic.gdx.Gdx;
import dev.therealdan.realtimechess.main.RealTimeChessApp;

public class GameInstance {

    private Board board;
    private Bot bot;
    private Piece.Colour colour;

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
}
