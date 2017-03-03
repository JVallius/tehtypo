package com.jarkkovallius.tehtypo;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

/**
 * Word class represents the moving word on the game screen
 *
 * Created by Jarkko on 24.2.2017.
 */
public class Word implements Pool.Poolable {

    private String text ;
    private float x, y ;
    private BitmapFont font ;
    private GlyphLayout glyphLayout ;
    private Rectangle rectangle ;

    @Override
    public void reset() {}

    /**
     * Get the rectangle surrounding the word
     *
     * @return
     */
    public Rectangle getRectangle() {
        return rectangle;
    }

    /**
     * Set the rectangle surrounding the word
     *
     * @param rectangle
     */
    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    /**
     * Get the word's text
     *
     * @return String text of the word
     */
    public String getText() {
        return text ;
    }

    /**
     * Apply a new text for the word.
     *
     * GlyphLayout is also updated to correspond the new text
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text ;
        this.getGlyphLayout().setText(this.getFont(), this.text);
    }

    /**
     * Get the word's X coordinate on the screen
     *
     * @return x coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Set the word's X coordinate on the screen
     *
     * Rectangle X coordinate is also updated
     *
     * @param x new X coordinate
     */
    public void setX(float x) {
        this.x = x;
        if (rectangle != null) this.rectangle.setX(x);
    }

    /**
     * Get the word's Y coordinate on the screen
     *
     * @return y coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Set the word's Y coordinate on the screen
     *
     * @param y Y coordinate
     */
    public void setY(float y) {
        this.y = y;
    }


    /**
     * Get the word's BitmapFont object
     *
     * @return BitmapFont object
     */
    public BitmapFont getFont() {
        return font;
    }

    /**
     * Set the word's BitmapFont
     *
     * @param font BitmapFont object
     */
    public void setFont(BitmapFont font) {
        this.font = font;
    }


    /**
     * Get the word's GlyphLayout object
     *
     * @return GlyphLayout object
     */
    public GlyphLayout getGlyphLayout() {
        return glyphLayout;
    }

    /**
     * Set the word's GlyphLayout object
     *
     * @param glyphLayout
     */
    public void setGlyphLayout(GlyphLayout glyphLayout) {
        this.glyphLayout = glyphLayout;
    }


}
