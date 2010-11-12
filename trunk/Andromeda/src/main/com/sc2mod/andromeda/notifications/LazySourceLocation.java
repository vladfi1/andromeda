/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.notifications;

import java.io.IOException;

import com.sc2mod.andromeda.parsing.Source;
import com.sc2mod.andromeda.parsing.SourceManager;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class LazySourceLocation implements SourceLocation {

	private SourceManager env;
	private int column = -1;
	private int line = -1;
	private int offset;
	private int length;
	private Source source;
	private int fileId;
	private SourceLocationContent loadedLocation;
	private String description;
	
	public LazySourceLocation(SyntaxNode sn, SourceManager env){
		this(sn.getLeftPos(),sn.getRightPos(),env);
	}
	
	public LazySourceLocation(int left, int right, SourceManager env){
		this.env = env;
		offset = (left&0x00FFFFFF);
		length = (right&0x00FFFFFF)-offset;
		fileId = (left&0xFF000000);
	}
	
	private void loadPosition(){
		if(loadedLocation != null) return;
		if(source==null) loadSource();
		try {
			loadedLocation = new SourceLocationContent(source, offset, length);
			column = loadedLocation.column;
			line = loadedLocation.line;
		} catch (IOException e) {
			loadedLocation = null;
			column = line = -2;
		}
		
	}

	
	private void loadSource() {
		source = env.getSourceById(fileId);
	}
	
	@Override
	public int getColumn() {
		if(column==-1) loadPosition();
		return column;
	}

	@Override
	public int getFileOffset() {
		return offset;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public int getLine() {
		if(line==-1) loadPosition();
		return line;
	}

	@Override
	public Source getSource() {
		if(source==null) loadSource();
		return source;
	}

	@Override
	public String getDescriptionString() {
		if(description == null){
			loadPosition();
			if(column == -2){
				description = "-- SOURCE INFORMATION COULDN'T BE READ --";
			} else{
				description = loadedLocation.toString();
			}
		}
		return description;
	}




}
