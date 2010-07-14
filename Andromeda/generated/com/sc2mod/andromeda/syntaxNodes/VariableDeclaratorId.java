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

public class VariableDeclaratorId extends SyntaxNode {

  private SyntaxNode parent;
  private String name;
  private VariableDeclaratorId arrayChild;
  private com.sc2mod.andromeda.environment.types.Type inferedType;

  public VariableDeclaratorId (String name, VariableDeclaratorId arrayChild) {
    this.name = name;
    this.arrayChild = arrayChild;
    if (arrayChild != null) arrayChild.setParent(this);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public VariableDeclaratorId getArrayChild() {
    return arrayChild;
  }

  public void setArrayChild(VariableDeclaratorId arrayChild) {
    this.arrayChild = arrayChild;
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

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (arrayChild != null) arrayChild.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (arrayChild != null) arrayChild.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (arrayChild != null) arrayChild.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString() {
    return toString("");
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("VariableDeclaratorId(\n");
    buffer.append("  "+tab+name);
    buffer.append("\n");
      if (arrayChild != null)
        buffer.append(arrayChild.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [VariableDeclaratorId]");
    return buffer.toString();
  }
}