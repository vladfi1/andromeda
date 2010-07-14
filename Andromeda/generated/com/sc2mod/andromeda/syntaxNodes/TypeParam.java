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

public class TypeParam extends SyntaxNode {

  private SyntaxNode parent;
  private String name;
  private Object typeBound;

  public TypeParam (String name, Object typeBound) {
    this.name = name;
    this.typeBound = typeBound;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getTypeBound() {
    return typeBound;
  }

  public void setTypeBound(Object typeBound) {
    this.typeBound = typeBound;
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
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    accept(visitor);
  }

  public String toString() {
    return toString("");
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("TypeParam(\n");
    buffer.append("  "+tab+name);
    buffer.append("\n");
    buffer.append("  "+tab+typeBound);
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [TypeParam]");
    return buffer.toString();
  }
}
