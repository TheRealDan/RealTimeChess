package dev.therealdan.realtimechess.network.packets;

import dev.therealdan.realtimechess.game.Board;
import dev.therealdan.realtimechess.game.Piece;
import dev.therealdan.realtimechess.game.Position;
import dev.therealdan.realtimechess.network.Packet;

import java.util.ArrayList;
import java.util.List;

public class BoardPacket extends Packet {

    private List<Piece> pieces = new ArrayList<>();

    public BoardPacket(Board board) {
        super(Type.BOARD, board.getNotation());
        this.pieces = new ArrayList<>(board.copy().getPieces());
    }

    private BoardPacket(String data) {
        super(data);
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public static BoardPacket parse(String data) {
        BoardPacket packet = new BoardPacket(data);
        for (String string : data.substring(1).split(",")) {
            packet.pieces.add(new Piece(
                Piece.Type.byNotation(string.substring(1, 2)),
                Piece.Colour.byNotation(string.substring(0, 1)),
                Position.byNotation(string.substring(2, 4))
            ));
        }
        return packet;
    }
}
