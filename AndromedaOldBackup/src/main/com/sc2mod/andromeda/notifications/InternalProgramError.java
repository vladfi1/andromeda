package com.sc2mod.andromeda.notifications;

import java.io.IOException;

import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class InternalProgramError extends Error{

	public InternalProgramError(SyntaxNode where, String message){
		this(message);
	}
	
	public InternalProgramError(String message){
		super(message);
	}

	public InternalProgramError(Throwable e) {
		super(e);
	}
}
