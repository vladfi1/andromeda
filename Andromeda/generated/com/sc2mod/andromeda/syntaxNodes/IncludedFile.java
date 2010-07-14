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

public class IncludedFile extends GlobalStructure {

  private AndromedaFile includedContent;

  public IncludedFile (AndromedaFile includedContent) {
    this.includedContent = includedContent;
    if (includedContent != null) includedContent.setParent(this);
  }

  public AndromedaFile getIncludedContent() {
    return includedContent;
  }

  public void setIncludedContent(AndromedaFile includedContent) {
    this.includedContent = includedContent;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
    if (includedContent != null) includedContent.accept(visitor);
  }

  public void traverseTopDown(Visitor visitor) {
    accept(visitor);
    if (includedContent != null) includedContent.traverseTopDown(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    if (includedContent != null) includedContent.traverseBottomUp(visitor);
    accept(visitor);
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("IncludedFile(\n");
      if (includedContent != null)
        buffer.append(includedContent.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [IncludedFile]");
    return buffer.toString();
  }
}
