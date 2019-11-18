package com.mygdx.othello.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygdx.othello.controllers.AbstractController;
import com.mygdx.othello.controllers.GameStateManager;
import com.mygdx.othello.controllers.RulesController;

/**
 * This class manage the Rules screen
 */
public class RulesScreen extends AbstractScreen {
    /** Static int that indicates a GAME_SCREEN */
    public static int GAME_SCREEN = 100;
    
    /** Static int that indicates a MENU_SCREEN */
    public static int MENU_SCREEN = 101;

    /** Indicate the type of the previous screen */
    private int previousScreen;
    
    /** Used for scrolling on the screen */
    private ScrollPane scrollPane;

    /** Container for the scrollPane table and input processor */
    private Stage stage;
    
    /** Contains the ScrollPane or the screenshots, labels and the Main Menu button */
    private Table table, table2;
     
    /** Font used in the labels (=screenshots descriptions) */
    private BitmapFont font;

    /** Skin used for the labels and the button */
    private Skin skin;

    /** Stores the Main Menu button texture */
    private TextureAtlas atlas;

    /** Texture when button pressed */
    private Texture buttonPressed;

    /** Texture when button not pressed */
    private Texture buttonNotPressed;

    /** Descriptions of the corresponding screenshots */
    private Label label1, label2, label3, label4;

    /** Background texture */
    private Texture Background;

    /** Button to return to the Main Menu */
    private TextButton menuButton;

    /** Button to resume the Game */
    private TextButton resumeGameButton;

    /** TextButton Style for the button */
    private TextButton.TextButtonStyle buttonStyle;

    /** Used to import the screenshots */
    private Texture texture1, texture2, texture3, texture4;

    /** Used to display the screenshots */
    private Image screenshot1, screenshot2, screenshot3, screenshot4;

    /** Controller of the Rule Screen */
    private RulesController rulesController;

    /**
     * Initialize the Rules Screen
     * @param gsm
     * @param previousScreen
     */
    public RulesScreen(GameStateManager gsm, int previousScreen) {
        super(gsm);
        Background = new Texture("Stone_bright_lv1.jpg");

        texture1 = new Texture("rules1.png");
        texture2 = new Texture("rules2.png");
        texture3 = new Texture("rules_start.png");
        texture4 = new Texture("rules_end.png");

        screenshot1 = new Image(texture1);
        screenshot2 = new Image(texture2);
        screenshot3 = new Image(texture3);
        screenshot4 = new Image(texture4);

        font = new BitmapFont();
        skin = new Skin(Gdx.files.internal("skin_directory/uiskin.json"));

        String text1 = "To start the game, touch on one of the legal moves represented as small dots. " +
                "The opponent's disc(s) in between your own disc(s) and the one you just placed change color.";
        String text2 = "Here you see an example of how the game works. If player black places a disc where the ring shows, " +
                "the white discs bounded by the now three black discs change color.";
        String text3 = "This is the result after the last move, and as you can see the white discs changed color. " +
                "Black is now up by four points.";
        String text4 = "The game ends when there's no legal moves left. The player with the most discs on the board wins.";

        label1 = new Label(text1, skin);
        label1.setWrap(true);
        label1.setFontScale(3);

        label2 = new Label(text2, skin);
        label2.setWrap(true);
        label2.setFontScale(3);

        label3 = new Label(text3, skin);
        label3.setWrap(true);
        label3.setFontScale(3);

        label4 = new Label(text4, skin);
        label4.setWrap(true);
        label4.setFontScale(3);

        stage = new Stage();
        table = new Table();

        table.setDebug(false);
        table.setFillParent(true);
        table.bottom();
        table.padTop(0.07f*cam.viewportHeight).padBottom(0.07f*cam.viewportHeight);

        this.previousScreen = previousScreen;

        atlas = new TextureAtlas();

        //Import the textures and create the atlas
        buttonNotPressed = new Texture("WoodenButton.png");
        buttonPressed = new Texture("WoodenButton_dark.png");
        atlas.addRegion("ButtonNotPressed", buttonNotPressed, 0,0,buttonNotPressed.getWidth(),buttonNotPressed.getHeight());
        atlas.addRegion("ButtonPressed", buttonPressed, 0,0,buttonPressed.getWidth(),buttonPressed.getHeight());
        skin.addRegions(atlas);
        font.getData().setScale(3);

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = skin.getDrawable("ButtonNotPressed");
        buttonStyle.down = skin.getDrawable("ButtonPressed");

        //Menu Button (when previousScreen = MENU_SCREEN)
        menuButton = new TextButton("Main Menu", buttonStyle);
        menuButton.getLabel().setColor(Color.GRAY);

        //Resume Game Button (when previousScreen = GAME_SCREEN)
        resumeGameButton = new TextButton("Resume Game", buttonStyle);
        resumeGameButton.getLabel().setColor(Color.GRAY);

        //Initiate controller
        this.rulesController = new RulesController(gsm, this);

        //Place all buttons on the view
        if (this.previousScreen == MENU_SCREEN) {
            menuButton.addListener(rulesController);
            table.add(menuButton).width(cam.viewportWidth-cam.viewportWidth/10).height(cam.viewportHeight/10).expandY()
                    .top().spaceBottom(cam.viewportHeight / 20);
            table.row();
        } else if (this.previousScreen == GAME_SCREEN) {
            resumeGameButton.addListener(rulesController);
            table.add(resumeGameButton).width(cam.viewportWidth-cam.viewportWidth/10).height(cam.viewportHeight/10).expandY()
                    .top().spaceBottom(cam.viewportHeight / 20);
            table.row();
        }

        //The two controller we used for this Rules Screen
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(rulesController);
        Gdx.input.setInputProcessor(inputMultiplexer);
        //Cancel the back key android paradigm
        Gdx.input.setCatchBackKey(true);

        //Add all the screenshots and labels to the screens
        table.row();
        table.add(screenshot3).width(cam.viewportWidth - cam.viewportWidth / 10).height(cam.viewportWidth - cam.viewportWidth / 10);
        table.row();
        table.add(label1).width(cam.viewportWidth - cam.viewportWidth / 10).height(500);
        table.row();
        label1.setAlignment(1);
        table.add(screenshot1).width(cam.viewportWidth - cam.viewportWidth / 10).height(cam.viewportWidth - cam.viewportWidth / 10);
        table.row();
        table.add(label2).width(cam.viewportWidth - cam.viewportWidth / 10).height(500);
        table.row();
        label2.setAlignment(1);
        table.add(screenshot2).width(cam.viewportWidth - cam.viewportWidth / 10).height(cam.viewportWidth - cam.viewportWidth / 10);
        table.row();
        table.add(label3).width(cam.viewportWidth - cam.viewportWidth / 10).height(500);
        table.row();
        label3.setAlignment(1);
        table.add(screenshot4).width(cam.viewportWidth - cam.viewportWidth / 10).height(cam.viewportWidth - cam.viewportWidth / 10);
        table.row();
        table.add(label4).width(cam.viewportWidth - cam.viewportWidth / 10).height(500);
        table.row();
        label3.setAlignment(1);

        scrollPane = new ScrollPane(table);

        table2 = new Table();
        table2.setFillParent(true);
        table2.add(scrollPane).fill().expand();

        stage.addActor(table2);
    }

    /**
     * Function to get the previous screen
     * @return the int corresponding to the screen
     */
    public int getPreviousScreen() { return previousScreen; }

    @Override
    public AbstractController getController() {
        return this.rulesController;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        //Draw the background
        int numberWidth = (int)cam.viewportWidth/Background.getWidth() + 1;
        int numberHeight = (int)cam.viewportHeight/Background.getHeight() + 1;
        for (int i =0;i<=numberHeight;i++) {
            for (int j=0;j<=numberWidth;j++) {
                sb.draw(Background,j*Background.getWidth(),i*Background.getHeight());
            }
        }

        sb.end();
        this.stage.act(Gdx.graphics.getDeltaTime());
        this.stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
        skin.dispose();
        atlas.dispose();
        buttonNotPressed.dispose();
        buttonPressed.dispose();
        skin.dispose();
        Background.dispose();
        texture1.dispose();
        texture2.dispose();
        texture3.dispose();
        texture4.dispose();
    }
}
