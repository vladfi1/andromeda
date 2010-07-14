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

public class ForStatement extends Statement {

  private Statement forInit;
  private Expression condition;
  private BlockStatement forUpdate;
  private Statement thenStatement;

  public ForStatement (Statement forInit, Expression condition, BlockStatement forUpdate, Statement thenStatement) {
    this.forInit = forInit;
    if (forInit != null) forInit.setParent(this);
    this.condition = condition;
    if (condition != null) condition.setParent(this);
    this.forUpdate = forUpdate;
    if (forUpdate != null) forUpdate.setParent(this);
    this.thenStatement = thenStatement;
    if (thenStatement != null) thenStatement.setParent(this);
  }

  public Statement getForInit() {
    return forInit;
  }

  public void setForInit(Statement forInit) {
    this.forInit = forInit;
  }

  public Expression getCondition() {
    return condition;
  }

  public void setCondition(Expression condition) {
    this.condition = condition;
  }

  public BlockStatement getForUpdate() {
    return forUpdate;
  }

  public void setForUpdate(BlockStatement forUpdate) {
    this.forUpdate = forUpdate;
  }

  public Statement getThenStatement() {
    return thenStatement;
  }

  public void setThenStatement(Statement thenStatement) {
    this.thenStatement = thenStatement;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (forInit != null) forInit.accept(visitor);
    if (condition != null) condition.accept(visitor);
    if (forUpdate != null) forUpdate.accept(visitor);
    if (thenStatement != null) thenStatement.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (forInit != null) forInit.traverseTopDown(visitor);
    if (condition != null) condition.traverseTopDown(visitor);
    if (forUpdate != null) forUpdate.traverseTopDown(visitor);
    if (thenStatement != null) thenStatement.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (forInit != null) forInit.traverseBottomUp(visitor);
    if (condition != null) condition.traverseBottomUp(visitor);
    if (forUpdate != null) forUpdate.traverseBottomUp(visitor);
    if (thenStatement != null) thenStatement.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("ForStatement(\n");
      if (forInit != null)
        buffer.append(forInit.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (condition != null)
        buffer.append(condition.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (forUpdate != null)
        buffer.append(forUpdate.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (thenStatement != null)
        buffer.append(thenStatement.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [ForStatement]");
    return buffer.toString();
  }
}
