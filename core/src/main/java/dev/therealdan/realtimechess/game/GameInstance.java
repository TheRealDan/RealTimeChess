package dev.therealdan.realtimechess.game;

import com.badlogic.gdx.Gdx;
import dev.therealdan.realtimechess.main.RealTimeChessApp;

public class GameInstance {

    private Board board;

    public GameInstance() {
        board = Board.standardBoard();
    }

    public void render(RealTimeChessApp app) {
        float width = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.8f, height = width;
        float x = -width / 2f;
        float y = -height / 2f;
        getBoard().render(app, x, y, width, height);
    }

    public Board getBoard() {
        return board;
    }
}
