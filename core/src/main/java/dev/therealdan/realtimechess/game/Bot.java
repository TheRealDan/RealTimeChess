package dev.therealdan.realtimechess.game;

import com.badlogic.gdx.graphics.Texture;

import java.util.*;
import java.util.stream.Collectors;

public class Bot {

    private static HashMap<Difficulty, Texture> textures = new HashMap<>();

    private Random random = new Random();

    private Difficulty difficulty;
    private Piece.Colour colour;

    private long lastMove = System.currentTimeMillis();

    public Bot(Difficulty difficulty, Piece.Colour colour) {
        this.difficulty = difficulty;
        this.colour = colour;
    }

    public void think(Board board) {
        if (getDifficulty().equals(Difficulty.BRAINLESS)) return;

        Piece promoting = board.getPromoting();
        if (promoting != null) {
            if (promoting.getColour().equals(getColour())) {
                board.promote(promoting, Piece.Type.QUEEN);
            } else {
                return;
            }
        }

        switch (getDifficulty()) {
            case LEMONS:
                if (getTimeSinceLastMove() < 5000) return;
                doAnyMove(board);
                break;
            case SLOW:
                if (getTimeSinceLastMove() < 4000) return;
                doSmartMove(board);
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
        BRAINLESS, LEMONS, SLOW, CAUTIOUS, TACTFUL, RECKLESS, IMPOSSIBLE;

        public Texture getTexture() {
            if (!textures.containsKey(this)) {
                try {
                    textures.put(this, new Texture("images/bots/" + toString().toLowerCase() + ".png"));
                } catch (Exception e) {
                    textures.put(this, new Texture("images/bots/unknown.png"));
                }
            }
            return textures.get(this);
        }

        public String getName() {
            return toString().substring(0, 1) + toString().substring(1).toLowerCase();
        }

        public String getDifficulty() {
            switch (this) {
                default:
                    return "";
                case BRAINLESS:
                    return "(very easy)";
                case LEMONS:
                    return "(easy)";
            }
        }

        public String getDescription() {
            switch (this) {
                default:
                    return "No one actually knows anything about this particular bot.";
                case BRAINLESS:
                    return "Unlike other advanced bots, brainless has no brains, meaning it does not posses any intelligence or ability to make decisions. It serves as a great learning tool for beginners to real time chess as it will never actually move any chess pieces. Literally brainless.";
            }
        }
    }
}
