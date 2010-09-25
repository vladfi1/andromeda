/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.notifications;

import com.sc2mod.andromeda.parsing.CompilationFileManager;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class WarningMessage implements Message{

	
	private SourceLocation[] positions;
	private String text;
	
	
	public WarningMessage(SyntaxNode where, CompilationFileManager env, String text){
		this.text = text;
		positions = new SourceLocation[]{new LazySourceLocation(where, env)};
	}

	@Override
	public int getCode() {
		return 0;
	}
	
	@Override
	public int getId() {
		return 0;
	}

	@Override
	public SourceLocation[] getPositions() {
		return positions;
	}

	@Override
	public int getSeverity() {
		return MessageSeverity.WARNING;
	}

	@Override
	public String getText() {
		return text;
	}

	
}
