package dev.therealdan.realtimechess.network.packets;

import dev.therealdan.realtimechess.game.Piece;
import dev.therealdan.realtimechess.game.Position;
import dev.therealdan.realtimechess.network.Packet;

public class PromotionPacket extends Packet {

    private Position position;
    private Piece.Type promoteTo;

    public PromotionPacket(Position position, Piece.Type promoteTo) {
        super(Type.PROMOTE, position.getNotation() + promoteTo.getNotation());
        this.position = position.copy();
        this.promoteTo = promoteTo;
    }

    private PromotionPacket(String data) {
        super(data);
    }

    public Position getPosition() {
        return position;
    }

    public Piece.Type getPromoteTo() {
        return promoteTo;
    }

    public static PromotionPacket parse(String data) {
        PromotionPacket packet = new PromotionPacket(data);
        packet.position = Position.byNotation(data.substring(1, 3));
        packet.promoteTo = Piece.Type.byNotation(data.substring(3, 4));
        return packet;
    }
}
