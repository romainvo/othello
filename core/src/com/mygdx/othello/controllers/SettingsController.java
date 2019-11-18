package com.mygdx.othello.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.othello.MyOthelloGame;
import com.mygdx.othello.views.MenuScreen;

/**
 * This class manages the Settings logic, namely allows to save the preferences, and the touch
 * inputs on the screen
 */
public class SettingsController extends AbstractController implements InputProcessor {

    /** Button for the LegalMove display setting */
    private TextButton LegalMove;

    /** Button for the Score display setting */
    private TextButton ScoreDisplay;

    /** Button for the Last Flips display setting */
    private TextButton LastFlipsDisplay;

    /** Button for turning the music on/off */
    private TextButton MusicOff;

    /** Text displayed next to the slider for the handicap setting */
    private Label handicapLabel;

    /** Text displayed next to the slider for the Time Mode setting */
    private Label timeModeLabel;

    /** Import the preference */
    private Preferences prefs;

    /** Music from the game*/
    private Music music;

    /**
     * Initialize the controller
     * @param gsm The GameStateManager
     * @param LegalMove Button for legal move
     * @param ScoreDisplay Button to display the score
     * @param LastFlipsDisplay Button to display the last move
     * @param MusicOff Button to put the music On/Off
     * @param handicapLabel Button for the handicap
     * @param timeModeLabel Button for the time mode
     */
    public SettingsController(GameStateManager gsm, TextButton LegalMove,TextButton ScoreDisplay,
                              TextButton LastFlipsDisplay,TextButton MusicOff, Label handicapLabel,
                              Label timeModeLabel) {
        super(gsm);
        this.LegalMove = LegalMove;
        this.ScoreDisplay = ScoreDisplay;
        this.LastFlipsDisplay = LastFlipsDisplay;
        this.MusicOff = MusicOff;
        this.handicapLabel = handicapLabel;
        this.timeModeLabel = timeModeLabel;
        prefs = Gdx.app.getPreferences("My preferences");
        music = MyOthelloGame.getMusic();
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
        if (actor instanceof TextButton) {
            //Option display or not the legal moves
            if (((TextButton)actor).getText().toString().equals("Show Legal Moves")) {
                LegalMove.setText("Hide Legal Moves");
                LegalMove.getLabel().setColor(Color.RED);
                prefs.putBoolean("ShowLegalMoves", false);
                prefs.flush();
            } else if (((TextButton)actor).getText().toString().equals("Hide Legal Moves")) {
                LegalMove.setText("Show Legal Moves");
                LegalMove.getLabel().setColor(Color.GREEN);
                prefs.putBoolean("ShowLegalMoves", true);
                prefs.flush();
                //Options to display or not the score
            }else if (((TextButton)actor).getText().toString().equals("Display Score : Yes")) {
                ScoreDisplay.setText("Display Score : No");
                ScoreDisplay.getLabel().setColor(Color.RED);
                prefs.putBoolean("DisplayScore", false);
                prefs.flush();
            } else if (((TextButton)actor).getText().toString().equals("Display Score : No")) {
                ScoreDisplay.setText("Display Score : Yes");
                ScoreDisplay.getLabel().setColor(Color.GREEN);
                prefs.putBoolean("DisplayScore", true);
                prefs.flush();
                //Options to display or not the last flip
            } else if (((TextButton)actor).getText().toString().equals("Display Last Flips : Yes")) {
                LastFlipsDisplay.setText("Display Last Flips : No");
                LastFlipsDisplay.getLabel().setColor(Color.RED);
                prefs.putBoolean("DisplayLastFlips", false);
                prefs.flush();
            } else if (((TextButton)actor).getText().toString().equals("Display Last Flips : No")) {
                LastFlipsDisplay.setText("Display Last Flips : Yes");
                LastFlipsDisplay.getLabel().setColor(Color.GREEN);
                prefs.putBoolean("DisplayLastFlips", true);
                prefs.flush();
                //Options to play or not the music
            } else if (((TextButton)actor).getText().toString().equals("Music On")) {
                MusicOff.setText("Music Off");
                MusicOff.getLabel().setColor(Color.RED);
                prefs.putBoolean("IsMusicOff", true);
                prefs.flush();
                music.pause();
            } else if (((TextButton)actor).getText().toString().equals("Music Off")) {
                MusicOff.setText("Music On");
                MusicOff.getLabel().setColor(Color.GREEN);
                prefs.putBoolean("IsMusicOff", false);
                prefs.flush();
                music.play();
                //Button to exit to the menu screen
            } else if (((TextButton) actor).getText().toString().equals("Exit to Menu Screen")) {
                gsm.set(new MenuScreen(gsm));
                //Button to Erase the save
            } else if (((TextButton) actor).getText().toString().equals("Erase Save")) {
                FileHandle saveFile= Gdx.files.local("bin/GameData.json");
                if (saveFile.exists()) { if (saveFile.delete()) {System.err.println("Delete Data");}}
            }
            //Controller for the slider button about the handicap
        } else if (actor instanceof Slider) {
            if (((Slider)actor).getMaxValue() == 4) {
                prefs.putFloat("handicap",((Slider)actor).getValue());
                prefs.flush();
                handicapLabel.setText("Handicap : "+((Slider)actor).getValue());
            } else if (((Slider) actor).getMaxValue() == 10) {
                prefs.putFloat("time", ((Slider) actor).getValue());
                prefs.flush();
                timeModeLabel.setText("Time mode : "+prefs.getFloat("time")+" mn");
            }
        }
    }

    /**
     * Use to return to the menu screen.
     * @param keycode, the int corresponding to the input
     */
    @Override
    public boolean keyDown(int keycode) {
        if((keycode == Input.Keys.ESCAPE) || (keycode == Input.Keys.BACK)){
            gsm.push(new MenuScreen(gsm));
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