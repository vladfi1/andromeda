package mopaqlib;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Encryption {
	private static long[] cryptTable;
	
	public static final int HASH_TYPE_HASH_TABLE_OFFSET=0;
	public static final int HASH_TYPE_NAMEA=1;
	public static final int HASH_TYPE_NAMEB=2;
	public static final int HASH_TYPE_FILE_KEY=3;
    
	
	static
	{
		cryptTable=new long[0x500];
        long v1;
        long v2;
        long seed = 0x00100001;
        int i1 = 0;
        int i2 = 0;

        
        
        for (i1 = 0; i1 < 0x100; i1++)
        {
            for (i2 = i1;i2<0x500; i2 += 0x100)
            {
                seed = (seed * 125 + 3) % 0x2AAAAB;
                v1 = (seed & 0xFFFF) << 0x10;

                seed = (seed * 125 + 3) % 0x2AAAAB;
                v2 = (seed & 0xFFFF);

                cryptTable[i2] = (v1 | v2);
            }
        }
        
        
                
        /*for(i1=0;i1<0x500;i1++)
        {
        	System.out.println(cryptTable[i1]);
        }*/
        
	}
	
	public static long hashFileName(String filename)
	{
		return hashString(filename,HASH_TYPE_NAMEA)<<32 | hashString(filename,HASH_TYPE_NAMEB);
	}
	
	public static long hashFileName(byte[] filename)
	{
		return hashByteArray(filename,HASH_TYPE_NAMEA)<<32 | hashByteArray(filename,HASH_TYPE_NAMEB);
	}

	public static long hashByteArray(byte[] data, int hashType)
	{
        int CH;
        
        long seed1 = 0x7FED7FEDL;
        long seed2 = 0xEEEEEEEEL;
        
        //char[] chars=data.toUpperCase().toCharArray();
        
        for (int i = 0; i < data.length; i++)
        {
            CH = (int)data[i]&0xff;
            if(CH>0x60 && CH<0x7B)
            {
            	CH-=0x20;
            }
            
            //seed1 = (cryptTable[(hashType * 0x100) + CH] ^ (seed1 + seed2)) & 0xffffffffl;
            seed1 = (cryptTable[(hashType <<8) + CH] ^ (seed1 + seed2)) & 0xffffffffl;
            seed2 = (CH + seed1 + seed2 + (seed2 << 5) + 3) & 0xffffffffl;
            
        }
        return seed1;
	}

	
	public static long hashString(String data, int hashType)
	{
        int CH;
        
        long seed1 = 0x7FED7FEDL;
        long seed2 = 0xEEEEEEEEL;
        
        char[] chars=data.toUpperCase().toCharArray();
        
        for (int i = 0; i < chars.length; i++)
        {
            CH = chars[i];
            
            //seed1 = (cryptTable[(hashType * 0x100) + CH] ^ (seed1 + seed2)) & 0xffffffffl;
            seed1 = (cryptTable[(hashType <<8) + CH] ^ (seed1 + seed2)) & 0xffffffffl;
            seed2 = (CH + seed1 + seed2 + (seed2 << 5) + 3) & 0xffffffffl;
            
        }
        return seed1;
	}
	
	public static byte[] decryptFromStream(RandomAccessFile source,long key, long pos, int length) throws IOException
	{
		byte[] data=new byte[length];
		source.seek(pos);
		source.read(data);
		return decrypt(data,key);
	}
	
    public static byte[] encrypt(byte[] data,long key)
    {
    	ByteBuffer bb=ByteBuffer.wrap(data);
    	bb.order(ByteOrder.LITTLE_ENDIAN);
    	ByteBuffer result=ByteBuffer.allocate(data.length);
    	result.order(ByteOrder.LITTLE_ENDIAN);
    	long ch;
    	//UInt32 Seed=0xEEEEEEEE;
    	long seed=0xEEEEEEEEl;
    	long cur;
    	
    	
        while(bb.remaining()>3)
        {
        	cur=((long)bb.getInt())&0xffffffffl;
        	//System.out.println("Cur: " + cur);
        	//Seed += (UInt32) CryptTable[0x400 + (Key & 0xFF)];
        	seed=(seed+cryptTable[(int)(0x400l + (key & 0xFFl))])&0xffffffffl;
        	//System.out.println("Seed: " + seed);
        	//CH = BitConverter.ToUInt32(Buffer1,I) ^ (Key + Seed);
        	ch=(cur^(key+seed))&0xffffffffl;
        	//System.out.println("ch: " + ch);
        	//Key = ((~Key << 0x15) + 0x11111111) | (Key >> 0x0B);
        	key=((((~key) << 0x15l) + 0x11111111l) | (key >> 0x0Bl))&0xffffffffl;
        	//System.out.println("Key: " + key);
        	
        	//Seed = BitConverter.ToUInt32(Buffer1, I) + Seed + (Seed << 5) + 3;
        	seed=(cur+seed+(seed<<5l)+3l)&0xffffffffl;
        	//System.out.println("seed: " + seed);
        	
        	result.putInt((int)ch);
            
            

            
            
/*
            Temp=BitConverter.GetBytes(CH);
            Buffer2[I] = Temp[0];
            Buffer2[I+1] = Temp[1];
            Buffer2[I+2] = Temp[2];
            Buffer2[I+3] = Temp[3];*/
        }
        
        result.put(bb);
        return result.array();
/*        UInt32 CH;
        
        Int32 I;
        Byte[] Buffer1=new Byte[(Int32)Math.Ceiling((double)Data.Length/4.0)*4];
        Data.CopyTo(Buffer1, 0);
        Byte[] Buffer2 = new Byte[(Int32)Math.Ceiling((double)Data.Length / 4.0) * 4];
        Data.CopyTo(Buffer2,0);
        Byte[] Output = new Byte[Data.Length];
        Byte[] Temp;

        for (I = 0; I < Data.Length-3; I += 4)
        {

        }

        for (I = 0; I < Data.Length; I++)
        {
            Output[I] = Buffer2[I];
        }

        return Output;*/
    }
	
	
    public static byte[] decrypt(byte[] data, long key)
    {
    	ByteBuffer bb=ByteBuffer.wrap(data);
    	bb.order(ByteOrder.LITTLE_ENDIAN);
    	ByteBuffer result=ByteBuffer.allocate(data.length);
    	result.order(ByteOrder.LITTLE_ENDIAN);
        long CH;
        long seed = 0xEEEEEEEEl;
        
        //long uintRead=0;
        
        //Byte[] Temp;
        //Byte[] Buffer1 = new Byte[Data.Length];

        //Data.CopyTo(Buffer1, 0);
        

        //for (int i = 0; i < data.length - 3; i += 4)
        
        //System.out.println("Key: " + String.valueOf(key));
        
        while(bb.remaining()>3)
        {
            seed = (seed+cryptTable[0x400 + (int)(key & 0xFFl)])&0xffffffffl;
            //System.out.println("Seed1: " + String.valueOf(seed));
            //uintRead=(long)bb.getInt()&0xffffffffl;
            //System.out.println("UInt1: " + String.valueOf(uintRead));
            CH = (((long)bb.getInt()&0xffffffffl) ^ (key + seed))&0xffffffffl;
            //CH = ((long)bb.getInt() ^ (key + seed))&0xffffffffl;
            //System.out.println("CH1: " + String.valueOf(CH));
            key = (((~key << 0x15) + 0x11111111l) | (key >> 0x0B))&0xffffffffl;
            //System.out.println("Key1: " + String.valueOf(key));
            seed = (CH + seed + (seed << 5) + 3)&0xffffffffl;
            //System.out.println("Seed2: " + String.valueOf(seed));
            
            result.putInt((int)(CH&0xffffffffl));
            
            /*Temp = BitConverter.GetBytes(CH);

            Buffer1[I] = Temp[0];
            Buffer1[I + 1] = Temp[1];
            Buffer1[I + 2] = Temp[2];
            Buffer1[I + 3] = Temp[3];*/
        }
        result.put(bb);
        return result.array();
    }

}
