/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.syntaxNodes.Annotation;
import com.sc2mod.andromeda.syntaxNodes.ReturnStatement;
import com.sc2mod.andromeda.syntaxNodes.Statement;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

//XPilot: added signature to GenericFunctionProxy
public class GenericFunctionProxy extends AbstractFunction {

	private AbstractFunction function;
	private Signature signature;
	private Type returnType;
	
	public GenericFunctionProxy(AbstractFunction vd, Signature signature, Type returnType) {
		function = vd;
		this.signature = signature;
		this.returnType = returnType;
	}
	
	@Override
	public Type getReturnType() {
		return returnType;
	}
	
	@Override
	public void addImplicitParams(ArrayList<LocalVarDecl> vars) {
		function.addImplicitParams(vars);
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
	public void addReturnStmt(ReturnStatement r) {
		function.addReturnStmt(r);
	}

	@Override
	public boolean flowReachesEnd() {
		return function.flowReachesEnd();
	}

	@Override
	public RecordType getContainingType() {
		return function.getContainingType();
	}

	@Override
	public String getDescription() {
		return function.getDescription();
	}

	@Override
	public int getFunctionType() {
		return function.getFunctionType();
	}

	@Override
	public String getGeneratedName() {
		return function.getGeneratedName();
	}

	@Override
	public int getIndex() {
		return function.getIndex();
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
	public boolean isForwardDeclaration() {
		return function.isForwardDeclaration();
	}

	@Override
	public boolean isMarked() {
		return function.isMarked();
	}

	@Override
	public boolean isMember() {
		return function.isMember();
	}

	@Override
	public void mark() {
		function.mark();
	}

	@Override
	public void resolveTypes(TypeProvider t, List<ParamDecl> implicitParameters) {
		function.resolveTypes(t, implicitParameters);
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
	public void setIndex(int index) {
		function.setIndex(index);
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
	public boolean usesThis() {
		return function.usesThis();
	}

	@Override
	public String getUid() {
		return function.getUid();
	}

	@Override
	public int getVisibility() {
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
	public void setVisibility(int visibility) {
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
	public Statement getBody() {
		return function.getBody();
	}
	
	@Override
	public void addOverride(AbstractFunction m) {
		function.addOverride(m);
	}

	@Override
	public boolean isOverridden() {
		return function.isOverridden();
	}

	@Override
	public boolean isCalledVirtually() {
		return function.isCalledVirtually();
	}

	@Override
	public AbstractFunction getOverridenMethod() {
		return function.getOverridenMethod();
	}

	@Override
	public int getVirtualTableIndex() {
		return function.getVirtualTableIndex();
	}

	@Override
	public int getCurVirtualCallChildIndex() {
		return function.getCurVirtualCallChildIndex();
	}

	@Override
	public void setVirtualCallIndex(int callIndex) {
		function.setVirtualCallIndex(callIndex);
	}

	@Override
	public void setVirtualTableIndex(int tableIndex) {
		function.setVirtualTableIndex(tableIndex);
	}

	@Override
	public int getNextVirtualCallChildIndex() {
		return function.getNextVirtualCallChildIndex();
	}

	@Override
	public void setVirtualCallerName(String virtualCallerName) {
		function.setVirtualCallerName(virtualCallerName);
	}

	@Override
	public int getVirtualCallIndex() {
		return function.getVirtualCallIndex();
	}

	@Override
	public String getVirtualCaller() {
		return function.getVirtualCaller();
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
	public void setAnnotationTable(HashMap<String, Annotation> annotations) {
		function.setAnnotationTable(annotations);
	}

	@Override
	public void afterAnnotationsProcessed() {
		function.afterAnnotationsProcessed();
	}
	
	@Override
	protected void registerVirtualCall() {
		function.registerVirtualCall();
	}
	
	@Override
	protected void setOverriddenMethod(Method method) {
		function.setOverriddenMethod(method);
	}

	@Override
	public FuncPointerDecl getPointerDecl(TypeProvider tp) {
		return function.getPointerDecl(tp);
	}

}
