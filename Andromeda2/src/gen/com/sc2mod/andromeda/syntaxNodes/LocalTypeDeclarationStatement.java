/*
 * Generated by classgen, version 1.5
 * 25.09.10 11:38
 */
package com.sc2mod.andromeda.syntaxNodes;

public class LocalTypeDeclarationStatement extends Statement {

  private ClassDeclaration classDeclaration;

  public LocalTypeDeclarationStatement (ClassDeclaration classDeclaration) {
    this.classDeclaration = classDeclaration;
    if (classDeclaration != null) classDeclaration.setParent(this);
  }

  public ClassDeclaration getClassDeclaration() {
    return classDeclaration;
  }

  public void setClassDeclaration(ClassDeclaration classDeclaration) {
    this.classDeclaration = classDeclaration;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (classDeclaration != null) classDeclaration.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (classDeclaration != null) classDeclaration.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (classDeclaration != null) classDeclaration.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("LocalTypeDeclarationStatement(\n");
      if (classDeclaration != null)
        buffer.append(classDeclaration.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [LocalTypeDeclarationStatement]");
    return buffer.toString();
  }
}