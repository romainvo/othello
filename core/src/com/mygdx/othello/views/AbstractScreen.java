package com.mygdx.othello.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.othello.controllers.AbstractController;
import com.mygdx.othello.controllers.GameStateManager;

/**
 * This class is a template for all the Screens in the application
 */
public abstract class AbstractScreen {
    /** Orthographic camera that manages the render dimensions on the screen */
    protected OrthographicCamera cam;

    /** The overall controller of the game that manages the saving, the Screens and Controllers */
    protected GameStateManager gsm;

    /**
     * Initialize the AbstractScreen
     * @param gsm
     */
    protected AbstractScreen(GameStateManager gsm) {
        this.gsm = gsm;
        cam = new OrthographicCamera();
        cam.setToOrtho(true,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Returns the Controller of the current Screen
     * @return an AbstractController
     */
    public abstract AbstractController getController();

    /**
     * Renders all textures and graphics on the screen
     * @param sb
     */
    public abstract void render(SpriteBatch sb);

    /**
     * Dispose the stage, textures and font saved on the disk
     */
    public abstract void dispose();

}
