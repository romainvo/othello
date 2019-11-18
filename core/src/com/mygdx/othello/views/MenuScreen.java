
package com.mygdx.othello.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.othello.controllers.AbstractController;
import com.mygdx.othello.controllers.GameStateManager;
import com.mygdx.othello.controllers.MenuController;

/**
 * This class manages the menu display, knowing the buttons to switch to the other screens 
 */
public class MenuScreen extends AbstractScreen {

    /** Container for the only table and input processor */
    private Stage stage;
    
    /** Container for the buttons */
    private Table table;
    
    /** Font used in the Text Buttons */
    private BitmapFont font;
    
    /** Skin of the Text Buttons */
    private Skin skin;

    /** Atlas used to store all the buttons' textures */
    private TextureAtlas atlas;
    
    /** Texture when button not pressed */
    private Texture buttonNotPressed;
    
    /** Texture when button pressed */
    private Texture buttonPressed;
    
    /** Texture for the title 'button' */
    private Texture menuTexture;
    
    /** Background texture */
    private Texture Background;

    /** Title button always disabled to display the game title */
    private TextButton GameName;
    
    /** Resume Game Button */
    private TextButton Resume;
    
    /** Player vs Player Button */
    private TextButton Offline;
    
    /** Player vs AI Button */
    private TextButton AIMode;
    
    /** Settings Button */
    private TextButton Settings;
    
    /** Rules Button */
    private TextButton Rules;

    /** TextButton Style for the title button */
    private TextButton.TextButtonStyle titleStyle;

    /** TextButton Style for all buttons */
    private TextButton.TextButtonStyle buttonStyle;

    /** The controller of all buttons */
    private MenuController menuController;

    /**
     * Initialize the Menu Screen
     * @param gsm
     */
    public MenuScreen(GameStateManager gsm) {
        super(gsm);
        //Initialize the game data to search for potential previous save
        super.gsm.initializeGameData();
        Background = new Texture("Stone_bright_lv1.jpg");
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        table = new Table(); table.setDebug(false);
        table.setFillParent(true); table.bottom();
        table.padTop(0.07f*cam.viewportHeight).padBottom(0.07f*cam.viewportHeight);
        font = new BitmapFont();
        skin = new Skin();

        atlas = new TextureAtlas();

        //Add all the png
        buttonNotPressed = new Texture("WoodenButton.png");
        buttonPressed = new Texture("WoodenButton_dark.png");
        menuTexture = new Texture("MainMenu.png");
        atlas.addRegion("ButtonNotPressed", buttonNotPressed, 0,0,buttonNotPressed.getWidth(),buttonNotPressed.getHeight());
        atlas.addRegion("ButtonPressed", buttonPressed, 0,0,buttonPressed.getWidth(),buttonPressed.getHeight());
        atlas.addRegion("MenuButton",menuTexture,0,0,menuTexture.getWidth(),menuTexture.getHeight());
        skin.addRegions(atlas);
        font.getData().setScale(3);

        //The label for the name of the game
        titleStyle = new TextButton.TextButtonStyle();
        titleStyle.font = font;
        titleStyle.up = skin.getDrawable("MenuButton");
        GameName = new TextButton("Othello 2.0", titleStyle);
        GameName.getLabel().setColor(Color.WHITE);

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = skin.getDrawable("ButtonNotPressed");
        buttonStyle.down = skin.getDrawable("ButtonPressed");

        //Optional button for resume mode
        Resume = new TextButton("Resume Game", buttonStyle);
        Resume.getLabel().setColor(Color.GRAY);

        //The first button for offline mode
        Offline = new TextButton("Player VS Player", buttonStyle);
        Offline.getLabel().setColor(Color.GRAY);

        //Second button for the AIMode mode
        AIMode = new TextButton("Player VS AI", buttonStyle);
        AIMode.getLabel().setColor(Color.GRAY);

        //Third button for the settings
        Settings = new TextButton("Settings",buttonStyle);
        Settings.getLabel().setColor(Color.GRAY);

        //Last button for the rules
        Rules = new TextButton("Rules", buttonStyle);
        Rules.getLabel().setColor(Color.GRAY);

        //Add controllers
        this.menuController = new MenuController(gsm);
        Offline.addListener(menuController);
        AIMode.addListener(menuController);
        Settings.addListener(menuController);
        Rules.addListener(menuController);
        Resume.addListener(menuController);

        //Place all button on the view
        table.add(GameName).width(cam.viewportWidth-cam.viewportWidth/10).height(cam.viewportHeight/10).expandY().top();
        table.row();
        if (!gsm.gameData.isEmpty()) {
            table.add(Resume).width(cam.viewportWidth-cam.viewportWidth/10).height(cam.viewportHeight/10).spaceBottom(cam.viewportHeight/20);
            table.row();
        }
        table.add(Offline).width(cam.viewportWidth-cam.viewportWidth/10).height(cam.viewportHeight/10).spaceBottom(cam.viewportHeight/20);
        table.row();
        table.add(AIMode).width(cam.viewportWidth-cam.viewportWidth/10).height(cam.viewportHeight/10).spaceBottom(cam.viewportHeight/20);
        table.row();
        table.add(Settings).width(cam.viewportWidth-cam.viewportWidth/10).height(cam.viewportHeight/10).spaceBottom(cam.viewportHeight/20);
        table.row();
        table.add(Rules).width(cam.viewportWidth-cam.viewportWidth/10).height(cam.viewportHeight/10).spaceBottom(cam.viewportHeight/20);

        stage.addActor(table);

        GameName.setDisabled(true);


    }

    @Override
    public AbstractController getController() {
        return this.menuController;
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

        //Draw the stage
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
        menuTexture.dispose();
    }
}
