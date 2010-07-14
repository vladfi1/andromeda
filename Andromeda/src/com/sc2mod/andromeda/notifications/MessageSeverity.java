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

public final class MessageSeverity {
	public static final int INFO = 0;
	public static final int WARNING = 1;
	public static final int ERROR = 2;
	/**
	 * Util class
	 */
	private MessageSeverity(){}
	
	public static String getName(int severity){
		switch(severity){
		case INFO: return "info";
		case WARNING: return "warning";
		case ERROR: return "error";
		default: return "unknown";
		}
	}
	
}
