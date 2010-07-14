package mopaqlib;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.Deflater;

class MoPaQFile {
	short language;
	short platform;
	String path;
	long compressedSize;
	byte[] content;
	byte[] contentCompressed;
	long blockOffset;
	
	MoPaQFile(String path, byte[] content, short language, short platform)
	{
		create(path,content,language,platform,content.length);
	}
	
	MoPaQFile(String path, byte[] content, short language, short platform, long compressedSize)
	{
		create(path,content,language,platform,compressedSize);
	}
	
	private void create(String path, byte[] content, short language, short platform, long compressedSize)
	{
		this.language=language;
		this.platform=platform;
		this.path=path;
		this.compressedSize=compressedSize;
		this.content=content;
	}
	
	void setContent(byte[] newData)
	{
		this.content=newData;
		this.compressedSize=newData.length;
	}
	
	void setContent(byte[] newData, long compressedSize)
	{
		this.content=newData;
		this.compressedSize=compressedSize;
	}
	
	FileInfo getFileInfo()
	{
		return new FileInfo(path,0,language,platform,content.length,compressedSize);
	}

	byte[] getContent()
	{
		return content.clone();
	}
	
	void compress()
	{
		byte[] output=new byte[content.length];
		ByteBuffer bb=ByteBuffer.wrap(output);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int outLength;
		
		Deflater deflater=new Deflater(9,false);
		deflater.setInput(content);
		deflater.finish();
		outLength=deflater.deflate(output, 1, content.length-2)+1;
		if(deflater.finished())
		{
			//bb.putInt(8);
			//bb.putInt(outLength);
			output[0]=BlockTableEntry.COMPRESSION_DEFLATED;
			compressedSize=outLength;
			contentCompressed=BlockTableEntry.cloneByteArray(output, 0, outLength);
			//System.out.println("Saved " + (content.length-outLength) + " bytes");
		}
		else
		{
			compressedSize=content.length;
			contentCompressed=content.clone();
		}

	}
	
}
