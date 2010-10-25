/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.util.logging;

import java.util.List;

import com.sc2mod.andromeda.notifications.Message;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.parsing.SourceReader;
import com.sc2mod.andromeda.parsing.CompilationFileManager;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public abstract class Logger {
	
	private LogLevel logLevel;
	private int level;
	private LogLevel lastLevel;
	
	public void println(LogLevel logLevel, LogFormat logFormat, String message){
		print(logLevel,logFormat,message + "\n");
	}

	public abstract void print(LogLevel logLevel, LogFormat logFormat, String message);
	
	
	public boolean log(LogLevel ll){
		if(ll.getLevel()>=level) return true;
		return false;
	}

	public abstract void printProblem(Problem problem);
	

	
	
}
