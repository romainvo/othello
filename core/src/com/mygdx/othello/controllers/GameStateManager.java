package com.mygdx.othello.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.othello.models.Board;
import com.mygdx.othello.models.Cell;
import com.mygdx.othello.models.GameData;
import com.mygdx.othello.views.AbstractScreen;
import com.mygdx.othello.views.GameScreen;

import java.util.ArrayList;
import java.util.Stack;

/**
 * This class manages the overall state of the game through a Stack of Screen and a GameData class
 * it is the overall controller of the game that manages the saving, the screens and controllers
 */
public class GameStateManager {
    /** Stack of AbstractScreen that the app renders on the screen */
    private Stack<AbstractScreen> abstractScreens;

    /** Ensures that there is only one instance of GameStateManager */
    private static GameStateManager ourInstance = new GameStateManager();

    /** Variable use to load/save the game data */
    public GameData gameData;

    /** This json file is used to store the game data while the app is terminated */
    private Json json = new Json();
    private FileHandle fileHandle = Gdx.files.local("bin/GameData.json");

    /**
     *
     */
    public GameStateManager() {
        abstractScreens = new Stack<AbstractScreen>();
    }

    /**
     * Test if a previous game is saved in the app
     * And load it in a GameData instance if needs be
     */
    public void initializeGameData() {
        if (!fileHandle.exists()) {
            gameData = new GameData();
            System.err.println("Create Data");
        } else {
            loadData();
        }
    }

    /** Save the game data stored temporary in a GameData instance into the json file */
    public void saveData() {
        if (gameData != null) {
            fileHandle.writeString(Base64Coder.encodeString(json.prettyPrint(gameData)),false);
            System.err.println("Save Data");
        }
    }

    /** Load the game data saved into the json file in a GameData instance */
    public void loadData() {
        gameData = json.fromJson(GameData.class, Base64Coder.decodeString(fileHandle.readString()));
        System.err.println("Load Data");
    }

    /** Ensures that the GameStateManager instance is unique */
    public static GameStateManager getInstance() { return ourInstance; }

    /** Push a Screen forward */
    public void push(AbstractScreen abstractScreen) {
        abstractScreens.push(abstractScreen);
    }

    /** Pop a Screen (dispose it) */
    public void pop() {
        abstractScreens.pop().dispose();
    }

    /** Pop the current Screen and push forward a new Screen */
    public void set(AbstractScreen abstractScreen) {
        abstractScreens.pop().dispose();
        abstractScreens.push(abstractScreen);

        if (abstractScreen instanceof GameScreen) {
            final GameScreen gameScreen = (GameScreen) abstractScreen;
            final Board board = gameScreen.getBoard();

            //Disable all buttons on the board while the game logic is starting (prevent the flip animation from bugging)
            ArrayList<Cell> emptyCells = new ArrayList<Cell>(board.getCells());
            ArrayList<Cell> activeCells = new ArrayList<Cell>();
            activeCells.addAll(board.getActivePlayerCells(1)); activeCells.addAll(board.getActivePlayerCells(2));
            emptyCells.removeAll(activeCells);
            for (Cell c : emptyCells) {
                gameScreen.getBoardButtons().get(c.getPos()).setDisabled(true);
            }

            float delay = 0.75f; // seconds
            Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                    ArrayList<Cell> emptyCells = new ArrayList<Cell>(board.getCells());
                    ArrayList<Cell> activeCells = new ArrayList<Cell>();
                    activeCells.addAll(board.getActivePlayerCells(1)); activeCells.addAll(board.getActivePlayerCells(2));
                    emptyCells.removeAll(activeCells);
                    for (Cell c : emptyCells) {
                        gameScreen.getBoardButtons().get(c.getPos()).setDisabled(false);
                    }
                }
            }, delay);
        }
    }

    /** Called the update method of the active Controller */
    public void update(float dt) {
        abstractScreens.peek().getController().update(dt);
    }

    /** Render the game on the SpriteBatch */
    public void render(SpriteBatch sb) {
        abstractScreens.peek().render(sb);
    }

}
