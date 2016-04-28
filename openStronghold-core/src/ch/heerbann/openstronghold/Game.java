package ch.heerbann.openstronghold;

import java.io.IOException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO.PNG;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;

import ch.heerbann.openstronghold.assets.AssetRipper;

public class Game extends ApplicationAdapter implements InputProcessor{
	SpriteBatch batch;
	OrthographicCamera camera;
	Sprite[] img;
	
	@Override
	public void create () {
		Gdx.input.setInputProcessor(this);
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0, 0, 0);
		try {
			Pixmap[] glyphs = new Pixmap[10];
			for(int i = 0; i < 10; i++){
				glyphs[i] = new Pixmap(Gdx.files.internal("glyphs/" + i + ".png"));
			}
			//String[] files = Gdx.files.internal("strings/gm1Files").readString().split("\\r\\n");
			
				String input = "tile_land8" + ".gm1";
				Pixmap[] pix = AssetRipper.getTest("C://GOG Games//Stronghold HD//gm//" + input);
				int width = pix[0].getWidth(), height = pix[0].getHeight();
				int maxHeight = height * (pix.length / 8);
				
				
				Pixmap[] outputs = new Pixmap[(maxHeight > 32767)?maxHeight/32767 + 1: 1];
				for(int i = 0; i < outputs.length; i++){
					outputs[i] = new Pixmap(width*8, (pix.length / 8)*height, Format.RGBA8888);
				}
	
				int y = 0, x=0, p = 0;
				for(int i = 0; i < pix.length; i++){
					if(i%8 == 0 && i != 0){
						y++;
						x = 0;
					}
				
					int rest = i;
					int[] numbers = null;
					if(pix.length > 10000){
						numbers = new int[5];
						numbers[4] = (int)(rest / 10000);
						rest = rest% 10000;
						numbers[3] = (int)(rest / 1000);
						rest = rest% 10000;
						numbers[2] = (int)(rest / 100);
						rest = rest% 1000;
						numbers[1] = (int)(rest / 10);
						rest = rest% 10;
						numbers[0] = rest;
					}else{
						if(pix.length > 1000){
							numbers = new int[4];
							numbers[3] = (int)(rest / 1000);
							rest = rest% 1000;
							numbers[2] = (int)(rest / 100);
							rest = rest% 100;
							numbers[1] = (int)(rest / 10);
							rest = rest% 10;
							numbers[0] = rest;
						}else{
							if(pix.length > 100){
								numbers = new int[3];
								numbers[2] = (int)(rest / 100);
								rest = rest% 100;
								numbers[1] = (int)(rest / 10);
								rest = rest% 10;
								numbers[0] = rest;
							}else{
								if(pix.length > 10){
									numbers = new int[2];				
									numbers[1] = (int)(rest / 10);
									rest = rest% 10;
									numbers[0] = rest;
								}else{
									numbers = new int[1];
									numbers[0] = rest;
								}
							}
						}
					}
					
					
					Pixmap t = outputs[p];
					t.drawPixmap(pix[i], x*width, y*height);
					int currentWidth = 0;
					for(int k = numbers.length-1; k >= 0 ; k--){
						//t.drawPixmap(glyphs[numbers[k]], x*width + currentWidth, y*height);	
						currentWidth += glyphs[k].getWidth() + 5;
					}
					x++;
					
				}	
				PNG png = new PNG();
				png.setFlipY(false);
				for(int i = 0; i < outputs.length; i++){
					png.write(Gdx.files.local(input + ".png"), outputs[i]);
				}
				png.dispose();
			Gdx.app.exit();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(Sprite s : img){
			s.draw(batch);
		};
		batch.end();
	}
	
	/**
	private static FreeTypeFontGenerator generatorArial = new FreeTypeFontGenerator(Gdx.files.internal("Arial.ttf"));
	public static BitmapFont requestFont(Color color, Color borderColor, int size, int borderWidth){		
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		parameter.color = color;
		parameter.borderWidth = borderWidth;
		parameter.borderColor = borderColor;
		return generatorArial.generateFont(parameter);	
	}
	*/

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override	
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	private final Vector2 oldPosition = new Vector2(), delta = new Vector2();
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		delta.set(screenX, screenY).sub(oldPosition).scl(-1, 1);
		camera.translate(delta);
		oldPosition.set(screenX, screenY);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		oldPosition.set(screenX, screenY);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
