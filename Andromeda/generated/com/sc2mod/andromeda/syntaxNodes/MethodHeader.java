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

public class MethodHeader extends SyntaxNode {

  private SyntaxNode parent;
  private AnnotationList annotations;
  private Modifiers modifiers;
  private Type returnType;
  private String name;
  private ParameterList parameters;
  private Object throwDeclaration;

  public MethodHeader (AnnotationList annotations, Modifiers modifiers, Type returnType, String name, ParameterList parameters, Object throwDeclaration) {
    this.annotations = annotations;
    if (annotations != null) annotations.setParent(this);
    this.modifiers = modifiers;
    if (modifiers != null) modifiers.setParent(this);
    this.returnType = returnType;
    if (returnType != null) returnType.setParent(this);
    this.name = name;
    this.parameters = parameters;
    if (parameters != null) parameters.setParent(this);
    this.throwDeclaration = throwDeclaration;
  }

  public AnnotationList getAnnotations() {
    return annotations;
  }

  public void setAnnotations(AnnotationList annotations) {
    this.annotations = annotations;
  }

  public Modifiers getModifiers() {
    return modifiers;
  }

  public void setModifiers(Modifiers modifiers) {
    this.modifiers = modifiers;
  }

  public Type getReturnType() {
    return returnType;
  }

  public void setReturnType(Type returnType) {
    this.returnType = returnType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ParameterList getParameters() {
    return parameters;
  }

  public void setParameters(ParameterList parameters) {
    this.parameters = parameters;
  }

  public Object getThrowDeclaration() {
    return throwDeclaration;
  }

  public void setThrowDeclaration(Object throwDeclaration) {
    this.throwDeclaration = throwDeclaration;
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
    if (annotations != null) annotations.accept(visitor);
    if (modifiers != null) modifiers.accept(visitor);
    if (returnType != null) returnType.accept(visitor);
    if (parameters != null) parameters.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (annotations != null) annotations.traverseTopDown(visitor);
    if (modifiers != null) modifiers.traverseTopDown(visitor);
    if (returnType != null) returnType.traverseTopDown(visitor);
    if (parameters != null) parameters.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (annotations != null) annotations.traverseBottomUp(visitor);
    if (modifiers != null) modifiers.traverseBottomUp(visitor);
    if (returnType != null) returnType.traverseBottomUp(visitor);
    if (parameters != null) parameters.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString() {
    return toString("");
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("MethodHeader(\n");
      if (annotations != null)
        buffer.append(annotations.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (modifiers != null)
        buffer.append(modifiers.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (returnType != null)
        buffer.append(returnType.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append("  "+tab+name);
    buffer.append("\n");
      if (parameters != null)
        buffer.append(parameters.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append("  "+tab+throwDeclaration);
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [MethodHeader]");
    return buffer.toString();
  }
}