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

public class IfThenElseStatement extends Statement {

  private Expression condition;
  private Statement thenStatement;
  private Statement elseStatement;

  public IfThenElseStatement (Expression condition, Statement thenStatement, Statement elseStatement) {
    this.condition = condition;
    if (condition != null) condition.setParent(this);
    this.thenStatement = thenStatement;
    if (thenStatement != null) thenStatement.setParent(this);
    this.elseStatement = elseStatement;
    if (elseStatement != null) elseStatement.setParent(this);
  }

  public Expression getCondition() {
    return condition;
  }

  public void setCondition(Expression condition) {
    this.condition = condition;
  }

  public Statement getThenStatement() {
    return thenStatement;
  }

  public void setThenStatement(Statement thenStatement) {
    this.thenStatement = thenStatement;
  }

  public Statement getElseStatement() {
    return elseStatement;
  }

  public void setElseStatement(Statement elseStatement) {
    this.elseStatement = elseStatement;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (condition != null) condition.accept(visitor);
    if (thenStatement != null) thenStatement.accept(visitor);
    if (elseStatement != null) elseStatement.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (condition != null) condition.traverseTopDown(visitor);
    if (thenStatement != null) thenStatement.traverseTopDown(visitor);
    if (elseStatement != null) elseStatement.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (condition != null) condition.traverseBottomUp(visitor);
    if (thenStatement != null) thenStatement.traverseBottomUp(visitor);
    if (elseStatement != null) elseStatement.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("IfThenElseStatement(\n");
      if (condition != null)
        buffer.append(condition.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (thenStatement != null)
        buffer.append(thenStatement.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (elseStatement != null)
        buffer.append(elseStatement.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [IfThenElseStatement]");
    return buffer.toString();
  }
}
