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

public class PackageDecl extends SyntaxNode {

  private SyntaxNode parent;
  private FieldAccess packageName;

  public PackageDecl (FieldAccess packageName) {
    this.packageName = packageName;
    if (packageName != null) packageName.setParent(this);
  }

  public FieldAccess getPackageName() {
    return packageName;
  }

  public void setPackageName(FieldAccess packageName) {
    this.packageName = packageName;
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
    if (packageName != null) packageName.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (packageName != null) packageName.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (packageName != null) packageName.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString() {
    return toString("");
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("PackageDecl(\n");
      if (packageName != null)
        buffer.append(packageName.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [PackageDecl]");
    return buffer.toString();
  }
}
