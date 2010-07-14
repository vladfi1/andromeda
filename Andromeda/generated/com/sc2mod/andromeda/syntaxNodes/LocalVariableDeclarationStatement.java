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

public class LocalVariableDeclarationStatement extends Statement {

  private LocalVariableDeclaration varDeclaration;

  public LocalVariableDeclarationStatement (LocalVariableDeclaration varDeclaration) {
    this.varDeclaration = varDeclaration;
    if (varDeclaration != null) varDeclaration.setParent(this);
  }

  public LocalVariableDeclaration getVarDeclaration() {
    return varDeclaration;
  }

  public void setVarDeclaration(LocalVariableDeclaration varDeclaration) {
    this.varDeclaration = varDeclaration;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (varDeclaration != null) varDeclaration.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (varDeclaration != null) varDeclaration.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (varDeclaration != null) varDeclaration.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("LocalVariableDeclarationStatement(\n");
      if (varDeclaration != null)
        buffer.append(varDeclaration.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [LocalVariableDeclarationStatement]");
    return buffer.toString();
  }
}