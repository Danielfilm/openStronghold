package com.heerbann.stronghold;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.heerbann.stronghold.utils.LoopProperty;

public class Engine {
	
	private final Array<LoopProperty> cache = new Array<LoopProperty>();
	private int cacheSize = 0;

	public void update (float deltaTime) {
		for(int i = 0; i < cacheSize; i++)
			cache.get(i).update(deltaTime);
	}

	public void render (float deltaTime, SpriteBatch batch) {
		for(int i = 0; i < cacheSize; i++)
			cache.get(i).render(deltaTime, batch);
	}

	public void debug (float deltaTime, ShapeRenderer shape) {
		for(int i = 0; i < cacheSize; i++)
			cache.get(i).debug(deltaTime, shape);
	}
	
	public void addProperty(LoopProperty property){
		cache.add(property);
		cache.sort(Stronghold.comperator);
	}
	
	public void removeProperty(LoopProperty property){
		cache.removeValue(property, false);
		cache.sort(Stronghold.comperator);
	}

}
