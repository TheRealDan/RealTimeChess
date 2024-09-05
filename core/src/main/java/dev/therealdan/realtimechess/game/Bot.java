package dev.therealdan.realtimechess.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Bot {

    private Random random = new Random();

    private Difficulty difficulty;
    private Piece.Colour colour;

    private long lastMove = System.currentTimeMillis();

    public Bot(Difficulty difficulty, Piece.Colour colour) {
        this.difficulty = difficulty;
        this.colour = colour;
    }

    public void think(Board board) {
        switch (getDifficulty()) {
            case LEMONS:
                if (getTimeSinceLastMove() < 3000) return;
                doAnyMove(board);
                break;
            case CAUTIOUS:
                if (getTimeSinceLastMove() < 2000) return;
                doAttackMove(board);
                if (getTimeSinceLastMove() < 5000) return;
                doSmartMove(board);
                break;
            case TACTFUL:
                if (getTimeSinceLastMove() < 3000) return;
                doAttackMove(board);
                if (random.nextBoolean()) doAttackMove(board);
                if (random.nextBoolean()) doAttackMove(board);
                doSmartMove(board);
                break;
            case RECKLESS:
                if (getTimeSinceLastMove() < 200) return;
                doAnyMove(board);
                break;
            case IMPOSSIBLE:
                if (getTimeSinceLastMove() < 400) return;
                doSmartMove(board);
                if (random.nextBoolean()) doSmartMove(board);
                if (random.nextBoolean()) doSmartMove(board);
                break;
        }
    }

    private boolean doSmartMove(Board board) {
        if (!doAttackMove(board))
            return doAnyMove(board);
        return false;
    }

    private boolean doAttackMove(Board board) {
        for (Piece piece : getRandomOrder(board, getColour())) {
            for (Position move : board.getPossibleMoves(piece)) {
                if (board.byPosition(move) != null) {
                    board.moveTo(piece, move);
                    lastMove = System.currentTimeMillis();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean doAnyMove(Board board) {
        for (Piece piece : getRandomOrder(board, getColour())) {
            for (Position move : board.getPossibleMoves(piece)) {
                board.moveTo(piece, move);
                lastMove = System.currentTimeMillis();
                return true;
            }
        }
        return false;
    }

    private List<Piece> getRandomOrder(Board board, Piece.Colour colour) {
        List<Piece> pieces = new ArrayList<>(board.getPieces().stream().filter(piece -> piece.getColour().equals(getColour())).collect(Collectors.toList()));
        Collections.shuffle(pieces);
        return pieces;
    }

    public long getTimeSinceLastMove() {
        return System.currentTimeMillis() - lastMove;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Piece.Colour getColour() {
        return colour;
    }

    public enum Difficulty {
        LEMONS, CAUTIOUS, TACTFUL, RECKLESS, IMPOSSIBLE
    }
}
