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

public class UnlocatedErrorMessage implements Message {

	private String text;

	public UnlocatedErrorMessage(String text) {
		super();
		this.text = text;
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
	public SourcePosition[] getPositions() {
		return new SourcePosition[0];
	}

	@Override
	public int getSeverity() {
		return MessageSeverity.ERROR;
	}

	@Override
	public String getText() {
		return text;
	}

}
