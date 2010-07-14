package mopaqlib;



import java.beans.DesignMode;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;


public class MoPaQ {
	private RandomAccessFile source;
	private File sourceFile;
	private boolean isFileOwner;
	
	private static final int MAGIC = 0x1A51504D;
	private static final int INFO_MAGIC = 0x1B51504D;
	
	private static final short VERSION_ORIGINAL=0;
	private static final short VERSION_BURNING_CRUSADE=1;
	
	private static final long HASH_TABLE_KEY=Encryption.hashString("(hash table)", Encryption.HASH_TYPE_FILE_KEY);
	private static final long BLOCK_TABLE_KEY=Encryption.hashString("(block table)", Encryption.HASH_TYPE_FILE_KEY);
	//Header (does not influence the header written to the destination file)
	private long headerOffset;
	private long headerSize;
	
	private long magic;
	private long archiveSize; //deprecated
	private short formatVersion;
	private long sectorSize;

	private long hashTableOffset;
	private int hashTableLength;
	
	private long blockTableOffset;
	private long extendedBlockTableOffset;
	private int blockTableLength;

	public byte[] infoHeader;
	
	
	public int unidentifiedFiles;
	
	private Hashtable<Long, ArrayList<HashTableEntry>> hashtable;
	private ArrayList<BlockTableEntry> blocktable;
	ArrayList<MoPaQFile> files; 
	
	private void parseHeader() throws MoPaQException, IOException
	{
		ByteBuffer bb=ByteBuffer.allocate(32);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		source.seek(headerOffset);
		source.read(bb.array());
		magic=bb.getInt();
		if(magic!=MAGIC)
		{
			throw new MoPaQException("Invalid MoPaQ source stream: MAGIC does not match! (magic: " + magic + ")");
		}
		headerSize=(long)bb.getInt() & 0xffffffffl;
		archiveSize=(long)bb.getInt() & 0xffffffffl;
		formatVersion=bb.getShort();
		sectorSize=512*(long)Math.pow(2, bb.get());
		//System.out.println("Sector Size: " + sectorSize);
		bb.get();
		hashTableOffset=headerOffset+(long)bb.getInt() & 0xffffffffl;
		//System.out.println(hashTableOffset);
		blockTableOffset=headerOffset+(long)bb.getInt() & 0xffffffffl;
		hashTableLength=bb.getInt();
		blockTableLength=bb.getInt();
		
		//Version dependent
		switch(formatVersion)
		{
		case VERSION_BURNING_CRUSADE:
			source.read(bb.array(), 0, 12);
			bb.position(0);
			extendedBlockTableOffset=bb.getLong();
			hashTableOffset+=(long)bb.getShort()<<32;
			blockTableOffset+=(long)bb.getShort()<<32;
			break;
		
		case VERSION_ORIGINAL:
			break;
			
		default:
			throw new MoPaQException("Invalid MoPaQ source stream: Unknown version or header information! (version: " + formatVersion + "; header size: " + headerSize + ")");
		}
	}
	
	private void parseHashTable() throws IOException
	{
		HashTableEntry current;
		ByteBuffer bb;
		
		bb=ByteBuffer.wrap(Encryption.decryptFromStream(source,HASH_TABLE_KEY,hashTableOffset,hashTableLength*16));
		
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		hashtable=new Hashtable<Long, ArrayList<HashTableEntry>>();
		
		for(int i=0;i<hashTableLength;i++)
		{
			current=new HashTableEntry(bb.getInt(), bb.getInt(), bb.getShort(), bb.getShort(), bb.getInt());
			if(! hashtable.containsKey(current.filePathHash))
			{
				hashtable.put(current.filePathHash, new ArrayList<HashTableEntry>());
			}
			hashtable.get(current.filePathHash).add(current);
			if(current.isIndexValid() && current.fileBlockIndex>=0 && blocktable.size()>current.fileBlockIndex)//current.isIndexValid())
			{
				current.fileBlock=blocktable.get(current.fileBlockIndex);
				current.fileBlock.references.add(current);
			}
			//System.out.println("A:" + current.filePathHashA);
			//System.out.println("B:" + current.filePathHashB);
			//System.out.println("I:" + current.fileBlockIndex);
		}
	}
	
	private void doNothing()
	{
	
	}
	
	private void parseBlockTable() throws IOException
	{
		BlockTableEntry current;
		ByteBuffer bb;
		
		bb=ByteBuffer.wrap(Encryption.decryptFromStream(source,BLOCK_TABLE_KEY,blockTableOffset,blockTableLength*16));
		bb.order(ByteOrder.LITTLE_ENDIAN);

		
		
		
		blocktable=new ArrayList<BlockTableEntry>();
		
		//source.seek(blockTableOffset);
		//source.read(bb.array());
		
		for(int i=0;i<blockTableLength;i++)
		{
			current=new BlockTableEntry(this,bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt(),source,headerOffset,sectorSize);
			blocktable.add(current);
			//System.out.println(current.fileSize);
		
			if(current.isFile() && ! current.wasDeleted())
			{
				unidentifiedFiles+=1;
			}
		}
		doNothing();
	}
	
	public void identify(String filename) throws IOException, DataFormatException, MoPaQException
	{
		long filenameHash=Encryption.hashFileName(filename);
		HashTableEntry oneEntry;
		ArrayList<HashTableEntry> entries;
		if(hashtable.containsKey(filenameHash))
		{
			entries=hashtable.get(filenameHash);
			for(int i=0;i<entries.size();i++)
			{
				oneEntry=entries.get(i);
				if(oneEntry.fileName==null)
				{
					oneEntry.fileName=filename;
					//System.out.println("Found: " + filename);
					if(oneEntry.fileBlock!=null && oneEntry.fileBlock.isFile() && !oneEntry.fileBlock.wasDeleted())
					{
						unidentifiedFiles-=1;
						oneEntry.fileBlock.findSectors();
						if(unidentifiedFiles==0 && isFileOwner)
						{
							source.close();
						}
					}
				}
			}
		}
	}
	
	private void identify(byte[] rawdata, String filename) throws IOException, DataFormatException, MoPaQException
	{
		long filenameHash=Encryption.hashFileName(rawdata);
		HashTableEntry oneEntry;
		ArrayList<HashTableEntry> entries;
		if(hashtable.containsKey(filenameHash))
		{
			entries=hashtable.get(filenameHash);
			for(int i=0;i<entries.size();i++)
			{
				oneEntry=entries.get(i);
				if(oneEntry.fileName==null)
				{
					oneEntry.fileName=filename;
					//System.out.println("Found: " + filename);
					
					if(oneEntry.fileBlock!=null && oneEntry.fileBlock.isFile() && !oneEntry.fileBlock.wasDeleted())
					{
						unidentifiedFiles-=1;
						oneEntry.fileBlock.findSectors();
						if(unidentifiedFiles==0 && isFileOwner)
						{
							source.close();
						}
							
					}
				}
			}
		}
	}
	
	public void bulkIdentify(String[] filenames) throws IOException, DataFormatException, MoPaQException
	{
		for (String filename : filenames) {
			if(filename!="")
			{
				identify(filename);
			}
		}
	}
	
	private void bulkIdentify(byte[][] rawdata,String[] filenames) throws IOException, DataFormatException, MoPaQException
	{
		for(int i=0;i<rawdata.length;i++)
		{
			identify(rawdata[i],filenames[i]);
		}
		
	}
	
	private String[] readFromInputStream(InputStream in) throws IOException
	{
		ArrayList<String> lines=new ArrayList<String>();
		BufferedReader reader;
		String oneLine;
		reader=new BufferedReader(new InputStreamReader(in));
		while((oneLine=reader.readLine())!=null)
		{
			if(oneLine!="")
			{
				lines.add(oneLine);
			}
		}
		
		return lines.toArray(new String[0]);
	}
	
	private byte[][] readBytesFromInputStream(InputStream in) throws IOException
	{
		int len=0;
		int cur;
		
		byte[] temp=new byte[in.available()];
		
		byte[] temp2;
		
		in.read(temp);
		
		ArrayList<byte[]> lines=new ArrayList<byte[]>();
		//while((cur=in.read())!=-1)
		for(int x=0;x<temp.length;x++)
		{
			cur=temp[x];
			if(cur==10 || cur==13)
			{
				if(len>0)
				{
					temp2=new byte[len];
					for(int i=0;i<len;i++)
					{
						temp2[i]=temp[i];
					}
					lines.add(temp2);
					len=0;
				}
			}
			else
			{
				temp[len]=(byte)cur;
				len+=1;
			}
		}
		return lines.toArray(new byte[0][]);

	}
	
	public FileInfo[] getFileInfos()
	{
		FileInfo[] result=new FileInfo[files.size()];
		for(int i=0;i<files.size();i++)
		{
			result[i]=files.get(i).getFileInfo();
		}
		return result;
	}
	
	public FileInfo[] list()
	{
		ArrayList<FileInfo> result=new ArrayList<FileInfo>();
		for(int i=0;i<files.size();i++)
		{
			MoPaQFile oneFile=files.get(i);
			if(oneFile.language==0 && oneFile.platform==0)
			{
				result.add(oneFile.getFileInfo());
			}
		}
		return (FileInfo[]) result.toArray();
	}
	
	public FileInfo[] list(Pattern filter)
	{
		return list(filter,(short)0,(short)0);
	}
	
	public FileInfo[] list(Pattern filter, short language)
	{
		return list(filter,language,(short)0);
	}
	
	
	public FileInfo[] list(Pattern filter, short language, short platform)
	{
		ArrayList<FileInfo> result=new ArrayList<FileInfo>();
		for(int i=0;i<files.size();i++)
		{
			MoPaQFile oneFile=files.get(i);
			if(oneFile.language==language && oneFile.platform==platform && oneFile.path!=null && filter.matcher(oneFile.path).matches())
			{
				result.add(oneFile.getFileInfo());
			}
		}
		return (FileInfo[]) result.toArray();
	}
	
	public FileInfo[] listAny(Pattern filter)
	{
		ArrayList<FileInfo> result=new ArrayList<FileInfo>();
		for(int i=0;i<files.size();i++)
		{
			MoPaQFile oneFile=files.get(i);
			if(oneFile.path!=null && filter.matcher(oneFile.path).matches())
			{
				result.add(oneFile.getFileInfo());
			}
		}
		return (FileInfo[]) result.toArray();
	}
	
	public FileInfo[] listAnyLanguage(Pattern filter)
	{
		return listAnyLanguage(filter,(short)0);
	}
	
	public FileInfo[] listAnyPlatform(Pattern filter)
	{
		return listAnyPlatform(filter,(short)0);
	}
	
	public FileInfo[] listAnyLanguage(Pattern filter, short language)
	{
		ArrayList<FileInfo> result=new ArrayList<FileInfo>();
		for(int i=0;i<files.size();i++)
		{
			MoPaQFile oneFile=files.get(i);
			if(oneFile.language==language && oneFile.path!=null && filter.matcher(oneFile.path).matches())
			{
				result.add(oneFile.getFileInfo());
			}
		}
		return (FileInfo[]) result.toArray();
	}
	
	public FileInfo[] listAnyPlatform(Pattern filter, short language)
	{
		ArrayList<FileInfo> result=new ArrayList<FileInfo>();
		for(int i=0;i<files.size();i++)
		{
			MoPaQFile oneFile=files.get(i);
			if(oneFile.language==language && oneFile.path!=null && filter.matcher(oneFile.path).matches())
			{
				result.add(oneFile.getFileInfo());
			}
		}
		return (FileInfo[]) result.toArray();
	}
	
	public void copy(FileInfo source,FileInfo target) throws MoPaQException
	{
		copy(source.getFullPath(),source.getLanguage(),source.getPlatform(),target.getFullPath(),target.getLanguage(),target.getPlatform());
	}
	
	public void copy(String sourceName,String targetName) throws MoPaQException
	{
		copy(sourceName,(short)0, (short)0,targetName,(short)0, (short)0);
	}
	
	public void copy(String sourceName,short sourceLanguage,String targetName,short targetLanguage) throws MoPaQException
	{
		copy(sourceName,sourceLanguage, (short)0,targetName,targetLanguage, (short)0);
	}
	
	public void copy(String sourceName,short sourceLanguage, short sourcePlatform,String targetName,short targetLanguage, short targetPlatform) throws MoPaQException
	{
		byte[] data=returnFileByName(sourceName,sourceLanguage,sourcePlatform);
		if(data==null)
		{
			throw new MoPaQException("The source file does not exist");
		}
		writeFile(targetName,data,targetLanguage,targetPlatform);
	}
	
	public void move(FileInfo source,FileInfo target) throws MoPaQException
	{
		move(source.getFullPath(),source.getLanguage(),source.getPlatform(),target.getFullPath(),target.getLanguage(),target.getPlatform());
	}
	
	public void move(String sourceName,String targetName) throws MoPaQException
	{
		move(sourceName,(short)0,(short)0,targetName,(short)0,(short)0);
	}
	
	public void move(String sourceName,short sourceLanguage,String targetName,short targetLanguage) throws MoPaQException
	{
		move(sourceName,sourceLanguage,(short)0,targetName,targetLanguage,(short)0);
	}
	
	public void move(String sourceName,short sourceLanguage, short sourcePlatform,String targetName,short targetLanguage, short targetPlatform) throws MoPaQException
	{
		byte[] data=returnFileByName(sourceName,sourceLanguage,sourcePlatform);
		if(data==null)
		{
			throw new MoPaQException("The source file does not exist");
		}
		writeFile(targetName,data,targetLanguage,targetPlatform);
		delete(sourceName,sourceLanguage,sourcePlatform);
	}
	
	public byte[] returnFileByFileInfo(FileInfo info) throws MoPaQException
	{
		return returnFileByName(info.getFullPath(),info.getLanguage(),info.getPlatform());
	}
	
	public byte[] returnFileByName(String name)
	{
		return returnFileByName(name,(short)0,(short)0);
	}
	
	public byte[] returnFileByName(String name,short language)
	{
		return returnFileByName(name,language,(short)0);
	}
	
	public boolean exists(FileInfo info) throws MoPaQException
	{
		return exists(info.getFullPath(),info.getLanguage(),info.getPlatform());
	}
	
	public boolean exists(String name)
	{
		return exists(name,(short)0,(short)0);
	}
	
	public boolean exists(String name,short language)
	{
		return exists(name,language,(short)0);
	}
	
	public boolean exists(String name,short language, short platform)
	{
		for(int i=0;i<files.size();i++)
		{
		 	MoPaQFile oneFile=files.get(i);
		 	if(oneFile.path.equalsIgnoreCase(name) && oneFile.language==language && oneFile.platform==platform)
		 	{
		 		return true;
		 	}
		}
		
		return false;
	}
	
	public byte[] returnFileByName(String name,short language, short platform)
	{
		for(int i=0;i<files.size();i++)
		{
		 	MoPaQFile oneFile=files.get(i);
		 	if(oneFile.path.equalsIgnoreCase(name) && oneFile.language==language && oneFile.platform==platform)
		 	{
		 		return oneFile.getContent();
		 	}
		}
		
		return null;
	}
	
	public void writeFile(String name,byte[] content)
	{
		writeFile(name,content,(short)0,(short)0);	
	}
	
	public void writeFile(String name,byte[] content,short language)
	{
		writeFile(name,content,language,(short)0);	
	}
	
	public void writeFile(String name,byte[] content,short language, short platform)
	{
		for(int i=0;i<files.size();i++)
		{
		 	MoPaQFile oneFile=files.get(i);
		 	if(oneFile.path.equalsIgnoreCase(name) && oneFile.language==language && oneFile.platform==platform)
		 	{
		 		oneFile.setContent(content);
		 		return;
		 	}
		}
		files.add(new MoPaQFile(name, content, language, platform));
	}
	
	public void deleteAllFiles(String name)
	{
		for(int i=0;i<files.size();i++)
		{
		 	MoPaQFile oneFile=files.get(i);
		 	if(oneFile.path.equalsIgnoreCase(name))
		 	{
		 		files.remove(i);
		 		i-=1;
		 	}
		}
	}

	public void delete(FileInfo info) throws MoPaQException
	{
		delete(info.getFullPath(),info.getLanguage(),info.getPlatform());
	}
	
	public void delete(String name)
	{
		delete(name,(short)0,(short)0);
	}
	
	public void delete(String name, short language)
	{
		delete(name,language,(short)0);
	}
	
	public void delete(String name, short language, short platform)
	{
		for(int i=0;i<files.size();i++)
		{
		 	MoPaQFile oneFile=files.get(i);
		 	if(oneFile.language==language && oneFile.platform==platform && oneFile.path.equalsIgnoreCase(name))
		 	{
		 		files.remove(i);
		 		i-=1;
		 	}
		}
	}
	
	private byte[] writeHeader(int archiveSize,int hashTableOffset, int hashTableLength,int blockTableOffset,int blockTableLength)
	{
		ByteBuffer bb=ByteBuffer.allocate(32);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		bb.putInt(MAGIC);
		bb.putInt(32);
		bb.putInt(archiveSize);
		bb.putShort(VERSION_ORIGINAL);
		bb.put((byte)20); //sector size==max file size with this lib==512MB
		bb.put((byte)0);
		
		bb.putInt(hashTableOffset);
		bb.putInt(blockTableOffset);
		bb.putInt(hashTableLength);
		bb.putInt(blockTableLength);
		
		return bb.array();
	}
	
	
	private byte[] writeHashTable()
	{
		//System.out.println("hashtable start");
		int len=1<<(int)Math.ceil(Math.log(files.size()+1)/Math.log(2));
		long andWith=len-1;
		ByteBuffer bb=ByteBuffer.allocate(len*16);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int[] indexArray=new int[len];
		int cur;
		
		for(int i=0;i<len;i++)
		{
			indexArray[i]=-1;
		}
			
		for(int i=0;i<files.size();i++)
		{
		 	MoPaQFile oneFile=files.get(i);
		 	cur=(int)(Encryption.hashString(oneFile.path, Encryption.HASH_TYPE_HASH_TABLE_OFFSET)&andWith);
		 	while(indexArray[cur]!=-1)
		 	{
		 		cur=(cur+1)%len;
		 	}
		 	indexArray[cur]=i;
		}
		
		for(int i=0;i<len;i++)
		{
			if(indexArray[i]!=-1)
			{
				MoPaQFile oneFile=files.get(indexArray[i]);
				bb.putInt((int)Encryption.hashString(oneFile.path, Encryption.HASH_TYPE_NAMEA));
				bb.putInt((int)Encryption.hashString(oneFile.path, Encryption.HASH_TYPE_NAMEB));
				bb.putShort(oneFile.language);
				bb.putShort(oneFile.platform);
				bb.putInt(indexArray[i]);
			}
			else
			{
				/*bb.putInt(0);
				bb.putInt(0);
				bb.putShort((short)0);
				bb.putShort((short)0);*/
				bb.position(bb.position()+12);
				bb.putInt(HashTableEntry.INDEX_EMPTY);
			}
		}
		//System.out.println("hashtable done");
		return Encryption.encrypt(bb.array(), HASH_TABLE_KEY);
	}
	
	private byte[] writeBlockTable()
	{
		//System.out.println("blocktable start");
		ByteBuffer bb=ByteBuffer.allocate(files.size()*16);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		for(int i=0;i<files.size();i++)
		{
		
			MoPaQFile oneFile=files.get(i);
			bb.putInt((int)oneFile.blockOffset);
			bb.putInt((int)oneFile.compressedSize);
			bb.putInt((int)oneFile.content.length);
			if(oneFile.compressedSize<oneFile.content.length)
			{
				bb.putInt(BlockTableEntry.FLAGS_SINGLE_UNIT|BlockTableEntry.FLAGS_FILE|BlockTableEntry.FLAGS_COMPRESSED);
			}
			else
			{
				bb.putInt(BlockTableEntry.FLAGS_SINGLE_UNIT|BlockTableEntry.FLAGS_FILE);
			}
		}
		
		//System.out.println("blocktable done");
		
		return Encryption.encrypt(bb.array(), BLOCK_TABLE_KEY);	
	
	}
	
	public void save(File destination) throws IOException, MoPaQException
	{
		if(unidentifiedFiles>0)
		{
			throw new MoPaQException("Unidentified files remaining");
		}
		RandomAccessFile dest;
		if(destination.exists())
		{
			destination.delete();
		}
		dest=new RandomAccessFile(destination, "rw");
		save(dest);
		dest.close();
	}
	
	public void save(RandomAccessFile destination) throws IOException, MoPaQException
	{
		int curPos=32;
		int hashPos;
		int blockPos;
		int fileSize;
		byte[] hashtable;
		byte[] blocktable;
		
		if(unidentifiedFiles>0)
		{
			throw new MoPaQException("Unidentified files remaining");
		}
		
		createListfile();
		
		for(int i=0;i<files.size();i++)
		{
			MoPaQFile oneFile=files.get(i);
			//oneFile.compressedSize=oneFile.content.length;
			oneFile.compress();
			oneFile.blockOffset=curPos;
			curPos+=oneFile.compressedSize;
		}
		
		hashPos=curPos;
		hashtable=writeHashTable();
		blockPos=hashPos+hashtable.length;
		blocktable=writeBlockTable();
		fileSize=blockPos+blocktable.length;
		
		//System.out.println("write start");
		destination.write(writeInfoHeader());
		
		destination.write(writeHeader(fileSize, hashPos, hashtable.length/16,blockPos,blocktable.length/16));
		for(int i=0;i<files.size();i++)
		{
			MoPaQFile oneFile=files.get(i);
			destination.write(oneFile.contentCompressed);
		}
		destination.write(hashtable);
		destination.write(blocktable);
		//System.out.println("write done");
	}
	
	private byte[] writeInfoHeader()
	{
		if(infoHeader!=null && infoHeader.length>0)
		{
			int len=(int)Math.ceil((double)(infoHeader.length+12)/512.)*512;
			byte[] result=new byte[len];
			ByteBuffer bb=ByteBuffer.wrap(result);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.putInt(INFO_MAGIC);
			bb.putInt(infoHeader.length);
			bb.putInt(len);
			bb.put(infoHeader);
		
			return result;
		}
		return new byte[0];
	}
	
	private void createListfile() throws MoPaQException
	{
		//Find file names
		ArrayList<String> names=new ArrayList<String>();
		for(int i=0;i<files.size();i++)
		{
			MoPaQFile oneEntry=files.get(i);
			if(oneEntry.path==null)
			{
				throw new MoPaQException("Unidentified files remaining");
			}
			if((! names.contains(oneEntry.path)) && (!oneEntry.path.equalsIgnoreCase("(listfile)")))
			{
				names.add(oneEntry.path);
			}
		}
		
		//Create listfile
		String nameString="";
		for(int i=0;i<names.size();i++)
		{
			nameString+=names.get(i) + "\r\n";
		}
		
		//Write listfile
		writeFile("(listfile)", nameString.getBytes());
	}
	
	private void parseInfoHeader() throws IOException
	{
		ByteBuffer bb;
		int len;
		int offset;
		bb=ByteBuffer.allocate(12);
		source.seek(headerOffset);
		source.read(bb.array());
		bb.order(ByteOrder.LITTLE_ENDIAN);
		if(bb.getInt()!=INFO_MAGIC)
		{
			//System.out.println("No Info Header detected");
			return;
		}
		len=bb.getInt();
		//System.out.println("Info Header length: " + len);
		offset=bb.getInt();
//		System.out.println("Info Header offset: " + offset);
		
		infoHeader=new byte[len];
		source.read(infoHeader);
		
		headerOffset+=offset;
	}
	
	public MoPaQ(File source) throws IOException, MoPaQException, DataFormatException
	{
		sourceFile=source;
		isFileOwner=true;
		open(new RandomAccessFile(source, "r"));
	}
	
	public MoPaQ(RandomAccessFile source) throws IOException, MoPaQException, DataFormatException
	{
		open(source);
	}
	
	private void open(RandomAccessFile source) throws IOException, MoPaQException, DataFormatException
	{
		InputStream listfilein;
		String[] lines;
		byte[][] rawdata;
		long taskTimeNano;
		long startTimeNano;// = System.nanoTime( );
		//InputStreamReader listfileReader;
		//BufferedReader listfileReader;
		
		this.source=source;
		files=new ArrayList<MoPaQFile>();
		//streamStart=source.
		headerOffset=source.getFilePointer();
		
		parseInfoHeader();
		
		parseHeader();
		
		unidentifiedFiles=0;
		parseBlockTable();
		
		parseHashTable();
		
		
		
		
		//startTimeNano = System.nanoTime( );
		listfilein= this.getClass().getResourceAsStream("/mopaqlib/listfile");
		//System.out.println(listfilein.getClass().getSimpleName());
		lines=readFromInputStream(listfilein);
		listfilein.close();
		
		//taskTimeNano = System.nanoTime( ) - startTimeNano;
		//System.out.println("Read Lines: " + String.valueOf((double)taskTimeNano/1e9));
		
		//startTimeNano = System.nanoTime( );
		listfilein= this.getClass().getResourceAsStream("/mopaqlib/listfile");
		rawdata=readBytesFromInputStream(listfilein);
		listfilein.close();
		
		//taskTimeNano = System.nanoTime( ) - startTimeNano;
		//System.out.println("Read ByteArray: " + String.valueOf((double)taskTimeNano/1e9));
		
		//startTimeNano = System.nanoTime( );
		bulkIdentify(rawdata,lines);
		//taskTimeNano = System.nanoTime( ) - startTimeNano;
		//System.out.println("BulkIdentify: " + String.valueOf((double)taskTimeNano/1e9));
		byte[] listFileData;
		listFileData=returnFileByName("(listfile)");
		
		if(listFileData!=null)
		{
			ByteArrayInputStream listFileStream=new ByteArrayInputStream(listFileData);
			lines=readFromInputStream(listFileStream);
			//listFileStream.skip(-listFileData.length);
			listFileStream.reset();
			rawdata=readBytesFromInputStream(listFileStream);
			
			bulkIdentify(rawdata,lines);
		}
		
		//listfileReader.
		
		//bb.order(ByteOrder.LITTLE_ENDIAN);
		
		
		
		
	}
	
	public void extractAll(String target) throws IOException
	{
		MoPaQFile oneEntry;
		RandomAccessFile raf;
		
		for(int i=0;i<files.size();i++)
		{
			oneEntry=files.get(i);
			raf=new RandomAccessFile(target + oneEntry.path.replace("\\","___"), "rw");
			raf.write(oneEntry.content);
			raf.close();
			
		}
	}
	
	public void printDebugInfo()
	{
		System.out.println(String.format("%1$-50s%2$-10s%3$-10s%4$-10s", "NAME","SIZE","COMP.SIZE","FLAGS"));
		
		MoPaQFile oneEntry;
		FileInfo oneInfo;
		String name;
		String size;
		String compSize;
		String flagString;
		
		for(int i=0;i<files.size();i++)
		{
			oneEntry=files.get(i);
			oneInfo=oneEntry.getFileInfo();
			/*if(oneEntry.isFile() && !oneEntry.wasDeleted() && oneEntry.references.size()>0)
			{*/
				name=oneInfo.getDisplayPath();
				size=String.valueOf(oneInfo.getFileSize());
				compSize=String.valueOf(oneInfo.getCompressedSize());
				flagString="";
				/*if(oneEntry.isCompressed())
				{
					flagString+="C";
				}
				if(oneEntry.isImploded())
				{
					flagString+="I";
				}
				if(oneEntry.isEncrypted())
				{
					flagString+="E";
				}
				if(oneEntry.isEncryptionAdjusted())
				{
					flagString+="A";
				}
				if(oneEntry.isSingleUnit())
				{
					flagString+="S";
				}*/
				System.out.println(String.format("%1$-50s%2$-10s%3$-10s%4$-10s", name,size,compSize,flagString));
			//}
		}
	}

	public File getSourceFile() {
		return sourceFile;
	}
	
}
