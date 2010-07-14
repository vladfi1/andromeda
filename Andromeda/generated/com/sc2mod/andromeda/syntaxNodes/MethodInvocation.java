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

public class MethodInvocation extends Expression {

  private int invocationType;
  private Expression prefix;
  private String funcName;
  private ExpressionList arguments;
  private boolean inline;

  public MethodInvocation (int invocationType, Expression prefix, String funcName, ExpressionList arguments, boolean inline) {
    this.invocationType = invocationType;
    this.prefix = prefix;
    if (prefix != null) prefix.setParent(this);
    this.funcName = funcName;
    this.arguments = arguments;
    if (arguments != null) arguments.setParent(this);
    this.inline = inline;
  }

  public int getInvocationType() {
    return invocationType;
  }

  public void setInvocationType(int invocationType) {
    this.invocationType = invocationType;
  }

  public Expression getPrefix() {
    return prefix;
  }

  public void setPrefix(Expression prefix) {
    this.prefix = prefix;
  }

  public String getFuncName() {
    return funcName;
  }

  public void setFuncName(String funcName) {
    this.funcName = funcName;
  }

  public ExpressionList getArguments() {
    return arguments;
  }

  public void setArguments(ExpressionList arguments) {
    this.arguments = arguments;
  }

  public boolean getInline() {
    return inline;
  }

  public void setInline(boolean inline) {
    this.inline = inline;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (prefix != null) prefix.accept(visitor);
    if (arguments != null) arguments.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (prefix != null) prefix.traverseTopDown(visitor);
    if (arguments != null) arguments.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (prefix != null) prefix.traverseBottomUp(visitor);
    if (arguments != null) arguments.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("MethodInvocation(\n");
    buffer.append("  "+tab+invocationType);
    buffer.append("\n");
      if (prefix != null)
        buffer.append(prefix.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append("  "+tab+funcName);
    buffer.append("\n");
      if (arguments != null)
        buffer.append(arguments.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append("  "+tab+inline);
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [MethodInvocation]");
    return buffer.toString();
  }
}
