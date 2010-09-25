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
class DFS{
	public static void e(){}
}
public class classTest extends DFS{

	public void bla(A a){System.out.println("A");}
	
	public void bla(B b,A a){System.out.println("B");}
	
	public void bla(A b,B a){ System.out.println("B");}


	
}

class A{
	
	public void a(){}
}

abstract class B extends A{
	@Override
	public abstract void a();
}

class C extends B implements D{

	@Override
	public void a() {
		// TODO Auto-generated method stub
		
	}}

interface D{}
