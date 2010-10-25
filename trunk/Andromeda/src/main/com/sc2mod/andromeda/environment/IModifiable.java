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

import com.sc2mod.andromeda.environment.scopes.Visibility;

public interface IModifiable {

	void setVisibility(Visibility visibility);
	void setConst();
	void setAbstract();
	void setFinal();
	void setStatic();
	void setNative();
	void setOverride();
	
	public Visibility getVisibility();
	public boolean isConst();
	public boolean isAbstract();
	public boolean isFinal();
	public boolean isStatic();
	public boolean isNative();
	public boolean isOverride();
	
	
}
