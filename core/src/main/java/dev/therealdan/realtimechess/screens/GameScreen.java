package dev.therealdan.realtimechess.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import dev.therealdan.realtimechess.game.Board;
import dev.therealdan.realtimechess.game.Piece;
import dev.therealdan.realtimechess.game.Position;
import dev.therealdan.realtimechess.main.Mouse;
import dev.therealdan.realtimechess.main.RealTimeChessApp;
import dev.therealdan.realtimechess.network.DevicePeer;
import dev.therealdan.realtimechess.network.Packet;
import dev.therealdan.realtimechess.network.packets.AssignmentPacket;
import dev.therealdan.realtimechess.network.packets.BoardPacket;
import dev.therealdan.realtimechess.network.packets.MovePacket;
import dev.therealdan.realtimechess.network.packets.PromotionPacket;

public abstract class GameScreen extends AScreen {

    protected Piece.Colour colour;
    protected Board board;

    private Piece.Type promotion = Piece.Type.QUEEN;

    public GameScreen(RealTimeChessApp app) {
        super(app);
    }

    public void incoming(String data) {
        Packet.Type packetType = Packet.getType(data);
        if (packetType == null) return;

        switch (packetType) {
            case ASSIGNMENT:
                switch (getDevicePeerType()) {
                    case SERVER:
                        getDevicePeer().send(new AssignmentPacket(getColour().opposite()));
                        break;
                    case CLIENT:
                        AssignmentPacket assignmentPacket = AssignmentPacket.parse(data);
                        colour = assignmentPacket.getColour();
                        break;
                }
                break;
            case BOARD:
                switch (getDevicePeerType()) {
                    case SERVER:
                        getDevicePeer().send(new BoardPacket(getBoard()));
                        break;
                    case CLIENT:
                        BoardPacket boardPacket = BoardPacket.parse(data);
                        if (board == null) board = new Board();
                        getBoard().getPieces().clear();
                        getBoard().getPieces().addAll(boardPacket.getPieces());
                        break;
                }
                break;
            case MOVE:
                MovePacket movePacket = MovePacket.parse(data);
                Piece piece = getBoard().byPosition(movePacket.getFrom());
                if (piece == null || !piece.getType().equals(movePacket.getPieceType()) || piece.getColour().equals(getColour())) {
                    if (getDevicePeerType().equals(DevicePeer.Type.SERVER))
                        getDevicePeer().send(new BoardPacket(getBoard()));
                    break;
                }
                getBoard().moveTo(piece, movePacket.getTo());
                break;
            case PROMOTE:
                PromotionPacket promotionPacket = PromotionPacket.parse(data);
                Piece toPromote = getBoard().byPosition(promotionPacket.getPosition());
                if (toPromote == null || !toPromote.getType().equals(Piece.Type.PAWN) || toPromote.getColour().equals(getColour())) break;
                getBoard().promote(toPromote, promotionPacket.getPromoteTo());
                break;
        }
    }

    public void moveTo(Piece piece, Position position) {
        if (getDevicePeer() != null)
            getDevicePeer().send(new MovePacket(piece.getType(), piece.getPosition(), position));

        getBoard().moveTo(piece, position);
    }

    @Override
    public void render(float delta) {
        camera.update();
        app.shapeRenderer.setProjectionMatrix(camera.combined);
        app.batch.setProjectionMatrix(camera.combined);

        float width = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.8f, height = width;
        float x = -width / 2f;
        float y = -height / 2f;

        if (hasGameStarted())
            getBoard().render(app, x, y, width, height);

        Piece piece = getBoard().getPromoting();
        if (piece != null && piece.getColour().equals(getColour())) {
            float cell = width / 8;
            x += (piece.getPosition().getX() - 1) * cell;
            y += (piece.getPosition().getY() - 1) * cell;
            app.batch.setColor(getColour().getColor());
            app.batch.draw(app.textures.white, x, y - cell * 3f, cell, cell * 4);

            Piece.Type[] types = {Piece.Type.QUEEN, Piece.Type.KNIGHT, Piece.Type.ROOK, Piece.Type.BISHOP};
            for (Piece.Type type : types) {
                if (Mouse.containsMouse(x, y, cell, cell) || type.equals(promotion)) {
                    promotion = type;
                    app.batch.setColor(Color.WHITE);
                    app.batch.draw(app.textures.navy, x, y, cell, cell);
                }
                Piece.render(app, x, y, cell, type, type.equals(promotion) ? getColour().getColor() : Color.NAVY);
                y -= cell;
            }
        }
    }

    public boolean hasGameStarted() {
        return true;
    }

    public boolean canMove(Piece piece) {
        if (piece == null) return false;
        if (!piece.getColour().equals(getColour())) return false;
        if (piece.isOnCooldown()) return false;

        if (getBoard().isChecked(getColour()) && getBoard().getPossibleMoves(piece).isEmpty()) return false;

        return true;
    }

    public DevicePeer getDevicePeer() {
        return null;
    }

    public DevicePeer.Type getDevicePeerType() {
        return getDevicePeer() != null ? getDevicePeer().getType() : DevicePeer.Type.OFFLINE;
    }

    public Piece.Colour getColour() {
        return colour;
    }

    public Board getBoard() {
        return board != null ? board : new Board();
    }

    public Piece.Type getPromotion() {
        return promotion;
    }

    @Override
    public boolean keyDown(int i) {
        switch (i) {
            case 111:
                app.setScreen(new MainMenuScreen(app));
                break;
        }
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        if (hasGameStarted()) {
            if (getBoard().getPromoting() != null && getBoard().getPromoting().getColour().equals(getColour())) {
                if (getDevicePeer() != null)
                    getDevicePeer().send(new PromotionPacket(getBoard().getPromoting().getPosition(), getPromotion()));
                getBoard().promote(getBoard().getPromoting(), getPromotion());
                return false;
            }

            if (getBoard().getHovering() != null) {
                Piece piece = getBoard().byPosition(getBoard().getHovering());
                if (canMove(piece)) {
                    getBoard().select(piece);
                    getBoard().setHolding(true);
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        if (getBoard().isHolding()) {
            Piece piece = getBoard().getSelected();
            Position position = getBoard().getHovering();
            moveTo(piece, position);
        }
        getBoard().setHolding(false);
        return false;
    }
}
