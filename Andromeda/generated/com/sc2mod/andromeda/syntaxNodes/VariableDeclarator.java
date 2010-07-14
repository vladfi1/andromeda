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

public abstract class VariableDeclarator extends SyntaxNode {

  private com.sc2mod.andromeda.environment.types.Type inferedType;
  private SyntaxNode parent;

  public VariableDeclaratorId getName() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setName(VariableDeclaratorId name) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Expression getInitializer() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setInitializer(Expression initializer) {
    throw new ClassCastException("tried to call abstract method");
  }

  public ArrayInitializer getArrayInit() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setArrayInit(ArrayInitializer arrayInit) {
    throw new ClassCastException("tried to call abstract method");
  }

  public com.sc2mod.andromeda.environment.types.Type getInferedType() {
    return inferedType;
  }

  public void setInferedType(com.sc2mod.andromeda.environment.types.Type inferedType) {
    this.inferedType = inferedType;
  }

  public SyntaxNode getParent() {
    return parent;
  }

  public void setParent(SyntaxNode parent) {
    this.parent = parent;
  }

  public abstract void accept(Visitor visitor);
  public abstract void childrenAccept(Visitor visitor);
  public abstract void traverseTopDown(Visitor visitor);
  public abstract void traverseBottomUp(Visitor visitor);
  public String toString() {
    return toString("");
  }

  public abstract String toString(String tab);
}
