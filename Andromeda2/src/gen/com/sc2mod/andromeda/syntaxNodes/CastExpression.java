/*
 * Generated by classgen, version 1.5
 * 25.09.10 11:38
 */
package com.sc2mod.andromeda.syntaxNodes;

public class CastExpression extends Expression {

  private Type type;
  private Expression rightExpression;

  public CastExpression (Type type, Expression rightExpression) {
    this.type = type;
    if (type != null) type.setParent(this);
    this.rightExpression = rightExpression;
    if (rightExpression != null) rightExpression.setParent(this);
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Expression getRightExpression() {
    return rightExpression;
  }

  public void setRightExpression(Expression rightExpression) {
    this.rightExpression = rightExpression;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (type != null) type.accept(visitor);
    if (rightExpression != null) rightExpression.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (type != null) type.traverseTopDown(visitor);
    if (rightExpression != null) rightExpression.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (type != null) type.traverseBottomUp(visitor);
    if (rightExpression != null) rightExpression.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("CastExpression(\n");
      if (type != null)
        buffer.append(type.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (rightExpression != null)
        buffer.append(rightExpression.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [CastExpression]");
    return buffer.toString();
  }
}