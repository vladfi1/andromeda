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

public interface ClassMemberType {

  public final static int ACCESSOR_DECLARATION = 0;
  public final static int ACCESSOR_GET = 1;
  public final static int ACCESSOR_SET = 2;
  public final static int METHOD_DECLARATION = 3;
  public final static int FIELD_DECLARATION = 4;
  public final static int CONSTRUCTOR_DECLARATION = 5;
  public final static int DESTRUCTOR_DECLARATION = 6;
  public final static int STATIC_INIT = 7;

}
