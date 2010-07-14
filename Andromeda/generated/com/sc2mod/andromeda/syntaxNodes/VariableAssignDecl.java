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

public class VariableAssignDecl extends VariableDeclarator {

  private VariableDeclaratorId name;
  private Expression initializer;

  public VariableAssignDecl (VariableDeclaratorId name, Expression initializer) {
    this.name = name;
    if (name != null) name.setParent(this);
    this.initializer = initializer;
    if (initializer != null) initializer.setParent(this);
  }

  public VariableDeclaratorId getName() {
    return name;
  }

  public void setName(VariableDeclaratorId name) {
    this.name = name;
  }

  public Expression getInitializer() {
    return initializer;
  }

  public void setInitializer(Expression initializer) {
    this.initializer = initializer;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (name != null) name.accept(visitor);
    if (initializer != null) initializer.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (name != null) name.traverseTopDown(visitor);
    if (initializer != null) initializer.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (name != null) name.traverseBottomUp(visitor);
    if (initializer != null) initializer.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("VariableAssignDecl(\n");
      if (name != null)
        buffer.append(name.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (initializer != null)
        buffer.append(initializer.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [VariableAssignDecl]");
    return buffer.toString();
  }
}
