/*
 * Generated by classgen, version 1.5
 * 25.09.10 11:38
 */
package com.sc2mod.andromeda.syntaxNodes;

public class StructDeclaration extends GlobalStructure {

  private AnnotationList annotations;
  private Modifiers modifiers;
  private String name;
  private ClassBody body;

  public StructDeclaration (AnnotationList annotations, Modifiers modifiers, String name, ClassBody body) {
    this.annotations = annotations;
    if (annotations != null) annotations.setParent(this);
    this.modifiers = modifiers;
    if (modifiers != null) modifiers.setParent(this);
    this.name = name;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
    if (body != null) body.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (annotations != null) annotations.traverseTopDown(visitor);
    if (modifiers != null) modifiers.traverseTopDown(visitor);
    if (body != null) body.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (annotations != null) annotations.traverseBottomUp(visitor);
    if (modifiers != null) modifiers.traverseBottomUp(visitor);
    if (body != null) body.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("StructDeclaration(\n");
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
      if (body != null)
        buffer.append(body.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [StructDeclaration]");
    return buffer.toString();
  }
}