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

public class FieldDeclaration extends ClassMemberDeclaration {

  private int memberType;
  private AnnotationList annotations;
  private Modifiers fieldModifiers;
  private Type type;
  private VariableDeclarators declaredVariables;

  public FieldDeclaration (int memberType, AnnotationList annotations, Modifiers fieldModifiers, Type type, VariableDeclarators declaredVariables) {
    this.memberType = memberType;
    this.annotations = annotations;
    if (annotations != null) annotations.setParent(this);
    this.fieldModifiers = fieldModifiers;
    if (fieldModifiers != null) fieldModifiers.setParent(this);
    this.type = type;
    if (type != null) type.setParent(this);
    this.declaredVariables = declaredVariables;
    if (declaredVariables != null) declaredVariables.setParent(this);
  }

  public int getMemberType() {
    return memberType;
  }

  public void setMemberType(int memberType) {
    this.memberType = memberType;
  }

  public AnnotationList getAnnotations() {
    return annotations;
  }

  public void setAnnotations(AnnotationList annotations) {
    this.annotations = annotations;
  }

  public Modifiers getFieldModifiers() {
    return fieldModifiers;
  }

  public void setFieldModifiers(Modifiers fieldModifiers) {
    this.fieldModifiers = fieldModifiers;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public VariableDeclarators getDeclaredVariables() {
    return declaredVariables;
  }

  public void setDeclaredVariables(VariableDeclarators declaredVariables) {
    this.declaredVariables = declaredVariables;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (annotations != null) annotations.accept(visitor);
    if (fieldModifiers != null) fieldModifiers.accept(visitor);
    if (type != null) type.accept(visitor);
    if (declaredVariables != null) declaredVariables.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (annotations != null) annotations.traverseTopDown(visitor);
    if (fieldModifiers != null) fieldModifiers.traverseTopDown(visitor);
    if (type != null) type.traverseTopDown(visitor);
    if (declaredVariables != null) declaredVariables.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (annotations != null) annotations.traverseBottomUp(visitor);
    if (fieldModifiers != null) fieldModifiers.traverseBottomUp(visitor);
    if (type != null) type.traverseBottomUp(visitor);
    if (declaredVariables != null) declaredVariables.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("FieldDeclaration(\n");
    buffer.append("  "+tab+memberType);
    buffer.append("\n");
      if (annotations != null)
        buffer.append(annotations.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (fieldModifiers != null)
        buffer.append(fieldModifiers.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (type != null)
        buffer.append(type.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (declaredVariables != null)
        buffer.append(declaredVariables.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [FieldDeclaration]");
    return buffer.toString();
  }
}
