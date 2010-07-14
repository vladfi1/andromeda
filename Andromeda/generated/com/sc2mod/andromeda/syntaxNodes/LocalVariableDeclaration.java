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

public class LocalVariableDeclaration extends SyntaxNode {

  private SyntaxNode parent;
  private Modifiers modifiers;
  private Type type;
  private VariableDeclarators declarators;

  public LocalVariableDeclaration (Modifiers modifiers, Type type, VariableDeclarators declarators) {
    this.modifiers = modifiers;
    if (modifiers != null) modifiers.setParent(this);
    this.type = type;
    if (type != null) type.setParent(this);
    this.declarators = declarators;
    if (declarators != null) declarators.setParent(this);
  }

  public Modifiers getModifiers() {
    return modifiers;
  }

  public void setModifiers(Modifiers modifiers) {
    this.modifiers = modifiers;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public VariableDeclarators getDeclarators() {
    return declarators;
  }

  public void setDeclarators(VariableDeclarators declarators) {
    this.declarators = declarators;
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
    if (modifiers != null) modifiers.accept(visitor);
    if (type != null) type.accept(visitor);
    if (declarators != null) declarators.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (modifiers != null) modifiers.traverseTopDown(visitor);
    if (type != null) type.traverseTopDown(visitor);
    if (declarators != null) declarators.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (modifiers != null) modifiers.traverseBottomUp(visitor);
    if (type != null) type.traverseBottomUp(visitor);
    if (declarators != null) declarators.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString() {
    return toString("");
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("LocalVariableDeclaration(\n");
      if (modifiers != null)
        buffer.append(modifiers.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (type != null)
        buffer.append(type.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (declarators != null)
        buffer.append(declarators.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [LocalVariableDeclaration]");
    return buffer.toString();
  }
}
