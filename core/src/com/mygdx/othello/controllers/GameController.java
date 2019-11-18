package com.mygdx.othello.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.othello.models.Board;
import com.mygdx.othello.models.Cell;
import com.mygdx.othello.views.DialogScreen;
import com.mygdx.othello.views.GameScreen;
import com.mygdx.othello.views.MenuScreen;
import com.mygdx.othello.views.RulesScreen;
import java.util.ArrayList;
import java.util.Map;

/**
 * This class manages the game logic while playing as well as all the touch inputs on the screen
 */
public class GameController extends AbstractController implements InputProcessor {

    /** Current GameScreen */
    protected GameScreen gameScreen;

    /** Current Board */
    protected Board board;

    /** Current saved preferences, concerns the game settings/options */
    protected Preferences prefs;

    /** Boolean that indicates in the games is ending (used for the end game in time mode) */
    private boolean endGame;

    /**
     * Initialize the controller
     * @param gsm
     * @param gameScreen
     * @param board
     */
    public GameController(GameStateManager gsm, GameScreen gameScreen, Board board) {
        super(gsm);
        this.gameScreen = gameScreen;
        this.board = board;
        this.prefs = Gdx.app.getPreferences("My preferences");
        endGame = false;
    }

    /**
     * This method checks if the game ends and calls a DialogScreen if needed
     * @param timeMode, boolean indicating if the game is in time mode
     */
    public void endGame(boolean timeMode) {
        int playerTurn = board.getPlayerTurn();
        Map<Cell, ArrayList<Cell>> legalMoves = board.getLegalMoves(playerTurn);

        //Check if there are still moves to play or if the time is elapsed
        if (legalMoves.isEmpty()) {
            Skin skin = new Skin(Gdx.files.internal("skin_directory/uiskin.json"));
            int cellSize = gameScreen.getPointSize();
            int width = 6 * cellSize; int height = 3*cellSize;
            DialogScreen dialog = new DialogScreen("", skin, gsm, gameScreen, board, width, height, false);
            Gdx.input.setInputProcessor(gameScreen.getDialogStage());
            dialog.show(gameScreen.getDialogStage());
        } else if (timeMode) {
            Skin skin = new Skin(Gdx.files.internal("skin_directory/uiskin.json"));
            int cellSize = gameScreen.getPointSize();
            int width = 6 * cellSize; int height = 3*cellSize;
            DialogScreen dialog = new DialogScreen("", skin, gsm, gameScreen, board, width, height, true);
            Gdx.input.setInputProcessor(gameScreen.getDialogStage());
            dialog.show(gameScreen.getDialogStage());
        }
    }

    /**
     * Save all the useful game data in the class :
     * (player turn, size of the board, powns position, etc..)
     * @param timeMode
     */
    public void saveData(boolean timeMode) {
        //Save the current player turn
        gsm.gameData.setPlayerTurn(board.getPlayerTurn());

        //Save the current board size
        gsm.gameData.setBoardSize(board.getBoardSize());

        //Save the cells value
        int[] temp = new int[board.getBoardSize()*board.getBoardSize()];
        for (int i=0; i<temp.length; i++) {
            temp[i] = board.getCells().get(i).getValue();
        }
        gsm.gameData.setCellsValue(temp);

        //Save the last flipped cells
        temp = new int[board.getBoardSize()*board.getBoardSize()];
        if (board.getLastFlips() != null) {
            for (Cell c : board.getLastFlips()) {
                temp[c.getPos()] = 1;
            }
        }
        gsm.gameData.setLastFlips(temp);

        //Save the remaining time for both players
        if (timeMode) {
            gsm.gameData.setTimeBlack(board.getTimeBlack());
            gsm.gameData.setTimeWhite(board.getTimeWhite());
        }

        //Save the AI mode
        gsm.gameData.setAI(gameScreen.getIsAI());

        gsm.saveData();
    }

    @Override
    /**
     * This method is used to update the remaining time for both
     * players when playing in Time Mode
     */
    public void update(float dt) {
        //Update the remaining time for both players and call the engGame method when time is elapsed
        //for one of the players
        if ((board.getTimeWhite() > 0 || board.getTimeBlack() > 0)
                && !gameScreen.getIsAI() && !endGame) {
            if (board.getPlayerTurn() == 1) {
                float temp = board.getTimeBlack();
                board.setTimeBlack(temp - dt);
            } else {
                float temp = board.getTimeWhite();
                board.setTimeWhite(temp - dt);
            }
            if (board.getTimeBlack() <= 0 || board.getTimeWhite() <= 0) {
                endGame = true;
                this.endGame(true);
            }
        }
    }

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        // If the button is part of the game cells
        if (actor instanceof ImageButton) {
            int playerTurn = board.getPlayerTurn();
            Map<Cell, ArrayList<Cell>> legalMoves = board.getLegalMoves(playerTurn);
            int index = gameScreen.getBoardButtons().indexOf(actor);

            int oppositePlayer = (playerTurn == 1 ? 2 : 1);

            //Places a pown at the touch location
            if (board.placeDisc(index, board.getPlayerTurn(), legalMoves)) {
                ((ImageButton) actor).setDisabled(true);

                //Disable all buttons on the board while the flip animation is running
                ArrayList<Cell> emptyCells = new ArrayList<Cell>(board.getCells());
                ArrayList<Cell> activeCells = new ArrayList<Cell>();
                activeCells.addAll(board.getActivePlayerCells(1)); activeCells.addAll(board.getActivePlayerCells(2));
                emptyCells.removeAll(activeCells);
                for (Cell c : emptyCells) {
                    gameScreen.getBoardButtons().get(c.getPos()).setDisabled(true);
                }

                float delay = 1f; // seconds
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

                board.setPlayerTurn(oppositePlayer);

                //Check if the game ends
                legalMoves = board.getLegalMoves(oppositePlayer);
                int nextPlayer = (oppositePlayer == 1 ? 2 : 1);
                Map<Cell, ArrayList<Cell>> legalMovesNextPlayer = board.getLegalMoves(nextPlayer);

                if (legalMoves.isEmpty() && legalMovesNextPlayer.isEmpty()) {
                    //Check if the game ends
                    //timeMode = false because we check the score condition and not the time condition
                    this.endGame(false);
                } else if (legalMoves.isEmpty() && !legalMovesNextPlayer.isEmpty()) {
                    board.setPlayerTurn(nextPlayer);
                }
            }

        // If the button is an 'option' button
        } else if (actor instanceof TextButton) {
            if (((TextButton) actor).getText().toString().equals("Main Menu")) {
                this.saveData(board.getTimeBlack()>0);
                gsm.set(new MenuScreen(gsm));
            }else if (((TextButton)actor).getText().toString().equals("Rules")) {
                this.saveData(board.getTimeBlack() > 0);
                gsm.set(new RulesScreen(gsm, RulesScreen.GAME_SCREEN));
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            this.saveData(board.getTimeBlack()>0);
            gsm.set(new MenuScreen(gsm));
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
