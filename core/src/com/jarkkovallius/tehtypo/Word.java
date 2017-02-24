package com.jarkkovallius.tehtypo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Jarkko on 24.2.2017.
 */
public class Word implements Pool.Poolable {

    public float x, y ;

    public BitmapFont font ;
    public String text ;
    public GlyphLayout glyphLayout ;

    public boolean alive = false;


    @Override
    public void reset() {
        alive = true ;
    }




    /*
    public void draw(SpriteBatch batch) {
        //wordFont.draw(batch, this.text, this.x, this.y);
        wordFont.draw(batch, glyphLayout,this.x, this.y);
    }*/
/*
    @Override
    public void draw(Batch batch, float parentAlpha) {
        //System.out.println("drawing");
        //super.draw(batch, parentAlpha);
        wordFont.draw(batch, glyphLayout,this.x, this.y);
        //batch.draw(wordFont.getRegion().);
    }
    */
}
