package com.mygdx.othello.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.othello.MyOthelloGame;
import com.mygdx.othello.controllers.AbstractController;
import com.mygdx.othello.controllers.GameStateManager;
import com.mygdx.othello.controllers.SettingsController;

/**
 * This class manages the settings display, i.e the buttons, the slider and the scroll pane
 */
public class SettingsScreen extends AbstractScreen  {

    /** Container for the scrollPane and input processor */
    private Stage stage;

    /** Container for the buttons, labels and sliders */
    private Table table;

    /** Used for scrolling on the screen */
    private ScrollPane scrollPane;

    /** Font used for text display (in buttons or labels) */
    private BitmapFont font;

    /** Skin used for the buttons */
    private Skin skin;

    /** Stores the texture for the buttons */
    private TextureAtlas atlas;

    /** Background texture */
    private Texture background;

    /** Title texture */
    private Texture titleTexture;

    /** Texture when button not pressed */
    private Texture buttonTextureNotPressed;

    /** Texture when button pressed */
    private Texture buttonTexturePressed;

    /** TextButton style for the always disabled title button */
    private TextButton.TextButtonStyle titleStyle;

    /** TextButton style for the settings button */
    private TextButton.TextButtonStyle buttonStyle;

    /** Skin for the slider, bar and knob */
    private Skin sliderSkin;

    /** Style for the labels */
    private Label.LabelStyle labelStyle;

    /** Container for the handicapSlider and its label */
    private Table handicapTable;

    /** Slider used to set the handicap mode level */
    private Slider handicapSlider;

    /** Handicap Mode label */
    private Label handicapLabel;

    /** Title Button, always disabled */
    private TextButton titleButton;

    /** LegalMove button */
    private TextButton LegalMove;

    /** Music Button */
    private TextButton MusicOff;

    /** Score Button */
    private TextButton ScoreDisplay;

    /** LastFlips Button */
    private TextButton LastFlipsDisplay;

    /** Container for the timeMode slider and its label */
    private Table timeModeTable;

    /** Slider used to set the remaining time in Time Mode */
    private Slider timeModeSlider;

    /** Time Mode label */
    private Label timeModeLabel;

    /** Erase Save Button */
    private TextButton eraseSaveButton;

    /** Exit to Menu Screen Button */
    private TextButton menuScreenButton;

    private Preferences prefs;

    /** The controller we used in this settings screen */
    private SettingsController settingsController;

    /**
     * Initialize the Settings Screen
     * @param gsm
     */
    public SettingsScreen(GameStateManager gsm) {
        super(gsm);
        stage = new Stage();

        table = new Table(); table.setDebug(false);
        table.bottom();
        table.padTop(0.07f*cam.viewportHeight).padBottom(0.07f*cam.viewportHeight);

        scrollPane = new ScrollPane(table); scrollPane.setFillParent(true);
        scrollPane.setVelocityX(2f);
        stage.addActor(scrollPane);

        font = new BitmapFont();
        skin = new Skin();
        atlas = new TextureAtlas();

        background = new Texture("Stone_bright_lv1.jpg");

        prefs = Gdx.app.getPreferences("My preferences");

        //Load the textures and create the atlas
        sliderSkin = new Skin(Gdx.files.internal("skin_directory/uiskin.json"));
        buttonTextureNotPressed = new Texture("WoodenButton.png");
        buttonTexturePressed = new Texture("WoodenButton_dark.png");
        titleTexture = new Texture("MainMenu.png");
        atlas.addRegion("BasicButtonNotPressed", buttonTextureNotPressed
                , 0, 0, buttonTextureNotPressed.getWidth(), buttonTextureNotPressed.getHeight());
        atlas.addRegion("BasicButtonPressed", buttonTexturePressed
                , 0, 0, buttonTexturePressed.getWidth(), buttonTexturePressed.getHeight());
        atlas.addRegion("title", titleTexture, 0, 0, titleTexture.getWidth(), titleTexture.getHeight());
        skin.addRegions(atlas);
        font.getData().setScale(3);

        //Create the TextButtonStyle
        titleStyle = new TextButton.TextButtonStyle();
        titleStyle.font = font;
        titleStyle.up = skin.getDrawable("title");

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = skin.getDrawable("BasicButtonNotPressed");
        buttonStyle.down = skin.getDrawable("BasicButtonPressed");

        //The title of the screen
        titleButton = new TextButton("Settings Menu", titleStyle);
        titleButton.setDisabled(true);
        titleButton.getLabel().setColor(Color.WHITE);

        labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();

        //First option - Choose handicap mode
        handicapTable = new Table(); handicapTable.background(skin.getDrawable("BasicButtonNotPressed"));
        handicapTable.left();

        handicapSlider = new Slider(0,4,1,false, sliderSkin);
        handicapSlider.getStyle().knob.setMinHeight(0.04f*cam.viewportHeight);
        handicapSlider.getStyle().knob.setMinWidth(0.02f*cam.viewportHeight);
        handicapSlider.getStyle().background.setMinHeight(0.01f*cam.viewportHeight);

        handicapSlider.setValue(prefs.getFloat("handicap"));
        handicapLabel = new Label("Handicap :"+prefs.getFloat("handicap"), labelStyle);
        handicapLabel.setColor(Color.GRAY); handicapLabel.setFontScale(3f);

        //Second Option - Show legal Moves
        LegalMove = new TextButton("Show Legal Moves", buttonStyle);
        if (prefs.getBoolean("ShowLegalMoves")) {
            LegalMove.getLabel().setColor(Color.GREEN);
        } else {
            LegalMove.getLabel().setColor(Color.RED);
            LegalMove.setText("Hide Legal Moves");
        }

        //Third option - Score Display
        ScoreDisplay = new TextButton("Display Score : Yes", buttonStyle);
        if (prefs.getBoolean("DisplayScore")) {
            ScoreDisplay.getLabel().setColor(Color.GREEN);
        } else {
            ScoreDisplay.getLabel().setColor(Color.RED);
            ScoreDisplay.setText("Display Score : No");
        }

        //Fourth option - Last Move Display
        LastFlipsDisplay = new TextButton("Display Last Flips : Yes", buttonStyle);
        if (prefs.getBoolean("DisplayLastFlips")) {
            LastFlipsDisplay.getLabel().setColor(Color.GREEN);
        } else {
            LastFlipsDisplay.getLabel().setColor(Color.RED);
            LastFlipsDisplay.setText("Display Last Flips : No");
        }

        //Fifth option - Music
        MusicOff = new TextButton("Music Off", buttonStyle);
        if (prefs.getBoolean("IsMusicOff")) {
            MusicOff.getLabel().setColor(Color.RED);
            MyOthelloGame.getMusic().pause();
        } else {
            MusicOff.getLabel().setColor(Color.GREEN);
            MusicOff.setText("Music On");
            MyOthelloGame.getMusic().play();
        }

        //Sixth option - Chess time mode
        timeModeTable = new Table(); timeModeTable.left();
        timeModeTable.background(skin.getDrawable("BasicButtonNotPressed"));

        timeModeSlider = new Slider(0,10,0.5f,false,sliderSkin);
        timeModeSlider.setValue(prefs.getFloat("time"));
        timeModeLabel = new Label("Time mode : "+prefs.getFloat("time")+" mn", labelStyle);
        timeModeLabel.setColor(Color.GRAY); timeModeLabel.setFontScale(3f);

        //7th option - Erase Previous Save
        eraseSaveButton = new TextButton("Erase Save", buttonStyle);
        eraseSaveButton.getLabel().setColor(Color.GRAY);

        //8th option - Go back to Menu Screen
        menuScreenButton = new TextButton("Exit to Menu Screen", buttonStyle);
        menuScreenButton.getLabel().setColor(Color.GRAY);

        this.settingsController = new SettingsController(gsm, LegalMove, ScoreDisplay, LastFlipsDisplay,
                MusicOff, handicapLabel, timeModeLabel);

        //Adding all the listener
        LegalMove.addListener(settingsController);
        ScoreDisplay.addListener(settingsController);
        LastFlipsDisplay.addListener(settingsController);
        MusicOff.addListener(settingsController);
        handicapSlider.addListener(settingsController);
        timeModeSlider.addListener(settingsController);
        eraseSaveButton.addListener(settingsController);
        menuScreenButton.addListener(settingsController);

        //Two controllers we used for the Settings Screen
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(settingsController);
        Gdx.input.setInputProcessor(multiplexer);
        //Cancel the back key android paradigm
        Gdx.input.setCatchBackKey(true);

        //Place all buttons on the view
        table.setFillParent(true);
        table.add(titleButton).size(cam.viewportWidth - cam.viewportWidth / 10, cam.viewportHeight / 10)
                .expandY().top().spaceBottom(cam.viewportHeight / 15);
        table.row();
        handicapTable.setDebug(false);
        table.add(handicapTable).size(cam.viewportWidth - cam.viewportWidth / 10, cam.viewportHeight / 10).spaceBottom(cam.viewportHeight / 20);
        handicapTable.add(handicapSlider).size((3/8f)*cam.viewportWidth, cam.viewportHeight / 10)
                .padLeft(0.07f*cam.viewportWidth).padRight(0.07f*cam.viewportWidth);
        handicapTable.add(handicapLabel);
        table.row();
        table.add(LegalMove).size(cam.viewportWidth - cam.viewportWidth / 10, cam.viewportHeight / 10).spaceBottom(cam.viewportHeight / 20);
        table.row();
        table.add(ScoreDisplay).size(cam.viewportWidth - cam.viewportWidth / 10, cam.viewportHeight / 10).spaceBottom(cam.viewportHeight / 20);
        table.row();
        table.add(LastFlipsDisplay).size(cam.viewportWidth - cam.viewportWidth / 10, cam.viewportHeight / 10).spaceBottom(cam.viewportHeight / 20);
        table.row();
        table.add(MusicOff).size(cam.viewportWidth - cam.viewportWidth / 10, cam.viewportHeight / 10).spaceBottom(cam.viewportHeight / 20);
        table.row();
        table.add(timeModeTable).size(cam.viewportWidth - cam.viewportWidth / 10, cam.viewportHeight / 10).spaceBottom(cam.viewportHeight / 20);
        timeModeTable.add(timeModeSlider).size((3/8f)*cam.viewportWidth, cam.viewportHeight / 10)
                .padLeft(0.07f*cam.viewportWidth).padRight(0.03f*cam.viewportWidth);
        timeModeTable.add(timeModeLabel);
        table.row();
        table.add(eraseSaveButton).size(cam.viewportWidth - cam.viewportWidth / 10, cam.viewportHeight / 10).spaceBottom(cam.viewportHeight / 20);
        table.row();
        table.add(menuScreenButton).size(cam.viewportWidth - cam.viewportWidth / 10, cam.viewportHeight / 10).spaceBottom(cam.viewportHeight / 20);
        table.row();
    }

    @Override
    public AbstractController getController() {
        return this.settingsController;
    }

    @Override
    public void render(SpriteBatch sb) {
        //Draw the background
        sb.begin();
        int numberWidth = (int) cam.viewportWidth / background.getWidth() + 1;
        int numberHeight = (int) cam.viewportHeight / background.getHeight() + 1;
        for (int i = 0; i <= numberHeight; i++) {
            for (int j = 0; j <= numberWidth; j++) {
                sb.draw(background, j * background.getWidth(), i * background.getHeight());
            }
        }
        sb.end();
        this.stage.act(Gdx.graphics.getDeltaTime());
        //Draw the stage
        this.stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
        skin.dispose();
        atlas.dispose();
        background.dispose();
        titleTexture.dispose();
        buttonTextureNotPressed.dispose();
        buttonTexturePressed.dispose();
        sliderSkin.dispose();
    }
}