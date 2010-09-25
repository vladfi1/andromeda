/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.codetransform;

import com.sc2mod.andromeda.environment.Invocation;

public class InlineDecider {
	public static final int INLINE_NO = 1;
	public static final int INLINE_SIMPLE = 1;
	public static final int INLINE_ADVANCED = 1;
	public static int decide(Invocation inv) {
		return INLINE_NO;
	}
	
}
