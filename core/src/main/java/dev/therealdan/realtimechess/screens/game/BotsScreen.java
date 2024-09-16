package dev.therealdan.realtimechess.screens.game;

import dev.therealdan.realtimechess.game.Board;
import dev.therealdan.realtimechess.game.Bot;
import dev.therealdan.realtimechess.game.Piece;
import dev.therealdan.realtimechess.main.RealTimeChessApp;
import dev.therealdan.realtimechess.main.Settings;
import dev.therealdan.realtimechess.screens.GameScreen;

public class BotsScreen extends GameScreen {

    private Bot bot;

    public BotsScreen(RealTimeChessApp app, Bot.Difficulty difficulty) {
        super(app);

        colour = app.settings.getToggle(Settings.Setting.PREFERENCE) ? Piece.Colour.WHITE : Piece.Colour.BLACK;
        board = Board.standardBoard();
        bot = new Bot(difficulty, getColour().opposite());
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        getBot().think(getBoard());
    }

    public Bot getBot() {
        return bot;
    }

    @Override
    public boolean canMove(Piece piece) {
        if (piece == null) return false;
        if (getBot().getDifficulty().equals(Bot.Difficulty.BRAINLESS)) {
            piece = piece.copy();
            piece.setColour(getColour());
            return super.canMove(piece);
        }
        return super.canMove(piece);
    }
}
