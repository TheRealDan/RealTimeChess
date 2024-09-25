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
            for (Position move : getPossibleMoves(board, piece)) {
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
            for (Position move : getPossibleMoves(board, piece)) {
                board.moveTo(piece, move);
                lastMove = System.currentTimeMillis();
                return true;
            }
        }
        return false;
    }

    private List<Position> getPossibleMoves(Board board, Piece piece) {
        List<Position> moves = new ArrayList<>(board.getPossibleMoves(piece));
        Collections.shuffle(moves);
        return moves;
    }

    private List<Piece> getRandomOrder(Board board, Piece.Colour colour) {
        List<Piece> pieces = new ArrayList<>(board.getPieces().stream().filter(piece -> piece.getColour().equals(colour) && !piece.isOnCooldown()).collect(Collectors.toList()));
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
                    return "Unlike other advanced bots, brainless has no brains, meaning it does not posses any intelligence or ability to make decisions.\n\nIt serves as a great learning tool for beginners to real time chess as it will never actually move any chess pieces. Literally brainless.";
                case LEMONS:
                    return "The easy AI chess bot that looks like a lemon!\n\nDon't let its appearance fool you, because underneath that bright yellow peel lies a very dim-witted artificial intelligence.";
                case SLOW:
                    return "While many chess bots may boast about their quick decision-making abilities, Slow takes a different approach.\n\nSlow is not the fastest thinker on the chessboard and instead takes its time before making a decision.\n\nSlow originally wanted to be called \"Turtle\" but unfortunately missed the application deadline.";
                case CAUTIOUS:
                    return "Like a sharp-witted detective, Cautious carefully assesses every move and considers all possible outcomes before making a strategic decision.\n\nIt is not one to rush into battle without a plan, but instead, carefully plots its moves to outsmart its opponent.";
                case TACTFUL:
                    return "Meet Tactful, the AI chess bot that strikes the perfect balance between strategic thinking and tactical execution.\n\nLike a true commander, Tactful is always one step ahead, constantly assessing the board and anticipating your moves.";
                case RECKLESS:
                    return "While most chess bots prioritize strategic and calculated moves, Reckless thrives on spontaneity and thrills in making lightning-fast decisions.\n\nReckless may seem like an unlikely opponent, but don't let its cute and cunning exterior fool you. This AI is driven by a relentless desire to play the game in a way that is both chaotic and efficient.";
                case IMPOSSIBLE:
                    return "Introducing Impossible: the ultimate chess playing AI designed to dominate the board and outsmart even the most skilled human players.\n\nSuperior processing power and lightning-fast decision-making skills give it an edge over its human counterparts. Prepare to face the ultimate challenge and test your skills against this truly unbeatable AI.\n\nWill you be able to outsmart the impossible?";
            }
        }
    }
}
