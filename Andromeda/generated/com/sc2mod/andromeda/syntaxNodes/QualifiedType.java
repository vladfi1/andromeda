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

public class QualifiedType extends Type {

  private int category;
  private FieldAccess qualifiedName;
  private TypeList typeArguments;

  public QualifiedType (int category, FieldAccess qualifiedName, TypeList typeArguments) {
    this.category = category;
    this.qualifiedName = qualifiedName;
    if (qualifiedName != null) qualifiedName.setParent(this);
    this.typeArguments = typeArguments;
    if (typeArguments != null) typeArguments.setParent(this);
  }

  public int getCategory() {
    return category;
  }

  public void setCategory(int category) {
    this.category = category;
  }

  public FieldAccess getQualifiedName() {
    return qualifiedName;
  }

  public void setQualifiedName(FieldAccess qualifiedName) {
    this.qualifiedName = qualifiedName;
  }

  public TypeList getTypeArguments() {
    return typeArguments;
  }

  public void setTypeArguments(TypeList typeArguments) {
    this.typeArguments = typeArguments;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (qualifiedName != null) qualifiedName.accept(visitor);
    if (typeArguments != null) typeArguments.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (qualifiedName != null) qualifiedName.traverseTopDown(visitor);
    if (typeArguments != null) typeArguments.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (qualifiedName != null) qualifiedName.traverseBottomUp(visitor);
    if (typeArguments != null) typeArguments.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("QualifiedType(\n");
    buffer.append("  "+tab+category);
    buffer.append("\n");
      if (qualifiedName != null)
        buffer.append(qualifiedName.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (typeArguments != null)
        buffer.append(typeArguments.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [QualifiedType]");
    return buffer.toString();
  }
}
