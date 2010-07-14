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

public interface UnaryOperator {

  public final static int COMP = 0;
  public final static int MINUS = 1;
  public final static int NOT = 2;
  public final static int PREPLUSPLUS = 3;
  public final static int PREMINUSMINUS = 4;
  public final static int POSTPLUSPLUS = 5;
  public final static int POSTMINUSMINUS = 6;
  public final static int DEREFERENCE = 7;
  public final static int ADDRESSOF = 8;

}
