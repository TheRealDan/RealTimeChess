package dev.therealdan.realtimechess.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;

public class FontManager implements Disposable {

    private FreeTypeFontGenerator freeTypeFontGenerator;

    private HashMap<Integer, BitmapFont> fonts = new HashMap<>();

    public float scale;

    public FontManager() {
        freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat-Medium.ttf"));
    }

    public void draw(SpriteBatch batch, String text, float x, float y, Color color) {
        draw(batch, text, x, y, 16, color);
    }

    public void draw(SpriteBatch batch, String text, float x, float y, int fontSize, Color color) {
        BitmapFont font = getFont(fontSize);
        font.setColor(color);
        font.draw(batch, text, x, y);
    }

    public void draw(SpriteBatch batch, String text, float x, float y, float width, int fontSize, Color color) {
        BitmapFont font = getFont(fontSize);
        font.setColor(color);
        font.draw(batch, text, x, y, width, Align.left, true);
    }

    public void center(SpriteBatch batch, String text, float x, float y, int fontSize, Color color) {
        BitmapFont font = getFont(fontSize);
        font.setColor(color);
        font.draw(batch, text, x, y + font.getCapHeight()/2f, 0, Align.center, false);
    }

    @Override
    public void dispose() {
        freeTypeFontGenerator.dispose();
    }

    private void generateFont(int fontSize) {
        FreeTypeFontGenerator.FreeTypeFontParameter freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        freeTypeFontParameter.size = fontSize;
        fonts.put(fontSize, freeTypeFontGenerator.generateFont(freeTypeFontParameter));
    }

    private BitmapFont getFont(int fontSize) {
        if (!fonts.containsKey(fontSize)) generateFont(fontSize);
        return fonts.get(fontSize);
    }
}
