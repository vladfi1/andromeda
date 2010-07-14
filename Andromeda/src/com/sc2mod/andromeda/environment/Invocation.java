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

public class Invocation extends SemanticsElement{

	public static final int TYPE_STATIC = 1;
	public static final int TYPE_VIRTUAL = 2;
	public static final int TYPE_METHOD = 3;
	public static final int TYPE_NATIVE = 4;
	public static final int TYPE_FUNCTION = 5;
	private AbstractFunction whichFunction;
	private int accessType;
	private boolean isUsed;
	
	public void use(){
		isUsed = true;
	}
	
	public boolean isUsed(){
		return isUsed;
	}

	public int getAccessType() {
		return accessType;
	}

	public AbstractFunction getWhichFunction() {
		return whichFunction;
	}
	
	public Invocation( AbstractFunction meth, int accessType){
		this.whichFunction = meth;
		this.accessType = accessType;
	}

	public static Invocation createVirtualInvocation(AbstractFunction meth, Environment env){
		Invocation inv = new Invocation(meth,TYPE_VIRTUAL);
		env.addVirtualInvocation(inv);
		return inv;
	}

	public void setAccessType(int at) {
		accessType = at;		
	}

	
}
