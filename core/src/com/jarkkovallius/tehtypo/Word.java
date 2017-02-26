package com.jarkkovallius.tehtypo;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

/**
 * W
 *
 * Created by Jarkko on 24.2.2017.
 */
public class Word implements Pool.Poolable {

    private String text ;
    private float x, y ;
    private BitmapFont font ;
    private GlyphLayout glyphLayout ;
    private boolean alive = false;
    private Rectangle rectangle ;

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    @Override
    public void reset() {
        alive = true ;
    }

    public String getText() {
        return text ;
    }

    public void setText(String text) {
        this.text = text ;
        this.getGlyphLayout().setText(this.getFont(), this.text);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        if (rectangle != null) this.rectangle.setX(x);
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public BitmapFont getFont() {
        return font;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }


    public GlyphLayout getGlyphLayout() {
        return glyphLayout;
    }

    public void setGlyphLayout(GlyphLayout glyphLayout) {
        this.glyphLayout = glyphLayout;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
