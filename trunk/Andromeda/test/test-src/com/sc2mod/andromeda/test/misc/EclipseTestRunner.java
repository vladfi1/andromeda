/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.test.misc;

import java.io.File;
import java.net.URISyntaxException;

import com.sc2mod.andromeda.program.Program;

public class EclipseTestRunner {

	/**
	 * Checks the command line parameters for a conf entry.
	 * If none is found it inserts the conf entry such that it points to the test
	 * config: test/andromeda.conf
	 * @return
	 */
	private static String[] addTestConfig(String[] args){
		for(String s: args){
			//a run config was specified, so do nothing
			if(s.startsWith("-c")) return args;
		}
		
		//no run config. Add one
		String[] args2 = new String[args.length+1];
		System.arraycopy(args, 0, args2, 1, args.length);
		args2[0]="-ctest/andromeda.conf";
		return args2;
	}
	
	
	public static void main(String args[]) throws URISyntaxException{
		Program.appDirectory = new File(".");
		args = addTestConfig(args);
		Program.main(args);
	}
}
