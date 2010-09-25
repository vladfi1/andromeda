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

import java.io.PrintStream;

import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.SourceLocation;
import com.sc2mod.andromeda.parsing.CompilationFileManager;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class ConsoleLog extends Logger{
	
	@Override
	public void print(LogLevel logLevel, LogFormat logFormat, String message) {
		System.out.print(message);
	}

	@Override
	public void printProblem(Problem problem) {
		String prefix = null;
		PrintStream out;
		switch(problem.getSeverity()){
		case ERROR:
		case FATAL_ERROR:
			prefix = "ERROR";
			out = System.err;
			break;
		case WARNING:
			prefix = "WARNING";
			out = System.out;
			break;
		default:
			prefix = "INFO";
			out = System.out;
		}
		SourceLocation[] locations = problem.getLocations();
		String suffix;
		if(locations.length == 1){
			suffix = "at: ";
		} else if(locations.length == 0){
			suffix = "(unlocated)";
		} else {
			suffix = "at (" + locations.length + " locations): ";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(prefix).append(": ").append(problem.getMessage()).append("\n").append(suffix);
		for(SourceLocation l : locations){
			sb.append(l.getDescriptionString()).append("\n");
		}
		out.print(sb.toString());
	}
}
