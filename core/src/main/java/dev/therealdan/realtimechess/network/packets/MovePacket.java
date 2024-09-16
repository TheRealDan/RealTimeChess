package dev.therealdan.realtimechess.network.packets;

import dev.therealdan.realtimechess.game.Piece;
import dev.therealdan.realtimechess.game.Position;
import dev.therealdan.realtimechess.network.Packet;

public class MovePacket extends Packet {

    private Piece.Type pieceType;
    private Position from;
    private Position to;

    public MovePacket(Piece.Type pieceType, Position from, Position to) {
        super(Type.MOVE, pieceType.getNotation() + from.getNotation() + to.getNotation());
        this.pieceType = pieceType;
        this.from = from.copy();
        this.to = to.copy();
    }

    private MovePacket(String data) {
        super(data);
    }

    public Piece.Type getPieceType() {
        return pieceType;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public static MovePacket parse(String data) {
        MovePacket packet = new MovePacket(data);
        packet.pieceType = Piece.Type.byNotation(data.substring(1, 2));
        packet.from = Position.byNotation(data.substring(2, 4));
        packet.to = Position.byNotation(data.substring(4, 6));
        return packet;
    }
}
