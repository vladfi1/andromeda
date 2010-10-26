/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.syntaxNodes.AnnotationNode;
import com.sc2mod.andromeda.syntaxNodes.ReturnStmtNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

//XPilot: added signature to GenericFunctionProxy
import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;
import com.sc2mod.andromeda.environment.variables.ImplicitParamDecl;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class GenericFunctionProxy extends Operation {

	private Operation function;
	private Signature signature;
	private Type returnType;
	
	public GenericFunctionProxy(Operation vd, Signature signature, Type returnType) {
		function = vd;
		this.signature = signature;
		this.returnType = returnType;
	}
	
	@Override
	public Type getReturnType() {
		return returnType;
	}
	
	@Override
	public void addImplicitLocals(ArrayList<LocalVarDecl> vars) {
		function.addImplicitLocals(vars);
	}

	@Override
	public void addInline() {
		function.addInline();
	}

	@Override
	public void addInvocation() {
		function.addInvocation();
	}

	@Override
	public void addReturnStmt(ReturnStmtNode r) {
		function.addReturnStmt(r);
	}

	@Override
	public boolean flowReachesEnd() {
		return function.flowReachesEnd();
	}

	@Override
	public Type getContainingType() {
		return function.getContainingType();
	}

	@Override
	public String getDescription() {
		return function.getDescription();
	}

	@Override
	public OperationType getOperationType() {
		return function.getOperationType();
	}

	@Override
	public String getGeneratedName() {
		return function.getGeneratedName();
	}


	@Override
	public int getInvocationCount() {
		return function.getInvocationCount();
	}

	@Override
	public LocalVarDecl[] getLocals() {
		return function.getLocals();
	}

	@Override
	public String getName() {
		return function.getName();
	}

	@Override
	public ParamDecl[] getParams() {
		return function.getParams();
	}

	@Override
	public Signature getSignature() {
		//now returns the modified signature
		return signature;
	}

	@Override
	public boolean hasBody() {
		return function.hasBody();
	}

	@Override
	public boolean isCreateCode() {
		return function.isCreateCode();
	}

	@Override
	public void setCreateCode(boolean createCode) {
		function.setCreateCode(createCode);
	}

	@Override
	public void setFlowReachesEnd(boolean b) {
		function.setFlowReachesEnd(b);
	}

	@Override
	public void setGeneratedName(String generatedName) {
		function.setGeneratedName(generatedName);
	}

	@Override
	public void setLocals(LocalVarDecl[] locals) {
		function.setLocals(locals);
	}

	@Override
	public void setName(String name) {
		function.setName(name);
	}

	@Override
	void setReturnType(Type returnType) {
		throw new Error("Setting returntype of a proxy not possible!");
	}

	@Override
	public String getUid() {
		return function.getUid();
	}

	@Override
	public Visibility getVisibility() {
		return function.getVisibility();
	}

	@Override
	public boolean isAbstract() {
		return function.isAbstract();
	}

	@Override
	public boolean isConst() {
		return function.isConst();
	}

	@Override
	public boolean isFinal() {
		return function.isFinal();
	}

	@Override
	public boolean isNative() {
		return function.isNative();
	}

	@Override
	public boolean isOverride() {
		return function.isOverride();
	}

	@Override
	public boolean isStatic() {
		return function.isStatic();
	}

	@Override
	public boolean isStrcall() {
		return function.isStrcall();
	}

	@Override
	public void setAbstract() {
		function.setAbstract();
	}

	@Override
	public void setConst() {
		function.setConst();
	}

	@Override
	public void setFinal() {
		function.setFinal();
	}

	@Override
	public void setNative() {
		function.setNative();
	}

	@Override
	public void setOverride() {
		function.setOverride();
	}

	@Override
	public void setStatic() {
		function.setStatic();
	}

	@Override
	public void setVisibility(Visibility visibility) {
		function.setVisibility(visibility);
	}

	@Override
	public SyntaxNode getDefinition() {
		return function.getDefinition();
	}

	@Override
	public Scope getScope() {
		return function.getScope();
	}


	@Override
	public HashSet<String> getAllowedAnnotations() {
		return function.getAllowedAnnotations();
	}


	@Override
	public boolean hasAnnotation(String name) {
		return function.hasAnnotation(name);
	}


	@Override
	public void setAnnotationTable(HashMap<String, AnnotationNode> annotations) {
		function.setAnnotationTable(annotations);
	}

	@Override
	public void afterAnnotationsProcessed() {
		function.afterAnnotationsProcessed();
	}

	@Override
	public FuncPointerDecl getPointerDecl(TypeProvider tp) {
		return function.getPointerDecl(tp);
	}
	
	@Override
	public String getElementTypeName() {
		return function.getElementTypeName();
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }

	@Override
	public List<ImplicitParamDecl> getImplicitParams() {
		return function.getImplicitParams();
	}
}
