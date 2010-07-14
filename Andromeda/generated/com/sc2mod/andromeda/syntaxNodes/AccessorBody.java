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

public class AccessorBody extends SyntaxNode {

  private SyntaxNode parent;
  private MethodDeclaration getMethod;
  private MethodDeclaration setMethod;

  public AccessorBody (MethodDeclaration getMethod, MethodDeclaration setMethod) {
    this.getMethod = getMethod;
    if (getMethod != null) getMethod.setParent(this);
    this.setMethod = setMethod;
    if (setMethod != null) setMethod.setParent(this);
  }

  public MethodDeclaration getGetMethod() {
    return getMethod;
  }

  public void setGetMethod(MethodDeclaration getMethod) {
    this.getMethod = getMethod;
  }

  public MethodDeclaration getSetMethod() {
    return setMethod;
  }

  public void setSetMethod(MethodDeclaration setMethod) {
    this.setMethod = setMethod;
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
    if (getMethod != null) getMethod.accept(visitor);
    if (setMethod != null) setMethod.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (getMethod != null) getMethod.traverseTopDown(visitor);
    if (setMethod != null) setMethod.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (getMethod != null) getMethod.traverseBottomUp(visitor);
    if (setMethod != null) setMethod.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString() {
    return toString("");
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("AccessorBody(\n");
      if (getMethod != null)
        buffer.append(getMethod.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (setMethod != null)
        buffer.append(setMethod.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [AccessorBody]");
    return buffer.toString();
  }
}
