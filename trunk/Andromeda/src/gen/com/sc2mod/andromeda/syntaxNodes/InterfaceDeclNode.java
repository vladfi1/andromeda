/*
 * Generated by classgen, version 1.5
 * 24.10.10 21:59
 */
package com.sc2mod.andromeda.syntaxNodes;

import com.sc2mod.andromeda.syntaxNodes.util.*;

import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.types.Interface;

public class InterfaceDeclNode extends GlobalStructureNode {

  private AnnotationListNode annotations;
  private ModifierListNode modifiers;
  private String name;
  private TypeListNode interfaces;
  private MemberDeclListNode body;

  public InterfaceDeclNode (AnnotationListNode annotations, ModifierListNode modifiers, String name, TypeListNode interfaces, MemberDeclListNode body) {
    this.annotations = annotations;
    if (annotations != null) annotations.setParent(this);
    this.modifiers = modifiers;
    if (modifiers != null) modifiers.setParent(this);
    this.name = name;
    this.interfaces = interfaces;
    if (interfaces != null) interfaces.setParent(this);
    this.body = body;
    if (body != null) body.setParent(this);
  }

	public void accept(VoidVisitor visitor) { visitor.visit(this); }
	public <P,R> R accept(Visitor<P,R> visitor,P state) { return visitor.visit(this,state); }
	public <P> void accept(NoResultVisitor<P> visitor,P state) { visitor.visit(this,state); }
  public AnnotationListNode getAnnotations() {
    return annotations;
  }

  public void setAnnotations(AnnotationListNode annotations) {
    this.annotations = annotations;
  }

  public ModifierListNode getModifiers() {
    return modifiers;
  }

  public void setModifiers(ModifierListNode modifiers) {
    this.modifiers = modifiers;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TypeListNode getInterfaces() {
    return interfaces;
  }

  public void setInterfaces(TypeListNode interfaces) {
    this.interfaces = interfaces;
  }

  public MemberDeclListNode getBody() {
    return body;
  }

  public void setBody(MemberDeclListNode body) {
    this.body = body;
  }

  private Interface semantics;
  @Override
  public void setSemantics(SemanticsElement semantics){
  	if(!(semantics instanceof Interface)) throw new InternalProgramError(this,"Trying to assign semantics of type "
  				+ semantics.getClass().getSimpleName() + " to node " + this.getClass().getSimpleName());
  	this.semantics = (Interface)semantics;
  }
  @Override
  public Interface getSemantics(){
  	return semantics;
  }

	public void childrenAccept(VoidVisitor visitor){
		if (annotations != null) annotations.accept(visitor);
		if (modifiers != null) modifiers.accept(visitor);
		if (interfaces != null) interfaces.accept(visitor);
		if (body != null) body.accept(visitor);
	}

	public <P,R> R childrenAccept(Visitor<P,R> visitor,P state){
		R result$ = null;
		if (annotations != null) result$ = visitor.reduce(result$,annotations.accept(visitor,state));
		if (modifiers != null) result$ = visitor.reduce(result$,modifiers.accept(visitor,state));
		if (interfaces != null) result$ = visitor.reduce(result$,interfaces.accept(visitor,state));
		if (body != null) result$ = visitor.reduce(result$,body.accept(visitor,state));
		return result$;
	}
	public <P> void childrenAccept(NoResultVisitor<P> visitor,P state){
		if (annotations != null) annotations.accept(visitor,state);
		if (modifiers != null) modifiers.accept(visitor,state);
		if (interfaces != null) interfaces.accept(visitor,state);
		if (body != null) body.accept(visitor,state);
	}
  public String toString(String tab) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(tab);
    buffer.append("InterfaceDeclNode(\n");
      if (annotations != null)
        buffer.append(annotations.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (modifiers != null)
        buffer.append(modifiers.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append("  "+tab+name);
    buffer.append("\n");
      if (interfaces != null)
        buffer.append(interfaces.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
      if (body != null)
        buffer.append(body.toString("  "+tab));
      else
        buffer.append(tab+"  null");
    buffer.append("\n");
    buffer.append(tab);
    buffer.append(") [InterfaceDeclNode]");
    return buffer.toString();
  }
}