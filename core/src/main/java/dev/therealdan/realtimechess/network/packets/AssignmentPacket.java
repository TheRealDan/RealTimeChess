package dev.therealdan.realtimechess.network.packets;

import dev.therealdan.realtimechess.game.Piece;
import dev.therealdan.realtimechess.network.Packet;

public class AssignmentPacket extends Packet {

    private Piece.Colour colour;

    public AssignmentPacket(Piece.Colour colour) {
        super(Type.ASSIGNMENT, colour.getNotation());
        this.colour = colour;
    }

    private AssignmentPacket(String data) {
        super(data);
    }

    public Piece.Colour getColour() {
        return colour;
    }

    public static AssignmentPacket parse(String data) {
        AssignmentPacket packet = new AssignmentPacket(data);
        packet.colour = Piece.Colour.byNotation(data.substring(1, 2));
        return packet;
    }
}
