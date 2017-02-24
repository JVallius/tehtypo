package com.jarkkovallius.tehtypo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

public class TehTypo extends ApplicationAdapter {
	SpriteBatch batch;




	OrthographicCamera camera ;

	final float VIEWPORT_WIDTH = 800 ;
	final float VIEWPORT_HEIGHT = 600 ;
    BitmapFont wordFont;
    BitmapFont textFieldFont;

	Stage stage ;



    private final Array<Word> activeWords = new Array<Word>();

    private final Pool<Word> wordPool = new Pool<Word>() {
        @Override
        protected Word newObject() {
            return constructWord("", 0,0, wordFont);
        }
    };

	@Override
	public void create () {
		batch = new SpriteBatch();

        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.setToOrtho(false);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // generate wordFont
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 18;
        wordFont = generator.generateFont(parameter) ;
        textFieldFont = generator.generateFont(parameter) ;
        generator.dispose(); // don't forget to dispose to avoid memory leaks!

        TextField textField = new TextField("", new TextField.TextFieldStyle(textFieldFont, Color.BLACK, null, null, null));
        textField.setX(0);
        textField.setY(20);
        textField.setWidth(VIEWPORT_WIDTH/2);

        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {

                // if ENTER pressed
                if ((int)c == 13) {

                    for (int i = 0; i < activeWords.size; i++) {
                        Word word = activeWords.get(i) ;

                        String wordText = word.text;
                        String textFieldText = textField.getText();

                        if (wordText.equalsIgnoreCase(textFieldText)) {
                            System.out.println("word destroyed " + word.text);
                            activeWords.removeIndex(i) ;
                            wordPool.free(word);
                            break ;
                        }
                    }

                    textField.setText("");

                }

                //System.out.println("char: " + (int)c);
            }
        });
        stage.addActor(textField);

        stage.setKeyboardFocus(textField);







	}



	private Word constructWord(String text, float x, float y, BitmapFont bitmapFont) {

	    Word word = new Word();
	    word.x = x;
        word.y = y;
        word.text = text;
        word.font = bitmapFont ;
        word.glyphLayout = new GlyphLayout(word.font, word.text);
        word.alive = true;

        return word;

	}


	long time = 0 ;
	long inteval = 500 ;
	float speed = 100f ;

	int wordCounter = 0 ;

	@Override
	public void render () {
		Gdx.gl.glClearColor(128/255f, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
        batch.setProjectionMatrix(camera.combined);

        stage.act();

        for (int i = 0; i < activeWords.size; i++) {
            Word word = activeWords.get(i) ;


            if (word.x + word.glyphLayout.width > VIEWPORT_WIDTH) {
                System.out.println("word destroyed " + word.text);
                activeWords.removeIndex(i) ;
                wordPool.free(word);

            }

        }


        long elapsedTime = TimeUtils.timeSinceMillis(time);
        if (elapsedTime > inteval) {
            // create label
            float y = MathUtils.random(0, VIEWPORT_HEIGHT);
            //Label label = constructLabel("SANA",0, y, wordFont) ;
            Word word = wordPool.obtain();
            word.text = "SANA" + wordCounter ;
            word.glyphLayout = new GlyphLayout(word.font, word.text);
            word.x = (0 - word.glyphLayout.width/2f);
            word.y = (MathUtils.random(50, VIEWPORT_HEIGHT-50));

            activeWords.add(word);
            wordCounter++ ;

            time = TimeUtils.millis();
        }



		for (Word word : activeWords) {
            word.x = (word.x + Gdx.graphics.getDeltaTime() * speed );
        }


        batch.begin();

        for (Word word : activeWords) {
            word.font.draw(batch, word.glyphLayout, word.x, word.y);
        }

        batch.end();


        //stage.setDebugAll(true);
        stage.draw();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
