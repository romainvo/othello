package com.mygdx.othello.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.othello.models.Board;
import com.mygdx.othello.views.GameScreen;
import com.mygdx.othello.views.MenuScreen;
import com.mygdx.othello.views.RulesScreen;

/**
 * This class manages the touch input on the Rules Screen
 */
public class RulesController extends AbstractController implements InputProcessor {
    /** Import the rules screen */
    private RulesScreen rs;

    /**
     * Initialize the Controller
     * @param gsm The GameStateManager
     * @param rs The RulesScreen
     */
    public RulesController(GameStateManager gsm, RulesScreen rs) {
        super(gsm);
        this.rs = rs;
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
        //Button to return to the main menu
        if (actor instanceof TextButton) {
            if (((TextButton) actor).getText().toString().equals("Main Menu")) {
                gsm.set(new MenuScreen(gsm));
                //Button to resume the game
            } else if (((TextButton) actor).getText().toString().equals("Resume Game")) {
                gsm.loadData();
                gsm.set(new GameScreen(gsm, new Board(gsm.gameData)));
            }
        }
    }

    /**
     * Use to return to the menu/game screen.
     * @param keycode, the int corresponding to the input
     * @return
     */
    @Override
    public boolean keyDown(int keycode) {
        if((keycode == Input.Keys.ESCAPE) || (keycode == Input.Keys.BACK)){
            if (rs.getPreviousScreen() == RulesScreen.MENU_SCREEN) {
                gsm.set(new MenuScreen(gsm));
            } else if (rs.getPreviousScreen() == RulesScreen.GAME_SCREEN) {
                gsm.set(new GameScreen(gsm, new Board(gsm.gameData)));
            }
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