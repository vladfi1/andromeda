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

public class EnrichDeclaration extends GlobalStructure {

  private AnnotationList annotations;
  private Modifiers modifiers;
  private Type enrichedType;
  private ClassBody body;

  public EnrichDeclaration (AnnotationList annotations, Modifiers modifiers, Type enrichedType, ClassBody body) {
    this.annotations = annotations;
    if (annotations != null) annotations.setParent(this);
    this.modifiers = modifiers;
    if (modifiers != null) modifiers.setParent(this);
    this.enrichedType = enrichedType;
    if (enrichedType != null) enrichedType.setParent(this);
    this.body = body;
    if (body != null) body.setParent(this);
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

  public Type getEnrichedType() {
    return enrichedType;
  }

  public void setEnrichedType(Type enrichedType) {
    this.enrichedType = enrichedType;
  }

  public ClassBody getBody() {
    return body;
  }

  public void setBody(ClassBody body) {
    this.body = body;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (annotations != null) annotations.accept(visitor);
    if (modifiers != null) modifiers.accept(visitor);
    if (enrichedType != null) enrichedType.accept(visitor);
    if (body != null) body.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (annotations != null) annotations.traverseTopDown(visitor);
    if (modifiers != null) modifiers.traverseTopDown(visitor);
    if (enrichedType != null) enrichedType.traverseTopDown(visitor);
    if (body != null) body.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (annotations != null) annotations.traverseBottomUp(visitor);
    if (modifiers != null) modifiers.traverseBottomUp(visitor);
    if (enrichedType != null) enrichedType.traverseBottomUp(visitor);
    if (body != null) body.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("EnrichDeclaration(\n");
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
      if (enrichedType != null)
        buffer.append(enrichedType.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (body != null)
        buffer.append(body.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [EnrichDeclaration]");
    return buffer.toString();
  }
}