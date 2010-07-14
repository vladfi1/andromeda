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

public class MethodDeclarator extends SyntaxNode {

  private SyntaxNode parent;
  private String name;
  private ParameterList parameters;

  public MethodDeclarator (String name, ParameterList parameters) {
    this.name = name;
    this.parameters = parameters;
    if (parameters != null) parameters.setParent(this);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ParameterList getParameters() {
    return parameters;
  }

  public void setParameters(ParameterList parameters) {
    this.parameters = parameters;
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
    if (parameters != null) parameters.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (parameters != null) parameters.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (parameters != null) parameters.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString() {
    return toString("");
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("MethodDeclarator(\n");
    buffer.append("  "+tab+name);
    buffer.append("\n");
      if (parameters != null)
        buffer.append(parameters.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [MethodDeclarator]");
    return buffer.toString();
  }
}
