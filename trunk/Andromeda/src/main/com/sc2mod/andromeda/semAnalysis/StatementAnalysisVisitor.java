/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.semAnalysis;

import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.access.ConstructorInvocation;
import com.sc2mod.andromeda.environment.access.Invocation;
import com.sc2mod.andromeda.environment.annotations.BasicAnnotations;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.OperationType;
import com.sc2mod.andromeda.environment.operations.OperationUtil;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.content.NameResolver;
import com.sc2mod.andromeda.environment.scopes.content.ResolveUtil;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.TypeUtil;
import com.sc2mod.andromeda.environment.types.basic.BasicTypeSet;
import com.sc2mod.andromeda.environment.variables.ImplicitParamDecl;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.ArrayInitNode;
import com.sc2mod.andromeda.syntaxNodes.ArrayTypeNode;
import com.sc2mod.andromeda.syntaxNodes.BlockStmtNode;
import com.sc2mod.andromeda.syntaxNodes.BreakStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ContinueStmtNode;
import com.sc2mod.andromeda.syntaxNodes.DeleteStmtNode;
import com.sc2mod.andromeda.syntaxNodes.DoWhileStmtNode;
import com.sc2mod.andromeda.syntaxNodes.EmptyStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ExplicitConsCallStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprStmtNode;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ForEachStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ForStmtNode;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.IfStmtNode;
import com.sc2mod.andromeda.syntaxNodes.InstanceLimitSetterNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;
import com.sc2mod.andromeda.syntaxNodes.LocalTypeDeclStmtNode;
import com.sc2mod.andromeda.syntaxNodes.LocalVarDeclStmtNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ReturnStmtNode;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.StmtListNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.ThrowStmtNode;
import com.sc2mod.andromeda.syntaxNodes.UninitedVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarAssignDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.WhileStmtNode;
import com.sc2mod.andromeda.util.visitors.TraceScopeScanVisitor;
import com.sc2mod.andromeda.vm.data.BoolObject;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.IntObject;

/**
 * The main visitor of the semantic analysis.
 * Checks many constraints, infers and checks types, calculates constant expressions,
 * Checks the control flow of a function for dead code.
 * @author J. 'gex' Finis 
 */
public class StatementAnalysisVisitor extends TraceScopeScanVisitor {
	
	//Useful things :)
	NameResolver nameResolver;
	TypeProvider typeProvider;
	ConstantResolveVisitor constResolve;
	ExecutionPathStack execPathStack = new ExecutionPathStack();
	private Configuration options;
	protected Environment env;
	private BasicTypeSet BASIC;
	private ExpressionAnalysisVisitor exprAnalyzer;
	
	//current variables
	LoopSemantics curLoop;
	protected Operation curOperation;
	boolean isOnTop;
	protected Variable curField;
	Variable curLocal;
	private TransientAnalysisData transientData;
	
	protected ExpressionAnalysisVisitor createExpressionAnalyisVisitor(){
		return new ExpressionAnalysisVisitor(this);
	}
	
	public StatementAnalysisVisitor(Environment env, Configuration options, TransientAnalysisData transientData) {
		this.nameResolver = new NameResolver(new ArrayLocalVarStack());
		this.env = env;
		this.typeProvider = env.typeProvider;
		this.constResolve = new ConstantResolveVisitor();
		this.options = options;
		this.transientData = transientData;
		this.exprAnalyzer = createExpressionAnalyisVisitor();
		this.BASIC = typeProvider.BASIC;
		
	}
		
	private void analyzeExpression(ExprNode expr){
		expr.accept(exprAnalyzer, ExpressionContext.STATEMENT);
	}
	
	//************** GLOBAL STRUCTURES *************
	@Override
	protected void onClassVisit(ClassDeclNode c) {
		if(c.getInstanceLimit() != null) analyzeExpression(c.getInstanceLimit());
	}
	
	@Override
	public void visit(ClassDeclNode classDeclaration) {
		//Process body, trace scope
		super.visit(classDeclaration);
	}
	
	@Override
	public void visit(InstanceLimitSetterNode instanceLimitSetterNode) {
		//Analyze the expression in the instance limit setter.
		analyzeExpression(instanceLimitSetterNode.getInstanceLimit());
	}
	
	public void visit(StaticInitDeclNode d) {
		//Remember old function (in case we have nested functions or local classes inside a function)
		Operation functionBefore = curOperation;
		LoopSemantics loopBefore = curLoop;
		
		//A function begins not in a loop
		curLoop = null;
		
		//We are on top of the function
		isOnTop = true;
		
		//Set current function
		curOperation = (Function)d.getSemantics();
		StmtNode body = d.getBody();
		
		//Analyze the body
		body.accept(this);
			
		//Get the local variable result
		curOperation.setLocals(nameResolver.methodFinished(0));

		//Check if the control flow reaches the end of the function
		curOperation.setFlowReachesEnd(!execPathStack.isTopFrameEmpty());
		
		//Pop the last frame from the method and check if the stack is empty
		execPathStack.popFrameAndDiscard();
		if(!execPathStack.isStackEmpty()){
			throw new InternalError(curOperation.getUid() + ": Execution path stack is not empty at the end of a function. Please contact gex!");
		}
		
		//Restore function before
		curOperation = functionBefore;
		curLoop = loopBefore;
	}
	
	
	@Override
	public void visit(FieldDeclNode fieldDeclNode) {
		
		//Visit type (if it is an array type, it could contain expressions)
		fieldDeclNode.getType().accept(this);
		
		//Only visit declared variables, not the type!
		VarDeclListNode vars = fieldDeclNode.getDeclaredVariables();
		for(VarDeclNode v : vars){
			
			//Set cur field
			Variable fieldBefore = curField;
			curField = v.getName().getSemantics();
			
			//Accept
			v.accept(this);
			
			//Reset cur field
			curField = fieldBefore;
		}
	}
	
	
	@Override
	public void visit(UninitedVarDeclNode variableDecl) {
		VarDecl decl = variableDecl.getName().getSemantics();
		
		//Constants must be defined at declaration
		if(decl.getModifiers().isConst()){
			throw Problem.ofType(ProblemId.CONST_VAR_NOT_INITED).at(variableDecl)
				.raiseUnrecoverable();
		}
		
		//The type for var decls is always correct
		variableDecl.getName().setInferedType(((VarDecl)variableDecl.getName().getSemantics()).getType());
	}

	@Override
	public void visit(VarAssignDeclNode variableAssignDecl) {
		
		analyzeExpression(variableAssignDecl.getInitializer());
		ExprNode init = variableAssignDecl.getInitializer();
		
		IType t = init.getInferedType();
		IdentifierNode identifier = variableAssignDecl.getName();
		VarDecl decl = identifier.getSemantics();
		identifier.setSemantics(decl);
		identifier.setInferedType(decl.getType());
		
		//Check type
		if(!t.canImplicitCastTo(decl.getType())){
			//For bytes, we might be able to cast if the value is a constant
			boolean error = true;
			if(decl.getType().getBaseType()==BASIC.BYTE){				
				DataObject obj = init.getValue();
				if(obj != null && obj instanceof IntObject){
					int i = obj.getIntValue();
					if(i >= 0 && i <= 255){
						error = false;
					}
				}
			}
			
			if(error)
				throw Problem.ofType(ProblemId.TYPE_ERROR_INCOMPATIBLE_ASSIGNMENT).at(variableAssignDecl)
						.details(t.getFullName(),decl.getType().getFullName())
						.raiseUnrecoverable();
		}
			
		//Constants must be initialized with constant values
		if(decl.getModifiers().isConst()){
			if(!variableAssignDecl.getInitializer().isConstant())
				throw Problem.ofType(ProblemId.CONST_VAR_UNCONST_INIT).at(variableAssignDecl)
					.raiseUnrecoverable();
			variableAssignDecl.accept(constResolve);
		}
	}
	
	

	
	ConstructorInvocation resolveConstructorCall(SyntaxNode def, IClass c, Signature sig, IScope from, boolean implicit, boolean useSuper){
		ArrayList<IClass> wrappedFieldInits = null;
		
		if(useSuper){
			if(TypeUtil.hasTypeFieldInits(c)){
				wrappedFieldInits = new ArrayList<IClass>(4);
				wrappedFieldInits.add(c);
			}
			c = c.getSuperClass();
			
		}
		
		if(c.hasConstructors()){
			//Class has explicit constructors
			Operation con = c.getConstructors().get(sig, def, from);
			if(con == null)
				throw Problem.ofType(ProblemId.NO_CONSTRUCTOR_WITH_THIS_SIGNATURE).at(def)
							.details(c.getFullName(),sig.getFullName())
							.raiseUnrecoverable();
			return new ConstructorInvocation(con, implicit, wrappedFieldInits);
		}
		
		//Class has no explicit constructors
		if(sig.size()>0)
			throw Problem.ofType(ProblemId.NO_CONSTRUCTOR_WITH_THIS_SIGNATURE).at(def)
				.details(c.getFullName(),sig.getFullName())
				.raiseUnrecoverable();
		
		IClass superClass = c;
		if(c.getSuperClass()!=null){
			
			
			while(true){
				//Superclass has no constructors
				if(!superClass.hasConstructors()){
					if(superClass.isTopType()){
						//If the superclass is a topclass we call the allocator
						return new ConstructorInvocation(superClass,implicit,wrappedFieldInits);
					} else {
						//If it is no topclass we add the field init and try the next class
						if(TypeUtil.hasTypeFieldInits(superClass)){
							if(wrappedFieldInits==null)wrappedFieldInits = new ArrayList<IClass>(4);
							wrappedFieldInits.add(superClass);
						}
						
						//Try next
						superClass = superClass.getSuperClass();
					}
				} else {
					//Superclass has constructors
					Operation con = superClass.getConstructors().get(Signature.EMPTY_SIGNATURE, def, from);
					if(con == null)
						throw Problem.ofType(ProblemId.NO_IMPLICIT_SUPER_CONSTRUCTOR).at(def)
									.details(superClass.getFullName())
									.raiseUnrecoverable();
					return new ConstructorInvocation(con, implicit,wrappedFieldInits);
				}
			
			}				

		} else {
			//No constructor available and this is a top class, so invoke allocator
			return new ConstructorInvocation(c, implicit, wrappedFieldInits);
		}
	}
	
	private void checkDestructor(Destructor d){
		
	}
	
	private void checkConstructor(Constructor c, StmtNode body){
		IClass clazz = (IClass) c.getContainingType();
		IClass superClass = clazz.getSuperClass();
		
		StmtListNode stmts = body.getStatements();
			
		
		if(stmts.isEmpty()||!(stmts.get(0) instanceof ExplicitConsCallStmtNode)){
			//No explicit constructor invocation
			
			if(superClass!=null){
				c.setInvokedConstructor(resolveConstructorCall(body, superClass,Signature.EMPTY_SIGNATURE,curScope, true,false));
			} else {
				c.setInvokedConstructor(new ConstructorInvocation(clazz, true, null));
			}

		} else {
			//Explicit constructor invocation
			ExplicitConsCallStmtNode explicitInvocation = (ExplicitConsCallStmtNode) stmts.get(0);
			explicitInvocation.getArguments().accept(exprAnalyzer, ExpressionContext.STATEMENT);
			boolean useSuper = explicitInvocation.isUseSuper();
			if(useSuper&&superClass==null)
				throw Problem.ofType(ProblemId.SUPER_CONSTRUCTOR_IN_TOP_CLASS).at(explicitInvocation)
							.raiseUnrecoverable();
 			
			//Resolve the constructor call
			
			ConstructorInvocation inv = resolveConstructorCall(explicitInvocation, clazz, new Signature(explicitInvocation.getArguments()), curScope, false,useSuper);
			explicitInvocation.setSemantics(inv);
			c.setInvokedConstructor(inv);
		}
	}
	
	@Override
	public void visit(MethodDeclNode functionDeclaration) {
		
		//Get function body, if this function has none (abstract/inteface) we don't have to do anything
		StmtNode body = functionDeclaration.getBody();
		if(body == null||(!options.getParamBool(Parameter.TEST_CHECK_NATIVE_FUNCTION_BODIES)&&curSourceInfo.getType()==InclusionType.NATIVE)) return;
		
		//Remember old function (in case we have nested functions or local classes inside a function)
		Operation functionBefore = curOperation;
		LoopSemantics loopBefore = curLoop;
		
		//A function begins not in a loop
		curLoop = null;
		
		//We are on top of the function
		isOnTop = true;
		
		//Set current function
		curOperation = (Function)functionDeclaration.getSemantics();
		
		//Register parameters as local variables
		for(VarDecl v: curOperation.getParams()){
			nameResolver.registerLocalVar(v);
		}
		
		//Register implicit parameters, too
		List<ImplicitParamDecl> implParams = curOperation.getImplicitParams();
		if(implParams != null){
			for(ImplicitParamDecl p : implParams){
				nameResolver.registerLocalVar(p);
			}
		}
		
		
		//If this is a con-/destructor, do checks for it
		OperationType functionType = curOperation.getOperationType();
		switch(functionType){
		case CONSTRUCTOR:
			checkConstructor((Constructor)curOperation,body);
			break;
		case DESTRUCTOR:
			checkDestructor((Destructor)curOperation);
			break;
		}

		
		//Analyze the body
		body.accept(this);
			
		//Get the local variable result
		curOperation.setLocals(nameResolver.methodFinished(OperationUtil.countParams(curOperation)));

		//If this is a non void function, check if the exec path does not end without a return
		if(curOperation.getReturnType()!=BASIC.VOID&&functionType!=OperationType.CONSTRUCTOR){
			if(!execPathStack.isTopFrameEmpty())
				throw Problem.ofType(ProblemId.MISSING_RETURN).at(functionDeclaration)
						.raiseUnrecoverable();
		}
		//Check if the control flow reaches the end of the function
		curOperation.setFlowReachesEnd(!execPathStack.isTopFrameEmpty());
		
		//Pop the last frame from the method and check if the stack is empty
		execPathStack.popFrameAndDiscard();
		if(!execPathStack.isStackEmpty()){
			throw new InternalProgramError(functionDeclaration,"Execution path stack is not empty at the end of a function. Please file a bug report!");
		}
		
		//Restore function before
		curOperation = functionBefore;
		curLoop = loopBefore;
				
	}
	
	//************** MISC STRUCTURES **************
	
	//FIXME testcases for array types in various positions to see if instance limit is checked correctly
	public void visit(ArrayTypeNode arrayTypeNode) {
		ExprNode expr = arrayTypeNode.getDimension();
		analyzeExpression(expr);
		
		//Recursively check subtype
		arrayTypeNode.getWrappedType().accept(this);
		
		//Add to the list of array type nodes for later instance checking
		transientData.getArrayTypes().add(arrayTypeNode);
	}
	
	
	@Override
	public void visit(ExprListNode expressionList) {
		throw new InternalProgramError(expressionList,"Trying to visit expression list!");
	}
	
	@Override
	public void visit(ArrayInitNode arrayInitializer) {
		throw new InternalProgramError(arrayInitializer,"Array initializers not yet supported!");
	}
	
	//************** STATEMENTS (walk through, check blocks, return types, ...) **************
	@Override
	public void visit(StmtListNode statementList) {
		int size = statementList.size();
		StmtNode stmt = null;
		boolean deadCode = false;
		boolean remove = false;
		for(int i=0;i<size;i++){
			stmt = statementList.get(i);
			
			//Top frame empty? Dead code!
			if(execPathStack.isTopFrameEmpty()&&!deadCode){
				Problem p = Problem.ofType(ProblemId.UNREACHABLE_CODE).at(stmt)
							.raise();
				remove = p.wantRemove();
				deadCode = true;
			}
			
			//Link statements for the exec path
			execPathStack.popFrameAndConnect(stmt);			
			
			//Check if on top
			if(isOnTop){
				if(!(stmt instanceof LocalVarDeclStmtNode)){
					isOnTop = false;
				}
			} 
			stmt.accept(this);
			
			if(remove){
				statementList.get(i);
				size--;
				i--;
			}
		}
		
		//If this was dead code, throw away the result and push an empty frame
		if(deadCode){
			execPathStack.popFrameAndDiscard();
			execPathStack.pushFrame();
		}
	}
	

	
	@Override
	public void visit(BlockStmtNode blockStatement) {
		//Block statements create local variable scopes
		nameResolver.pushLocalBlock();
		
		//Push the block statement onto the execution block, that it gets
		//linked to the first statement in the block (if there is any)
		//or to the next statement (if there is no statement in this block)
		execPathStack.pushSingleStatementFrame(blockStatement);
		
		blockStatement.childrenAccept(this);
		nameResolver.popLocalBlock();
	}
	
	@Override
	public void visit(BreakStmtNode breakStatement) {
		//Pushes an empty frame, since the following statement will have no predecessor!
		execPathStack.pushFrame();
		
		//Check if we are in a loop
		if(curLoop==null) 
			Problem.ofType(ProblemId.BREAK_OUTSIDE_LOOP).at(breakStatement)
				.raise();
			
		//Add break
		curLoop.addBreak(breakStatement);
	}
	
	@Override
	public void visit(ContinueStmtNode continueStatement) {
		//Pushes an empty frame, since the following statement will have no predecessor!
		execPathStack.pushFrame();
		
		//Check if we are in a loop
		if(curLoop==null)
			Problem.ofType(ProblemId.CONTINUE_OUTSIDE_LOOP).at(continueStatement)
				.raise();
		
		//Set the use continue flag
		curLoop.addContinue(continueStatement);
	}
	
	@Override
	public void visit(DoWhileStmtNode doWhileStatement) {
		StmtNode thenStmt = doWhileStatement.getThenStatement();
		
		LoopSemantics loopBefore = curLoop;
		doWhileStatement.setSemantics(curLoop = new LoopSemantics());
		
		analyzeExpression(doWhileStatement.getCondition());
		doWhileStatement.getThenStatement().accept(this);
		
		//Execution path:
		//Add an end of loop statement and link it
		IntermediateLoopStatement eolStmt = new IntermediateLoopStatement();
		execPathStack.popFrameAndConnect(eolStmt);
		eolStmt.getSuccessors().add(doWhileStatement);
		//Also, link this statement to the block
		doWhileStatement.setSuccessor(thenStmt);
		//Push the eol Statement on the stack that it will be linked to later elements
		execPathStack.pushSingleStatementFrame(eolStmt);
		//All continue statements go to the eolStmt
		for(StmtNode s: curLoop.getContinues()) s.setSuccessor(eolStmt);
		//All break statements are added onto the stack, so they get bound to the next stmt
		for(StmtNode s: curLoop.getBreaks()) execPathStack.pushStatement(s);
		
		
		if(!doWhileStatement.getCondition().getInferedType().canImplicitCastTo(BASIC.BOOL))
			throw Problem.ofType(ProblemId.TYPE_ERROR_NONBOOL_LOOP_CONDITION).at(doWhileStatement.getCondition())
				.details(doWhileStatement.getCondition().getInferedType().getUid())
				.raiseUnrecoverable();
	
		curLoop = loopBefore;		
}
	
	@Override
	public void visit(WhileStmtNode whileStatement) {
		StmtNode thenStmt = whileStatement.getThenStatement();
		
		//Body
		LoopSemantics loopBefore = curLoop;
		whileStatement.setSemantics(curLoop = new LoopSemantics());
		analyzeExpression(whileStatement.getCondition());
		whileStatement.getThenStatement().accept(this);
		
		//Execution path:
		//Set the while statement as successor of all the last statements in the body
		execPathStack.popFrameAndConnect(whileStatement);
		//Push it on the stack so it will be linked with later elements
		execPathStack.pushSingleStatementFrame(whileStatement);
		//Also add the body to it as a seconds successor
		SuccessorList.addAdditionalSuccessor(whileStatement,thenStmt);	
		//All continue statements go to the while Statement
		for(StmtNode s: curLoop.getContinues()) s.setSuccessor(whileStatement);
		//All break statements are added onto the stack, so they get bound to the next stmt
		for(StmtNode s: curLoop.getBreaks()) execPathStack.pushStatement(s);

		//Check condition type
		if(!whileStatement.getCondition().getInferedType().canImplicitCastTo(BASIC.BOOL))
			throw Problem.ofType(ProblemId.TYPE_ERROR_NONBOOL_LOOP_CONDITION).at(whileStatement.getCondition())
					.details(whileStatement.getCondition().getInferedType().getUid())
					.raiseUnrecoverable();
		
		curLoop = loopBefore;	
	}
	
	@Override
	public void visit(ExplicitConsCallStmtNode e) {
		e.getArguments().accept(exprAnalyzer, ExpressionContext.STATEMENT);
		execPathStack.pushSingleStatementFrame(e);
	}
	
	@Override
	public void visit(ForEachStmtNode forEachStmt) {
		StmtNode thenStmt = forEachStmt.getThenStatement();
		
		//Body
		LoopSemantics loopBefore = curLoop;
		ForeachSemantics semantics;
		forEachStmt.setSemantics(curLoop = semantics = new ForeachSemantics());	
		//There is a var definition in the header, so create a new local block
		nameResolver.pushLocalBlock();		
		
		//Check init expression
		ExprNode iteree = forEachStmt.getExpression();
		analyzeExpression(iteree);
		
		//Register local var for iterator
		IType iterVarType = typeProvider.resolveType(forEachStmt.getIteratorType(),curScope);
		LocalVarDecl iterVarDecl = new LocalVarDecl(null, iterVarType, forEachStmt.getIterator(), false, curScope, true);
		nameResolver.registerLocalVar(iterVarDecl);
		semantics.setIterVarDecl(iterVarDecl);
		forEachStmt.getIterator().setSemantics(iterVarDecl);
		
		//Body
		thenStmt.accept(this);

		//Pop the local var block
		nameResolver.popLocalBlock();
		
		//Execution path:
		//Set the foreach statement as successor of all the last statements in the body
		execPathStack.popFrameAndConnect(forEachStmt);
		//Push it on the stack so it will be linked with later elements
		execPathStack.pushSingleStatementFrame(forEachStmt);
		//Also add the body to it as a seconds successor
		SuccessorList.addAdditionalSuccessor(forEachStmt,thenStmt);	
		//All continue statements go to the foreach Statement
		for(StmtNode s: curLoop.getContinues()) s.setSuccessor(forEachStmt);
		//All break statements are added onto the stack, so they get bound to the next stmt
		for(StmtNode s: curLoop.getBreaks()) execPathStack.pushStatement(s);


		
		//Check iter type
		IType itereeType = iteree.getInferedType();
		
		
		switch(itereeType.getCategory()){
		case ARRAY:
			//TODO Array foreach
			
			throw new Error("array foreach not yet supported");
		case BASIC:
		case CLASS:
		case INTERFACE:
			//Get iterator method
			Invocation iv = ResolveUtil.resolvePrefixedInvocation(itereeType, "getIterator", Signature.EMPTY_SIGNATURE, curScope, iteree, false, false, false);
			
			if(iv == null){
				Problem.ofType(ProblemId.FOREACH_NO_GET_ITERATOR_METHOD).at(forEachStmt)
					.details(itereeType.getUid())
					.raise();
			}
			semantics.setGetIterator(iv);
			
			//Get iterator type and check if it has all needed methods
			IType iteratorType = iv.getReturnType();
			
			// hasNext()
			iv = ResolveUtil.resolvePrefixedInvocation(iteratorType, "hasNext", Signature.EMPTY_SIGNATURE, curScope, iteree, false, false, false);
			
			if(iv == null){
				Problem.ofType(ProblemId.FOREACH_NO_HAS_NEXT_METHOD).at(forEachStmt)
					.details(iteratorType)
					.raiseUnrecoverable();
			}
			if(iv.getWhichFunction().getReturnType()!=BASIC.BOOL){
				Problem.ofType(ProblemId.FOREACH_HAS_NEXT_DOES_NOT_RETURN_BOOL).at(forEachStmt)
					.details(iv.getWhichFunction().getReturnType().getUid())
					.raiseUnrecoverable();
			}
			semantics.setHasNext(iv);
			
			//next()
			iv = ResolveUtil.resolvePrefixedInvocation(iteratorType, "next", Signature.EMPTY_SIGNATURE, curScope, iteree, false, false, false);
			if(iv == null) {
				Problem.ofType(ProblemId.FOREACH_NO_NEXT_METHOD).at(forEachStmt)
					.details(iteratorType.getUid())
					.raiseUnrecoverable();
			}
			IType nextType = iv.getReturnType();			
			if(!nextType.canImplicitCastTo(iterVarType)){
				Problem.ofType(ProblemId.FOREACH_INCOMPATIBLE_ITERATION_TYPE).at(forEachStmt)
					.details(iv.getWhichFunction().getReturnType().getUid(),iterVarType.getUid())
					.raiseUnrecoverable();
			}
			semantics.setNext(iv);
			
			boolean destroyAfter = false;
			//destroy after foreach
			if(iteratorType instanceof IClass){
				IClass c = (IClass)iteratorType;
				if(!c.getAnnotations(false).hasAnnotation(BasicAnnotations.KEEP_AFTER_FOREACH)){
					destroyAfter = true;
				}
			}
			semantics.setDestroyAfter(destroyAfter);
			
			//Types
			semantics.setIterVarType(iterVarType);
			semantics.setNextType(nextType);
			semantics.setItereeType(itereeType);
			semantics.setIteratorType(iteratorType);
			
					
		}

		curLoop = loopBefore;
	}
	
	@Override
	public void visit(ForStmtNode forStatement) {
		StmtNode thenStmt = forStatement.getThenStatement();
		
		//Body
		LoopSemantics loopBefore = curLoop;
		forStatement.setSemantics(curLoop = new LoopSemantics());	
		//There could be a var definition in the header, so create a new local block
		nameResolver.pushLocalBlock();		
		
		//Since the init can contain statements, we have to cut those statements from the execution path
		int execPathFrame = execPathStack.getCurFrame();
		StmtNode s = forStatement.getForInit();
		if(s!=null) s.accept(this);
		execPathStack.cutToFrame(execPathFrame);
		//The rest can be processed normally
		ExprNode e = forStatement.getCondition();
		if(e!=null) analyzeExpression(e);
		thenStmt.accept(this);
		//does the control flow reach the end of the body?
		if(execPathStack.isTopFrameEmpty()){
			curLoop.setControlFlowReachesEnd(false);;
		}
		
		StmtNode el = forStatement.getForUpdate();
		if(el!=null){ 
			el.accept(this);
			execPathStack.popFrameAndDiscard();
		}
		nameResolver.popLocalBlock();
		
		//Execution path:
		//Add an end of loop statement and link it
		IntermediateLoopStatement eolStmt = new IntermediateLoopStatement();
		execPathStack.popFrameAndConnect(eolStmt);
		eolStmt.setSuccessor(forStatement);
		//Also, link this statement to the block
		SuccessorList.addAdditionalSuccessor(forStatement,thenStmt);
		//Push the eol Statement on the stack that it will be linked to later elements
		execPathStack.pushSingleStatementFrame(forStatement);
		//All continue statements go to the eolStmt
		for(StmtNode ss: curLoop.getContinues()) ss.setSuccessor(eolStmt);
		//All break statements are added onto the stack, so they get bound to the next stmt
		for(StmtNode ss: curLoop.getBreaks()) execPathStack.pushStatement(ss);
			
		//Check condition type
		ExprNode cond = forStatement.getCondition();
		if(cond == null){
			forStatement.setCondition(cond = new LiteralExprNode(new LiteralNode(BoolObject.getBool(true), LiteralTypeSE.BOOL)));
			cond.setInferedType(BASIC.BOOL);
		}
		if(!cond.getInferedType().canImplicitCastTo(BASIC.BOOL))
			Problem.ofType(ProblemId.TYPE_ERROR_NONBOOL_LOOP_CONDITION).at(forStatement.getCondition())
				.details(forStatement.getCondition().getInferedType().getUid())
				.raiseUnrecoverable();
		curLoop = loopBefore;
	}
	

	@Override
	public void visit(ExprStmtNode expressionStatement) {
		analyzeExpression(expressionStatement.getExpression());
		execPathStack.pushSingleStatementFrame(expressionStatement);
	}
	
	@Override
	public void visit(EmptyStmtNode emptyStatement) {
		execPathStack.pushSingleStatementFrame(emptyStatement);
	}
	
	@Override
	public void visit(DeleteStmtNode deleteStatement) {
		ExprNode expr = deleteStatement.getExpression();
		analyzeExpression(expr);
		IType type = expr.getInferedType();
		//made generic class delete-able
		if(type.getCategory()!=TypeCategory.CLASS){
			Problem.ofType(ProblemId.DELETE_NON_CLASS).at(expr)
				.details(type.getUid(),type.getDescription())
				.raiseUnrecoverable();
		}

		//Register destructor invocation
		Invocation in = ResolveUtil.registerDelete((IClass)type,deleteStatement);
		deleteStatement.setSemantics(in);
		
		execPathStack.pushSingleStatementFrame(deleteStatement);
		
		
	}
	
	@Override
	public void visit(IfStmtNode ifThenElseStatement) {
		//Do not allow a non block statement as body, (in the else case possible if an ifthenelse follows)
		StmtNode thenStmt = ifThenElseStatement.getThenStatement();
		StmtNode elseStmt = ifThenElseStatement.getElseStatement();
	
		//Infer condition
		analyzeExpression(ifThenElseStatement.getCondition());
		
		//Execution path & child visit:
		//THEN-path
		thenStmt.accept(this);
		SuccessorList.addAdditionalSuccessor(ifThenElseStatement, thenStmt);
		//ELSE-path
		if(elseStmt != null){
			elseStmt.accept(this);
			ifThenElseStatement.setSuccessor(elseStmt);	
		} else {
			//If we have no else path, push the if then else on the stack, so it will
			//be linked to later statements
			execPathStack.pushSingleStatementFrame(ifThenElseStatement);
		}
		//Merge the two frames on the stack, so they all will be linked to later statements
		execPathStack.mergeTopFrames();
		
		//Check condition type
		IType t = ifThenElseStatement.getCondition().getInferedType();
		if(!t.canImplicitCastTo(BASIC.BOOL)&&!t.canBeNull())
			Problem.ofType(ProblemId.TYPE_ERROR_NONBOOL_IF_CONDITION).at(ifThenElseStatement.getCondition())
					.details(ifThenElseStatement.getCondition().getInferedType().getUid())
					.raiseUnrecoverable();
			
		
	}
	
	@Override
	public void visit(
			LocalTypeDeclStmtNode localTypeDeclarationStatement) {
		execPathStack.pushSingleStatementFrame(localTypeDeclarationStatement);
		throw new InternalProgramError(localTypeDeclarationStatement,"Local type declarations not yet supported!");
	}
	
	@Override
	public void visit(LocalVarDeclStmtNode l) {
		execPathStack.pushSingleStatementFrame(l);
		
		//Get and resolve local var type
		l.getVarDeclaration().getType().accept(this);
		
		VarDeclListNode decls = l.getVarDeclaration().getDeclarators();
		IType t = typeProvider.resolveType(l.getVarDeclaration().getType(),curScope);
		
		//Register and init all variables in the correct order
		for(VarDeclNode decl : decls){
			
			//Register the local var
			LocalVarDecl local = new LocalVarDecl(l.getVarDeclaration().getModifiers(),t, decl, isOnTop, curScope);
			
			nameResolver.registerLocalVar(local);
					
			//Check the init expression
			Variable curLocalBefore = curLocal;
			curLocal = local;
			decl.accept(this);
			curLocal = curLocalBefore;
			
		
			
		}
	}
	
	@Override
	public void visit(ReturnStmtNode returnStatement) {
		//Pushes an empty frame, since the following statement will have no predecessor!
		execPathStack.pushFrame();
		
		//Register
		curOperation.addReturnStmt(returnStatement);
		
		//For later use, we set the function as semantics of this return stmt
		returnStatement.setSemantics(curOperation);
		
		ExprNode result = returnStatement.getResult();
		
		if(result!=null){
			//Analyze the expression
			analyzeExpression(result);
			
			//Void function? No return expression allowed!
			if(curOperation.getReturnType()==BASIC.VOID)
				throw Problem.ofType(ProblemId.RETURNING_VALUE_IN_VOID_METHOD).at(returnStatement)
							.raiseUnrecoverable();
				
			//Check if the return type is correct
			if(!result.getInferedType().canImplicitCastTo(curOperation.getReturnType())){
				throw Problem.ofType(ProblemId.TYPE_ERROR_INCOMPATIBLE_RETURN_TYPE).at(returnStatement)
							.details(curOperation.getReturnType().getFullName(),result.getInferedType().getFullName())
							.raiseUnrecoverable();
				
			}
		
		} else {
			//Void return
			if(curOperation.getReturnType()!=BASIC.VOID)
				throw Problem.ofType(ProblemId.RETURN_WITHOUT_VALUE).at(returnStatement)
							.raiseUnrecoverable();
		}

	}
	
	@Override
	public void visit(ThrowStmtNode throwStatement) {
		throw new InternalProgramError(throwStatement, "throw not (yet?) supported!");
	}
	

	
	

}
