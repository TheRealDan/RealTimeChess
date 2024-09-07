package dev.therealdan.realtimechess.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dev.therealdan.realtimechess.main.Mouse;
import dev.therealdan.realtimechess.main.RealTimeChessApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Board {

    private static Texture black = new Texture("images/wood_black.png");
    private static Texture white = new Texture("images/wood_white.png");

    private List<Piece> pieces = new ArrayList<>();

    private Position hovering = null;
    private Piece selected = null;
    private boolean holding = false;
    private boolean simulation = false;

    public void render(RealTimeChessApp app, float ox, float oy, float width, float height) {
        app.batch.begin();
        Piece.Colour colour = Piece.Colour.WHITE;
        float cell = width / 8;
        oy += height - cell;
        float x = ox, y = oy;
        setHovering(null);
        for (int number = 8; number >= 1; number--) {
            for (String letter : Position.letters.split("")) {
                Position position = new Position(letter, number);
                Piece piece = byPosition(position);
                if (Mouse.containsMouse(x, y, cell, cell))
                    setHovering(position);

                app.batch.setColor(Color.WHITE);
                app.batch.draw(colour.equals(Piece.Colour.BLACK) ? black : white, x, y, cell, cell);
                if (Gdx.input.isKeyPressed(Input.Keys.TAB)) {
                    float spacing = cell * 0.1f;
                    if (position.getNumber() == 1) app.font.center(app.batch, position.getLetter(), x + cell - spacing, y + spacing * 2f, (int) (10f * app.font.scale), Color.BLACK);
                    if (position.getLetter().equals("a")) app.font.center(app.batch, position.getNumber() + "", x + spacing, y + cell - spacing * 2f, (int) (10f * app.font.scale), Color.BLACK);
                }
                if (piece == null && isHolding() && getPossibleMoves(getSelected()).stream().anyMatch(move -> move.equals(position))) {
                    app.batch.end();
                    app.shapeRenderer.setAutoShapeType(true);
                    app.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    app.shapeRenderer.setColor(Color.WHITE);
                    app.shapeRenderer.circle(x + cell / 2f, y + cell / 2f, cell / 8f, 64);
                    app.shapeRenderer.end();
                    app.batch.begin();
                }
                if (piece != null && (!piece.equals(getSelected()) || !isHolding()))
                    piece.render(app, x, y, cell, isHolding() && getPossibleMoves(getSelected()).stream().anyMatch(move -> move.equals(piece.getPosition())) ? Color.FIREBRICK : piece.getColour().getColor());

                colour = colour.opposite();
                x += cell;
            }
            y -= cell;
            x = ox;
            colour = colour.opposite();
        }
        if (isHolding() && getSelected() != null)
            getSelected().render(app, Mouse.getX() - cell / 2f, Mouse.getY() - cell / 2f, cell);
        app.batch.end();
    }

    public Piece moveTo(Piece piece, Position position) {
        if (piece == null || position == null) return null;
        Piece captured = byPosition(position);
        if (captured != null && captured.getColour().equals(piece.getColour())) return null;

        if (!getPossibleMoves(piece).stream().anyMatch(move -> move.equals(position))) return null;

        if (!simulation) {
            Board simulation = copy();
            simulation.simulation = true;
            Piece pieceSim = simulation.byPosition(piece.getPosition());
            simulation.getPieces().remove(simulation.byPosition(position));
            pieceSim.getPosition().set(position);
            if (simulation.isChecked(piece.getColour())) return null;
        }

        piece.getPosition().set(position);
        if (captured != null)
            getPieces().remove(captured);
        return captured;
    }

    public void setHovering(Position position) {
        hovering = position;
    }

    public Position getHovering() {
        return hovering;
    }

    public void select(Piece piece) {
        selected = piece;
    }

    public Piece getSelected() {
        return selected;
    }

    public void setHolding(boolean holding) {
        this.holding = holding;
    }

    public boolean isHolding() {
        return holding;
    }

    public boolean isChecked(Piece.Colour colour) {
        Optional<Piece> optionalKing = getPieces().stream().filter(piece -> piece.getType().equals(Piece.Type.KING) && piece.getColour().equals(colour)).findFirst();
        if (optionalKing.isEmpty()) return false;
        Piece king = optionalKing.get();

        if (king == null) return false;
        for (Piece opponent : getPieces()) {
            if (opponent.getColour().equals(colour)) continue;
            for (Position opponentMove : getPossibleMoves(opponent))
                if (king.getPosition().equals(opponentMove))
                    return true;
        }
        return false;
    }

    public boolean isStalemate(Piece.Colour colour) {
        for (Piece piece : getPieces()) {
            if (!piece.getColour().equals(colour)) continue;
            if (!getPossibleMoves(piece).isEmpty())
                return false;
        }
        return !isChecked(colour);
    }

    public boolean isCheckmate(Piece.Colour colour) {
        if (!isChecked(colour)) return false;
        for (Piece piece : getPieces()) {
            if (!piece.getColour().equals(colour)) continue;
            if (!getPossibleMoves(piece).isEmpty())
                return false;
        }
        return true;
    }

    public List<Position> getPossibleMoves(Piece piece) {
        List<Position> moves = new ArrayList<>();
        if (piece == null) return moves;

        Position position = piece.getPosition();
        boolean black = piece.getColour().equals(Piece.Colour.BLACK);
        switch (piece.getType()) {
            case PAWN:
                Piece current = byPosition(position.copy().move(0, black ? -1 : 1));
                if (current == null) {
                    moves.add(position.copy().move(0, black ? -1 : 1));
                    if (piece.isStartPosition()) {
                        current = byPosition(position.copy().move(0, black ? -2 : 2));
                        if (current == null) moves.add(position.copy().move(0, black ? -2 : 2));
                    }
                }
                Piece captureRight = byPosition(position.copy().moveDiagonally(1, black ? -1 : 1));
                if (captureRight != null && !captureRight.getColour().equals(piece.getColour())) moves.add(captureRight.getPosition());
                Piece captureLeft = byPosition(position.copy().moveDiagonally(-1, black ? -1 : 1));
                if (captureLeft != null && !captureLeft.getColour().equals(piece.getColour())) moves.add(captureLeft.getPosition());
                break;
            case QUEEN:
            case ROOK:
                for (int x = piece.getPosition().getX() + 1; x <= 8; x++) {
                    moves.add(position.copy().setX(x));
                    if (byPosition(position.copy().setX(x)) != null) break;
                }
                for (int x = piece.getPosition().getX() - 1; x >= 1; x--) {
                    moves.add(position.copy().setX(x));
                    if (byPosition(position.copy().setX(x)) != null) break;
                }
                for (int y = piece.getPosition().getY() + 1; y <= 8; y++) {
                    moves.add(position.copy().setY(y));
                    if (byPosition(position.copy().setY(y)) != null) break;
                }
                for (int y = piece.getPosition().getY() - 1; y >= 1; y--) {
                    moves.add(position.copy().setY(y));
                    if (byPosition(position.copy().setY(y)) != null) break;
                }
                if (piece.getType().equals(Piece.Type.ROOK)) break;
            case BISHOP:
                for (int m = 1; m <= 7; m++) {
                    moves.add(position.copy().moveDiagonally(m, m));
                    if (byPosition(position.copy().moveDiagonally(m, m)) != null) break;
                }
                for (int m = 1; m <= 7; m++) {
                    moves.add(position.copy().moveDiagonally(-m, -m));
                    if (byPosition(position.copy().moveDiagonally(-m, -m)) != null) break;
                }
                for (int m = 1; m <= 7; m++) {
                    moves.add(position.copy().moveDiagonally(m, -m));
                    if (byPosition(position.copy().moveDiagonally(m, -m)) != null) break;
                }
                for (int m = 1; m <= 7; m++) {
                    moves.add(position.copy().moveDiagonally(-m, m));
                    if (byPosition(position.copy().moveDiagonally(-m, m)) != null) break;
                }
                break;
            case KNIGHT:
                for (Position move : piece.getPosition().getKnightPositions()) {
                    Piece moveTo = byPosition(move);
                    if (moveTo != null && moveTo.getColour().equals(piece.getColour())) continue;
                    moves.add(move);
                }
                break;
            case KING:
                for (int x = -1; x <= 1; x++)
                    for (int y = -1; y <= 1; y++)
                        if (x != 0 || y != 0)
                            moves.add(position.copy().move(x, y));
                break;
        }

        for (Position move : new ArrayList<>(moves)) {
            Piece current = byPosition(move);
            if (current != null && current.getColour().equals(piece.getColour())) {
                moves.remove(move);
                continue;
            }

            if (!simulation) {
                Board simulation = copy();
                simulation.simulation = true;
                Piece pieceSim = simulation.byPosition(piece.getPosition());
                simulation.getPieces().remove(simulation.byPosition(move));
                pieceSim.getPosition().set(move);
                if (simulation.isChecked(piece.getColour()))
                    moves.remove(move);
            }
        }

        return moves;
    }

    public Piece byPosition(Position position) {
        for (Piece piece : getPieces())
            if (piece.getPosition().equals(position))
                return piece;
        return null;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public Board copy() {
        Board board = new Board();
        for (Piece piece : getPieces())
            board.pieces.add(piece.copy());
        return board;
    }

    public static Board standardBoard() {
        Board board = new Board();
        board.pieces.add(new Piece(Piece.Type.ROOK, Piece.Colour.BLACK, new Position("a", 8)));
        board.pieces.add(new Piece(Piece.Type.KNIGHT, Piece.Colour.BLACK, new Position("b", 8)));
        board.pieces.add(new Piece(Piece.Type.BISHOP, Piece.Colour.BLACK, new Position("c", 8)));
        board.pieces.add(new Piece(Piece.Type.QUEEN, Piece.Colour.BLACK, new Position("d", 8)));
        board.pieces.add(new Piece(Piece.Type.KING, Piece.Colour.BLACK, new Position("e", 8)));
        board.pieces.add(new Piece(Piece.Type.BISHOP, Piece.Colour.BLACK, new Position("f", 8)));
        board.pieces.add(new Piece(Piece.Type.KNIGHT, Piece.Colour.BLACK, new Position("g", 8)));
        board.pieces.add(new Piece(Piece.Type.ROOK, Piece.Colour.BLACK, new Position("h", 8)));

        board.pieces.add(new Piece(Piece.Type.ROOK, Piece.Colour.WHITE, new Position("a", 1)));
        board.pieces.add(new Piece(Piece.Type.KNIGHT, Piece.Colour.WHITE, new Position("b", 1)));
        board.pieces.add(new Piece(Piece.Type.BISHOP, Piece.Colour.WHITE, new Position("c", 1)));
        board.pieces.add(new Piece(Piece.Type.QUEEN, Piece.Colour.WHITE, new Position("d", 1)));
        board.pieces.add(new Piece(Piece.Type.KING, Piece.Colour.WHITE, new Position("e", 1)));
        board.pieces.add(new Piece(Piece.Type.BISHOP, Piece.Colour.WHITE, new Position("f", 1)));
        board.pieces.add(new Piece(Piece.Type.KNIGHT, Piece.Colour.WHITE, new Position("g", 1)));
        board.pieces.add(new Piece(Piece.Type.ROOK, Piece.Colour.WHITE, new Position("h", 1)));

        for (String letter : Position.letters.split("")) {
            board.pieces.add(new Piece(Piece.Type.PAWN, Piece.Colour.BLACK, new Position(letter, 7)));
            board.pieces.add(new Piece(Piece.Type.PAWN, Piece.Colour.WHITE, new Position(letter, 2)));
        }
        return board;
    }
}
