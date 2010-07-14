package mopaqlib;

public class FileInfo {
	public static final short NEUTRAL 		= 0x000;
	public static final short CHINESE      = 0x404;
	public static final short CZECH        = 0x405;
	public static final short GERMAN       = 0x407;
	public static final short ENGLISH      = 0x409;
	public static final short SPANISH      = 0x40a;
	public static final short FRENCH       = 0x40c;
	public static final short ITALIAN      = 0x410;
	public static final short JAPANESE     = 0x411;
	public static final short KOREAN       = 0x412;
	public static final short DUTCH        = 0x413;
	public static final short POLISH       = 0x415;
	public static final short PORTUGUESE   = 0x416;
	public static final short RUSSSIAN     = 0x419;
	public static final short ENGLISH_UK   = 0x809;
	
	
	private String fullpath;
	public String getFullPath() throws MoPaQException
	{
		if(fullpath!=null)
		{
			return fullpath;
		}
		throw new MoPaQException("This file has not been identified yet!");
	}
	
	public String getFileName() throws MoPaQException
	{
		if(fullpath!=null)
		{
			return fullpath.substring(fullpath.lastIndexOf('\\')+1);
		}
		throw new MoPaQException("This file has not been identified yet!");
	}
	
	private long fileHash;
	public long getFileHash()
	{
		return fileHash;
	}
	
	public String getDisplayPath()
	{
		if(fullpath!=null)
		{
			return fullpath;
		}
		return "~~~UNKNOWN~~~\\" + Long.toHexString(fileHash);
	}
	
	private short language;
	public short getLanguage()
	{
		return language;
	}
	
	private short platform;
	public short getPlatform()
	{
		return platform;
	}
	
	private long fileSize;
	public long getFileSize()
	{
		return fileSize;
	}
	
	
	private long compressedSize;
	public long getCompressedSize()
	{
		return compressedSize;
	}	
	

	FileInfo(String fullpath, long fileHash, short language, short platform, long fileSize, long compressedSize)
	{
		this.fullpath=fullpath;
		this.fileHash=fileHash;
		this.language=language;
		this.platform=platform;
		this.fileSize=fileSize;
		this.compressedSize=compressedSize;
	}
	FileInfo()
	{
		
	}
	
	
}
