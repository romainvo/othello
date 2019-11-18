package com.mygdx.othello;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.othello.controllers.GameStateManager;
import com.mygdx.othello.views.MenuScreen;

/**
 * Calls the create() method when first launching the app, it is the main class of the application
 */
public class MyOthelloGame extends ApplicationAdapter {
	/** The overall controller of the game that manages the saving, the screens and controllers */
	private GameStateManager gsm;
	SpriteBatch batch;

	/** Use to save the preferences of the game */
	private Preferences prefs;

	/** Nice music played while playing */
	private static Music music;

	@Override
	public void create () {
		batch = new SpriteBatch();
		Gdx.gl.glClearColor(1, 1, 1, 1);

		//Instanciate unique GameStateManager
		gsm = GameStateManager.getInstance();

		//Initialize the game preferences and the music
		prefs = Gdx.app.getPreferences("My preferences");
		prefs.putBoolean("ShowLegalMoves", true); prefs.flush();
		prefs.putBoolean("DisplayScore", true); prefs.flush();
		prefs.putBoolean("DisplayLastFlips", true); prefs.flush();

		music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		music.setLooping(true);
		music.setVolume(0.1f);
		music.play();
		if (prefs.getBoolean("IsMusicOff")) {
			music.pause();
		}
		gsm.push(new MenuScreen(gsm));
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	/**
	 * Called to get the background music of the game
	 * @return a Music
	 */
	public static Music getMusic() {
		return music;
	}
}
