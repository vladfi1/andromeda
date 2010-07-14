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

public interface AssignmentOperatorType {

  public final static int EQ = 0;
  public final static int MULTEQ = 1;
  public final static int DIVEQ = 2;
  public final static int MODEQ = 3;
  public final static int PLUSEQ = 4;
  public final static int MINUSEQ = 5;
  public final static int LSHIFTEQ = 6;
  public final static int RSHIFTEQ = 7;
  public final static int URSHIFTEQ = 8;
  public final static int ANDEQ = 9;
  public final static int XOREQ = 10;
  public final static int OREQ = 11;

}
