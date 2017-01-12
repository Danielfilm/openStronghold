
package com.heerbann.stronghold.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface LoopProperty extends ZIndexProperty{
	public void update (float deltaTime);

	public void render (float deltaTime, SpriteBatch batch);

	public void debug (float deltaTime, ShapeRenderer shape);
}
