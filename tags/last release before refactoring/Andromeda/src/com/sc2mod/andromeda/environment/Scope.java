/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment;

public class Scope {

	private int inclusionType;
	
	public int getInclusionType() {
		return inclusionType;
	}

	public void setInclusionType(int inclusionType) {
		this.inclusionType = inclusionType;
	}

	public Scope(int inclusionType) {
		this.inclusionType = inclusionType;
	}
}
