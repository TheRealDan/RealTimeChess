package dev.therealdan.realtimechess.network;

public abstract class Packet {

    private String data;
    private Type type;

    public Packet(Type type, String data) {
        this.type = type;
        this.data = type.getKey() + data;
    }

    public Packet(String data) {
        this.type = getType(data);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public byte[] getBytes() {
        return (getData() + "\n").getBytes();
    }

    public Type getType() {
        return type;
    }

    public static Type getType(String data) {
        if (data != null)
            for (Type type : Type.values())
                if (type.getKey().equals(data.substring(0, 1)))
                    return type;
        return null;
    }

    public enum Type {
        ASSIGNMENT, BOARD, MOVE, PROMOTE;

        public String getKey() {
            return toString().substring(0, 1);
        }
    }
}
