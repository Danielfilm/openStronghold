package com.heerbann.stronghold.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.heerbann.stronghold.Stronghold;
import com.heerbann.stronghold.tilemap.TileMap;
import com.heerbann.stronghold.utils.InteractiveViewport;
import com.heerbann.stronghold.utils.LoopProperty;

public class TileRenderer extends InputAdapter implements LoopProperty{
		
	private final InteractiveViewport viewport = new InteractiveViewport(new OrthographicCamera());

	private final TileMap map;
	private final SpriteBatch batch;
	
	private int screenX, screenY, screenWidth, screenHeight;
	
	public TileRenderer(TileMap map, SpriteBatch batch, int screenX, int screenY, int screenWidth, int screenHeight){
		this.map = map;
		this.batch = batch;
		this.screenX = screenX;
		this.screenY = screenY;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		Stronghold.inputs.addInputProcessor(this);
	}
	
	public TileRenderer(TileMap map, SpriteBatch batch){
		this(map, batch, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	@Override
	public void update (float deltaTime) {
		
	}
	
	private FrameBuffer buffer;
	private boolean cameraChanged = true;
	private final Sprite sprite = new Sprite();

	@Override
	public void render (float deltaTime, SpriteBatch batch) {
		if(cameraChanged){
			cameraChanged = false;
			viewport.getCamera().update();			
			drawToBuffer();								
		}else{
			viewport.setScreenBounds(screenX, screenY, screenWidth, screenHeight);
			viewport.apply();
		}
		batch.begin();
		sprite.draw(batch);
		batch.end();
	}
	
	private final IntArray points = new IntArray();
	
	private void drawToBuffer(){
		buffer.dispose();
		buffer = new FrameBuffer(Format.RGBA8888, screenWidth, screenHeight, false);
		buffer.begin();
		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.begin();
		
		map.getRenderPoints((OrthographicCamera)viewport.getCamera(), points);
		
		for(int i = 0; i < points.size; i+=2){
			int pX = points.get(i);
			int pY = points.get(i + 1);		
			long tile = map.map[map.convertToIndex(pX, pY)];		
			Vector2 pos = map.convertToGlobal(pX, pY);
			Sprite s = Stronghold.manager.getTile((short)(tile & TileMap.maskShort));			
			s.setCenter(pos.x, pos.y);
			s.draw(batch);
		}
		batch.end();
		buffer.end(screenX, screenY, screenWidth, screenHeight);
		sprite.setTexture(buffer.getColorBufferTexture());		
	}

	@Override
	public void debug (float deltaTime, ShapeRenderer shape) {
		
	}
	
	private int zIndex = 0;
	
	public void setZ(int newZ){
		this.zIndex = newZ;
	}

	@Override
	public int getZ () {
		return zIndex;
	}
	
	private int panButton = Buttons.LEFT;
	
	private boolean panLockX = false, panLockY = false;
	private boolean zoomLock;
	private float zoomIncrement = 0.25f, minZoom = 1, maxZoom = 10;
	
	/**
	 * @return the {@link Buttons} used for panning in this camera
	 */
	public int getPanButton() {
		return panButton;
	}

	/**
	 * @param panButton the {@link Buttons} used for panning in this camera
	 */
	public void setPanButton(int panButton) {
		this.panButton = panButton;
	}

	/**
	 * if true the camera can't be panned in x direction. default = false
	 */
	public boolean isPanLockX() {
		return panLockX;
	}

	/**
	 * if true the camera can't be panned in x direction. default = false
	 */
	public void setPanLockX(boolean panLockX) {
		this.panLockX = panLockX;
	}

	/**
	 * if true the camera can't be panned in y direction. default = false
	 */
	public boolean isPanLockY() {
		return panLockY;
	}

	/**
	 * if true the camera can't be panned in y direction. default = false
	 */
	public void setPanLockY(boolean panLockY) {
		this.panLockY = panLockY;
	}

	/**
	 * if true the camera can't be zoomed. default = false
	 */
	public boolean isZoomLock() {
		return zoomLock;
	}

	/**
	 * if true the camera can't be zoomed. default = false
	 */
	public void setZoomLock(boolean zoomLock) {
		this.zoomLock = zoomLock;
	}

	/**
	 * @return the min zoom. default = 1
	 */
	public float getMinZoom() {
		return minZoom;
	}

	/**
	 * @param minZoom the min zoom. default = 1
	 */
	public void setMinZoom(float minZoom) {
		this.minZoom = minZoom;
	}

	/**
	 * @return the max zoom. default = 10
	 */
	public float getMaxZoom() {
		return maxZoom;
	}

	/**
	 * @param the max zoom. default = 10 (max: 1000)
	 */
	public void setMaxZoom(float maxZoom) {
		this.maxZoom = maxZoom;
	}

	/**
	 * Translates the camera (not the viewport) by the given delta
	 * @param dx
	 * @param dy
	 */
	public void translateCamera(float dx, float dy){
		OrthographicCamera c = (OrthographicCamera) viewport.getCamera();
		c.translate((panLockX)?0:dx*c.zoom, (panLockY)?0:dy*c.zoom, 0);
	}
	
	/**
	 * Sets the camera (and not the viewport) to the given position
	 * @param x
	 * @param y
	 */
	public void setCameraPosition(float x, float y){
		viewport.getCamera().position.x = x;
		viewport.getCamera().position.y = y;
	}
	
	private void zoomCamera(int direction){
		OrthographicCamera c = (OrthographicCamera) viewport.getCamera();
		c.zoom += direction*zoomIncrement;
		c.zoom = (c.zoom > maxZoom)?maxZoom:(c.zoom < minZoom)?minZoom:c.zoom;
	}
	
	/**
	 * Sets the camera to the given zoom. (<=1 closest; 1000 max out)
	 * @param newZoom
	 */
	public void setCameraZoom(float newZoom){
		((OrthographicCamera) viewport.getCamera()).zoom = (newZoom > maxZoom)?maxZoom:(newZoom < minZoom)?minZoom:newZoom;
	}
	
	/**
	 * @return the current zoom of the camera
	 */
	public float getCameraZoom(){
		return ((OrthographicCamera) viewport.getCamera()).zoom;
	}
	
	private final Vector2 mouseWorldPosition = new Vector2(), mouseScreenPosition = new Vector2();
	private boolean isMouseOver = false;
	
	private void calculateMouseOver(int screenX, int screenY){
		isMouseOver = viewport.getScreenX() <= screenX && viewport.getScreenX() + viewport.getScreenWidth() >= screenX && viewport.getScreenY() <= screenY && viewport.getScreenY() + viewport.getScreenHeight() >= screenY;
	}
	
	/**
	 * @return the vector of the mouse position in world coordinates. it is updated every frame.
	 */
	public Vector2 getMouseWorldPosition() {
		return mouseWorldPosition;
	}
	
	/**
	 * @return the vector of the mouse position in screen coordinates. it is updated every frame.
	 */
	public Vector2 getMouseScreenPosition() {
		return mouseScreenPosition;
	}
	
	/**
	 * @return if the mouse pointer is currently over the viewport
	 */
	public boolean isMouseOver(){
		return isMouseOver;
	}
	
	/**
	 * Projects the given point to viewport coordinates
	 * @param input
	 * @return input
	 */
	public Vector2 projectScreenToVirtual(Vector2 input){
		return input.sub(viewport.getScreenWidth()/2, viewport.getScreenHeight()/2).sub(viewport.getScreenX(), viewport.getScreenY()).scl(1, -1);
	}
	
	/**
	 * unprojects the given vector
	 * @param input
	 * @return
	 */
	public Vector2 unprojectCoordinates(Vector2 input){
		return viewport.unproject(input);
	}
	
	private final Vector2 oldPosition = new Vector2(), deltaPan = new Vector2();
	private boolean override = false;
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {	
		override = isMouseOver() && panButton == button;
		oldPosition.set(screenX, screenY);
		calculateMouseOver(screenX, Gdx.graphics.getHeight() - screenY);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		oldPosition.set(screenX, screenY);
		override = false;
		calculateMouseOver(screenX, Gdx.graphics.getHeight() - screenY);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {	
		calculateMouseOver(screenX, Gdx.graphics.getHeight() - screenY);
		projectScreenToVirtual(mouseScreenPosition.set(screenX, screenY));
		unprojectCoordinates(mouseWorldPosition.set(screenX, screenY));
		if(override){
			deltaPan.set(screenX, screenY).sub(oldPosition).scl(-1, 1);
			oldPosition.set(screenX, screenY);
			translateCamera(deltaPan.x, deltaPan.y);
			return true;
		}
		return false;	
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		calculateMouseOver(screenX, Gdx.graphics.getHeight() - screenY);
		projectScreenToVirtual(mouseScreenPosition.set(screenX, screenY));
		unprojectCoordinates(mouseWorldPosition.set(screenX, screenY));
		return false;	
	}
	
	@Override
	public boolean scrolled(int amount) {
		if(isMouseOver() && !zoomLock){		
			zoomCamera(amount);
			return true;
		}
		return false;
	}

}
