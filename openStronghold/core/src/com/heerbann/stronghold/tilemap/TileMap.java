package com.heerbann.stronghold.tilemap;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;

public class TileMap {

	public static final int maskByte = 0xFF, maskShort = 0xFFFF, maskInt = 0xFFFFFFFF;
	public static final int shiftByte = 4, shiftShort = 8, shiftInt = 16;
	
	// Tile: long -> 8 bytes. [][][][][height: 2 bytes -> unsigned short][type: 2 bytes -> signed short]

	public final long[] map;
	
	private final int tilesX, tilesY, tileWidth, tileHeight;
	
	public TileMap(int tilesX, int tilesY, int tileWidth, int tileHeight){
		this.tilesX = tilesX;
		this.tilesY = tilesY;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.map = new long[tilesX * tilesY];
	}
	
	private final Vector2 vec = new Vector2();
	private final GridPoint2 point = new GridPoint2();
	
	public boolean isValid(float globalX, float globalY){
		GridPoint2 p = convertToMap(globalX, globalY);
		return isValid(p.x, p.y);
	}
	
	public boolean isValid(int gridX, int gridY){
		return gridX >= 0 && gridY >= 0 && gridX < tilesX && gridY < tilesY;
	}
	
	public boolean isValidIndex(int index){
		return index >= 0 && index < map.length;
	}
	
	public GridPoint2 convertToMap(float globalX, float globalY){
		float x = (int)(globalX / tileWidth);
		float y = (int)(globalY / tileHeight);
		vec.set(x - y, (int)((x + y) * 0.5f));
		return point;
	}
	
	public Vector2 convertToGlobal(int gridX, int gridY){
		return vec.set(((2 * gridY + gridX) / 2) * tileWidth, ((2 * gridY - gridX) / 2) * tileHeight);
	}
	
	public int convertToIndex(int gridX, int gridY){
		return gridX + tilesY * gridY;
	}
	
	public IntArray getRenderPoints(OrthographicCamera camera, IntArray points){
		points.clear();
		
		GridPoint2 p = convertToMap(camera.position.x - camera.viewportWidth/2, camera.position.y - camera.viewportHeight/2);
		int lowerLeftXMap = p.x - 1;
		int lowerLeftYMap = p.y - 1;
		//int lowerLeftXCart = (2 * lowerLeftYMap + lowerLeftXMap) / 2;
		int lowerLeftYCart = (2 * lowerLeftYMap - lowerLeftXMap) / 2;
		
		convertToMap(camera.position.x - camera.viewportWidth/2, camera.position.y + camera.viewportHeight/2);
		int upperLeftXMap = p.x - 1;
		int upperLeftYMap = p.y + 1;
		int upperLeftXCart = (2 * upperLeftYMap + upperLeftXMap) / 2;
		int upperLeftYCart = (2 * upperLeftYMap - upperLeftXMap) / 2;
		
		//convertToMap(camera.position.x + camera.viewportWidth/2, camera.position.y - camera.viewportHeight/2);
		//int lowerRightXMap = p.x + 1;
		//int lowerRightYMap = p.y - 1;
		//int lowerRightXCart = (2 * lowerRightYMap + lowerRightXMap) / 2;
		//int lowerRightYCart = (2 * lowerRightYMap - lowerRightXMap) / 2;
		
		convertToMap(camera.position.x + camera.viewportWidth/2, camera.position.y + camera.viewportHeight/2);
		int upperRightXMap = p.x + 1;
		int upperRightYMap = p.y + 1;
		int upperRightXCart = (2 * upperRightYMap + upperRightXMap) / 2;
		//int upperRightYCart = (2 * upperRightYMap - upperRightXMap) / 2;
		
		//int width = upperRightXCart - upperLeftXCart;
		//int height = upperRightYCart - lowerRightYCart;
		
		for(int x = upperLeftXCart; x < upperRightXCart; x++){
			for(int y = upperLeftYCart; y >= lowerLeftYCart; y++){
				if(!isValid(x, y)) continue;
				points.add(x);
				points.add(y);
			}
		}		
		return points;
	}
	
}
