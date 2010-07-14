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

public class InstanceLimitSetter extends GlobalStructure {

  private Type enrichedType;
  private Expression instanceLimit;

  public InstanceLimitSetter (Type enrichedType, Expression instanceLimit) {
    this.enrichedType = enrichedType;
    if (enrichedType != null) enrichedType.setParent(this);
    this.instanceLimit = instanceLimit;
    if (instanceLimit != null) instanceLimit.setParent(this);
  }

  public Type getEnrichedType() {
    return enrichedType;
  }

  public void setEnrichedType(Type enrichedType) {
    this.enrichedType = enrichedType;
  }

  public Expression getInstanceLimit() {
    return instanceLimit;
  }

  public void setInstanceLimit(Expression instanceLimit) {
    this.instanceLimit = instanceLimit;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (enrichedType != null) enrichedType.accept(visitor);
    if (instanceLimit != null) instanceLimit.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (enrichedType != null) enrichedType.traverseTopDown(visitor);
    if (instanceLimit != null) instanceLimit.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (enrichedType != null) enrichedType.traverseBottomUp(visitor);
    if (instanceLimit != null) instanceLimit.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("InstanceLimitSetter(\n");
      if (enrichedType != null)
        buffer.append(enrichedType.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (instanceLimit != null)
        buffer.append(instanceLimit.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [InstanceLimitSetter]");
    return buffer.toString();
  }
}