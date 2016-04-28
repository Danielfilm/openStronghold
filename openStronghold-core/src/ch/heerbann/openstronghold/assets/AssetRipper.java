package ch.heerbann.openstronghold.assets;

import java.io.IOException;
import java.io.Serializable;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;

import ch.heerbann.openstronghold.utlity.TgxReader;

public class AssetRipper {

	private static final AssetRipper instance = new AssetRipper();
	
	private AssetRipper(){		
	}
	
	public static Pixmap[] getTest(String path) throws IOException{
		return AssetRipper.instance.getTestLocal1(path);
	}
	
	private Pixmap getTestLocal(String path) throws IOException{
		return TgxReader.convertPixmap(path);
	}
	
	private Pixmap[] getTestLocal1(String path) throws IOException{
		return TgxReader.convertAnimation(path);
	}
	
	
	private Array<FileStruct> ripTGXImages(String pathToRoot) throws IOException{
		Array<FileStruct> structs = new Array<FileStruct>();
		String[] files = Gdx.files.internal("strings/tgxFiles").readString().split("$"); //probably doesnt work yet
		for(String s : files){
			structs.add(new FileStruct(s, TgxReader.convertPixmap(pathToRoot + s)));
		}
		return structs;
	}
	
	private class FileStruct{
		public String name;
		public Pixmap picture;
		public FileStruct(String name, Pixmap picture) {
			this.name = name;
			this.picture = picture;
		}
	}
	
	private final String[] tgxItems = new String[]{
			
	};
}
