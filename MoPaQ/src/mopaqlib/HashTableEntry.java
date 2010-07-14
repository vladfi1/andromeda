package mopaqlib;

public class HashTableEntry {
	static final short INDEX_EMPTY=0xffffffff;
	static final short INDEX_DELETED=0xfffffffe;
	
	public int filePathHashA;
	public int filePathHashB;
	public long filePathHash;
	public short language;
	public short platform;
	public int fileBlockIndex;

	public BlockTableEntry fileBlock;
	
	public String fileName;
	
	public HashTableEntry(int filePathHashA,int filePathHashB,short language,short platform,int fileBlockIndex)
	{
		this.filePathHashA=filePathHashA;
		this.filePathHashB=filePathHashB;
		this.language=language;
		this.platform=platform;
		this.fileBlockIndex=fileBlockIndex;
		
		filePathHash=(long)filePathHashA<<32 | ((long)filePathHashB & 0xffffffffl);
		fileBlock=null;
		fileName=null;
	}
	
	public boolean isIndexValid()
	{
		return (fileBlockIndex!=INDEX_EMPTY) && (fileBlockIndex!=INDEX_DELETED);
	}
}
