package dev.therealdan.realtimechess.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import dev.therealdan.realtimechess.main.Mouse;
import dev.therealdan.realtimechess.main.RealTimeChessApp;
import dev.therealdan.realtimechess.network.Client;
import dev.therealdan.realtimechess.network.DevicePeer;
import dev.therealdan.realtimechess.network.Packet;
import dev.therealdan.realtimechess.network.Server;
import dev.therealdan.realtimechess.network.packets.AssignmentPacket;
import dev.therealdan.realtimechess.network.packets.BoardPacket;
import dev.therealdan.realtimechess.network.packets.MovePacket;
import dev.therealdan.realtimechess.network.packets.PromotionPacket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GameInstance {

    private Board board;
    private Bot bot;
    private Server server;
    private Client client;
    private Piece.Colour colour;

    private Piece.Type promotion = Piece.Type.QUEEN;

    public GameInstance(Bot.Difficulty difficulty, Server server, Client client, Piece.Colour preference) {
        board = Board.standardBoard();
        if (difficulty != null) bot = new Bot(difficulty, preference.opposite());
        this.server = server;
        this.client = client;
        this.colour = getClient() != null ? null : preference;

        if (getServer() != null) {
            serverConnectToClient();
        } else if (getClient() != null) {
            clientConnectToServer(preference);
        }
    }

    private void serverConnectToClient() {
        new Thread(() -> {
            getServer().accept();
            while (true) {
                try {
                    incoming(new BufferedReader(new InputStreamReader(getServer().getSocket().getInputStream())).readLine());
                } catch (IOException e) {
                    Gdx.app.log("Server", "Error", e);
                }
            }
        }).start();
    }

    private void clientConnectToServer(Piece.Colour preference) {
        new Thread(() -> {
            while (true) {
                try {
                    incoming(new BufferedReader(new InputStreamReader(getClient().getInputStream())).readLine());
                } catch (IOException e) {
                    Gdx.app.log("Client", "Error", e);
                }
            }
        }).start();
        new Thread(() -> {
            long checkInterval = 200;
            long lastCheck = System.currentTimeMillis() - checkInterval;
            while (getColour() == null) {
                if (System.currentTimeMillis() - lastCheck < checkInterval) continue;
                lastCheck = System.currentTimeMillis();
                getDevicePeer().send(new AssignmentPacket(preference));
            }
        }).start();
    }

    private void incoming(String data) {
        Packet.Type packetType = Packet.getType(data);
        if (packetType == null) return;

        switch (packetType) {
            case ASSIGNMENT:
                if (getServer() != null) {
                    getServer().send(new AssignmentPacket(getColour().opposite()));
                } else if (getClient() != null) {
                    AssignmentPacket assignmentPacket = AssignmentPacket.parse(data);
                    colour = assignmentPacket.getColour();
                }
                break;
            case BOARD:
                if (getServer() != null) {
                    getServer().send(new BoardPacket(getBoard()));
                } else if (getClient() != null) {
                    BoardPacket boardPacket = BoardPacket.parse(data);
                    getBoard().getPieces().clear();
                    getBoard().getPieces().addAll(boardPacket.getPieces());
                }
                break;
            case MOVE:
                MovePacket movePacket = MovePacket.parse(data);
                Piece piece = getBoard().byPosition(movePacket.getFrom());
                if (piece == null || !piece.getType().equals(movePacket.getPieceType()) || piece.getColour().equals(getColour())) {
                    if (getServer() != null)
                        getServer().send(new BoardPacket(getBoard()));
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

    public void render(RealTimeChessApp app) {
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

    public void moveTo(Piece piece, Position position) {
        if (getBoard().moveTo(piece, position)) {
            if (getDevicePeer() != null) {
                getDevicePeer().send(new MovePacket(piece.getType(), piece.getPosition(), position.copy()));
            }
        }
    }

    public boolean hasGameStarted() {
        if (getBot() != null) return true;
        if (getServer() != null) return getServer().getSocket() != null;
        if (getClient() != null) return getClient().isConnected() && getColour() != null;
        return false;
    }

    public Board getBoard() {
        return board;
    }

    public Bot getBot() {
        return bot;
    }

    public DevicePeer getDevicePeer() {
        return getClient() != null ? getClient() : getServer();
    }

    public Server getServer() {
        return server;
    }

    public Client getClient() {
        return client;
    }

    public Piece.Colour getColour() {
        return colour;
    }

    public Piece.Type getPromotion() {
        return promotion;
    }
}
