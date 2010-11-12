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

import com.sc2mod.andromeda.notifications.InternalProgramError;

public class FileSource extends Source{

	private File file;
	private String pkgName;
	public FileSource(String path, String pkgName){
		this(new File(path), pkgName);
	}

	public FileSource(File file, String pkgName) {
		try {
			this.file = file.getCanonicalFile();
		} catch (IOException e) {
			throw new InternalProgramError(e);
		}
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

	@Override
	public Language getAnticipatedLanguage() {
		if(file.getName().endsWith(".galaxy")){
			return Language.GALAXY;
		} else {
			return Language.ANDROMEDA;
		}
	}

	@Override
	public String getPathInSourceFolder() {
		return pkgName;
	}
	
	
}
