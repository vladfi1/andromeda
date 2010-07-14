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

import java.util.List;

import com.sc2mod.andromeda.notifications.Message;
import com.sc2mod.andromeda.parsing.AndromedaReader;
import com.sc2mod.andromeda.parsing.SourceEnvironment;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public interface Log {
	
	public void print(String message);
	
	public void caption(String message);
	
	public void println(String message);

	public void warning(SyntaxNode where, String message);
	
	public List<Message> getMessages();
	
	public List<Message> flushMessages();
	
	public void addMessage(Message m);
}
