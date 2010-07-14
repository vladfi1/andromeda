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

public abstract class Expression extends SyntaxNode {

  private com.sc2mod.andromeda.environment.types.Type inferedType;
  private boolean constant;
  private boolean simple;
  private boolean lValue;
  private com.sc2mod.andromeda.vm.data.DataObject value;
  private SyntaxNode parent;

  public int getAssignmentType() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setAssignmentType(int assignmentType) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Expression getLeftExpression() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setLeftExpression(Expression leftExpression) {
    throw new ClassCastException("tried to call abstract method");
  }

  public int getOperator() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setOperator(int operator) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Expression getRightExpression() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setRightExpression(Expression rightExpression) {
    throw new ClassCastException("tried to call abstract method");
  }

  public int getAccessType() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setAccessType(int accessType) {
    throw new ClassCastException("tried to call abstract method");
  }

  public String getName() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setName(String name) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Expression getRightExpression2() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setRightExpression2(Expression rightExpression2) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Expression getExpression() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setExpression(Expression expression) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Type getType() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setType(Type type) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Expression getSuperClassName() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setSuperClassName(Expression superClassName) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Expression getThisClassName() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setThisClassName(Expression thisClassName) {
    throw new ClassCastException("tried to call abstract method");
  }

  public int getInvocationType() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setInvocationType(int invocationType) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Expression getPrefix() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setPrefix(Expression prefix) {
    throw new ClassCastException("tried to call abstract method");
  }

  public String getFuncName() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setFuncName(String funcName) {
    throw new ClassCastException("tried to call abstract method");
  }

  public ExpressionList getArguments() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setArguments(ExpressionList arguments) {
    throw new ClassCastException("tried to call abstract method");
  }

  public boolean getInline() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setInline(boolean inline) {
    throw new ClassCastException("tried to call abstract method");
  }

  public ExpressionList getDefinedDimensions() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setDefinedDimensions(ExpressionList definedDimensions) {
    throw new ClassCastException("tried to call abstract method");
  }

  public int getAdditionalDimensions() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setAdditionalDimensions(int additionalDimensions) {
    throw new ClassCastException("tried to call abstract method");
  }

  public ArrayInitializer getArrayInitializer() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setArrayInitializer(ArrayInitializer arrayInitializer) {
    throw new ClassCastException("tried to call abstract method");
  }

  public ClassBody getClassBody() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setClassBody(ClassBody classBody) {
    throw new ClassCastException("tried to call abstract method");
  }

  public Literal getLiteral() {
    throw new ClassCastException("tried to call abstract method");
  }

  public void setLiteral(Literal literal) {
    throw new ClassCastException("tried to call abstract method");
  }

  public com.sc2mod.andromeda.environment.types.Type getInferedType() {
    return inferedType;
  }

  public void setInferedType(com.sc2mod.andromeda.environment.types.Type inferedType) {
    this.inferedType = inferedType;
  }

  public boolean getConstant() {
    return constant;
  }

  public void setConstant(boolean constant) {
    this.constant = constant;
  }

  public boolean getSimple() {
    return simple;
  }

  public void setSimple(boolean simple) {
    this.simple = simple;
  }

  public boolean getLValue() {
    return lValue;
  }

  public void setLValue(boolean lValue) {
    this.lValue = lValue;
  }

  public com.sc2mod.andromeda.vm.data.DataObject getValue() {
    return value;
  }

  public void setValue(com.sc2mod.andromeda.vm.data.DataObject value) {
    this.value = value;
  }

  public SyntaxNode getParent() {
    return parent;
  }

  public void setParent(SyntaxNode parent) {
    this.parent = parent;
  }

  public abstract void accept(Visitor visitor);
  public abstract void childrenAccept(Visitor visitor);
  public abstract void traverseTopDown(Visitor visitor);
  public abstract void traverseBottomUp(Visitor visitor);
  public String toString() {
    return toString("");
  }

  public abstract String toString(String tab);
}
