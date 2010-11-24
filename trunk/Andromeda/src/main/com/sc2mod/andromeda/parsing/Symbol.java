/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parsing;

public class Symbol extends com.sc2mod.andromeda.parser.cup.Symbol {
  //private int line;
  //private int column;

  public Symbol(int type, int start, int end) {
    this(type, start, end, null);
  }

  public Symbol(int type, int start, int end, Object value) {
	 super(type, start, end, value);
  }

 /* public AndromedaSymbol(int type, int line, int column, int left, int right, Object value) {
    super(type, left, right, value);
    this.line = line;
    this.column = column;
  }*/

  
  
  public int getLine() {
    return left;
  }

  public int getColumn() {
    return right;
  }

  public String toString() {   
    return "line "+left+", column "+right+", sym: "+sym+(value == null ? "" : (", value: '"+value+"'"));
  }
}
