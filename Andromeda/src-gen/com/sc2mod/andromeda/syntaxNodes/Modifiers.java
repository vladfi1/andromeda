/*
 * Generated by classgen, version 1.5
 * 25.09.10 11:38
 */
package com.sc2mod.andromeda.syntaxNodes;

import java.util.Vector;
import java.util.Enumeration;

public class Modifiers extends SyntaxNode {

  private Vector items;
  private SyntaxNode parent;

  public Modifiers() {
    items = new Vector();
  }

  public Modifiers(Integer anItem) {
    this();
    append(anItem);
  }

  public Modifiers append(Integer anItem) {
    if (anItem == null) return this;
    items.addElement(anItem);
    return this;
  }

  public Enumeration elements() {
    return items.elements();
  }

  public Integer elementAt(int index) {
    return (Integer) items.elementAt(index);
  }

  public void setElementAt(Integer item, int index) {
    items.setElementAt(item, index);
  }

  public void insertElementAt(Integer item, int index) {
    items.insertElementAt(item, index);
  }

  public void removeElementAt(int index) {
    items.removeElementAt(index);
  }

  public int size() { return items.size(); }

  public boolean isEmpty() { return items.isEmpty(); }

  public boolean contains(Integer item) {
    int size = items.size();
    for (int i = 0; i < size; i++)
      if ( item.equals(items.elementAt(i)) ) return true;
    return false;
  }

  public int indexOf(Integer item) {
    return items.indexOf(item);
  }

  public String toString() {
    return toString("");
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("Modifiers (\n");
    int size = items.size();
    for (int i = 0; i < size; i++) {
      buffer.append("  "+tab+items.elementAt(i));
      buffer.append("\n");
    }
    buffer.append(tab);
    buffer.append(") [Modifiers]");
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
