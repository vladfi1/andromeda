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

import java.util.ArrayList;

class DFS{
	public DFS e(){ return null; }
}
public class classTest extends DFS{

	public void bla(A a){System.out.println("A");}
	
	public void bla(B b,A a){System.out.println("B");}
	
	public void bla(A b,B a){ System.out.println("B");}


	public DFS e(){ return null; }
	
}

class Z<T>{
	
	public T bla(){
		new ArrayList<T>();
		new T();
	}
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
