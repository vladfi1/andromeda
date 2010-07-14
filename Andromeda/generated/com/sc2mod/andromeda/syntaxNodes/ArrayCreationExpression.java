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

public class ArrayCreationExpression extends Expression {

  private Type type;
  private ExpressionList definedDimensions;
  private int additionalDimensions;
  private ArrayInitializer arrayInitializer;

  public ArrayCreationExpression (Type type, ExpressionList definedDimensions, int additionalDimensions, ArrayInitializer arrayInitializer) {
    this.type = type;
    if (type != null) type.setParent(this);
    this.definedDimensions = definedDimensions;
    if (definedDimensions != null) definedDimensions.setParent(this);
    this.additionalDimensions = additionalDimensions;
    this.arrayInitializer = arrayInitializer;
    if (arrayInitializer != null) arrayInitializer.setParent(this);
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public ExpressionList getDefinedDimensions() {
    return definedDimensions;
  }

  public void setDefinedDimensions(ExpressionList definedDimensions) {
    this.definedDimensions = definedDimensions;
  }

  public int getAdditionalDimensions() {
    return additionalDimensions;
  }

  public void setAdditionalDimensions(int additionalDimensions) {
    this.additionalDimensions = additionalDimensions;
  }

  public ArrayInitializer getArrayInitializer() {
    return arrayInitializer;
  }

  public void setArrayInitializer(ArrayInitializer arrayInitializer) {
    this.arrayInitializer = arrayInitializer;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (type != null) type.accept(visitor);
    if (definedDimensions != null) definedDimensions.accept(visitor);
    if (arrayInitializer != null) arrayInitializer.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (type != null) type.traverseTopDown(visitor);
    if (definedDimensions != null) definedDimensions.traverseTopDown(visitor);
    if (arrayInitializer != null) arrayInitializer.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (type != null) type.traverseBottomUp(visitor);
    if (definedDimensions != null) definedDimensions.traverseBottomUp(visitor);
    if (arrayInitializer != null) arrayInitializer.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("ArrayCreationExpression(\n");
      if (type != null)
        buffer.append(type.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (definedDimensions != null)
        buffer.append(definedDimensions.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append("  "+tab+additionalDimensions);
    buffer.append("\n");
      if (arrayInitializer != null)
        buffer.append(arrayInitializer.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [ArrayCreationExpression]");
    return buffer.toString();
  }
}
