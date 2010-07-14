/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.syntaxNodes;

public interface AccessType {

  public final static int SIMPLE = 0;
  public final static int SUPER = 1;
  public final static int NATIVE = 2;
  public final static int NAMED_SUPER = 3;
  public final static int EXPRESSION = 4;
  public final static int POINTER = 5;

}