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

public abstract class Type extends SyntaxNode {

  private SyntaxNode parent;

  public int getCategory() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setCategory(int category) {
    throw new ClassCastException("tried to call abstract method");
  }

  public String getName() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setName(String name) {
    throw new ClassCastException("tried to call abstract method");
  }

  public TypeList getTypeArguments() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setTypeArguments(TypeList typeArguments) {
    throw new ClassCastException("tried to call abstract method");
  }

  public FieldAccess getQualifiedName() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setQualifiedName(FieldAccess qualifiedName) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Type getWrappedType() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setWrappedType(Type wrappedType) {
    throw new ClassCastException("tried to call abstract method");
  }

  public ExpressionList getDimensions() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setDimensions(ExpressionList dimensions) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Type getReturnType() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setReturnType(Type returnType) {
    throw new ClassCastException("tried to call abstract method");
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
