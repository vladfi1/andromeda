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

public abstract class ClassMemberDeclaration extends SyntaxNode {

  private SyntaxNode parent;

  public int getMemberType() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setMemberType(int memberType) {
    throw new ClassCastException("tried to call abstract method");
  }

  public MethodHeader getHeader() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setHeader(MethodHeader header) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Statement getBody() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setBody(Statement body) {
    throw new ClassCastException("tried to call abstract method");
  }

  public AnnotationList getAnnotations() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setAnnotations(AnnotationList annotations) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Modifiers getFieldModifiers() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setFieldModifiers(Modifiers fieldModifiers) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Type getType() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setType(Type type) {
    throw new ClassCastException("tried to call abstract method");
  }

  public VariableDeclarators getDeclaredVariables() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setDeclaredVariables(VariableDeclarators declaredVariables) {
    throw new ClassCastException("tried to call abstract method");
  }

  public VariableDeclaratorId getAccessorName() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setAccessorName(VariableDeclaratorId accessorName) {
    throw new ClassCastException("tried to call abstract method");
  }

  public ParameterList getAccessorParameters() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setAccessorParameters(ParameterList accessorParameters) {
    throw new ClassCastException("tried to call abstract method");
  }

  public MethodDeclaration getGetMethod() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setGetMethod(MethodDeclaration getMethod) {
    throw new ClassCastException("tried to call abstract method");
  }

  public MethodDeclaration getSetMethod() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setSetMethod(MethodDeclaration setMethod) {
    throw new ClassCastException("tried to call abstract method");
  }

  public boolean getAccessorUseThis() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setAccessorUseThis(boolean accessorUseThis) {
    throw new ClassCastException("tried to call abstract method");
  }

  public SyntaxNode getParent() {
    return parent;
  }

  public void setParent(SyntaxNode parent) {
    this.parent = parent;
  }

  public abstract void accept(Visitor visitor);
  public abstract void childrenAccept(Visitor visitor);
  public abstract void traverseTopDown(Visitor visitor);
  public abstract void traverseBottomUp(Visitor visitor);
  public String toString() {
    return toString("");
  }

  public abstract String toString(String tab);
}
