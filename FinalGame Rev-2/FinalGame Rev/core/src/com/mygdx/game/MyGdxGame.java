package com.mygdx.game;

import Screens.GameScreen;
import Screens.MenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public class MyGdxGame extends Game {

	public SpriteBatch batch;
	public static final int WIDTH = 480;
	public static final int HEIGHT = 720;

	public ArrayList<Integer> HSdata;

	@Override
	public void create () {
		batch = new SpriteBatch();
		HSdata = new ArrayList<>();
		this.setScreen(new MenuScreen(this));
		dispose();
	}

	@Override
	public void render () {
		super.render();
	}



}
