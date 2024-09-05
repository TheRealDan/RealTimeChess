package dev.therealdan.realtimechess.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dev.therealdan.realtimechess.main.Mouse;
import dev.therealdan.realtimechess.main.RealTimeChessApp;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private Texture black = new Texture("images/wood_black.png");
    private Texture white = new Texture("images/wood_white.png");

    private List<Piece> pieces = new ArrayList<>();

    private Position hovering = null;
    private Piece selected = null;
    private boolean holding = false;

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
                if (getHovering() != null && getHovering().equals(position)) {
                    app.batch.end();
                    app.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    app.shapeRenderer.setColor(Color.YELLOW);
                    app.shapeRenderer.rect(x, y, cell, cell);
                    app.shapeRenderer.end();
                    app.batch.begin();
                }
                if (piece != null && (!piece.equals(getSelected()) || !isHolding()))
                    piece.render(app, x, y, cell);

                colour = colour.opposite();
                x += cell;
            }
            y -= cell;
            x = ox;
            colour = colour.opposite();
        }
        if (isHolding())
            getSelected().render(app, Mouse.getX() - cell / 2f, Mouse.getY() - cell / 2f, cell);
        app.batch.end();
    }

    public Piece moveTo(Piece piece, Position position) {
        if (piece == null || position == null) return null;
        Piece captured = byPosition(position);
        if (captured != null && captured.getColor().equals(piece.getColor())) return null;

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

    public Piece byPosition(Position position) {
        for (Piece piece : getPieces())
            if (piece.getPosition().equals(position))
                return piece;
        return null;
    }

    public List<Piece> getPieces() {
        return pieces;
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
