/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.parsing.options.InvalidParameterException;
import com.sc2mod.andromeda.program.Program;

public class Files {

	public static File getAppFile(String path){
		if(path.length() != 0){
			char c = path.charAt(0);
			switch(c){
			case '$' : path = path.substring(1);
			case '&' : return getUserFile(path.substring(1));
			}
		}
		File f = new File(path);
		try {
			if(f.isAbsolute()) return f.getCanonicalFile();
			//If this is no absolute file, use the app directory as base
			f = new File(Program.appDirectory,path).getCanonicalFile();
			return f;
		} catch (IOException e) {
			throw new InternalProgramError(e);
		} 
	}
	
	public static File getUserFile(String path){
		if(path.length() != 0){
			char c = path.charAt(0);
			switch(c){
			case '$' : return getAppFile(path.substring(1)); 
			case '&' : path = path.substring(1);
			}
		}
		try {
			return new File(path).getCanonicalFile();
		} catch (IOException e) {
			throw new InternalProgramError(e);
		}
	}
	
	public static BufferedReader getReaderFromResource(String resourcePath){
		return new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(resourcePath)));
	}
	
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
