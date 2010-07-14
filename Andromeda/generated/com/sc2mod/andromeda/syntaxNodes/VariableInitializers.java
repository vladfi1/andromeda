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

import java.util.Vector;
import java.util.Enumeration;

public class VariableInitializers extends SyntaxNode {

  private Vector items;
  private SyntaxNode parent;

  public VariableInitializers() {
    items = new Vector();
  }

  public VariableInitializers(Object anItem) {
    this();
    append(anItem);
  }

  public VariableInitializers append(Object anItem) {
    if (anItem == null) return this;
    items.addElement(anItem);
    return this;
  }

  public Enumeration elements() {
    return items.elements();
  }

  public Object elementAt(int index) {
    return (Object) items.elementAt(index);
  }

  public void setElementAt(Object item, int index) {
    items.setElementAt(item, index);
  }

  public void insertElementAt(Object item, int index) {
    items.insertElementAt(item, index);
  }

  public void removeElementAt(int index) {
    items.removeElementAt(index);
  }

  public int size() { return items.size(); }

  public boolean isEmpty() { return items.isEmpty(); }

  public boolean contains(Object item) {
    int size = items.size();
    for (int i = 0; i < size; i++)
      if ( item.equals(items.elementAt(i)) ) return true;
    return false;
  }

  public int indexOf(Object item) {
    return items.indexOf(item);
  }

  public String toString() {
    return toString("");
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("VariableInitializers (\n");
    int size = items.size();
    for (int i = 0; i < size; i++) {
      buffer.append("  "+tab+items.elementAt(i));
      buffer.append("\n");
    }
    buffer.append(tab);
    buffer.append(") [VariableInitializers]");
    return buffer.toString();
  }

  public SyntaxNode getParent() {
    return parent;
  }

  public void setParent(SyntaxNode parent) {
    this.parent = parent;
  }

  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public void childrenAccept(Visitor visitor) {
  }

  public void traverseTopDown(Visitor visitor) {
    this.accept(visitor);
  }

  public void traverseBottomUp(Visitor visitor) {
    this.accept(visitor);
  }

}
