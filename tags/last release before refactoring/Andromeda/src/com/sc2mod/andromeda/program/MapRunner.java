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
import java.io.IOException;

public class MapRunner {
 
	private String params;
	private String executablePath;
	private Platform platform;
	
	public MapRunner(Platform platform, Options options, ConfigHandler config) {
		this.params = options.runParams;
		this.platform = platform;
		executablePath = config.getPropertyString("GENERAL", "sc2Executable", "");
	}
	
	public MapRunner(Platform platform, String sc2ExecutablePath, String params){
		this.params = params;
		this.platform = platform;
		this.executablePath = sc2ExecutablePath;
	}

	public void test(File mapFile) throws IOException{

		if(executablePath==""){
			throw new IOException("No SC2Folder specified in the conf file");
		}
		if(mapFile==null){
			throw new IOException("No map file to test specified!");
		}
		if(!mapFile.exists()){
			throw new IOException("Map file to test does not exist!");
		}
		File f = new File(executablePath);
		if(!f.exists()){
			throw new IOException("Starcraft 2 executable does not exist.\n(" + f.getAbsolutePath() + ")");
		}
		String processString;
		if(platform.isOSX){
			processString = "open \"" + f.getAbsolutePath() + "\" --args " + " "+ params + " -run \"" + mapFile.getAbsolutePath() + "\"";
			String[] cmd = {"/bin/bash", "-c", processString};
			System.out.println(processString);
			Runtime.getRuntime().exec(cmd);

		} else {
			processString = "\"" + f.getAbsolutePath() + "\"" + " "+ params + " -run \"" + mapFile.getAbsolutePath() + "\"";
			Runtime.getRuntime().exec(processString);

		}

	}
}
