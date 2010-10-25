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

import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.parsing.FileSource;
import com.sc2mod.andromeda.parsing.Source;

public class FileCollector {

	
	public static List<Source> getFiles(File baseDir){
		List<Source> result = new ArrayList<Source>();
		if(!baseDir.exists()) return result;
		getFilesInternal(baseDir,result);
		return result;
	}

	private static void getFilesInternal(File file, List<Source> result) {
		if(file.isDirectory()){
			for(File f: file.listFiles()){
				getFilesInternal(f, result);
			}
			return;
		}
		String name = file.getName();
		if(name.endsWith(".galaxy")||name.endsWith(".a")) result.add(new FileSource(file));
		
	}

	public static List<Source> getFilesFromList(File rootPath, String[] files) {
		ArrayList<Source> result = new ArrayList<Source>();
		for(String s: files){
			File f = new File(rootPath.getAbsolutePath() + "/" + s);
			if(!f.exists()) throw Problem.ofType(ProblemId.MISSING_NATIVE_LIB).details(f.getAbsolutePath())
									.raiseUnrecoverable();
			result.add(new FileSource(f));
		}
		return result;
	}


}
