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
import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.parsing.AndromedaSource;
import com.sc2mod.andromeda.parsing.FileSource;

public class FileCollector {

	
	public static List<AndromedaSource> getFiles(File baseDir){
		List<AndromedaSource> result = new ArrayList<AndromedaSource>();
		if(!baseDir.exists()) throw new Error("Native library directory " + baseDir.getAbsolutePath() + " does not exist!");
		getFilesInternal(baseDir,result);
		return result;
	}

	private static void getFilesInternal(File file, List<AndromedaSource> result) {
		if(file.isDirectory()){
			for(File f: file.listFiles()){
				getFilesInternal(f, result);
			}
			return;
		}
		String name = file.getName();
		if(name.endsWith(".galaxy")||name.endsWith(".a")) result.add(new FileSource(file));
		
	}

	public static List<AndromedaSource> getFilesFromList(File rootPath, String fileNames) {
		ArrayList<AndromedaSource> result = new ArrayList<AndromedaSource>();
		String[] files = fileNames.split(",");
		for(String s: files){
			File f = new File(rootPath.getAbsolutePath() + "/" + s);
			if(!f.exists()) throw new CompilationError("Missing native library file '" + f.getAbsolutePath() + "'!");
			result.add(new FileSource(f));
		}
		return result;
	}


}
