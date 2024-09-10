package dev.therealdan.realtimechess.game;

public class Notation {

    private String notation;
    private Piece.Type type;
    private Position from, to;

    public Notation(Piece piece, Position position) {
        this.notation = piece.getType().getNotation() + piece.getPosition().getNotation() + position.getNotation();

        this.type = piece.getType();
        this.from = piece.getPosition().copy();
        this.to = position.copy();
    }

    public Notation(String notation) {
        this.notation = notation;

        this.type = Piece.Type.byNotation(notation.substring(0, 1));
        this.from = Position.byNotation(notation.substring(1, 3));
        this.to = Position.byNotation(notation.substring(3, 5));
    }

    public String getNotation() {
        return notation;
    }

    public Piece.Type getType() {
        return type;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }
}
