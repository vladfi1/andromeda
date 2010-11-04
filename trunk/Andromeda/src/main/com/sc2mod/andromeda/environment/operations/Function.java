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

import com.sc2mod.andromeda.environment.Annotations;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.Util;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.semAnalysis.ResolveAndCheckTypesVisitor;
import com.sc2mod.andromeda.semAnalysis.SemanticsCheckerAndResolver;
import com.sc2mod.andromeda.syntaxNodes.AnnotationNode;
import com.sc2mod.andromeda.syntaxNodes.MemberDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalFuncDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MethodHeaderNode;
import com.sc2mod.andromeda.syntaxNodes.ParameterNode;
import com.sc2mod.andromeda.syntaxNodes.ReturnStmtNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;
import com.sc2mod.andromeda.environment.variables.ImplicitParamDecl;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class Function extends Operation {

	public MethodHeaderNode getHeader() {
		return header;
	}
	

	
	private static HashSet<String> allowedAnnotations = new HashSet<String>();
	static{
		allowedAnnotations.add(Annotations.STRING_CALL);
		allowedAnnotations.add(Annotations.INLINE);
	}
	

	
	protected StmtNode body;
	protected MemberDeclNode declaration;
	protected MethodHeaderNode header;


	private boolean createCode = true;
	private boolean flowReachesEnd;
	private String generatedName;
	private int inlineCount;
	private int invocationCount;
	private HashMap<String, AnnotationNode> annotations;
	
	private boolean isFinal;
	private boolean isInline;
	private boolean isNative;
	private boolean isOverride;
	private boolean isStrcall;
	private LocalVarDecl[] locals;
	private String name;
	protected ParamDecl[] params;
	protected List<ImplicitParamDecl> implicitParams;
	private List<ReturnStmtNode> returnStmts = new ArrayList<ReturnStmtNode>(4);
	private IType returnType;
	
	private FuncPointerDecl pointerDecl;
	
	private IScope scope;

	private Signature signature;

	private Visibility visibility = Visibility.DEFAULT;
	private HashMap<String, AnnotationNode> annotationTable;

	public Function(GlobalFuncDeclNode functionDeclaration, IScope scope) {
		this(functionDeclaration.getFuncDecl(),scope);
	}
	
	protected Function(MethodDeclNode decl, IScope scope){
		this.declaration = decl;
		this.header = decl.getHeader();
		this.name = header.getName();
		this.body = decl.getBody();
		this.scope = scope;
		decl.setSemantics(this);
		Util.processModifiers(this, decl.getHeader().getModifiers());
		Util.processAnnotations(this, decl.getHeader().getAnnotations());
	
	}
	
	//XPilot: for function proxies
	protected Function() {}
	
	protected Function(StaticInitDeclNode decl, IScope scope){
		this.declaration = decl;
		this.name = "static init";
		this.body = decl.getBody();
		this.scope = scope;
		decl.setSemantics(this);
		Util.processAnnotations(this, decl.getAnnotations());
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
		return visibility;
	}
	
	@Override
	public boolean hasBody(){
		return this.body != null;
	}

	@Override
	public boolean isAbstract() {
		return false;
	}
	
	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isCreateCode() {
		return createCode;
	}

	@Override
	public boolean isFinal() {
		return isFinal;
	}
	

	public boolean isInline() {
		return isInline;
	}
	
	@Override
	public boolean isNative() {
		return isNative;
	}

	@Override
	public boolean isOverride() {
		return isOverride;
	}

	/**
	 * Functions are considered static
	 */
	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public boolean isStrcall() {
		return isStrcall;
	}


	@Override
	public void setAbstract() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getHeader().getModifiers())
							.details("Functions", "abstract")
							.raiseUnrecoverable();
	}

	@Override
	public void setConst() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(declaration.getHeader().getModifiers())
							.details("Functions", "const")
							.raiseUnrecoverable();
	}

	@Override
	public void setCreateCode(boolean createCode) {
		this.createCode = createCode;
	}

	@Override
	public void setFinal() {
		isFinal = true;
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

	@Override
	public void setNative() {
		isNative = true;
	}

	@Override
	public void setOverride() {
		isOverride = true;
	}
	
	@Override
	public void setStatic() {
		//For functions, static means private *damn blizz*
		visibility = Visibility.PRIVATE;
	}
	
	@Override
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}
	
	@Override
	public void setReturnType(IType returnType) {
		this.returnType = returnType;
	}

	@Override
	public OperationType getOperationType(){
		return isNative?OperationType.NATIVE:OperationType.FUNCTION;
	}
	
	

	
	@Override
	public HashSet<String> getAllowedAnnotations() {
		return allowedAnnotations;
	}
	@Override
	public boolean hasAnnotation(String name) {
		return annotationTable.containsKey(name);
	}
	@Override
	public void setAnnotationTable(HashMap<String, AnnotationNode> annotations) {
		annotationTable = annotations;
	}
	@Override
	public void afterAnnotationsProcessed() {
		isStrcall = annotationTable.containsKey(Annotations.STRING_CALL);
		isInline = annotationTable.containsKey(Annotations.INLINE);
	}
	@Override
	public FuncPointerDecl getPointerDecl(TypeProvider typeProvider) {
		if(pointerDecl!=null) return pointerDecl;
		pointerDecl = new FuncPointerDecl(this,typeProvider);
		return pointerDecl;
	}
	
	@Override
	public String getElementTypeName() {
		return "function";
	}
	

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }


}
