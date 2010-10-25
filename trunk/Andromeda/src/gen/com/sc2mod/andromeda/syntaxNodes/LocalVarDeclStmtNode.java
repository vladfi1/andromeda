/*
 * Generated by classgen, version 1.5
 * 24.10.10 21:59
 */
package com.sc2mod.andromeda.syntaxNodes;

import com.sc2mod.andromeda.syntaxNodes.util.*;

public class LocalVarDeclStmtNode extends StmtNode {

  private LocalVarDeclNode varDeclaration;

  public LocalVarDeclStmtNode (LocalVarDeclNode varDeclaration) {
    this.varDeclaration = varDeclaration;
    if (varDeclaration != null) varDeclaration.setParent(this);
  }

	public void accept(VoidVisitor visitor) { visitor.visit(this); }
	public <P,R> R accept(Visitor<P,R> visitor,P state) { return visitor.visit(this,state); }
	public <P> void accept(NoResultVisitor<P> visitor,P state) { visitor.visit(this,state); }
  public LocalVarDeclNode getVarDeclaration() {
    return varDeclaration;
  }

  public void setVarDeclaration(LocalVarDeclNode varDeclaration) {
    this.varDeclaration = varDeclaration;
  }

	public void childrenAccept(VoidVisitor visitor){
		if (varDeclaration != null) varDeclaration.accept(visitor);
	}

	public <P,R> R childrenAccept(Visitor<P,R> visitor,P state){
		R result$ = null;
		if (varDeclaration != null) result$ = visitor.reduce(result$,varDeclaration.accept(visitor,state));
		return result$;
	}
	public <P> void childrenAccept(NoResultVisitor<P> visitor,P state){
		if (varDeclaration != null) varDeclaration.accept(visitor,state);
	}
  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("LocalVarDeclStmtNode(\n");
      if (varDeclaration != null)
        buffer.append(varDeclaration.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [LocalVarDeclStmtNode]");
    return buffer.toString();
  }
}