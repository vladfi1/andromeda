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

public class FunctionDeclaration extends GlobalStructure {

  private MethodDeclaration funcDecl;

  public FunctionDeclaration (MethodDeclaration funcDecl) {
    this.funcDecl = funcDecl;
    if (funcDecl != null) funcDecl.setParent(this);
  }

  public MethodDeclaration getFuncDecl() {
    return funcDecl;
  }

  public void setFuncDecl(MethodDeclaration funcDecl) {
    this.funcDecl = funcDecl;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (funcDecl != null) funcDecl.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (funcDecl != null) funcDecl.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (funcDecl != null) funcDecl.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("FunctionDeclaration(\n");
      if (funcDecl != null)
        buffer.append(funcDecl.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [FunctionDeclaration]");
    return buffer.toString();
  }
}
