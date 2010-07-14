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

public abstract class GlobalStructure extends SyntaxNode {

  private SyntaxNode parent;

  public AnnotationList getAnnotations() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setAnnotations(AnnotationList annotations) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Modifiers getModifiers() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setModifiers(Modifiers modifiers) {
    throw new ClassCastException("tried to call abstract method");
  }

  public String getName() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setName(String name) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Expression getInstanceLimit() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setInstanceLimit(Expression instanceLimit) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Type getSuperClass() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setSuperClass(Type superClass) {
    throw new ClassCastException("tried to call abstract method");
  }

  public TypeList getInterfaces() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setInterfaces(TypeList interfaces) {
    throw new ClassCastException("tried to call abstract method");
  }

  public ClassBody getBody() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setBody(ClassBody body) {
    throw new ClassCastException("tried to call abstract method");
  }

  public TypeParamList getTypeParams() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setTypeParams(TypeParamList typeParams) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Type getEnrichedType() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setEnrichedType(Type enrichedType) {
    throw new ClassCastException("tried to call abstract method");
  }

  public MethodDeclaration getFuncDecl() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setFuncDecl(MethodDeclaration funcDecl) {
    throw new ClassCastException("tried to call abstract method");
  }

  public FieldDeclaration getFieldDecl() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setFieldDecl(FieldDeclaration fieldDecl) {
    throw new ClassCastException("tried to call abstract method");
  }

  public StaticInitDeclaration getInitDecl() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setInitDecl(StaticInitDeclaration initDecl) {
    throw new ClassCastException("tried to call abstract method");
  }

  public AndromedaFile getIncludedContent() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setIncludedContent(AndromedaFile includedContent) {
    throw new ClassCastException("tried to call abstract method");
  }

  public boolean getIsKey() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setIsKey(boolean isKey) {
    throw new ClassCastException("tried to call abstract method");
  }

  public boolean getDisjoint() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setDisjoint(boolean disjoint) {
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
