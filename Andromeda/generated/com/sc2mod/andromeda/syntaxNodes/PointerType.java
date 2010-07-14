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

public class PointerType extends Type {

  private int category;
  private Type wrappedType;

  public PointerType (int category, Type wrappedType) {
    this.category = category;
    this.wrappedType = wrappedType;
    if (wrappedType != null) wrappedType.setParent(this);
  }

  public int getCategory() {
    return category;
  }

  public void setCategory(int category) {
    this.category = category;
  }

  public Type getWrappedType() {
    return wrappedType;
  }

  public void setWrappedType(Type wrappedType) {
    this.wrappedType = wrappedType;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (wrappedType != null) wrappedType.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (wrappedType != null) wrappedType.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (wrappedType != null) wrappedType.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("PointerType(\n");
    buffer.append("  "+tab+category);
    buffer.append("\n");
      if (wrappedType != null)
        buffer.append(wrappedType.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [PointerType]");
    return buffer.toString();
  }
}
