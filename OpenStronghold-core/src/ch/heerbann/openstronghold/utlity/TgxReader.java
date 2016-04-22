package ch.heerbann.openstronghold.utlity;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public final class TgxReader {
	
	private static final TgxReader instance = new TgxReader();
	
	//Gm1Image
	@SuppressWarnings("unused")
	private  final short TYPE_TGX = 1, TYPE_ANIMATION = 2, TYPE_TGXTILE = 3, TYPE_TGXFONT = 4, TYPE_BITMAP = 5,
			TYPE_TGXCONSTSIZE = 6, TYPE_BITMAPOTHER = 7;
		
	private final short GM1TILEBYTES  = 512, GM1TILEWIDTH  = 30, GM1TILEHEIGHT = 16;
		
	@SuppressWarnings("unused")
	private byte DIRECTION_NONE = 0, DIRECTION_DOWN = 1, DIRECTION_RIGHT = 2, DIRECTION_LEFT = 3;
		
	private final int[] GM1TilePixelsPerLine = new int[]{
				2, 6, 10, 14, 18, 22, 26, 30, 30, 26, 22, 18, 14, 10, 6, 2};
		
	//TgxImage	
	private class TGXHeader{
		@SuppressWarnings("unused")
		public short width, u0, height, u1;
	}
	
	private class TGXImage{
		@SuppressWarnings("unused")
		public String filename;
		@SuppressWarnings("unused")
		public int offset, size;
		@SuppressWarnings("unused")
		public byte[] palette;
		public Pixmap image;
		public TGXHeader header;
	}
	
	//Color
	private final short TGX_COLOR_BITS = 5, //0-31
			TGX_RED_START = 10,
			TGX_GREEN_START = 5,
			TGX_BLUE_START = 0,
			TGX_PALETTE_COLORS = 2560; //animated tgx (10 sets of 256 colors[2bytes])
	//Token
	@SuppressWarnings("unused")
	private final byte TGX_LENGTH_BITS = 5,
			TGX_NEWLINE     = 7,
			TGX_REPEAT      = 6,
			TGX_TRANSPARENT = 5,
			TGX_STREAM      = 0;	

	private class GM1Header{
		public int[] u0 = new int[3], u2 = new int[2], u3 = new int[11];
		@SuppressWarnings("unused")
		public int imageCount, u1, dataType, sizeType, dataSize, u4;
	}
	
	private class GM1ImageHeader{
		@SuppressWarnings("unused")
		public short width, height, positionX, positionY, tilePositionY;
		@SuppressWarnings("unused")
		public byte part, parts, direction, horizontalOffset, drawingBoxWdith, performanceID;
	}
	
	private class GM1EntryInfo{
		public int offset, size;
		@SuppressWarnings("unused")
		public Pixmap image, tile;
		public GM1ImageHeader header;
	}
	
	private class GM1Image{
		@SuppressWarnings("unused")
		public String filename;
		public GM1EntryInfo[] entries;
		public GM1Header header;
		byte[] tgxPalette = new byte[TGX_PALETTE_COLORS];
	}

	private TgxReader(){
	}
	
	public static Pixmap convertPixmap(String path) throws IOException{
		return TgxReader.instance.loadTGXImage(path);
	}
	
	public static TextureAtlas convertAnimation(String filepath) throws IOException{
		return TgxReader.instance.loadGM1Image(filepath);
	}
	
	private Pixmap loadTGXImage(String path) throws IOException{		
		TGXImage image = new TGXImage();
		image.filename = path;
		
		FileInputStream input = new FileInputStream(image.filename);
		ByteBuffer buffer = ByteBuffer.allocate((int) input.getChannel().size());
		input.getChannel().read(buffer);
		input.close();
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.rewind();
		
		//Read header 8bytes
		read(image.header, buffer);

		decodeTGX(image.header.width, image.header.height, buffer.limit(), null, buffer);
		return image.image;
	}
	
	private TextureAtlas loadGM1Image(String filepath) throws IOException{
		GM1Image image = new GM1Image();
		image.filename = filepath;
		
		FileInputStream input = new FileInputStream(filepath);
		ByteBuffer buffer = ByteBuffer.allocate((int) input.getChannel().size());
		input.getChannel().read(buffer);
		input.close();
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.rewind();
		
		//Read header
		image.header = read(new GM1Header(), buffer);
		//Read Palette
		image.tgxPalette = read(image.tgxPalette, buffer);
		//Read entry data
		image.entries = new GM1EntryInfo[image.header.imageCount];
		//offsets
		for(int i = 0; i < image.entries.length; i++){
			image.entries[i].offset = buffer.getInt();
		}
		//sizes
		for(int i = 0; i < image.entries.length; i++){
			image.entries[i].size = buffer.getInt();
		}
		
		//Read headers
		for(int i = 0; i < image.entries.length; i++){
			read(image.entries[i].header, buffer);
		}
		
		int endOfHeader = buffer.position(); //all offsets are relative to this
		
		//Read images
		for(int i = 0; i < image.entries.length; i++){
			GM1EntryInfo entry = image.entries[i];
			//seek to position
			buffer.position(entry.offset + endOfHeader);
			switch(image.header.dataType){
			case TYPE_BITMAP:
				entry.image = decodeBitmap(entry.header, entry.size, buffer);
				break;
			case TYPE_TGX :
				entry.image = decodeTGX(entry.header.width, entry.header.height, entry.size, null, buffer);
				break;
			case TYPE_TGXFONT:
				entry.image = decodeTGX(entry.header.width, entry.header.height, entry.size, null, buffer);
				break;
			case TYPE_TGXCONSTSIZE:
				entry.image = decodeTGX(entry.header.width, entry.header.height, entry.size, null, buffer);
				break;
			case TYPE_ANIMATION:
				entry.image = decodeTGX(entry.header.width, entry.header.height, entry.size, image.tgxPalette, buffer);
				break;
			case TYPE_TGXTILE:
				entry.tile = decodeTile(buffer);
				buffer.position(entry.offset + endOfHeader + GM1TILEBYTES);
				entry.image = decodeTGX(entry.header.width, entry.header.tilePositionY + GM1TILEHEIGHT, entry.size - GM1TILEBYTES, null, buffer);
				break;
			}
		}
		return null;
	}
	
	private GM1Header read(GM1Header header, ByteBuffer buffer){
		for(int i = 0; i < header.u0.length; i++){
			header.u0[i] = buffer.getInt();
		}
		header.imageCount = buffer.getInt();
		header.u1 = buffer.getInt();
		header.dataType = buffer.getInt();
		for(int i = 0; i < header.u2.length; i++){
			header.u2[i] = buffer.getInt();
		}
		header.sizeType = buffer.getInt();
		for(int i = 0; i < header.u3.length; i++){
			header.u3[i] = buffer.getInt();
		}
		header.dataSize = buffer.getInt();
		header.u4 = buffer.getInt();
		return header;
	}
	
	private GM1ImageHeader read(GM1ImageHeader header, ByteBuffer buffer){
		header.width = buffer.getShort();
		header.height = buffer.getShort();
		header.positionX = buffer.getShort();
		header.positionY = buffer.getShort();
		header.part = buffer.get();
		header.parts = buffer.get();
		header.tilePositionY = buffer.getShort();
		header.direction = buffer.get(); 
		header.horizontalOffset = buffer.get(); 
		header.drawingBoxWdith = buffer.get(); 
		header.performanceID = buffer.get();
		return header;
	}
	
	private void read(TGXHeader header, ByteBuffer buffer){
		header.width = buffer.getShort();
		header.u0 = buffer.getShort();
		header.height = buffer.getShort();
		header.u1 = buffer.getShort();
	}
	
	private byte[] read(byte[] tgxPalette, ByteBuffer buffer){
		buffer.get(tgxPalette);
		return tgxPalette;
	}
	
	private byte[] extractColors(int data){
		byte r = (byte) (extractBitsetValue(data, TGX_RED_START, TGX_COLOR_BITS) * 8);
		byte g = (byte) (extractBitsetValue(data, TGX_GREEN_START, TGX_COLOR_BITS) * 8);
		byte b = (byte) (extractBitsetValue(data, TGX_BLUE_START, TGX_COLOR_BITS) * 8);
		return new byte[]{r, g, b};
	}
	
	private byte getType(byte token){
		if(bitTest(token, TGX_NEWLINE)){
			return TGX_NEWLINE;
		}else if(bitTest(token, TGX_REPEAT)){
			return TGX_REPEAT;
		}else if(bitTest(token, TGX_TRANSPARENT)){
			return TGX_TRANSPARENT;
		}else{
			return TGX_STREAM;
		}
	}
	
	private int getLength(byte token){
		return extractBitsetValue(token, 0, 5) + 1;
	}
	
	public void save(){
		
	}
	
	public void saveCollection(){
		
	}
	
	public void saveTileset(){
		
	}
	
	private int extractBitsetValue(int data, int start, int count){
		int r = 0;
		int exp = 0;
		for(int i = start; i < start + count; i++){
			//test bit
			if(bitTest(data, i)){
				r += pow(2, exp);
			}
			exp++;
		}
		return r;
	}
	
	private boolean bitTest(int x, int pos){
		return(x & (1 << pos)) > 0;
	}
	
	private int pow(int x, int exp){
		int r = 1;
		for(int i = 0; i < exp; i++){
			r *=x;
		}
		return r;
	}
	
	private Pixmap init(int widht, int height){
		 return new Pixmap(widht, height, Format.RGBA8888);
	}
	
	private Pixmap setPixel(Pixmap picture, int x, int y, byte r, byte g, byte b){
		picture.setColor((r/255), (g/255), (b/255), 1);
		picture.drawPixel(x, y);
		return picture;
	}

	private Pixmap decodeTGX(int width, int height, int size, byte[] tgxPalette, ByteBuffer buffer){
		Pixmap picture = init(width, height);
		
		byte token = 0;
		int x = 0, y = 0;
		byte[] pixelData;

		while(buffer.hasRemaining() || buffer.position() < size){
			token = buffer.get();
			switch(getType(token)){
			case TGX_STREAM:
				for(int i = 0; i < getLength(token); i++){
					if(tgxPalette == null){
						pixelData = extractColors( buffer.getShort());
					}else{
						int index = buffer.get();
						pixelData = extractColors(index);
					}
					for(int j = 0; j < getLength(token); j++){
						setPixel(picture, x, y, pixelData[0], pixelData[1], pixelData[2]);
						x++;
					}		
				}
				break;
			case TGX_REPEAT:
				if(tgxPalette == null){
					pixelData = extractColors( buffer.getShort());
				}else{
					int index = buffer.get();
					pixelData = extractColors(index);
				}
				for(int j = 0; j < getLength(token); j++){
					setPixel(picture, x, y, pixelData[0], pixelData[1], pixelData[2]);
					x++;
				}	
				break;
			case TGX_TRANSPARENT:
				x += getLength(token);
				break;
			case TGX_NEWLINE:
				x = 0;
				y++;
				break;
			}
		}
		return picture;
	}
	
	private Pixmap decodeBitmap(GM1ImageHeader header, int size, ByteBuffer buffer){
		Pixmap picture = init(header.width, header.hashCode());
		short[] rawPixels = new short[size];
		for(int i = 0; i < size; i++){
			rawPixels[i] = buffer.getShort();
		}
		//put pixels into image
		int x = 0, y = 0;
		for(int i = 0; i < size; i++){
			byte[] pixelData = extractColors(rawPixels[i]);
			setPixel(picture, x, y, pixelData[0], pixelData[1], pixelData[2]);
			x++;
			if(x >= header.width){
				x = 0;
				y++;
			}
		}
		return picture;
	}
	
	private Pixmap decodeTile(ByteBuffer buffer){
		Pixmap picture =  init(GM1TILEWIDTH,GM1TILEHEIGHT);
		short[] rawPixels = new short[GM1TILEBYTES/2];
		for(int i = 0; i < GM1TILEBYTES/2; i++){
			rawPixels[i] = buffer.getShort();
		}
		//put pixels into image
		int x = 0, y = 0;
		for(int i = 0; i < rawPixels.length; i++){
			byte[] pixelData = extractColors(rawPixels[i]);
			setPixel(picture, 15-GM1TilePixelsPerLine[y]/2 + x, y, pixelData[0], pixelData[1], pixelData[2]); //diamond pattern
			x++;
			if(x >= GM1TilePixelsPerLine[y]){
				x = 0;
				y++;
			}
		}
		return picture;
	}
}
