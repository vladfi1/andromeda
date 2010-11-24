package com.sc2mod.andromeda.problems;

import com.sc2mod.andromeda.parsing.CompilerThread;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class InternalProgramError extends Error{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InternalProgramError(SyntaxNode where, String message){
		this(message + "\nat: " + CompilerThread.getSourceInfo(where));
	}
	
	public InternalProgramError(String message){
		super(message);
	}

	public InternalProgramError(Throwable e) {
		super(e);
	}
	

}
