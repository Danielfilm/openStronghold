package com.heerbann.stronghold;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class EntryPoint extends ApplicationAdapter {
	
	private final Engine engine = new Engine();
	private boolean isDebug = false, isPaused = false;
	
	private SpriteBatch batch;
	private ShapeRenderer shape;
	
	@Override
	public void create () {
		Stronghold.app = this;
		Stronghold.inputs = new InputManager();
		
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		if(!isPaused) engine.update(delta);
		engine.render(delta, getSpriteBatch());
		if(isDebug) engine.debug(delta, getShapeRenderer());
	}
	
	@Override
	public void dispose () {
		if(batch != null) batch.dispose();
		if(shape != null) shape.dispose();
	}
	
	@Override
	public void pause () {
		isPaused = true;
	}

	@Override
	public void resume () {
		isPaused = false;
	}
	
	public ShapeRenderer getShapeRenderer() {
		if(shape == null) shape = new ShapeRenderer();
		return shape;
	}
	
	public SpriteBatch getSpriteBatch() {
		if(batch == null) batch = new SpriteBatch();
		return batch;
	}
}
