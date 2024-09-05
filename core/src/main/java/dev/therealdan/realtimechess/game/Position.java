package dev.therealdan.realtimechess.game;

import java.util.ArrayList;
import java.util.List;

public class Position {

    public static String letters = "abcdefgh";

    private String letter;
    private int number;

    public Position(String letter, int number) {
        setLetter(letter);
        setNumber(number);
    }

    public Position(int x, int y) {
        setX(x);
        setY(y);
    }

    public Position move(int x, int y) {
        setX(getX() + x);
        setY(getY() + y);
        return this;
    }

    public Position moveDiagonally(int x, int y) {
        if (x == 0 || y == 0) return this;
        if (Math.abs(x) != Math.abs(y)) return this;
        if (getX() + x > 8 || getX() + x < 1) return this;
        if (getY() + y > 8 || getY() + y < 1) return this;
        setX(getX() + x);
        setY(getY() + y);
        return this;
    }

    public Position moveKnightly(int x, int y) {
        if (getX() + x > 8 || getX() + x < 1) return this;
        if (getY() + y > 8 || getY() + y < 1) return this;
        setX(getX() + x);
        setY(getY() + y);
        return this;
    }

    public void set(Position position) {
        setLetter(position.getLetter());
        setNumber(position.getNumber());
    }

    public void setLetter(String letter) {
        if (!letters.contains(letter)) return;
        this.letter = letter;
    }

    public void setNumber(int number) {
        if (number < 1 || number > 8) return;
        this.number = number;
    }

    public String getLetter() {
        return letter;
    }

    public int getNumber() {
        return number;
    }

    public Position setX(int x) {
        if (x < 1 || x > 8) return this;
        setLetter(letters.substring(x - 1, x));
        return this;
    }

    public Position setY(int y) {
        setNumber(y);
        return this;
    }

    public int getX() {
        return letters.indexOf(getLetter()) + 1;
    }

    public int getY() {
        return getNumber();
    }

    public List<Position> getKnightPositions() {
        List<Position> positions = new ArrayList<>();
        positions.add(copy().moveKnightly(2, 1));
        positions.add(copy().moveKnightly(2, -1));
        positions.add(copy().moveKnightly(-2, 1));
        positions.add(copy().moveKnightly(-2, -1));
        positions.add(copy().moveKnightly(1, 2));
        positions.add(copy().moveKnightly(-1, 2));
        positions.add(copy().moveKnightly(1, -2));
        positions.add(copy().moveKnightly(-1, -2));
        return positions;
    }

    public String getNotation() {
        return getLetter() + getNumber();
    }

    public boolean equals(Position position) {
        return getLetter().equals(position.getLetter()) && getNumber() == position.getNumber();
    }

    public Position copy() {
        return new Position(letter, number);
    }
}
