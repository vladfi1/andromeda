package com.sc2mod.andromeda.parsing;

public class SourceInfo {

	private final Source source;
	private final int fileId;
	private final InclusionType type;
	public String qualifiedName;
	


	public SourceInfo(Source source, int fileId, InclusionType type) {
		super();
		this.source = source;
		this.fileId = fileId;
		this.type = type;
	}
	
	public String getQualifiedName() {
		return qualifiedName;
	}

	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
	
	public Source getSource() {
		return source;
	}

	public int getFileId() {
		return fileId;
	}

	public InclusionType getType() {
		return type;
	}
	
	
	
	
}
