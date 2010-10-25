/*
 * Generated by classgen, version 1.5
 * 24.10.10 21:59
 */
package com.sc2mod.andromeda.syntaxNodes;

import com.sc2mod.andromeda.syntaxNodes.util.*;

public class ArrayDimensionsNode extends SyntaxNode {

  private SyntaxNode parent;
  private int numDimension;

  public ArrayDimensionsNode (int numDimension) {
    this.numDimension = numDimension;
  }

	public void accept(VoidVisitor visitor) { visitor.visit(this); }
	public <P,R> R accept(Visitor<P,R> visitor,P state) { return visitor.visit(this,state); }
	public <P> void accept(NoResultVisitor<P> visitor,P state) { visitor.visit(this,state); }
  public int getNumDimension() {
    return numDimension;
  }

  public void setNumDimension(int numDimension) {
    this.numDimension = numDimension;
  }

  public SyntaxNode getParent() {
    return parent;
  }

  public void setParent(SyntaxNode parent) {
    this.parent = parent;
  }

	public void childrenAccept(VoidVisitor visitor){
	}

	public <P,R> R childrenAccept(Visitor<P,R> visitor,P state){
		R result$ = null;
		return result$;
	}
	public <P> void childrenAccept(NoResultVisitor<P> visitor,P state){
	}
  public String toString() {
    return toString("");
  }

  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("ArrayDimensionsNode(\n");
    buffer.append("  "+tab+numDimension);
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [ArrayDimensionsNode]");
    return buffer.toString();
  }
}