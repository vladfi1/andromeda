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

public interface TypeCategory {

  public final static int BASIC = 0;
  public final static int SIMPLE = 1;
  public final static int QUALIFIED = 2;
  public final static int ARRAY = 3;
  public final static int POINTER = 4;
  public final static int FUNCTION = 5;

}
