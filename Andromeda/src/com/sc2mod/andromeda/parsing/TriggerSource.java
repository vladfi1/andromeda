/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parsing;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class TriggerSource extends AndromedaSource {

	private String code;
	private String name;
	private File mapFile;
	
	public TriggerSource(File mapFile,String name, String code){
		this.name = name;
		this.code = code;
		this.mapFile = mapFile;
	}
	
	@Override
	public Reader createReader() throws IOException {
		return new StringReader(code);
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public String getTypeName() {
		return "trigger";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long length() {
		return code.length();
	}

	@Override
	public String getFullPath() {
		return "trigger://" + mapFile.getAbsolutePath() + ":" + getName();
	}
	
}
