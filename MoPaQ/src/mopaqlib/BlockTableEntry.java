package mopaqlib;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.RandomAccess;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.apache.tools.bzip2.CBZip2InputStream;

public class BlockTableEntry {
	public long blockOffset;
	public long blockHeaderOffset;
	public int blockSize;                         
	public int fileSize;
	public int flags;
	public long maxSectorSize;
	
	public long hash=-1;
	public byte[] data;
	
	public ArrayList<HashTableEntry> references;
	public ArrayList<Long> sectorPositions;
	public ArrayList<Long> sectorLengths;
	public RandomAccessFile source;
	MoPaQ parent;
	
	static final int FLAGS_FILE=0x80000000;
	static final int FLAGS_CHECKSUMS=0x04000000;
	static final int FLAGS_DELETED=0x02000000;
	static final int FLAGS_SINGLE_UNIT=0x01000000;
	static final int FLAGS_ENCRYPTION_ADJUSTED=0x00020000;
	static final int FLAGS_ENCRYPTED=0x00010000; 
	static final int FLAGS_COMPRESSED=0x00000200;
	static final int FLAGS_IMPLODED=0x00000100;
	
	static final byte COMPRESSION_NONE=0x00;
	
	static final byte COMPRESSION_SPARSE=0x20;
	static final byte COMPRESSION_IMA_ADPCM_MONO=0x40;
	static final byte COMPRESSION_IMA_ADPCM_STEREO=(byte)0x80;
	static final byte COMPRESSION_HUFFMAN=0x01;
	static final byte COMPRESSION_DEFLATED=0x02;
	static final byte COMPRESSION_IMPLODED=0x08;
	static final byte COMPRESSION_BZIP2=0x10;
	
	static final byte COMPRESSION_LZMA=0x12;
	static final byte COMPRESSION_SPARSE_AND_DELFATE=0x22;
	static final byte COMPRESSION_SPARSE_AND_BZIP2=0x30;
	static final byte COMPRESSION_IMA_ADPCM_MONO_AND_HUFFMAN=0x41;
	static final byte COMPRESSION_IMA_ADPCM_MONO_AND_IMPLODE=0x48;
	static final byte COMPRESSION_IMA_ADPCM_STEREO_AND_HUFFMAN=(byte)0x81;
	static final byte COMPRESSION_IMA_ADPCM_STEREO_AND_IMPLODE=(byte)0x88;
	
	public static byte[] cloneByteArray(byte[] source, int offset, int length)
	{
		byte[] result=new byte[length];
		int i;
		for(i=0;i<length;i++)
		{
			result[i]=source[i+offset];
		}
		return result;
	}
	public static void cloneByteArrayTo(byte[] target, byte[] source, int offset, int length)
	{
		
		int i;
		for(i=0;i<length;i++)
		{
			target[i+offset]=source[i];
		}
		
	}
	
	public BlockTableEntry(MoPaQ parent,long blockHeaderOffset, int blockSize, int fileSize, int flags, RandomAccessFile source,long headerOffset,long maxSectorSize) throws IOException
	{
		this.parent=parent;
		this.blockHeaderOffset=blockHeaderOffset;
		this.blockOffset=blockHeaderOffset+headerOffset;
		this.blockSize=blockSize;
		this.fileSize=fileSize;
		this.flags=flags;
		this.source=source;
		this.maxSectorSize=maxSectorSize;
		
		references=new ArrayList<HashTableEntry>();
		sectorPositions=new ArrayList<Long>();
		sectorLengths=new ArrayList<Long>();
		

	}
	
	public void readData() throws IOException, DataFormatException, MoPaQException
	{
		byte[] sector;
		byte[] buffer;
		int i;
		int expectedSectorSize;
		
		if(sectorPositions.size()==0)
		{
			return;
		}
		if(isEncrypted() && hash==-1)
		{
			return;
		}
		data=new byte[fileSize];
		
		expectedSectorSize=(int)maxSectorSize;
		//System.out.println("START " + findFilename());
		//System.out.println("SECTOR COUNT " + sectorPositions.size());
		for(i=0;i<sectorPositions.size();i++)
		{
			//System.out.println("LENGTH " + (long)sectorLengths.get(i));
			if(i==sectorPositions.size()-1)
			{
				expectedSectorSize=fileSize-(int)maxSectorSize*i;
			}
			//System.out.println("EXPECTED " + expectedSectorSize);
			
			if(isEncrypted())
			{
				sector=Encryption.decryptFromStream(source,(hash+(long)i)&0xffffffffl,(long)sectorPositions.get(i),(int)(long)sectorLengths.get(i));
			//	return;
			}
			else
			{
				sector=new byte[(int)(long)sectorLengths.get(i)];
				source.seek(sectorPositions.get(i));
				source.read(sector);
			}
			
			if(isCompressed() && sectorLengths.get(i)!=expectedSectorSize)
			{
				//System.out.println("COMPRESSED");
				switch(sector[0])
				{
				case COMPRESSION_BZIP2:
					ByteArrayInputStream bin=new ByteArrayInputStream(cloneByteArray(sector, 3, sector.length-3));
					bin.reset();
					CBZip2InputStream bzipin=new CBZip2InputStream(bin);
					buffer=new byte[expectedSectorSize];
					bzipin.read(buffer);
					cloneByteArrayTo(data, buffer, i*(int)maxSectorSize, expectedSectorSize);
					bzipin.close();
					//throw new MoPaQException("Unsupported compression: BZIP2");
					break;
				case COMPRESSION_DEFLATED:
					//System.out.println("DEFLATE " + findFilename());
					Inflater inflater=new Inflater();
					buffer=cloneByteArray(sector, 1, sector.length-1);//=new byte[sector.length-3];
					
					inflater.setInput(buffer);
					buffer=new byte[expectedSectorSize];
					inflater.inflate(buffer);
					inflater.end();
					cloneByteArrayTo(data, buffer, i*(int)maxSectorSize, expectedSectorSize);
					
					//System.out.println("Unsupported compression: DEFLATED");
					
					//buffer=
					break;
				case COMPRESSION_HUFFMAN:
					throw new MoPaQException("Unsupported compression: HUFFMAN");
//					break;
				case COMPRESSION_IMA_ADPCM_MONO:
					throw new MoPaQException("Unsupported compression: IMA ADPCM MONO");
//					break;
				case COMPRESSION_IMA_ADPCM_STEREO:
					throw new MoPaQException("Unsupported compression: IMA ADPCM STEREO");
//					break;
				case COMPRESSION_IMPLODED:
					throw new MoPaQException("Unsupported compression: IMPLODED");
//					break;
				case COMPRESSION_LZMA:
					throw new MoPaQException("Unsupported compression: LZMA");
//					break;
				case COMPRESSION_SPARSE:
					throw new MoPaQException("Unsupported compression: SPARSE");
//					break;
				/*case COMPRESSION_NONE:
					buffer=cloneByteArray(sector, 1, sector.length-1);//=new byte[sector.length-3];
					cloneByteArrayTo(data, buffer, i*(int)maxSectorSize, buffer.length);
					if(sector.length-1<expectedSectorSize)
					{
						System.out.println("Sector too small!!!");
					}
					break;*/
					
				default:
					throw new MoPaQException("Unsupported compression id: " + String.valueOf(sector[0]));
				}
				//System.out.println("COMPRESSEDEND");
			}
			else
			{
				cloneByteArrayTo(data, sector, i*(int)maxSectorSize, expectedSectorSize);
			}
				
		}
		
		/*FileOutputStream fout=new FileOutputStream("exported" + findFilename().replace('\\', '_'));
		fout.write(data);
		fout.close();*/
		parent.files.add(new MoPaQFile(findFilename(), data,findLanguage() , findPlatform(),blockSize));
		//System.out.println("Added: " +findFilename());
	}
	
	public void findSectors() throws IOException, DataFormatException, MoPaQException
	{
		ByteBuffer bb;
		int numSectors;
		int i;
		long prev;
		long cur;
		String filename;
		//long hash;
		
		sectorPositions.clear();
		sectorLengths.clear();
		if(isFile() && isCompressed() && !isSingleUnit())
		{
			
			
			numSectors=(int)Math.ceil((double)fileSize/(double)maxSectorSize);
			
			
			
			
			if(isEncrypted())
			{
				//System.out.println("encrypted");
				filename=findFilename();
				if(filename==null)
				{
					return;
				}
				if(filename.indexOf('\\')>-1)
				{
					filename=filename.substring(filename.indexOf('\\')+1);
				}
				hash=Encryption.hashString(filename, Encryption.HASH_TYPE_FILE_KEY);
				
				if(isEncryptionAdjusted())
				{
					//System.out.println("adjusted");
					hash=(hash+blockHeaderOffset)^fileSize;
				}
				/*else
				{
				*/	bb=ByteBuffer.wrap(Encryption.decryptFromStream(source,hash,blockOffset,numSectors*4+4));
				//}
				
				bb=ByteBuffer.wrap(Encryption.decryptFromStream(source,(hash-1l)&0xffffffffl,blockOffset,numSectors*4+4));
			}
			else
			{
				source.seek(blockOffset);
				bb=ByteBuffer.allocate(numSectors*4+4);
				source.read(bb.array());
			}
			bb.order(ByteOrder.LITTLE_ENDIAN);
			
			//source.read(bb.array());
			
			prev=(long)bb.getInt()&0xffffffffl;
			for(i=0;i<numSectors;i++)
			{
				cur=(long)bb.getInt()&0xffffffffl;
				sectorPositions.add(blockOffset+prev);
				sectorLengths.add(cur-prev);
				//System.out.println(String.valueOf(sectorPositions.get(i)) + " : " + String.valueOf(sectorLengths.get(i)));
				prev=cur;
			}
			
		}
		else
		{
			sectorPositions.add(blockOffset);
			sectorLengths.add((long)blockSize);
		}
		readData();
	}
	
	
	public String findFilename()
	{
		int i;
		for(i=0;i<references.size();i++)
		{
			if(references.get(i).fileName!=null)
			{
				return references.get(i).fileName;
			}
		}
		return null;
	}
	
	public short findLanguage()
	{
		int i;
		if(references.size()>0)
		{
			return references.get(0).language;
		}
		return 0;
	}
	
	public short findPlatform()
	{
		int i;
		if(references.size()>0)
		{
			return references.get(0).platform;
		}
		return 0;
	}
	
	public boolean isFile()
	{
		return (flags&FLAGS_FILE)==FLAGS_FILE;
	}
	
	public boolean wasDeleted()
	{
		return (flags&FLAGS_DELETED)==FLAGS_DELETED;
	}
	public boolean hasChecksums()
	{
		return (flags&FLAGS_CHECKSUMS)==FLAGS_CHECKSUMS;
	}
	public boolean isSingleUnit()
	{
		return (flags&FLAGS_SINGLE_UNIT)==FLAGS_SINGLE_UNIT;
	}
	public boolean isEncryptionAdjusted()
	{
		return (flags&FLAGS_ENCRYPTION_ADJUSTED)==FLAGS_ENCRYPTION_ADJUSTED;
	}
	public boolean isEncrypted()
	{
		return (flags&FLAGS_ENCRYPTED)==FLAGS_ENCRYPTED;
	}
	public boolean isCompressed()
	{
		return (flags&FLAGS_COMPRESSED)==FLAGS_COMPRESSED;
	}
	public boolean isImploded()
	{
		return (flags&FLAGS_IMPLODED)==FLAGS_IMPLODED;
	}
	
	
}
