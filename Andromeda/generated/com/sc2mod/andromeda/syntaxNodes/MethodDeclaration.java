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

public class MethodDeclaration extends ClassMemberDeclaration {

  private int memberType;
  private MethodHeader header;
  private Statement body;

  public MethodDeclaration (int memberType, MethodHeader header, Statement body) {
    this.memberType = memberType;
    this.header = header;
    if (header != null) header.setParent(this);
    this.body = body;
    if (body != null) body.setParent(this);
  }

  public int getMemberType() {
    return memberType;
  }

  public void setMemberType(int memberType) {
    this.memberType = memberType;
  }

  public MethodHeader getHeader() {
    return header;
  }

  public void setHeader(MethodHeader header) {
    this.header = header;
  }

  public Statement getBody() {
    return body;
  }

  public void setBody(Statement body) {
    this.body = body;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (header != null) header.accept(visitor);
    if (body != null) body.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (header != null) header.traverseTopDown(visitor);
    if (body != null) body.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (header != null) header.traverseBottomUp(visitor);
    if (body != null) body.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("MethodDeclaration(\n");
    buffer.append("  "+tab+memberType);
    buffer.append("\n");
      if (header != null)
        buffer.append(header.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (body != null)
        buffer.append(body.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [MethodDeclaration]");
    return buffer.toString();
  }
}
