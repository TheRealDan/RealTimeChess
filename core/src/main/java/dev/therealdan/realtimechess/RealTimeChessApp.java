package dev.therealdan.realtimechess;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class RealTimeChessApp extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen());
    }
}