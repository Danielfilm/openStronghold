package ch.heerbann.openstronghold.utlity;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.Array;

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
		public String filename;
		public TGXHeader header = new TGXHeader();
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
		public short width, height, positionX, positionY, tilePositionY;
		@SuppressWarnings("unused")
		public byte part, parts, direction, horizontalOffset, drawingBoxWdith, performanceID;
	}
	
	private class GM1EntryInfo{
		public int offset, size;
		public Pixmap image, tile;
		public GM1ImageHeader header = new GM1ImageHeader();
	}
	
	private class GM1Image{
		@SuppressWarnings("unused")
		public String filename;
		public GM1EntryInfo[] entries;
		public GM1Header header;
		short[] tgxPalette = new short[TGX_PALETTE_COLORS];
	}
	
	public interface ProgressCallback{
		public void getProgress(float progress, String message);
	}
	
	private ProgressCallback callback;

	private TgxReader(){
	}
	
	/**
	 * Sets a callback. Overwrites the old one.
	 */
	public static void setProgressCallback(ProgressCallback callback){
		TgxReader.instance.callback = callback;
	}
	
	public static Pixmap convertPixmap(String path) throws IOException{
		return TgxReader.instance.loadTGXImage(path);
	}
	
	public static Pixmap[] convertAnimation(String filepath) throws IOException{
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
		return decodeTGX(image.header.width, image.header.height, buffer.limit(), null, buffer);
	}
	
	/**
	 * Converts a gm1 file to an array of pixmaps<br><br>
	 * <b>Data Type</b><br>
	 * Data type is and ID that represents what kind of images are stored, they are as follows:<br>
	 * 1 – Interface items and some building animations. Images are stored similar to TGX images.<br>
	 * 2 – Animations.<br>
	 * 3 – Buildings. Images are stored similar to TGX images but with a Tile object.<br>
	 * 4 – Font. TGX format.<br>
	 * 5 and 7 – Walls, grass, stones and other. No compression, stored with 2-bytes per pixel.<br>
	 * @param filepath absolut path
	 */
	private Pixmap[] loadGM1Image(String filepath) throws IOException{
		GM1Image image = new GM1Image();
		image.filename = filepath;
		
		FileInputStream input = new FileInputStream(filepath);
		ByteBuffer buffer = ByteBuffer.allocate((int) input.getChannel().size());
		input.getChannel().read(buffer);
		input.close();
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.rewind();
		
		boolean isTile = false;
		//1. Read header - 88 bytes
		image.header = read(new GM1Header(), buffer);
		
		//2. Read Palette - 5,120 bytes
		image.tgxPalette = read(image.tgxPalette, buffer);	
		
		//3. Read offsets
		image.entries = new GM1EntryInfo[image.header.imageCount];
		for(int i = 0; i < image.entries.length; i++){
			image.entries[i] = new GM1EntryInfo();
			image.entries[i].offset = buffer.getInt();
		}
		
		//4. Read sizes
		for(int i = 0; i < image.entries.length; i++){
			image.entries[i].size = buffer.getInt();
		}
		
		//5. Read headers - 16 bytes
		for(int i = 0; i < image.entries.length; i++){
			read(image.entries[i].header, buffer);
		}
		
		int endOfHeader = buffer.position(); //all offsets are relative to this
		float increment = 100f/image.entries.length, currentIncrement = 0;
		//Read images
		for(int i = 0; i < image.entries.length; i++){
			GM1EntryInfo entry = image.entries[i];
			callback.getProgress(currentIncrement, "Started!");
			currentIncrement +=increment;
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
				entry.image = decodeTGX(entry.header.width, entry.header.height, endOfHeader + entry.offset, entry.size, null, buffer);
				break;
			case TYPE_TGXCONSTSIZE:
				entry.image = decodeTGX(entry.header.width, entry.header.height, endOfHeader + entry.offset, null, buffer);
				break;
			case TYPE_ANIMATION:
				entry.image = decodeTGX(entry.header.width, entry.header.height, endOfHeader + entry.offset, entry.size, image.tgxPalette, buffer);
				break;
			case TYPE_TGXTILE:
				isTile = true;
				entry.tile = decodeTile(endOfHeader + entry.offset, buffer);
				entry.image = decodeTGX(entry.header.drawingBoxWdith, entry.header.tilePositionY + GM1TILEHEIGHT, entry.offset + endOfHeader + GM1TILEBYTES, entry.size - GM1TILEBYTES, null, buffer);
				break;
			}
		}	
		
		 if(isTile){ 
			 callback.getProgress(1, "All Finished!");
			 return finalizeTiles(image.entries);
		 }else{
			 Pixmap[] temp = new Pixmap[image.entries.length];
			 for(int i = 0; i < temp.length; i++){
				 temp[i] = image.entries[i].image;
			 }
			 callback.getProgress(1, "All Finished!");
			 return temp;
		 }	
	}
	
	/**
	 * Reads the Header of a gm1 File. The header has a length of 88-bytes, composed of 32-bit integers:<br>
	 * 4 -bytes – Unknown(1)<br>
	 * 4 -bytes – Unknown(2)<br>
	 * 4 -bytes – Unknown(3)<br>
	 * 4 -bytes – Quantity, number of pictures in file.<br>
	 * 4 -bytes – Unknown(4)<br>
	 * 4 -bytes – Data Type<br>
	 * 56-bytes – Unknown(5), array of 32bit integers.<br>
	 * 4 -bytes – Data Size<br>
	 * 4 -bytes - unknown(6)
	 */
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
	
	/**
	 * Reads the Header of a gm1 image. Image Header has a length of 16 bytes<br>
	 * 2-bytes – Image Width<br>
	 * 2-bytes – Image Height<br>
	 * 2-bytes – Width offset (Horizontal location of current frame on large surface.)<br>
	 * 2-bytes – Height offset (Vertical location of current frame on large surface.)<br>
	 * 2-bytes – Number, unique ID of image<br>
	 * 2-bytes – Distance from top to bottom of image, only used for buildings.<br>
	 * 1-byte – Direction, left,right, center... used for building only.<br>
	 * 1-byte – Initial horizontal offset of image.<br>
	 * 1-byte – Width of building part.<br>
	 * 1-byte – Color, used for animated units only
	 */
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
	
	/**
	 * Reads the header of a tgx file<br>
	 * 2-bytes = width<br>
	 * 2-bytes = 0<br>
	 * 2-bytes = height<br>
	 * 2-bytes = 0
	 */
	private void read(TGXHeader header, ByteBuffer buffer){
		header.width = buffer.getShort();
		header.u0 = buffer.getShort();
		header.height = buffer.getShort();
		header.u1 = buffer.getShort();
	}
	
	/**
	 * Reads the Palette. Size is always 5,120 bytes.
	 */
	private short[] read(short[] tgxPalette, ByteBuffer buffer){
		for(int i = 0; i < tgxPalette.length; i++){
			tgxPalette[i] = buffer.getShort();
		}	
		return tgxPalette;
	}
	
	/**
	 * 2 bytes per pixel (Little-Endian view, switching to Big-Endian is easer to read.)<br>
	 * 3 bits green(part-1)<br>
	 * 5 bits blue<br>
	 * 1 bit Unknown<br>
	 * 5 bits red<br>
	 * 2 bits green(part-2)
	 */
	private short[] extractColors(short data){
		short r = (short) (extractBitsetValue(data, TGX_RED_START, TGX_COLOR_BITS) * 8);
		short g = (short) (extractBitsetValue(data, TGX_GREEN_START, TGX_COLOR_BITS) * 8);
		short b = (short) (extractBitsetValue(data, TGX_BLUE_START, TGX_COLOR_BITS) * 8);
		return new short[]{r, g, b};
	}
	
	/**
	 * returns the data type<br>
	 * 1 – Interface items and some building animations. Images are stored similar to TGX images.<br>
	 * 2 – Animations.<br>
	 * 3 – Buildings. Images are stored similar to TGX images but with a Tile object.<br>
	 * 4 – Font. TGX format.<br>
	 * 5 and 7 – Walls, grass, stones and other. No compression, stored with 2-bytes per pixel.
	 */
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
	
	/**
	 * Does magic bit stuff
	 */
	private int extractBitsetValue(short data, int start, int count){
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
	
	private boolean bitTest(short x, int pos){
		return(x & (1 << pos)) > 0;
	}
	
	private int pow(int x, int exp){
		int r = 1;
		for(int i = 0; i < exp; i++){
			r *=x;
		}
		return r;
	}
	/**
	 * returns a new Pixmap with specified size
	 */
	private Pixmap init(int widht, int height){
		 return new Pixmap(widht, height, Format.RGBA8888);
	}
	
	private Pixmap setPixel(Pixmap picture, int x, int y, short r, short g, short b){
		picture.setColor((float)(r/255f), (float)(g/255f), (float)(b/255f), 1);
		picture.drawPixel(x, y);
		return picture;
	}
	
	private Pixmap setTransparentPixel(Pixmap picture, int x, int y){
		picture.setColor(0, 0, 0, 0);
		picture.drawPixel(x, y);
		return picture;
	}
	
	/**
	 * Decodes tgx pixel data.<br>
	 * <b>Token:</b><br>
	 * First byte is the header:<br>
	 * First 3 bits represent the following options.<br>
	 * 000: Stream-of-pixels<br>
	 * 100: Newline<br>
	 * 010: Repeating pixels<br>
	 * 001: Transparent-Pixel-String<br><br>
	 * Next 5 bits are the length to copy -1.<br>
	 * Length is always 00000 for new line.<br>
	 * <b>Stream of Pixels</b><br>
	 * A stream of pixels is a list of pixels to draw. They are 2 bytes per pixel, representing red, green and blue. The number of pixels in the stream is found in the Token.<br>
	 * <b>Newline</b><br>
	 * New line works like this. The y value is increased by 1, but the x value isn't set to 0 always. So when it goes to a new line it subtracts the length of the file 
	 * from x from the current x position. This means that when it writes pixels it can write an x position larger then the width.<br>
	 * <b>Repeating Pixel</b><br>
	 * A Repeating pixel is one pixel that is repeated a set number of times by the length in the token. The Pixel it self is 2-bytes and is located right after the Token.<br>
	 * <b>Transparent-Pixel-String</b><br>
	 * There are 2 ways to look at this. The first way to think it draws a number of transparent pixels set by the Token . The 2nd is to think of it; is that it moves the 
	 * drawing cursor forward a number of pixels defined by the Token. Either way you do think of it though is pretty much the same outcome. The 2nd option seems more proper 
	 * because of how the Newline token works. If the cursor isn't moved the to the length of the image and the Newline Token is called it will return in front of the image how 
	 * ever many pixels were left between the last drawn pixel and the width of the image.
	 */
	private Pixmap decodeTGX(int width, int height, int size, short[] tgxPalette, ByteBuffer buffer){
		return decodeTGX(width, height, 0, size, tgxPalette, buffer);
	}
	
	/**
	 * Decodes tgx pixel data.<br>
	 * <b>Token:</b><br>
	 * First byte is the header:<br>
	 * First 3 bits represent the following options.<br>
	 * 000: Stream-of-pixels<br>
	 * 100: Newline<br>
	 * 010: Repeating pixels<br>
	 * 001: Transparent-Pixel-String<br><br>
	 * Next 5 bits are the length to copy -1.<br>
	 * Length is always 00000 for new line.<br>
	 * <b>Stream of Pixels</b><br>
	 * A stream of pixels is a list of pixels to draw. They are 2 bytes per pixel, representing red, green and blue. The number of pixels in the stream is found in the Token.<br>
	 * <b>Newline</b><br>
	 * New line works like this. The y value is increased by 1, but the x value isn't set to 0 always. So when it goes to a new line it subtracts the length of the file 
	 * from x from the current x position. This means that when it writes pixels it can write an x position larger then the width.<br>
	 * <b>Repeating Pixel</b><br>
	 * A Repeating pixel is one pixel that is repeated a set number of times by the length in the token. The Pixel it self is 2-bytes and is located right after the Token.<br>
	 * <b>Transparent-Pixel-String</b><br>
	 * There are 2 ways to look at this. The first way to think it draws a number of transparent pixels set by the Token . The 2nd is to think of it; is that it moves the 
	 * drawing cursor forward a number of pixels defined by the Token. Either way you do think of it though is pretty much the same outcome. The 2nd option seems more proper 
	 * because of how the Newline token works. If the cursor isn't moved the to the length of the image and the Newline Token is called it will return in front of the image how 
	 * ever many pixels were left between the last drawn pixel and the width of the image.
	 */
	private Pixmap decodeTGX(int width, int height, int offset, int size, short[] tgxPalette, ByteBuffer buffer){
		Pixmap picture = init(width, height);
		byte token = 0;
		int x = 0, y = 0, length = 0;
		short[] pixelData;
		long read = 0;
		while(buffer.hasRemaining() && read < size){
			token = buffer.get();
			length = getLength(token);
			int currentPosition = buffer.position();
			byte type = getType(token);
			switch(type){
			case TGX_STREAM:
				for(int i = 0; i < length; i++){		
					if(tgxPalette == null){
						pixelData = extractColors(buffer.getShort());
					}else{
						byte index = buffer.get();
						int temp = index & 0xFF;

						pixelData = extractColors(tgxPalette[temp]);
					}
					setPixel(picture, x, y, pixelData[0], pixelData[1], pixelData[2]);
					x++;		
				}
				break;
			case TGX_REPEAT:
				if(tgxPalette == null){
					pixelData = extractColors( buffer.getShort());
				}else{
					byte index = buffer.get();
					int temp = index & 0xFF;

					pixelData = extractColors(tgxPalette[temp]);
				}
				for(int j = 0; j < length; j++){
					setPixel(picture, x, y, pixelData[0], pixelData[1], pixelData[2]);
					x++;
				}	
				break;
			case TGX_TRANSPARENT:
				for(int j = 0; j < length; j++){
					setTransparentPixel(picture, x, y);
					x++;
				}
				break;
			case TGX_NEWLINE:
				x = 0;
				y++;
				break;
			}
			read = buffer.position() - currentPosition;
		}
		return picture;
	}
	
	/**
	 * doesnt really work yet as it should
	 */
	private Pixmap[] finalizeTiles(GM1EntryInfo[] input){
		Array<GM1EntryInfo> tiles = new Array<GM1EntryInfo>();
		for(GM1EntryInfo g : input){
			if(g.header.parts != 0){
				tiles.add(g);
			}
		}
		Pixmap[] output = new Pixmap[tiles.size];	
		
		for(int i = 0; i < tiles.size; i++){
			GM1EntryInfo entry = tiles.get(i);
			int offsetX = 1<<31 - 1, offsetY = 1<<31 - 1, maxX = 0, maxY = 0;
			int x = 0, y = 0;
			/**
			//find bounds
			
			for(int j = 0; j < tiles.size; j++){
				GM1EntryInfo entry = tiles.get(j);
				x = entry.header.positionX;
				y = entry.header.positionY;				
				offsetX = (offsetX > x)?x:offsetX;
				offsetY = (offsetY > y)?x:offsetY;				
				maxX = (maxX < x)?x:maxX;
				maxY = (maxY < y)?y+entry.header.tilePositionY:maxY;
			}
			//create image
			Pixmap tile = init(maxX-offsetX+GM1TILEWIDTH, maxY-offsetY+GM1TILEHEIGHT);
			*/
			Pixmap tile = init(entry.header.width, entry.header.height);
			output[i] = tile;
			//paint entities
			for(int j = 0; j < tiles.size; j++){
				GM1EntryInfo e = tiles.get(j);
				x = entry.header.positionX - offsetX;
				y = entry.header.positionY + entry.header.tilePositionY- offsetX;
				tile.drawPixmap(entry.tile, x+GM1TILEWIDTH, y+GM1TILEHEIGHT);
				
				x = entry.header.positionX + entry.header.horizontalOffset - offsetX;
				y = entry.header.positionY - offsetX;
				tile.drawPixmap(entry.tile, x, y);
			}	
		}
		return output;
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
			short[] pixelData = extractColors(rawPixels[i]);
			setPixel(picture, x, y, pixelData[0], pixelData[1], pixelData[2]);
			x++;
			if(x >= header.width){
				x = 0;
				y++;
			}
		}
		return picture;
	}
	/**
	 * Decodes a tile object<br>
	 * <b>Tile Object</b><br>
	 * The tile object is 512 bytes in size, or 256 pixels (2-bytes per pixel). It is stored like a diamond. Starting with 2 
	 * pixels in the first line and then increasing to 6, then 10 and continues in increments of 4 till it reaches 30 and then works 
	 * its way down also in increments of 4.<br><br>
	 * List of pixels per line:<br>
	 * 2<br>
	 * 6<br>
	 * 10<br>
	 * 14<br>
	 * 18<br>
	 * 22<br>
	 * 26<br>
	 * 30<br>
	 * 30<br>
	 * 26<br>
	 * 22<br>
	 * 18<br>
	 * 14<br>
	 * 10<br>
	 * 6<br>
	 * 2<br><br>
	 * This is a 30x16 tile that is placed under the building.
	 */
	private Pixmap decodeTile(int offset, ByteBuffer buffer){
		buffer.position(offset);
		Pixmap picture =  init(GM1TILEWIDTH,GM1TILEHEIGHT);
		short[] rawPixels = new short[GM1TILEBYTES/2];
		for(int i = 0; i < GM1TILEBYTES/2; i++){
			rawPixels[i] = buffer.getShort();
		}
		//put pixels into image
		int x = 0, y = 0;
		for(int i = 0; i < rawPixels.length; i++){
			short[] pixelData = extractColors(rawPixels[i]);
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
