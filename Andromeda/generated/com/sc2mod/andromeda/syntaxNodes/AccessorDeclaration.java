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

public class AccessorDeclaration extends ClassMemberDeclaration {

  private int memberType;
  private AnnotationList annotations;
  private Modifiers fieldModifiers;
  private Type type;
  private VariableDeclaratorId accessorName;
  private ParameterList accessorParameters;
  private MethodDeclaration getMethod;
  private MethodDeclaration setMethod;
  private boolean accessorUseThis;

  public AccessorDeclaration (int memberType, AnnotationList annotations, Modifiers fieldModifiers, Type type, VariableDeclaratorId accessorName, ParameterList accessorParameters, MethodDeclaration getMethod, MethodDeclaration setMethod, boolean accessorUseThis) {
    this.memberType = memberType;
    this.annotations = annotations;
    if (annotations != null) annotations.setParent(this);
    this.fieldModifiers = fieldModifiers;
    if (fieldModifiers != null) fieldModifiers.setParent(this);
    this.type = type;
    if (type != null) type.setParent(this);
    this.accessorName = accessorName;
    if (accessorName != null) accessorName.setParent(this);
    this.accessorParameters = accessorParameters;
    if (accessorParameters != null) accessorParameters.setParent(this);
    this.getMethod = getMethod;
    if (getMethod != null) getMethod.setParent(this);
    this.setMethod = setMethod;
    if (setMethod != null) setMethod.setParent(this);
    this.accessorUseThis = accessorUseThis;
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

  public VariableDeclaratorId getAccessorName() {
    return accessorName;
  }

  public void setAccessorName(VariableDeclaratorId accessorName) {
    this.accessorName = accessorName;
  }

  public ParameterList getAccessorParameters() {
    return accessorParameters;
  }

  public void setAccessorParameters(ParameterList accessorParameters) {
    this.accessorParameters = accessorParameters;
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

  public boolean getAccessorUseThis() {
    return accessorUseThis;
  }

  public void setAccessorUseThis(boolean accessorUseThis) {
    this.accessorUseThis = accessorUseThis;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (annotations != null) annotations.accept(visitor);
    if (fieldModifiers != null) fieldModifiers.accept(visitor);
    if (type != null) type.accept(visitor);
    if (accessorName != null) accessorName.accept(visitor);
    if (accessorParameters != null) accessorParameters.accept(visitor);
    if (getMethod != null) getMethod.accept(visitor);
    if (setMethod != null) setMethod.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (annotations != null) annotations.traverseTopDown(visitor);
    if (fieldModifiers != null) fieldModifiers.traverseTopDown(visitor);
    if (type != null) type.traverseTopDown(visitor);
    if (accessorName != null) accessorName.traverseTopDown(visitor);
    if (accessorParameters != null) accessorParameters.traverseTopDown(visitor);
    if (getMethod != null) getMethod.traverseTopDown(visitor);
    if (setMethod != null) setMethod.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (annotations != null) annotations.traverseBottomUp(visitor);
    if (fieldModifiers != null) fieldModifiers.traverseBottomUp(visitor);
    if (type != null) type.traverseBottomUp(visitor);
    if (accessorName != null) accessorName.traverseBottomUp(visitor);
    if (accessorParameters != null) accessorParameters.traverseBottomUp(visitor);
    if (getMethod != null) getMethod.traverseBottomUp(visitor);
    if (setMethod != null) setMethod.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("AccessorDeclaration(\n");
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
      if (accessorName != null)
        buffer.append(accessorName.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (accessorParameters != null)
        buffer.append(accessorParameters.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
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
    buffer.append("  "+tab+accessorUseThis);
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [AccessorDeclaration]");
    return buffer.toString();
  }
}
