package com.mygdx.othello.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.othello.models.Board;
import com.mygdx.othello.models.Cell;
import com.mygdx.othello.views.GameScreen;
import com.mygdx.othello.views.MenuScreen;
import com.mygdx.othello.views.RulesScreen;

import java.util.ArrayList;
import java.util.Map;

/**
 * This class manipulates the game logic while playing as well as all the touch input on the screen
 * Only used when playing versus the AI
 */
public class AIGameController extends GameController {

    /**
     * @param gsm GameStateManager
     * @param gameScreen
     * @param board The board of the game
     */
    public AIGameController(GameStateManager gsm, GameScreen gameScreen, Board board) {
        super(gsm,gameScreen,board);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void changed(ChangeEvent event, Actor actor) {

        // If the button is part of the game cells
        if (actor instanceof ImageButton) {
            int playerTurn = board.getPlayerTurn();
            Map<Cell, ArrayList<Cell>> legalMoves = board.getLegalMoves(playerTurn);
            if (playerTurn == 1) {
                System.out.println("Player");
                int index = gameScreen.getBoardButtons().indexOf(actor);
                if (board.placeDisc(index, board.getPlayerTurn(), legalMoves)) {
                    ((ImageButton) actor).setDisabled(true);
                    board.setPlayerTurn(2);
                    super.endGame(false);
                }

            }

            //Disable all the buttons on the screen while the AI is playing
            ArrayList<Cell> emptyCells = new ArrayList<Cell>(board.getCells());
            ArrayList<Cell> activeCells = new ArrayList<Cell>();
            activeCells.addAll(board.getActivePlayerCells(1)); activeCells.addAll(board.getActivePlayerCells(2));
            emptyCells.removeAll(activeCells);
            for (Cell c : emptyCells) {
                gameScreen.getBoardButtons().get(c.getPos()).setDisabled(true);
            }


            float delay = 1.5f; // seconds

            //Play as the AI : Choose a random move from the legal moves
            Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                    if (board.getPlayerTurn() == 2 ) {
                        Map<Cell, ArrayList<Cell>> legalMoves = board.getLegalMoves(2);
                        System.out.println("IA");
                        int i = 0;
                        int size = legalMoves.size();
                        System.out.println(size);
                        int randomNumber = (int) (Math.random() * (size));
                        System.out.println(randomNumber);
                        for (Cell c : legalMoves.keySet()) {
                            if (i == randomNumber) {
                                System.out.println(c.getPos());
                                if (board.placeDisc(c.getPos(), board.getPlayerTurn(), legalMoves)) {
                                    gameScreen.getBoardButtons().get(c.getPos()).setDisabled(true);
                                    board.setPlayerTurn(1);
                                    AIGameController.super.endGame(false);
                                }
                                break;
                            }
                            i++;
                        }
                    }
                }
            }, delay);

            //Wait for the AI pawns to flip and then able them again
            delay = 2.5f; // seconds
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

        // If the button is an 'option' button
        } else if (actor instanceof TextButton) {
            if (((TextButton) actor).getText().toString().equals("Main Menu")) {
                this.saveData(false);
                gsm.set(new MenuScreen(gsm));
            } else if (((TextButton)actor).getText().toString().equals("Rules")) {
                this.saveData(false);
                gsm.set(new RulesScreen(gsm, RulesScreen.GAME_SCREEN));
            }
        }
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            this.saveData(false);
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
