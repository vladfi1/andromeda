/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.util.visitors.TreeScanVisitor;

/**
 * Visitor for analysis. Has access to the environment.
 * @author J. 'gex' Finis
 *
 */
public class AnalysisVisitor<P,R> extends TreeScanVisitor<P, R>{

	protected Environment env;
		
	public AnalysisVisitor(Environment env) {
		super();
		this.env = env;
	}

}
