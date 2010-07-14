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

public class ExplicitConstructorInvocationStatement extends Statement {

  private Expression expression;
  private boolean useSuper;
  private ExpressionList arguments;

  public ExplicitConstructorInvocationStatement (Expression expression, boolean useSuper, ExpressionList arguments) {
    this.expression = expression;
    if (expression != null) expression.setParent(this);
    this.useSuper = useSuper;
    this.arguments = arguments;
    if (arguments != null) arguments.setParent(this);
  }

  public Expression getExpression() {
    return expression;
  }

  public void setExpression(Expression expression) {
    this.expression = expression;
  }

  public boolean getUseSuper() {
    return useSuper;
  }

  public void setUseSuper(boolean useSuper) {
    this.useSuper = useSuper;
  }

  public ExpressionList getArguments() {
    return arguments;
  }

  public void setArguments(ExpressionList arguments) {
    this.arguments = arguments;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (expression != null) expression.accept(visitor);
    if (arguments != null) arguments.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (expression != null) expression.traverseTopDown(visitor);
    if (arguments != null) arguments.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (expression != null) expression.traverseBottomUp(visitor);
    if (arguments != null) arguments.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("ExplicitConstructorInvocationStatement(\n");
      if (expression != null)
        buffer.append(expression.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append("  "+tab+useSuper);
    buffer.append("\n");
      if (arguments != null)
        buffer.append(arguments.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [ExplicitConstructorInvocationStatement]");
    return buffer.toString();
  }
}
