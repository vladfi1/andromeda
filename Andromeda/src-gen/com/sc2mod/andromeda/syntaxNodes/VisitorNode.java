/*
 * Generated by classgen, version 1.5
 * 25.09.10 11:38
 */

package com.sc2mod.andromeda.syntaxNodes;

public interface VisitorNode {
  public void accept(Visitor visitor);

  public void childrenAccept(Visitor visitor);
  public void traverseBottomUp(Visitor visitor);
  public void traverseTopDown(Visitor visitor);
}