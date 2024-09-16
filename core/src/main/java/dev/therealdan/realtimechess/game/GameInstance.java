package dev.therealdan.realtimechess.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import dev.therealdan.realtimechess.main.Mouse;
import dev.therealdan.realtimechess.main.RealTimeChessApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GameInstance {

    private Board board;
    private Bot bot;
    private ServerSocket server;
    private Socket client, connected;
    private Piece.Colour colour;

    private Piece.Type promotion = Piece.Type.QUEEN;

    public GameInstance(Bot.Difficulty difficulty, ServerSocket server, Socket client, Piece.Colour preference) {
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
            connected = getServer().accept(new SocketHints());
            while (true) {
                try {
                    String incoming = new BufferedReader(new InputStreamReader(connected.getInputStream())).readLine();
                    if (incoming != null)
                        incoming(new Notation(incoming));
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
                    String incoming = new BufferedReader(new InputStreamReader(client.getInputStream())).readLine();
                    if (incoming != null)
                        incoming(new Notation(incoming));
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
                try {
                    getClient().getOutputStream().write((new Notation(preference).getNotation() + "\n").getBytes());
                } catch (IOException e) {
                    Gdx.app.log("Client", "Error", e);
                }
            }
        }).start();
    }

    private void incoming(Notation notation) {
        if (notation.isAssignment()) {
            if (getServer() != null && getConnected() != null) {
                try {
                    getConnected().getOutputStream().write((new Notation(getColour().opposite()).getNotation() + "\n").getBytes());
                } catch (IOException e) {
                    Gdx.app.log("Server", "Error", e);
                }
            } else if (getClient() != null) {
                colour = notation.getColour();
            }
        } else if (notation.isBoard()) {
            getBoard().getPieces().clear();
            for (String string : notation.getNotation().split(",")) {
                getBoard().getPieces().add(new Piece(
                    Piece.Type.byNotation(string.substring(1, 2)),
                    Piece.Colour.byNotation(string.substring(0, 1)),
                    Position.byNotation(string.substring(2, 4))
                ));
            }
        } else if (notation.isPromotion()) {
            Piece piece = getBoard().byPosition(notation.getFrom());
            if (piece == null || !piece.getType().equals(Piece.Type.PAWN) || piece.getColour().equals(getColour())) return;
            getBoard().promote(piece, notation.getType());
        } else if (notation.isMove()) {
            Piece piece = getBoard().byPosition(notation.getFrom());
            if (piece == null || !piece.getType().equals(notation.getType()) || piece.getColour().equals(getColour())) {
                if (getServer() != null) {
                    try {
                        getConnected().getOutputStream().write((new Notation(getBoard()).getNotation() + "\n").getBytes());
                    } catch (IOException e) {
                        Gdx.app.log("Server", "Error", e);
                    }
                }
                return;
            }
            getBoard().moveTo(piece, notation.getTo());
        }
    }

    public void render(RealTimeChessApp app) {
        float width = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.8f, height = width;
        float x = -width / 2f;
        float y = -height / 2f;
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
        Notation notation = new Notation(piece, position);
        if (getBoard().moveTo(piece, position)) {
            if (getClient() != null || getConnected() != null) {
                Socket socket = getClient() != null ? getClient() : getConnected();
                try {
                    socket.getOutputStream().write((notation.getNotation() + "\n").getBytes());
                } catch (IOException e) {
                    Gdx.app.log(getClient() != null ? "Client" : "Server", "Error", e);
                }
            }
        }
    }

    public Board getBoard() {
        return board;
    }

    public Bot getBot() {
        return bot;
    }

    public ServerSocket getServer() {
        return server;
    }

    public Socket getClient() {
        return client;
    }

    public Socket getConnected() {
        return connected;
    }

    public Piece.Colour getColour() {
        return colour;
    }

    public Piece.Type getPromotion() {
        return promotion;
    }
}
