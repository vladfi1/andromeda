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

import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.notifications.Message;
import com.sc2mod.andromeda.notifications.WarningMessage;
import com.sc2mod.andromeda.parsing.SourceEnvironment;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public abstract class CollectingLog implements Log {

	private ArrayList<Message> messages = new ArrayList<Message>();
	@Override
	public void warning(SyntaxNode where, String message) {
		messages.add(new WarningMessage(where, SourceEnvironment.getLastEnvironment(), message));
	}
	
	@Override
	public List<Message> getMessages() {
		return messages;
	}
	
	@Override
	public List<Message> flushMessages() {
		ArrayList<Message> result = messages;
		messages = new ArrayList<Message>();
		return result;
	}
	
	@Override
	public void addMessage(Message m) {
		messages.add(m);
	}
}
