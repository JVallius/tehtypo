package com.jarkkovallius.tehtypo;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Teh Typo
 * --------
 * By Jarkko Vallius 2017
 *
 *
 * Game Description
 * ----------------
 * The player is trying to gain points by destroying words
 * by writing them using a keyboard before the words reach
 * the right side of screen.
 *
 * Words used in the game are loaded from a XML text file.
 *
 */
public class TehTypo extends ApplicationAdapter {

    public static final String APPLICATION_NAME = "Teh Typo";
    public static final String VERSION_CODE = "0.5" ;


    boolean DEBUG = false ;

    final int HEALTH_POINTS = 1 ;

    private final String WORD_FONT_FILENAME = "roboto.ttf" ;
    private final String TEXTFIELD_FONT_FILENAME = "roboto.ttf" ;
    private final String FONT_FOLDER = "fonts" ;

    private final float VIEWPORT_HEIGHT_DIVIDER = 20f;

    private long wordSpawnIntervalMilliSeconds = 2000;
    private float wordCurrentSpeed = 100f;

    public enum GameState {STAND_BY, RUNNING, GAME_OVER} ;

    GameState currentGameState ;


    // fonts
    private BitmapFont GUIBitmapFont;
    private BitmapFont wordBitmapFont;
    private BitmapFont textFieldBitmapFont;
    private int wordFontSize;
    private int textFieldFontSize;


    // words
    private final Array<Word> activeWords = new Array<Word>();
    private final Pool<Word> wordPool = new Pool<Word>() {
        @Override
        protected Word newObject() {
            return constructWord("", 0, 0, wordBitmapFont);
        }
    };
    private Array<String> wordList ;

    private float wordHeight ;
    private float gameAreaMarginTop ;
    private float gameAreaMarginBottom ;


    private SpriteBatch batch;
    private OrthographicCamera camera;
    private float viewportWidth;
    private float viewportHeight;
    private long time = 0; // for handling word spawning
    private int wordCounter = 0;


    // STAGES
    private Stage gameStage;
    private TextField textField;


    private Stage standByStage ;


    private Stage gameOverStage ;
    private Label gameOverScoreLabel ;


    ShapeRenderer shapeRenderer ; // for debugging

    int score  ;
    int healthPoints  ;

    FileHandler fileHandler ;


    @Override
    public void create() {

        Gdx.graphics.setTitle(APPLICATION_NAME + " version " + VERSION_CODE);

        // initializing
        batch = new SpriteBatch();
        viewportWidth = Gdx.graphics.getWidth();
        viewportHeight = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(viewportWidth, viewportHeight);
        camera.setToOrtho(false);

        if (DEBUG) {
            shapeRenderer = new ShapeRenderer() ;
            shapeRenderer.setAutoShapeType(true);
        }

        wordFontSize = (int)(viewportHeight / VIEWPORT_HEIGHT_DIVIDER) ;
        textFieldFontSize = (int)(viewportHeight / VIEWPORT_HEIGHT_DIVIDER) ;

        // generate BitmapFonts used in game
        GUIBitmapFont = generateBitmapFont(wordFontSize, FONT_FOLDER +"/" + WORD_FONT_FILENAME);
        wordBitmapFont = generateBitmapFont(wordFontSize, FONT_FOLDER +"/" + WORD_FONT_FILENAME);
        textFieldBitmapFont = generateBitmapFont(textFieldFontSize, FONT_FOLDER +"/" + TEXTFIELD_FONT_FILENAME);

        // find out word height in pixels
        GlyphLayout glyphLayout = new GlyphLayout(wordBitmapFont, "ABC");
        wordHeight = glyphLayout.height ;



        // STAND BY STAGE
        standByStage = new Stage();
        Label label = new Label("PRESS ENTER TO START", new Label.LabelStyle(GUIBitmapFont, Color.BLACK));
        label.getGlyphLayout().setText(GUIBitmapFont, "PRESS ENTER TO START");
        label.setX(viewportWidth/2 - label.getGlyphLayout().width/2);
        label.setY(viewportHeight/2);
        standByStage.addActor(label);


        // TEXTFIELD CONSTRUCTION
        textField = new TextField("", new TextField.TextFieldStyle(textFieldBitmapFont, Color.BLACK, null, null, null));
        textField.setX(0);
        textField.setY(20);
        textField.setWidth(viewportWidth / 2);
        textField.setMaxLength(30); // set max word length player can type


        // GAME STAGE
        gameStage = new Stage();
        gameStage.addActor(textField);
        gameStage.setKeyboardFocus(textField);

        // GAME OVER STAGE
        gameOverStage = new Stage();
        gameOverScoreLabel = new Label("GAME OVER", new Label.LabelStyle(GUIBitmapFont, Color.BLACK));
        gameOverStage.addActor(gameOverScoreLabel);

        // set multiplexer to read keyboard inputs from gameStage first
        // and then the rest inputs available from render() method
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(gameStage);
        inputMultiplexer.addProcessor(new InputAdapter());
        Gdx.input.setInputProcessor(inputMultiplexer);

        // WORD FILE LOADING
        fileHandler = new FileHandler();
        wordList = fileHandler.loadWordListFromXMLFile("files/kotus-sanalista_v1.xml");

        // set gameState to stand by
        currentGameState = GameState.STAND_BY ;
    }

    /**
     * Initialize everything before game goes RUNNING state
     *
     */
    private void init() {
        score = 0 ;
        healthPoints = HEALTH_POINTS ;

        textField.setText("");
        time = TimeUtils.millis();

        wordPool.clear();
        activeWords.clear();
    }


    /**
     * Called when player submits a word
     *
     * @param userSubmittedWord
     */
    private void handleWordSubmit(String userSubmittedWord) {
        for (int i = 0; i < activeWords.size; i++) {
            Word word = activeWords.get(i);
            if (word.getText().equalsIgnoreCase(userSubmittedWord)) {
                //System.out.println("word destroyed " + word.getText());
                activeWords.removeIndex(i);
                wordPool.free(word);
                score++ ;
                break;
            }
        }
    }

    /**
     * Creates BitmapFont object
     *
     * @param fontSize the size of the font
     * @param fontFilePath the path of the font file in android/assets folder
     * @return BitmapFont object
     */
    private BitmapFont generateBitmapFont(int fontSize, String fontFilePath) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontFilePath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        parameter.minFilter = Texture.TextureFilter.Nearest;
        parameter.magFilter = Texture.TextureFilter.Nearest;

        BitmapFont bitmapFont = generator.generateFont(parameter);
        generator.dispose();
        return bitmapFont;
    }



    /**
     * Main game loop
     *
     */
    @Override
    public void render() {

        // clear screen
        Gdx.gl.glClearColor(128 / 255f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // exit game if ESCAPE is pressed
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }


        switch (currentGameState) {
            case STAND_BY:
                handleGameStandBy();
                break;
            case RUNNING:
                handleGameRunning();
                break;
            case GAME_OVER:
                handleGameOver();
                break;
        }
    }

    /**
     * Draw stand by screen and handle inputs
     */
    private void handleGameStandBy() {
        // start game if ENTER is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            currentGameState = GameState.RUNNING;
            init();
        }

        standByStage.act();
        standByStage.draw();
    }


    /**
     * Draw game over screen and handle inputs
     */
    private void handleGameOver() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            currentGameState = GameState.STAND_BY;
            init();
        }

        gameOverStage.act();
        gameOverStage.draw();
    }

    /**
     * Draw main game screen and handle inputs
     */
    private void handleGameRunning() {
        // INPUT HANDLING

        // read text from textField if ENTER is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            //System.out.println("enter pressed");
            handleWordSubmit(textField.getText());
            textField.setText("");
        }


        // DESTROYING WORDS
        // check if any words on screen has touched the right side of screen and should be destroyed
        for (int i = 0; i < activeWords.size; i++) {
            Word word = activeWords.get(i);
            if (word.getX() + word.getGlyphLayout().width > viewportWidth) {
                //System.out.println("word destroyed " + word.text);
                activeWords.removeIndex(i);
                wordPool.free(word);
                healthPoints-- ;

                //System.out.println("HEALTH POINTS LEFT " + healthPoints);

                // GAME STATE TO GAME OVER
                if (healthPoints <= 0) {
                    currentGameState = GameState.GAME_OVER;

                    gameOverScoreLabel.getGlyphLayout().setText(GUIBitmapFont, "SCORE " + score);
                    gameOverScoreLabel.setText("SCORE " + score);
                    gameOverScoreLabel.setX(viewportWidth/2 - gameOverScoreLabel.getGlyphLayout().width/2);
                    gameOverScoreLabel.setY(viewportHeight/2);


                }
            }
        }

        // SPAWNING WORDS
        // check if a new word should be spawn on the screen
        long elapsedTime = TimeUtils.timeSinceMillis(time);
        if (elapsedTime > wordSpawnIntervalMilliSeconds) {
            if (spawnWord(getRandomWord())) {
                wordCounter++;
                time = TimeUtils.millis();
            }
        }


        // UPDATE ON SCREEN OBJECTS
        // move all words towards to the right side of screen
        for (Word word : activeWords) {
            word.setX((word.getX() + Gdx.graphics.getDeltaTime() * wordCurrentSpeed));
        }

        // update gameStage elements
        gameStage.act();


        // DRAWING
        batch.begin();
        // draw words on the screen
        for (Word word : activeWords) {
            word.getFont().draw(batch, word.getGlyphLayout(), word.getX(), word.getY());
        }
        batch.end();

        // draw gameStage objects on the screen
        gameStage.draw();

        if (DEBUG) {
            shapeRenderer.begin();

            for (Word word : activeWords) {
                shapeRenderer.rect(
                        word.getRectangle().getX(),
                        word.getRectangle().getY(),
                        word.getRectangle().getWidth(),
                        word.getRectangle().getHeight());
            }
            shapeRenderer.end();
        }

    }


    /**
     * Returns a random word from wordList
     *
     * @return random word
     */
    private String getRandomWord() {
        return wordList.get(MathUtils.random(0, wordList.size - 1));
    }




    /**
     * Attempt to create a new word on the left side of screen.
     *
     * Y-coordinate is randomized, and if the word overlaps on any another word,
     * the word is not added to activeWords array.
     *
     * @param wordText text of the word
     * @return true if word was successfully added to activeWords array, false otherwise
     */
    private boolean spawnWord(String wordText) {
        Word word = wordPool.obtain();
        word.setText(wordText);
        word.setX(0 - word.getGlyphLayout().width / 2f);

        word.setY(MathUtils.random(50, viewportHeight - 50));
        word.getRectangle().set(word.getX(), word.getY() - word.getGlyphLayout().height, word.getGlyphLayout().width, word.getGlyphLayout().height);

        boolean isOverlapping = false;
        for (Word w : activeWords) {
            if (w.getRectangle().overlaps(word.getRectangle())) {
                //System.out.println("Overlapping!");
                isOverlapping = true ;
                break ;
            }
        }

        if (isOverlapping) {
            wordPool.free(word);
            return false ;
        } else {
            activeWords.add(word);
            return true ;
        }
    }


    /**
     * Creates and initializes the Word object
     *
     * @param text text that is shown on screen
     * @param x x position on the screen
     * @param y y position on the screen
     * @param bitmapFont BitmapFont object
     * @return Word object
     */
    private Word constructWord(String text, float x, float y, BitmapFont bitmapFont) {
        Word word = new Word();
        word.setX(x);
        word.setY(y);
        word.setFont(bitmapFont);
        word.setGlyphLayout(new GlyphLayout(word.getFont(), text));
        word.setText(text);
        word.setRectangle(new Rectangle(word.getX(), word.getY(), word.getGlyphLayout().width, word.getGlyphLayout().height));

        return word;
    }


    /**
     * Dispose everything before game is closed
     */
    @Override
    public void dispose() {

        // fonts
        textFieldBitmapFont.dispose();
        wordBitmapFont.dispose();

        // stages
        gameStage.dispose();
        gameOverStage.dispose();
        standByStage.dispose();

        batch.dispose();
        if (DEBUG) {
            shapeRenderer.dispose();
        }

    }
}
