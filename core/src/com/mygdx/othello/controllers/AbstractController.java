package com.mygdx.othello.controllers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * This class is the template for all Controllers in the application
 */
public abstract class AbstractController extends ChangeListener {

    /** The overall controller of the game that manages the saving and the screens and controllers switch */
    protected GameStateManager gsm;

    protected AbstractController(GameStateManager gsm) {
        this.gsm = gsm;
    }

    /** This method is called at each delta time and allows the Controller to update the dynamic variables */
    public abstract void update(float dt);

    @Override
    public void changed(ChangeEvent event, Actor actor) {

    }
}
