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
import java.util.List;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.ModifierSet;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.access.OperationAccess;
import com.sc2mod.andromeda.environment.annotations.AnnotationSet;
import com.sc2mod.andromeda.environment.annotations.BasicAnnotations;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.ImplicitParamDecl;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.GlobalFuncDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MemberDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MethodHeaderNode;
import com.sc2mod.andromeda.syntaxNodes.ReturnStmtNode;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class Function extends Operation {

	public MethodHeaderNode getHeader() {
		return header;
	}
	

	
	protected StmtNode body;
	protected MemberDeclNode declaration;
	protected MethodHeaderNode header;


	private boolean createCode = true;
	private boolean flowReachesEnd;
	private String generatedName;
	private int inlineCount;
	private int invocationCount;
	
	protected final ModifierSet modifiers;
	private LocalVarDecl[] locals;
	private String name;
	protected ParamDecl[] params;
	protected List<ImplicitParamDecl> implicitParams;
	private List<ReturnStmtNode> returnStmts = new ArrayList<ReturnStmtNode>(4);
	private IType returnType;
	private AnnotationSet annotations;
	
	private OperationAccess pointerDecl;
	
	private IScope scope;

	private Signature signature;

	private OverrideInformation overrideInformation;
	
	public Function(GlobalFuncDeclNode functionDeclaration, IScope scope, Environment env) {
		this(functionDeclaration.getFuncDecl(),scope, env);
	}
	
	
	protected Function(MethodDeclNode decl, IScope scope, Environment env){
		this.declaration = decl;
		this.header = decl.getHeader();
		this.name = header.getName();
		this.body = decl.getBody();
		this.scope = scope;
		decl.setSemantics(this);
		this.modifiers = ModifierSet.create(this, decl.getHeader().getModifiers());
		env.annotationRegistry.processAnnotations(this, decl.getHeader().getAnnotations());
		overrideInformation = createOverrideInformation();
	
	}
	
	protected OverrideInformation createOverrideInformation() {
		return new OverrideInformation(this);
	}
	
	@Override
	public OverrideInformation getOverrideInformation() {
		return overrideInformation;
	}
	
	protected Function(StaticInitDeclNode decl, IScope scope, Environment env){
		this.declaration = decl;
		this.name = "static init";
		this.body = decl.getBody();
		this.scope = scope;
		decl.setSemantics(this);
		this.modifiers = ModifierSet.EMPTY_SET;
		env.annotationRegistry.processAnnotations(this, decl.getAnnotations());
	}
	

	
	public void addImplicitLocals(ArrayList<LocalVarDecl> vars){

		int implicitSize = vars.size();
		LocalVarDecl[] newLocals = new LocalVarDecl[locals.length + implicitSize];
		vars.toArray(newLocals);
		System.arraycopy(locals, 0, newLocals, implicitSize, locals.length);
		locals = newLocals;
	}

	public void addImplicitParam(IType type, String name){
		if(implicitParams == null){
			implicitParams = new ArrayList<ImplicitParamDecl>(3);
		}
		implicitParams.add(new ImplicitParamDecl(type, name));
	}
	
	/**
	 * Called by the resolve & check visitor 
	 * to entry the parameter list.
	 */
	public void setResolvedParameters(ParamDecl[] params){
		this.params = params;
		this.signature = new Signature(params);
	}
	
	public void addInline(){
		inlineCount++;
	}
	
	public void addInvocation(){
		invocationCount++;
	}
	
	public void addReturnStmt(ReturnStmtNode r){
		returnStmts.add(r);
	}

	public boolean flowReachesEnd(){
		return flowReachesEnd;
	}
	
	/**
	 * Functions are not contained in any type
	 * @return null
	 */
	public IType getContainingType() {
		return null;
	}

	
	@Override
	public SyntaxNode getDefinition() {
		return declaration;
	}
	
	public String getDescription(){
		return "function " + getUid();
	}

	public String getGeneratedName() {
		return generatedName==null?name:generatedName;
	}


	public int getInvocationCount() {
		return invocationCount;
	}
	
	public LocalVarDecl[] getLocals() {
		return locals;
	}

	public String getName() {
		return name;
	}
	
	public ParamDecl[] getParams() {
		return params;
	}
	
	public List<ImplicitParamDecl> getImplicitParams(){
		return implicitParams;
	}

	public IType getReturnType() {
		return returnType;
	}

	@Override
	public IScope getScope() {
		return scope;
	}

	@Override
	public Signature getSignature() {
		return signature;
	}

	@Override
	public String getUid() {
		return name;
	}
	
	@Override
	public Visibility getVisibility() {
		return modifiers.getVisibility();
	}
	
	@Override
	public boolean hasBody(){
		return this.body != null;
	}

	@Override
	public boolean isCreateCode() {
		return createCode;
	}

	public boolean isInline() {
		return annotations != null && annotations.hasAnnotation(BasicAnnotations.INLINE);
	}

	/**
	 * Functions are considered static
	 */
	@Override
	public boolean isStaticElement() {
		return true;
	}

	@Override
	public void setCreateCode(boolean createCode) {
		this.createCode = createCode;
	}

	@Override
	public void setFlowReachesEnd(boolean b) {
		flowReachesEnd = b;
	}

	@Override
	public synchronized void setGeneratedName(String generatedName) {
		this.generatedName = generatedName;
	}
	
	@Override
	public void setLocals(LocalVarDecl[] locals) {
		this.locals = locals;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

//	//FIXME Add transformation from static to private for functions again
//	@Override
//	public void setStatic() {
//		//For functions, static means private *damn blizz*
//		visibility = Visibility.PRIVATE;
//	}
	
	@Override
	public void setReturnType(IType returnType) {
		this.returnType = returnType;
	}

	@Override
	public OperationType getOperationType(){
		return modifiers.isNative()?OperationType.NATIVE:OperationType.FUNCTION;
	}
	
	@Override
	public OperationAccess getPointerDecl(TypeProvider typeProvider) {
		if(pointerDecl!=null) return pointerDecl;
		pointerDecl = new OperationAccess(this,typeProvider);
		return pointerDecl;
	}
	
	@Override
	public String getElementTypeName() {
		return "function";
	}
	





	@Override
	public AnnotationSet getAnnotations(boolean createIfNotExistant) {
		if(annotations == null && createIfNotExistant){
			annotations = new AnnotationSet();
		}
		return annotations;
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }


	@Override
	public ModifierSet getModifiers() {
		return modifiers;
	}
}
