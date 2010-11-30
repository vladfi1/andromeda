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

import com.sc2mod.andromeda.parsing.options.ConfigFile;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;

public class MapRunner {
 
	private String params;
	private String executablePath;
	private Platform platform;
	
	public MapRunner(Platform platform, Configuration options, ConfigFile config) {
		this.params = options.getParamString(Parameter.TEST_SC2_PARAMS);
		this.platform = platform;
		executablePath = config.getPropertyString("GENERAL", "sc2Executable", "");
	}
	
	public MapRunner(Platform platform, String sc2ExecutablePath, String params){
		this.params = params;
		this.platform = platform;
		this.executablePath = sc2ExecutablePath;
	}

	public Process test(File mapFile) throws IOException{

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
			return Runtime.getRuntime().exec(cmd);

		} else {
			processString = "\"" + f.getAbsolutePath() + "\"" + " "+ params + " -run \"" + mapFile.getAbsolutePath() + "\"";
			System.out.println(processString);
			return Runtime.getRuntime().exec(processString);

		}

	}
}
