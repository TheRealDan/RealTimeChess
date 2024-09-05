package dev.therealdan.realtimechess.main;

import com.badlogic.gdx.Gdx;

public class Mouse {

    public static boolean containsMouse(float x, float y, float width, float height) {
        float mouseX = getX();
        float mouseY = getY();
        return mouseX > x && mouseX < x + width &&
            mouseY > y && mouseY < y + height;
    }

    public static float getX() {
        return Gdx.input.getX() - Gdx.graphics.getWidth() / 2f;
    }

    public static float getY() {
        return -Gdx.input.getY() + Gdx.graphics.getHeight() / 2f;
    }
}
