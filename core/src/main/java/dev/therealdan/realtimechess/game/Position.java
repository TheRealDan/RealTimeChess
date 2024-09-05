package dev.therealdan.realtimechess.game;

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

    public void setX(int x) {
        if (x < 1 || x > 8) return;
        setLetter(letters.substring(x - 1, x));
    }

    public void setY(int y) {
        setNumber(y);
    }

    public int getX() {
        return letters.indexOf(getLetter()) + 1;
    }

    public int getY() {
        return getNumber();
    }

    public String getNotation() {
        return getLetter() + getNumber();
    }

    public boolean equals(Position position) {
        return getLetter().equals(position.getLetter()) && getNumber() == position.getNumber();
    }
}
