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

public class GlobalInitDeclaration extends GlobalStructure {

  private StaticInitDeclaration initDecl;

  public GlobalInitDeclaration (StaticInitDeclaration initDecl) {
    this.initDecl = initDecl;
    if (initDecl != null) initDecl.setParent(this);
  }

  public StaticInitDeclaration getInitDecl() {
    return initDecl;
  }

  public void setInitDecl(StaticInitDeclaration initDecl) {
    this.initDecl = initDecl;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (initDecl != null) initDecl.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (initDecl != null) initDecl.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (initDecl != null) initDecl.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("GlobalInitDeclaration(\n");
      if (initDecl != null)
        buffer.append(initDecl.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [GlobalInitDeclaration]");
    return buffer.toString();
  }
}
