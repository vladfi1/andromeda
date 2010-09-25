/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.program;

import java.io.File;

public class Util {

	public static void checkForFile(File f) throws InvalidParameterException{
		checkForFile(f,true);
	}
	
	public static void checkForFile(File f, boolean mustExist) throws InvalidParameterException{
		if(!f.exists()){
			if(mustExist)throw new InvalidParameterException("The supplied file " + f.getAbsolutePath().toString() + " does not exist");
		} else if(f.isDirectory()){
			throw new InvalidParameterException("The supplied file " + f.getAbsolutePath().toString() + " is a directory, not a file");
		}
	}
	
	public static void checkForFile(File f, String extension) throws InvalidParameterException{
		checkForFile(f);
		if(!f.getName().endsWith(extension)){
			throw new InvalidParameterException("The supplied file " + f.getAbsolutePath().toString() + " has the wrong extension (" + extension + " required)");
		}
	}
	
	public static void checkForDir(File f, boolean mustExist) throws InvalidParameterException{
		if(!f.exists()){
			if(mustExist)throw new InvalidParameterException("The supplied directory " + f.getAbsolutePath().toString() + " does not exist");
		} else if(!f.isDirectory()){
			throw new InvalidParameterException("The supplied directory " + f.getAbsolutePath().toString() + " is a file, not a directory");
		}
	}
}
