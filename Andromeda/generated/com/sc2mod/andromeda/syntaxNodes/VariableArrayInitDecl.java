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

public class VariableArrayInitDecl extends VariableDeclarator {

  private VariableDeclaratorId name;
  private ArrayInitializer arrayInit;

  public VariableArrayInitDecl (VariableDeclaratorId name, ArrayInitializer arrayInit) {
    this.name = name;
    if (name != null) name.setParent(this);
    this.arrayInit = arrayInit;
    if (arrayInit != null) arrayInit.setParent(this);
  }

  public VariableDeclaratorId getName() {
    return name;
  }

  public void setName(VariableDeclaratorId name) {
    this.name = name;
  }

  public ArrayInitializer getArrayInit() {
    return arrayInit;
  }

  public void setArrayInit(ArrayInitializer arrayInit) {
    this.arrayInit = arrayInit;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (name != null) name.accept(visitor);
    if (arrayInit != null) arrayInit.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (name != null) name.traverseTopDown(visitor);
    if (arrayInit != null) arrayInit.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (name != null) name.traverseBottomUp(visitor);
    if (arrayInit != null) arrayInit.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("VariableArrayInitDecl(\n");
      if (name != null)
        buffer.append(name.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (arrayInit != null)
        buffer.append(arrayInit.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [VariableArrayInitDecl]");
    return buffer.toString();
  }
}
