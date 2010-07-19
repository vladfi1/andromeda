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

/**
 * Visibility modifier for a class, field, or function.
 * @author J. 'gex' Finis
 */
public final class Visibility {
	private Visibility(){}
	
	public static final int DEFAULT = 0;
	public static final int PUBLIC = 1;
	public static final int PROTECTED = 2;
	public static final int PRIVATE = 3;
	
	/**
	 * Determines the relationship between two visibilities.
	 * @param visibility1 The first visibility.
	 * @param visibility2 The second visibility.
	 * @return 
	 */
	public static boolean isLessVisibleThan(int visibility1, int visibility2){
		if(visibility1==DEFAULT){
			return visibility2 != DEFAULT;
		}
		if(visibility2==DEFAULT){
			return true;
		}
		return visibility1 > visibility2;
		
		
	}
	
	
	public static String getName(int visibility){
		switch(visibility){
		case DEFAULT:
			return "default";
		case PUBLIC:
			return "public";
		case PROTECTED:
			return "protected";
		case PRIVATE:
			return "private";
		}
		return "unknown";
	}
	
}
