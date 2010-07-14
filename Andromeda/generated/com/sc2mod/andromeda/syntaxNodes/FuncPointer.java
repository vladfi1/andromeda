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

public class FuncPointer extends Type {

  private int category;
  private Type returnType;
  private TypeList typeArguments;

  public FuncPointer (int category, Type returnType, TypeList typeArguments) {
    this.category = category;
    this.returnType = returnType;
    if (returnType != null) returnType.setParent(this);
    this.typeArguments = typeArguments;
    if (typeArguments != null) typeArguments.setParent(this);
  }

  public int getCategory() {
    return category;
  }

  public void setCategory(int category) {
    this.category = category;
  }

  public Type getReturnType() {
    return returnType;
  }

  public void setReturnType(Type returnType) {
    this.returnType = returnType;
  }

  public TypeList getTypeArguments() {
    return typeArguments;
  }

  public void setTypeArguments(TypeList typeArguments) {
    this.typeArguments = typeArguments;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (returnType != null) returnType.accept(visitor);
    if (typeArguments != null) typeArguments.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (returnType != null) returnType.traverseTopDown(visitor);
    if (typeArguments != null) typeArguments.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (returnType != null) returnType.traverseBottomUp(visitor);
    if (typeArguments != null) typeArguments.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("FuncPointer(\n");
    buffer.append("  "+tab+category);
    buffer.append("\n");
      if (returnType != null)
        buffer.append(returnType.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (typeArguments != null)
        buffer.append(typeArguments.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [FuncPointer]");
    return buffer.toString();
  }
}
