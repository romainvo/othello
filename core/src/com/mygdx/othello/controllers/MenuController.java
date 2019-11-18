package com.mygdx.othello.controllers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.othello.models.Board;
import com.mygdx.othello.views.GameScreen;
import com.mygdx.othello.views.RulesScreen;
import com.mygdx.othello.views.SettingsScreen;

/**
 * This class manages touch inputs on the Menu Screen
 */
public class MenuController extends AbstractController {
    /** Boolean to check if the game is in AI mode */
    private boolean isAI;

    /**
     * Initialize the Menu Controller
     * @param gsm The GameStateManager
     */
    public MenuController(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void update(float dt) {
    }

    @Override
    /**
     * For each button launch the Screen corresponding.
     * For instance Player vs Player launch a multiplayer game on the same device etc ...
     */
    public void changed(ChangeEvent event, Actor actor) {
        if (((TextButton)actor).getText().toString().equals("Player VS Player")) {
            isAI = false;
            gsm.set(new GameScreen(gsm, new Board(GameScreen.BOARD_SIZE, isAI)));
        } else if (((TextButton)actor).getText().toString().equals("Settings")) {
            gsm.set(new SettingsScreen(gsm));
        } else if (((TextButton)actor).getText().toString().equals("Player VS AI")) {
            isAI = true;
            gsm.set(new GameScreen(gsm, new Board(GameScreen.BOARD_SIZE, isAI)));
        } else if (((TextButton)actor).getText().toString().equals("Resume Game")) {
            isAI = gsm.gameData.isAI();
            gsm.set(new GameScreen(gsm, new Board(gsm.gameData)));
        } else if (((TextButton)actor).getText().toString().equals("Rules")) {
            gsm.set(new RulesScreen(gsm, RulesScreen.MENU_SCREEN));
        }
    }
}
