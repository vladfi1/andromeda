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

public class GlobalVarDeclaration extends GlobalStructure {

  private FieldDeclaration fieldDecl;

  public GlobalVarDeclaration (FieldDeclaration fieldDecl) {
    this.fieldDecl = fieldDecl;
    if (fieldDecl != null) fieldDecl.setParent(this);
  }

  public FieldDeclaration getFieldDecl() {
    return fieldDecl;
  }

  public void setFieldDecl(FieldDeclaration fieldDecl) {
    this.fieldDecl = fieldDecl;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (fieldDecl != null) fieldDecl.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (fieldDecl != null) fieldDecl.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (fieldDecl != null) fieldDecl.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("GlobalVarDeclaration(\n");
      if (fieldDecl != null)
        buffer.append(fieldDecl.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [GlobalVarDeclaration]");
    return buffer.toString();
  }
}
