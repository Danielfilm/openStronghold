package com.heerbann.stronghold.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class InteractiveViewport extends ScreenViewport{
		
	public InteractiveViewport(Camera camera){
		super(camera);
	}

	@Override
	public void update (int screenWidth, int screenHeight, boolean centerCamera) {
		Vector2 scaled = Scaling.fit.apply(screenWidth, screenHeight, screenWidth, screenHeight);

		// Extend in the short direction.
		int viewportWidth = Math.round(scaled.x);
		int viewportHeight = Math.round(scaled.y);
		if (viewportWidth < screenWidth) {
			float toViewportSpace = viewportHeight / screenHeight;
			float toWorldSpace = screenHeight / viewportHeight;
			float lengthen = (screenWidth - viewportWidth) * toWorldSpace;
			screenWidth += lengthen;
			viewportWidth += Math.round(lengthen * toViewportSpace);
		} else if (viewportHeight < screenHeight) {
			float toViewportSpace = viewportWidth / screenWidth;
			float toWorldSpace = screenWidth / viewportWidth;
			float lengthen = (screenHeight - viewportHeight) * toWorldSpace;
			screenWidth += lengthen;
			viewportHeight += Math.round(lengthen * toViewportSpace);
		}
		setWorldSize(screenWidth, screenHeight);
		setScreenBounds(super.getScreenX(), super.getScreenY(), viewportWidth, viewportHeight);
		apply();
	}

}
