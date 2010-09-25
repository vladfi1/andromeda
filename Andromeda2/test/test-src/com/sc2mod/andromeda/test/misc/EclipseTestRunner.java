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

	
	public static void main(String args[]) throws URISyntaxException{
		Program.appDirectory = new File(".");
		Program.main(args);
	}
}
