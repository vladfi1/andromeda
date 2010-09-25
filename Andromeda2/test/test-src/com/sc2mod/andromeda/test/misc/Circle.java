/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.test.misc;

import com.sc2mod.andromeda.util.Pair;


interface CircleInterface {

	public int getX();

	public void setX(int x);
}

public class Circle implements CircleInterface{

	private int x;

	public int getX() {
		return x;
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}
}

class Circle2 implements CircleInterface{

	private int x;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
}

class CircleUser {
	
	public static void bla(){
		Circle c = new Circle();
		c.setX(6);
		
	}
}
