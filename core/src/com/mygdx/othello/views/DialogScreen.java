package com.mygdx.othello.views;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.mygdx.othello.controllers.GameStateManager;
import com.mygdx.othello.models.Board;

/**
 * A Dialog Window that pops out of the Screen when the game ends
 */
public class DialogScreen extends Dialog {

    /** The overall controller of the game that manages the saving, the Screens and Controllers */
    private GameStateManager gsm;

    /** Screen of the now ended game */
    private GameScreen gameScreen;

    /** Board Game Model */
    private Board board;

    /** Preferred width of the window */
    private int prefWidth;

    /** Preferred height of the window */
    private int prefHeight;

    /** Boolean indicating if the game ended because of the elapsed time or not */
    private boolean timeMode;

    /**
     * Initialize the Dialog Screen
     * @param title, String giving the name of the windwow
     * @param skin, contains the graphics used to display the screen (edges, buttons, etc..)
     * @param gsm, GameStateManager of the app
     * @param gameScreen, current GameScreen
     * @param board, current Board
     * @param width, width of the window
     * @param height, height of the window
     */
    public DialogScreen(String title, Skin skin, GameStateManager gsm, GameScreen gameScreen,
                        Board board, int width, int height, boolean timeMode) {
        super(title, skin);

        //Set the DialogScreen main parameters
        this.gsm = gsm;
        this.gameScreen = gameScreen;
        this.board = board;
        this.timeMode = timeMode;

        //Interaction settings of the window
        this.setModal(true);
        this.setResizable(false);
        this.setModal(false);

        //Dimensions parameters used for display
        prefWidth = width;
        prefHeight = height;
        float cellSize = gameScreen.getPointSize();

        this.getContentTable().defaults().padTop(0.3f*cellSize);

        this.getButtonTable().defaults().width(2*cellSize);
        this.getButtonTable().defaults().height(cellSize);
        this.getButtonTable().defaults().pad(0.5f*cellSize);

        //Set the dialog main message
        Label label = new Label("",skin);
        if (this.timeMode) {
            if (board.getTimeBlack() <= 0) {
                label = new Label("Black loses at the clock ! \n White wins the game", skin);
            } else if (board.getTimeWhite() <= 0) {
                label = new Label("White loses at the clock ! \n Black wins the game", skin);
            }
        } else {
            if (board.getScore().get(0) > board.getScore().get(1)) {
                label = new Label("Black wins the game with "+board.getScore().get(0)+" points !", skin);
            } else if (board.getScore().get(0) < board.getScore().get(1)){
                label = new Label("White wins the game with "+board.getScore().get(1)+" points !", skin);
            } else {
                label = new Label("That's a draw, each player has "+board.getScore().get(0)+ " points !", skin);
            }
        }


        label.setWrap(true); label.setFontScale(2.5f); label.setAlignment(Align.center);
        this.text(label);

        //Set the Main Menu/New Game buttons
        TextButton mainMenu = new TextButton("Main Menu", skin);
        mainMenu.getLabel().setFontScale(2f);
        this.button(mainMenu, "menu");

        TextButton newGame = new TextButton("New Game", skin);
        newGame.getLabel().setFontScale(2f);
        this.button(newGame, "new");
    }

    @Override
    public void result(Object obj) {
        if (obj.equals("menu")) {
            gsm.set(new MenuScreen(gsm));
        } else {
            gsm.set(new GameScreen(gsm, new Board(GameScreen.BOARD_SIZE,gameScreen.getIsAI())));
        }
    }

    @Override
    public float getPrefWidth() { return prefWidth; }

    @Override
    public float getPrefHeight() {return prefHeight;}
}
