package com.heerbann.stronghold.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class StrongholdAssetManager {
	
	private final AssetManager manager = new AssetManager(new LocalFileHandleResolver());

	public StrongholdAssetManager(){
		//animations
		//anim
		manager.load("animations/anim/anim_a.atlas", TextureAtlas.class);
		manager.load("animations/anim/anim_b.atlas", TextureAtlas.class);
		//body
		manager.load("animations/body/body_a.atlas", TextureAtlas.class);		
		manager.load("animations/body/body_b.atlas", TextureAtlas.class);		
		manager.load("animations/body/body_c-0.atlas", TextureAtlas.class);
		manager.load("animations/body/body_c-1.atlas", TextureAtlas.class);		
		manager.load("animations/body/body_d.atlas", TextureAtlas.class);	
		manager.load("animations/body/body_e.atlas", TextureAtlas.class);
		manager.load("animations/body/body_f.atlas", TextureAtlas.class);
		manager.load("animations/body/body_g.atlas", TextureAtlas.class);
		manager.load("animations/body/body_h.atlas", TextureAtlas.class);		
		manager.load("animations/body/body_i-0.atlas", TextureAtlas.class);	
		manager.load("animations/body/body_i-1.atlas", TextureAtlas.class);
		manager.load("animations/body/body_i-2.atlas", TextureAtlas.class);
		manager.load("animations/body/body_i-3.atlas", TextureAtlas.class);		
		manager.load("animations/body/body_j.atlas", TextureAtlas.class);
		manager.load("animations/body/body_k.atlas", TextureAtlas.class);		
		manager.load("animations/body/body_l-0.atlas", TextureAtlas.class);
		manager.load("animations/body/body_l-1.atlas", TextureAtlas.class);
		manager.load("animations/body/body_l-2.atlas", TextureAtlas.class);
		manager.load("animations/body/body_l-3.atlas", TextureAtlas.class);
		//tree
		manager.load("animations/tree/tree-0.atlas", TextureAtlas.class);
		manager.load("animations/tree/tree-1.atlas", TextureAtlas.class);
		//bitmap
		manager.load("animations/bitmap/bitmap-0.atlas", TextureAtlas.class);
		//font
		manager.load("animations/font/fonts-0.atlas", TextureAtlas.class);
		//tgx_elements
		manager.load("animations/tgx_elements/tgx_elements-0.atlas", TextureAtlas.class);
		manager.load("animations/tgx_elements/tgx_elements-1.atlas", TextureAtlas.class);
		//tgx_sprites
		for(int i = 0; i <= 12; i++)
			manager.load("animations/tgx_sprites/tgx_sprites-" + i + ".atlas", TextureAtlas.class);
		//tgxconst
		for(int i = 0; i <= 2; i++)
			manager.load("animations/tgxconst/tgxconst-" + i + ".atlas", TextureAtlas.class);
		//tile
		for(int i = 0; i <= 4; i++)
			manager.load("animations/tile/tiles-" + i + ".atlas", TextureAtlas.class);

	}
	
	private final TextureAtlas[] sprites = new TextureAtlas[48];
	private boolean initialised = false;
	
	public void initialize(){
		if(initialised) return;
		initialised = true;
		int k = 0;
		//animations
		//anim
		sprites[k++] = manager.get("animations/anim/anim_a.atlas", TextureAtlas.class);
		sprites[k++] = manager.get("animations/anim/anim_b.atlas", TextureAtlas.class);
		//body
		sprites[k++] = manager.get("animations/body/body_a.atlas", TextureAtlas.class);		
		sprites[k++] = manager.get("animations/body/body_b.atlas", TextureAtlas.class);		
		sprites[k++] = manager.get("animations/body/body_c-0.atlas", TextureAtlas.class);
		sprites[k++] = manager.get("animations/body/body_c-1.atlas", TextureAtlas.class);		
		sprites[k++] = manager.get("animations/body/body_d.atlas", TextureAtlas.class);	
		sprites[k++] = manager.get("animations/body/body_e.atlas", TextureAtlas.class);
		sprites[k++] = manager.get("animations/body/body_f.atlas", TextureAtlas.class);
		sprites[k++] = manager.get("animations/body/body_g.atlas", TextureAtlas.class);
		sprites[k++] = manager.get("animations/body/body_h.atlas", TextureAtlas.class);		
		sprites[k++] = manager.get("animations/body/body_i-0.atlas", TextureAtlas.class);	
		sprites[k++] = manager.get("animations/body/body_i-1.atlas", TextureAtlas.class);
		sprites[k++] = manager.get("animations/body/body_i-2.atlas", TextureAtlas.class);
		sprites[k++] = manager.get("animations/body/body_i-3.atlas", TextureAtlas.class);		
		sprites[k++] = manager.get("animations/body/body_j.atlas", TextureAtlas.class);
		sprites[k++] = manager.get("animations/body/body_k.atlas", TextureAtlas.class);		
		sprites[k++] = manager.get("animations/body/body_l-0.atlas", TextureAtlas.class);
		sprites[k++] = manager.get("animations/body/body_l-1.atlas", TextureAtlas.class);
		sprites[k++] = manager.get("animations/body/body_l-2.atlas", TextureAtlas.class);
		sprites[k++] = manager.get("animations/body/body_l-3.atlas", TextureAtlas.class);
		//tree
		sprites[k++] = manager.get("animations/tree/tree-0.atlas", TextureAtlas.class);
		sprites[k++] = manager.get("animations/tree/tree-1.atlas", TextureAtlas.class);
		//bitmap
		sprites[k++] = manager.get("animations/bitmap/bitmap-0.atlas", TextureAtlas.class);
		//font
		sprites[k++] = manager.get("animations/font/fonts-0.atlas", TextureAtlas.class);
		//tgx_elements
		sprites[k++] = manager.get("animations/tgx_elements/tgx_elements-0.atlas", TextureAtlas.class);
		sprites[k++] = manager.get("animations/tgx_elements/tgx_elements-1.atlas", TextureAtlas.class);
		//tgx_sprites
		for(int i = 0; i <= 12; i++)
			sprites[k++] = manager.get("animations/tgx_sprites/tgx_sprites-" + i + ".atlas", TextureAtlas.class);
		//tgxconst
		for(int i = k; i <= 2; i++)
			sprites[k++] = manager.get("animations/tgxconst/tgxconst-" + i + ".atlas", TextureAtlas.class);
		//tile
		for(int i = 0; i <= 4; i++)
			sprites[k++] = manager.get("animations/tile/tiles-" + i + ".atlas", TextureAtlas.class);
	}
	
	public Sprite getSprite(int index, StrongholdAssets type){
		switch(type){
			case animations_anim:
				break;
			case animations_body:
				break;
			case animations_tree:
				break;
			case bitmap:
				break;
			case font:
				break;
			case tgx_elements:
				break;
			case tgx_sprites:
				break;
			case tgxconst:
				break;
			case tile_killing_pits:
				break;
			case tile_pitch_ditches:
				break;
			case tile_tile_buildings1:
				break;
			case tile_tile_buildings2:
				break;
			case tile_tile_burnt:
				break;
			case tile_tile_castle:
				break;
			case tile_tile_churches:
				break;
			case tile_tile_data:
				break;
			case tile_tile_farmland:
				break;
			case tile_tile_flatties:
				break;
			case tile_tile_goods:
				break;
			case tile_tile_land3:
				break;
			case tile_tile_land8:
				break;
			case tile_tile_land_and_stones:
				break;
			case tile_tile_land_macros:
				break;
			case tile_tile_rock8:
				break;
			case tile_tile_ruins:
				break;
			case tile_tile_sea8:
				break;
			case tile_tile_sea_new_01:
				break;
			case tile_tile_workshop:
				break;
			default:
				return null;
		}
		return null;
	}

	public Sprite getTile(short type){
		return null;
	}
	
	public boolean isLoading(){
		return !manager.update();
	}
	
	public void dispose(){
		manager.dispose();
	}
}
