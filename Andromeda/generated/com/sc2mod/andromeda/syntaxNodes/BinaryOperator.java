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

public interface BinaryOperator {

  public final static int OROR = 0;
  public final static int ANDAND = 1;
  public final static int OR = 2;
  public final static int AND = 3;
  public final static int XOR = 4;
  public final static int EQEQ = 5;
  public final static int NOTEQ = 6;
  public final static int GT = 7;
  public final static int LT = 8;
  public final static int GTEQ = 9;
  public final static int LTEQ = 10;
  public final static int LSHIFT = 11;
  public final static int RSHIFT = 12;
  public final static int URSHIFT = 13;
  public final static int PLUS = 14;
  public final static int MINUS = 15;
  public final static int MULT = 16;
  public final static int DIV = 17;
  public final static int MOD = 18;

}
