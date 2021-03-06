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
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
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

public class Function extends AbstractFunction {

	
	public static final int TYPE_FUNCTION = 1;
	public static final int TYPE_METHOD = 2;
	public static final int TYPE_STATIC_METHOD = 3;
	public static final int TYPE_CONSTRUCTOR = 4;
	public static final int TYPE_DESTRUCTOR = 5;
	public static final int TYPE_STATIC_INIT = 6;
	public static final int TYPE_NATIVE = 7;
	
	protected StmtNode body;
	protected MemberDeclNode declaration;
	protected MethodHeaderNode header;
	private boolean createCode = true;
	private boolean flowReachesEnd;
	private String generatedName;
	private int index;
	private int inlineCount;
	private int invocationCount;
	private HashMap<String, AnnotationNode> annotations;
	
	private boolean isFinal;
	private boolean isInline;
	private boolean isNative;
	private boolean isOverride;
	private boolean isStrcall;
	private LocalVarDecl[] locals;
	private boolean marked;
	private String name;
	protected ParamDecl[] params;
	private List<ReturnStmtNode> returnStmts = new ArrayList<ReturnStmtNode>(4);
	private Type returnType;
	
	private FuncPointerDecl pointerDecl;
	
	private Scope scope;

	private Signature signature;

	private int visibility;
	private HashMap<String, AnnotationNode> annotationTable;

	public Function(GlobalFuncDeclNode functionDeclaration, Scope scope) {
		this(functionDeclaration.getFuncDecl(),scope);
	}
	protected Function(MethodDeclNode decl, Scope scope){
		this.declaration = decl;
		this.header = decl.getHeader();
		this.name = header.getName();
		this.body = decl.getBody();
		this.scope = scope;
		decl.setSemantics(this);
		Util.processModifiers(this, decl.getHeader().getModifiers());
		Util.processAnnotations(this, decl.getHeader().getAnnotations());
		if(isNative){
			if(isFinal) 
				throw Problem.ofType(ProblemId.NATIVE_FUNCTION_FINAL).at(declaration.getHeader().getModifiers())
							.raiseUnrecoverable();
				throw Problem.ofType(ProblemId.NATIVE_FUNCTION_WITH_BODY).at(declaration.getHeader().getModifiers())
							.raiseUnrecoverable();
		}	
	}
	
	//XPilot: for function proxies
	protected Function() {}
	
	protected Function(StaticInitDeclNode decl, Scope scope){
		this.declaration = decl;
		this.name = "static init";
		this.body = decl.getBody();
		this.scope = scope;
		decl.setSemantics(this);
		Util.processAnnotations(this, decl.getAnnotations());
	}
	
	public void addImplicitParams(ArrayList<LocalVarDecl> vars){
		int implicitSize = vars.size();
		LocalVarDecl[] newLocals = new LocalVarDecl[locals.length + implicitSize];
		vars.toArray(newLocals);
		System.arraycopy(locals, 0, newLocals, implicitSize, locals.length);
		locals = newLocals;
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
	public RecordType getContainingType() {
		return null;
	}

	
	@Override
	public SyntaxNode getDefinition() {
		return declaration;
	}
	
	public String getDescription(){
		return "function " + getUid();
	}

	public synchronized String getGeneratedName() {
		return generatedName==null?name:generatedName;
	}

	public int getIndex() {
		return index;
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

	public Type getReturnType() {
		return returnType;
	}

	@Override
	public Scope getScope() {
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
	public int getVisibility() {
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
	
	@Override
	public boolean isForwardDeclaration(){
		return this.body == null && !isNative;
	}

	public boolean isInline() {
		return isInline;
	}

	@Override
	public boolean isMarked() {
		return marked;
	}
	
	@Override
	public boolean isNative() {
		return isNative;
	}

	@Override
	public boolean isOverride() {
		return isOverride;
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public boolean isStrcall() {
		return isStrcall;
	}

	@Override
	public void mark() {
		this.marked = true;
	}
	
	@Override
	public void resolveTypes(TypeProvider t, List<ParamDecl> implicitParameters) {
		
		//Only resolve return type if it hasn't been resolved yet (for constructors for example)
		if(returnType==null){
			//Static inits have no header
			if(header==null){
				setReturnType(SpecialType.VOID);
			} else {
				Type returnType = t.resolveType(header.getReturnType());
				if(!returnType.isValidAsParameter()) 
					throw Problem.ofType(ProblemId.ARRAY_OR_STRUCT_RETURNED).at(header.getReturnType())
								.raiseUnrecoverable();
				setReturnType(returnType);
			}
		}
		int implicitSize = implicitParameters==null?0:implicitParameters.size();
		int size = (header==null||header.getParameters()==null?0:header.getParameters().size()) + implicitSize;
		
		Type[] sig = new Type[size];
		params = new ParamDecl[size];
		for(int i=0;i<implicitSize;i++){
			ParamDecl implParam = implicitParameters.get(i);
			Type type = implParam.getType();
			if(!type.isValidAsParameter())
				throw Problem.ofType(ProblemId.ARRAY_OR_STRUCT_RETURNED).at(header)
						.raiseUnrecoverable();
			sig[i] = type;
			params[i] = implParam;
		}
		for(int i=implicitSize;i<size;i++){
			ParameterNode param = header.getParameters().elementAt(i-implicitSize);
			Type type = t.resolveType(param.getType());
			if(!type.isValidAsParameter()) 
				throw Problem.ofType(ProblemId.ARRAY_OR_STRUCT_AS_PARAMETER).at(param)
						.raiseUnrecoverable();
			sig[i] = type;
			params[i] = new ParamDecl(null,type,param.getName());
		}
		this.signature = new Signature(sig);
		
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
	public void setIndex(int index) {
		this.index = index;
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
	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}
	
	@Override
	public boolean usesThis() {
		return false;
	}
	
	@Override
	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	@Override
	public int getFunctionType(){
		return isNative?TYPE_NATIVE:TYPE_FUNCTION;
	}
	
	@Override
	public boolean isMember(){
		return false;
	}
	@Override
	public StmtNode getBody() {
		return body;
	}
	
	private static HashSet<String> allowedAnnotations = new HashSet<String>();
	static{
		allowedAnnotations.add(Annotations.STRING_CALL);
		allowedAnnotations.add(Annotations.INLINE);
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
}
