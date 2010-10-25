/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.gui.jobs;

import java.io.File;
import java.util.ArrayList;

import com.sc2mod.andromeda.parsing.Source;
import com.sc2mod.andromeda.parsing.andromeda.AndromedaWorkflow;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.InvalidParameterException;
import com.sc2mod.andromeda.program.Program;

public class JobExecMap implements Job{

	private String runConf;
	private boolean runMap;
	private File inFile;
	private File outFile;
	public JobExecMap(File inFile, File outFile, String runConf, boolean runMap) {
		this.runConf = runConf;
		this.runMap = runMap;
		this.inFile = inFile;
		this.outFile = outFile;
	}

	@Override
	public boolean execute() {
		Configuration o;
		try {
			o = new Configuration(Program.config, Program.clOptions, runConf);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			return false;
		}
		o.mapIn = inFile;
		o.mapOut = outFile;
		o.runMap = runMap;
		Program.log.println("");
		return new AndromedaWorkflow(new ArrayList<Source>(0),o).compile().isSuccessful();
	}

	@Override
	public boolean isRepeatable() {
		return true;
	}

}
