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

public class Platform {

	public final String name;
	public final boolean isOSX;
	public Platform(){
		name = System.getProperty("os.name").toLowerCase();
		isOSX = name.startsWith("mac os x");
		if(isOSX){
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Andromeda");
		}
	}
}
