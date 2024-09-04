package dev.therealdan.realtimechess.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import dev.therealdan.realtimechess.main.RealTimeChessApp;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private Texture black = new Texture("images/wood_black.png");
    private Texture white = new Texture("images/wood_white.png");

    private List<Piece> pieces = new ArrayList<>();

    public void render(RealTimeChessApp app, float ox, float oy, float width, float height) {
        app.batch.begin();
        Piece.Colour colour = Piece.Colour.WHITE;
        float cell = width / 8;
        oy += height - cell;
        float x = ox, y = oy;
        for (int number = 8; number >= 1; number--) {
            for (String letter : Position.letters.split("")) {
                app.batch.setColor(Color.WHITE);
                app.batch.draw(colour.equals(Piece.Colour.BLACK) ? black : white, x, y, cell, cell);
                x += cell;
                colour = colour.opposite();
                Piece piece = byPosition(new Position(letter, number));
                if (piece != null) {
                    piece.render(app, x, y, cell);
                }
            }
            y -= cell;
            x = ox;
            colour = colour.opposite();
        }
        app.batch.end();
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
