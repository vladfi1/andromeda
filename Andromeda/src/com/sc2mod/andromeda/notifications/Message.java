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

import com.sc2mod.andromeda.parsing.AndromedaSource;

public interface Message {


	
	
	/**
	 * Returns the message text
	 * @return message text
	 */
	public String getText();
	
	/**
	 * Returns the severity of the message (see constants).
	 * @return severity
	 */
	public int getSeverity();
	
	/**
	 * Returns all source positions this message refers to
	 * as an array
	 * @return source positions
	 */
	public SourcePosition[] getPositions();
	
	/**
	 * Gets the message code of this message
	 * @return message code
	 */
	public int getCode();

}
