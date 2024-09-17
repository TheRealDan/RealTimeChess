package dev.therealdan.realtimechess.network.packets;

import dev.therealdan.realtimechess.network.Packet;

public class UsernamePacket extends Packet {

    private String username;

    public UsernamePacket(String username) {
        super(Type.USERNAME, username);
        this.username = username;
    }

    public boolean isAsking() {
        return getUsername().equals("?");
    }

    public String getUsername() {
        return username;
    }

    public static UsernamePacket parse(String data) {
        return new UsernamePacket(data.substring(1));
    }
}
