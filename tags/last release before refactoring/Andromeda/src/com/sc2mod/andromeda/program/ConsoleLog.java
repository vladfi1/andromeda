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

import com.sc2mod.andromeda.parsing.SourceEnvironment;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class ConsoleLog extends CollectingLog{
	public void print(String message){
		System.out.print(message);
	}
	
	public void println(String message){
		System.out.println(message);
	}

	public void warning(SyntaxNode where, String message){
		super.warning(where, message);
		System.err.println("\nWARNING: " + message + "\nat: " + SourceEnvironment.getLastEnvironment().getSourceInformation(where));
	}

	@Override
	public void caption(String message) {
		System.out.println(message);
	}
}
