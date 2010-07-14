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
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class FileSource extends AndromedaSource{

	private File file;
	public FileSource(String path){
		this.file = new File(path);
	}

	public FileSource(File file) {
		this.file = file;
	}

	@Override
	public Reader createReader() throws IOException{
		return new FileReader(file);
	}

	@Override
	public boolean exists() {
		return file.exists();
	}

	@Override
	public String getTypeName() {
		return "file";
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public long length() {
		return file.length();
	}

	public File getFile() {
		return file;
	}
	
	@Override
	public String getFullPath() {
		return "file://" + file.getAbsolutePath();
	}
}
