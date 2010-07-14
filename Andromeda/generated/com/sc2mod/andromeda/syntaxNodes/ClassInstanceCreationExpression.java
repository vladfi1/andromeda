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

public class ClassInstanceCreationExpression extends Expression {

  private Type type;
  private ExpressionList arguments;
  private ClassBody classBody;

  public ClassInstanceCreationExpression (Type type, ExpressionList arguments, ClassBody classBody) {
    this.type = type;
    if (type != null) type.setParent(this);
    this.arguments = arguments;
    if (arguments != null) arguments.setParent(this);
    this.classBody = classBody;
    if (classBody != null) classBody.setParent(this);
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public ExpressionList getArguments() {
    return arguments;
  }

  public void setArguments(ExpressionList arguments) {
    this.arguments = arguments;
  }

  public ClassBody getClassBody() {
    return classBody;
  }

  public void setClassBody(ClassBody classBody) {
    this.classBody = classBody;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (type != null) type.accept(visitor);
    if (arguments != null) arguments.accept(visitor);
    if (classBody != null) classBody.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (type != null) type.traverseTopDown(visitor);
    if (arguments != null) arguments.traverseTopDown(visitor);
    if (classBody != null) classBody.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (type != null) type.traverseBottomUp(visitor);
    if (arguments != null) arguments.traverseBottomUp(visitor);
    if (classBody != null) classBody.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("ClassInstanceCreationExpression(\n");
      if (type != null)
        buffer.append(type.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (arguments != null)
        buffer.append(arguments.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (classBody != null)
        buffer.append(classBody.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [ClassInstanceCreationExpression]");
    return buffer.toString();
  }
}
