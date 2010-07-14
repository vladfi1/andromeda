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

public class ClassDeclaration extends GlobalStructure {

  private AnnotationList annotations;
  private Modifiers modifiers;
  private String name;
  private Expression instanceLimit;
  private Type superClass;
  private TypeList interfaces;
  private ClassBody body;
  private TypeParamList typeParams;

  public ClassDeclaration (AnnotationList annotations, Modifiers modifiers, String name, Expression instanceLimit, Type superClass, TypeList interfaces, ClassBody body, TypeParamList typeParams) {
    this.annotations = annotations;
    if (annotations != null) annotations.setParent(this);
    this.modifiers = modifiers;
    if (modifiers != null) modifiers.setParent(this);
    this.name = name;
    this.instanceLimit = instanceLimit;
    if (instanceLimit != null) instanceLimit.setParent(this);
    this.superClass = superClass;
    if (superClass != null) superClass.setParent(this);
    this.interfaces = interfaces;
    if (interfaces != null) interfaces.setParent(this);
    this.body = body;
    if (body != null) body.setParent(this);
    this.typeParams = typeParams;
    if (typeParams != null) typeParams.setParent(this);
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Expression getInstanceLimit() {
    return instanceLimit;
  }

  public void setInstanceLimit(Expression instanceLimit) {
    this.instanceLimit = instanceLimit;
  }

  public Type getSuperClass() {
    return superClass;
  }

  public void setSuperClass(Type superClass) {
    this.superClass = superClass;
  }

  public TypeList getInterfaces() {
    return interfaces;
  }

  public void setInterfaces(TypeList interfaces) {
    this.interfaces = interfaces;
  }

  public ClassBody getBody() {
    return body;
  }

  public void setBody(ClassBody body) {
    this.body = body;
  }

  public TypeParamList getTypeParams() {
    return typeParams;
  }

  public void setTypeParams(TypeParamList typeParams) {
    this.typeParams = typeParams;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (annotations != null) annotations.accept(visitor);
    if (modifiers != null) modifiers.accept(visitor);
    if (instanceLimit != null) instanceLimit.accept(visitor);
    if (superClass != null) superClass.accept(visitor);
    if (interfaces != null) interfaces.accept(visitor);
    if (body != null) body.accept(visitor);
    if (typeParams != null) typeParams.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (annotations != null) annotations.traverseTopDown(visitor);
    if (modifiers != null) modifiers.traverseTopDown(visitor);
    if (instanceLimit != null) instanceLimit.traverseTopDown(visitor);
    if (superClass != null) superClass.traverseTopDown(visitor);
    if (interfaces != null) interfaces.traverseTopDown(visitor);
    if (body != null) body.traverseTopDown(visitor);
    if (typeParams != null) typeParams.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (annotations != null) annotations.traverseBottomUp(visitor);
    if (modifiers != null) modifiers.traverseBottomUp(visitor);
    if (instanceLimit != null) instanceLimit.traverseBottomUp(visitor);
    if (superClass != null) superClass.traverseBottomUp(visitor);
    if (interfaces != null) interfaces.traverseBottomUp(visitor);
    if (body != null) body.traverseBottomUp(visitor);
    if (typeParams != null) typeParams.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("ClassDeclaration(\n");
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
    buffer.append("  "+tab+name);
    buffer.append("\n");
      if (instanceLimit != null)
        buffer.append(instanceLimit.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (superClass != null)
        buffer.append(superClass.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (interfaces != null)
        buffer.append(interfaces.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (body != null)
        buffer.append(body.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (typeParams != null)
        buffer.append(typeParams.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [ClassDeclaration]");
    return buffer.toString();
  }
}
