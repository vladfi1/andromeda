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

import com.sc2mod.andromeda.environment.Constructor;
import com.sc2mod.andromeda.environment.ConstructorInvocation;
import com.sc2mod.andromeda.environment.Destructor;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.Invocation;
import com.sc2mod.andromeda.environment.Scope;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.Visibility;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.Enrichment;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.SourceFileInfo;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.program.Program;
import com.sc2mod.andromeda.syntaxNodes.AccessorDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.ArrayCreationExprNode;
import com.sc2mod.andromeda.syntaxNodes.ArrayInitNode;
import com.sc2mod.andromeda.syntaxNodes.ArrayTypeNode;
import com.sc2mod.andromeda.syntaxNodes.AssignmentExprNode;
import com.sc2mod.andromeda.syntaxNodes.BinOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.BlockStmtNode;
import com.sc2mod.andromeda.syntaxNodes.BreakStmtNode;
import com.sc2mod.andromeda.syntaxNodes.CastExprNode;
import com.sc2mod.andromeda.syntaxNodes.MemberDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.NewExprNode;
import com.sc2mod.andromeda.syntaxNodes.ConditionalExprNode;
import com.sc2mod.andromeda.syntaxNodes.ContinueStmtNode;
import com.sc2mod.andromeda.syntaxNodes.DeleteStmtNode;
import com.sc2mod.andromeda.syntaxNodes.DoWhileStmtNode;
import com.sc2mod.andromeda.syntaxNodes.EmptyStmtNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ExplicitConsCallStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.ExprStmtNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureListNode;
import com.sc2mod.andromeda.syntaxNodes.ForEachStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ForStmtNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalFuncDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.IfStmtNode;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.InstanceLimitSetterNode;
import com.sc2mod.andromeda.syntaxNodes.InstanceofExprNode;
import com.sc2mod.andromeda.syntaxNodes.KeyOfExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;
import com.sc2mod.andromeda.syntaxNodes.LocalTypeDeclStmtNode;
import com.sc2mod.andromeda.syntaxNodes.LocalVarDeclStmtNode;
import com.sc2mod.andromeda.syntaxNodes.MetaClassExprNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocationExprNode;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExprNode;
import com.sc2mod.andromeda.syntaxNodes.ReturnStmtNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.StmtListNode;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.ThisExprNode;
import com.sc2mod.andromeda.syntaxNodes.ThrowStmtNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.VarAssignDeclNode;
import com.sc2mod.andromeda.syntaxNodes.UninitedVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.VisitorAdaptor;
import com.sc2mod.andromeda.syntaxNodes.WhileStmtNode;
import com.sc2mod.andromeda.vm.data.BoolObject;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.IntObject;

/**
 * The main visitor of the semantic analysis.
 * Checks many constraints, infers and checks types, calculates constant expressions,
 * Checks the control flow of a function for dead code.
 * @author J. 'gex' Finis 
 */
public class ExpressionAnalysisVisitor extends VisitorAdaptor {
	
	//Useful things :)
	NameResolver nameResolver;
	TypeProvider typeProvider;
	ConstantResolveVisitor constResolve;
	ExecutionPathStack execPathStack = new ExecutionPathStack();
	private Configuration options;
	private ExpressionAnalyzer exprResolver;

	
	public ExpressionAnalysisVisitor(ExpressionAnalyzer exprResolver, NameResolver nr, ConstantResolveVisitor constResolve, Environment env, Configuration options) {
		this.nameResolver = nr;
		this.typeProvider = env.typeProvider;
		this.constResolve = constResolve;
		this.exprResolver = exprResolver;
		this.options = options;
	}
	
	//current variables
	Scope curScope;
	RecordType curRecordType;
	Type curType;
	Function curFunction;
	LoopSemantics curLoop;
	int varDeclIndex;
	boolean inGlobalVarDecl;
	boolean isOnTop;
	private boolean inMember;
	
	//************** GLOBAL STRUCTURES *************
	
	/**
	 * Class/Struct/Interfaces:
	 * just set the curType to this one that it can be used when
	 * resolving invocations and field accesses.
	 */
	@Override
	public void visit(ClassDeclNode classDeclaration) {
		//Remember old type (in case we have nested classes)
		RecordType recordTypeBefore = curRecordType;
		Type typeBefore = curType;
		
		//Set current type
		curRecordType = (RecordType)classDeclaration.getSemantics();
		curType = curRecordType;
		
		//Instancelimit
		ExprNode instanceLimit = classDeclaration.getInstanceLimit();
		if(instanceLimit!=null){
			if(curRecordType.isStatic())
				throw Problem.ofType(ProblemId.STATIC_CLASS_HAS_INSTANCELIMIT).at(instanceLimit)
					.raiseUnrecoverable();
			instanceLimit.accept(this);
			if(instanceLimit.getInferedType()!=BasicType.INT)
				throw Problem.ofType(ProblemId.WRONGLY_TYPED_INSTANCELIMIT).at(instanceLimit).details(instanceLimit.getInferedType().getUid())
					.raiseUnrecoverable();
			DataObject val = instanceLimit.getValue();
			if(val == null)
				throw Problem.ofType(ProblemId.NONCONSTANT_INSTANCELIMIT).at(instanceLimit)
					.raiseUnrecoverable();
			int v = val.getIntValue();
			if(v < 0)
				throw Problem.ofType(ProblemId.NEGATIVE_INSTANCELIMIT).at(instanceLimit).details(v)
					.raiseUnrecoverable();
			if(v == 0)
				throw Problem.ofType(ProblemId.ZERO_INSTANCELIMIT).at(instanceLimit)
					.raiseUnrecoverable();
			((Class)curRecordType).setInstanceLimit(v);
		}
		
		//Process body
		boolean generic = curType.isGeneric();
		if(generic)typeProvider.pushTypeParams(curRecordType.getTypeParams());
		classDeclaration.getBody().accept(this);
		if(generic)typeProvider.popTypeParams(curRecordType.getTypeParams());
		
		//Reset to type before
		curRecordType = recordTypeBefore;	
		curType = typeBefore;
	}
	
	@Override
	public void visit(MemberDeclListNode classBody) {
		
		//Visit all class members
		classBody.childrenAccept(this);
	}
	
	@Override
	public void visit(GlobalFuncDeclNode functionDeclaration) {
		functionDeclaration.childrenAccept(this);
	}
	
	@Override
	public void visit(GlobalVarDeclNode globalVarDeclaration) {
		globalVarDeclaration.childrenAccept(this);
	}
	
	@Override
	public void visit(GlobalStaticInitDeclNode globalInitDeclaration) {
		globalInitDeclaration.getInitDecl().accept(this);
	}
	
	
	public void visit(StaticInitDeclNode d) {
		//Remember old function (in case we have nested functions or local classes inside a function)
		Function functionBefore = curFunction;
		LoopSemantics loopBefore = curLoop;
		
		//A function begins not in a loop
		curLoop = null;
		
		//We are on top of the function
		isOnTop = true;
		
		//Set current function
		curFunction = (Function)d.getSemantics();
		StmtNode body = d.getBody();
		
		//Analyze the body
		body.accept(this);
			
		//Get the local variable result
		curFunction.setLocals(nameResolver.methodFinished(0));

		//Check if the control flow reaches the end of the function
		curFunction.setFlowReachesEnd(!execPathStack.isTopFrameEmpty());
		
		//Pop the last frame from the method and check if the stack is empty
		execPathStack.popFrameAndDiscard();
		if(!execPathStack.isStackEmpty()){
			throw new InternalError(curFunction.getUid() + ": Execution path stack is not empty at the end of a function. Please contact gex!");
		}
		
		//Restore function before
		curFunction = functionBefore;
		curLoop = loopBefore;
	}
	
	
	@Override
	public void visit(EnrichDeclNode enrichDeclaration) {
		//Remember old type (in case we have nested classes)
		RecordType recordTypeBefore = curRecordType;
		Type typeBefore = curType;
		
		//Set current type
		curRecordType = (RecordType)enrichDeclaration.getSemantics();
		curType = ((Enrichment)curRecordType).getEnrichedType();
		
		//Process body
		enrichDeclaration.getBody().accept(this);
		
		//Reset to type before
		curRecordType = recordTypeBefore;	
		curType = typeBefore;
	}
	
	@Override
	public void visit(FieldDeclNode fieldDeclaration) {

		VarDeclListNode v = fieldDeclaration.getDeclaredVariables();
		boolean inGlobalVarBefore = inGlobalVarDecl;
		boolean inMemberBefore = inMember;
		VarDecl vd0 = ((VarDecl) v.elementAt(0).getSemantics());
		inGlobalVarDecl = vd0.isGlobalField();
		inMember = vd0.isMember();
		
		int size = v.size();
		for(int i=0;i<size;i++){
			VarDeclNode vd = v.elementAt(i);
			VarDecl decl = (VarDecl) vd.getName().getSemantics();
			varDeclIndex = decl.getIndex();
			vd.accept(this);
		}
		inGlobalVarDecl = inGlobalVarBefore;
		inMember = inMemberBefore;
	}
	
	@Override
	public void visit(UninitedVarDeclNode variableDecl) {
		VarDecl decl = (VarDecl)variableDecl.getName().getSemantics();
		
		//Constants must be defined at declaration
		if(decl.isConst()){
			throw Problem.ofType(ProblemId.CONST_VAR_NOT_INITED).at(variableDecl)
				.raiseUnrecoverable();
		}
		
		//The type for var decls is always correct
		variableDecl.setInferedType(((VarDecl)variableDecl.getName().getSemantics()).getType());
	}

	@Override
	public void visit(VarAssignDeclNode variableAssignDecl) {
		variableAssignDecl.getInitializer().accept(this);
		ExprNode init = variableAssignDecl.getInitializer();
		
		Type t = init.getInferedType();
		VarDecl decl = (VarDecl)variableAssignDecl.getName().getSemantics();
		variableAssignDecl.setSemantics(decl);
		variableAssignDecl.setInferedType(decl.getType());
		
		//Check type
		if(!t.canImplicitCastTo(decl.getType())){
			//For bytes, we might be able to cast if the value is a constant
			boolean error = true;
			if(decl.getType().getBaseType()==BasicType.BYTE){				
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
		if(decl.isConst()){
			if(!variableAssignDecl.getInitializer().getConstant())
				throw Problem.ofType(ProblemId.CONST_VAR_UNCONST_INIT).at(variableAssignDecl)
					.raiseUnrecoverable();
			variableAssignDecl.accept(constResolve);
		}
	}
	
	
	@Override
	public void visit(SourceFileNode andromedaFile) {
		//An included file brings a new scope
		Scope scopeBefore = curScope;
		curScope = andromedaFile.getScope();
		
		andromedaFile.childrenAccept(this);
		
		curScope = scopeBefore;
	}
	
	@Override
	public void visit(GlobalStructureListNode fileContent) {
		fileContent.childrenAccept(this);
	}
	
	@Override
	public void visit(IncludeNode includedFile) {		
		includedFile.childrenAccept(this);
	}
	
	@Override
	public void visit(InstanceLimitSetterNode instanceLimitSetter) {
		instanceLimitSetter.getInstanceLimit().accept(this);
	}
	
	private ConstructorInvocation resolveConstructorCall(SyntaxNode def, Class c, Signature sig, int maxVisibilityAllowed, boolean implicit, boolean useSuper){
		ArrayList<Class> wrappedFieldInits = null;
		
		if(useSuper){
			if(c.hasFieldInit()){
				wrappedFieldInits = new ArrayList<Class>(4);
				wrappedFieldInits.add(c);
			}
			c = c.getSuperClass();
			
		}
		
		if(c.hasConstructors()){
			//Class has explicit constructors
			Constructor con = c.resolveConstructorCall(def,sig, maxVisibilityAllowed);
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
		
		Class superClass = c;
		if(c.getSuperClass()!=null){
			
			
			while(true){
				//Superclass has no constructors
				if(!superClass.hasConstructors()){
					if(superClass.isTopClass()){
						//If the superclass is a topclass we call the allocator
						return new ConstructorInvocation(superClass,implicit,wrappedFieldInits);
					} else {
						//If it is no topclass we add the field init and try the next class
						if(superClass.hasFieldInit()){
							if(wrappedFieldInits==null)wrappedFieldInits = new ArrayList<Class>(4);
							wrappedFieldInits.add(superClass);
						}
						
						//Try next
						superClass = superClass.getSuperClass();
					}
				} else {
					//Superclass has constructors
					Constructor con = superClass.resolveConstructorCall(def,Signature.EMPTY_SIGNATURE, Visibility.PROTECTED);
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
		Class clazz = (Class) c.getContainingType();
		Class superClass = clazz.getSuperClass();
		
		StmtListNode stmts = body.getStatements();
			
		
		if(stmts.isEmpty()||!(stmts.elementAt(0) instanceof ExplicitConsCallStmtNode)){
			//No explicit constructor invocation
			
			if(superClass!=null){
				c.setInvokedConstructor(resolveConstructorCall(body, superClass,Signature.EMPTY_SIGNATURE,Visibility.PROTECTED, true,false));
			} else {
				c.setInvokedConstructor(new ConstructorInvocation(clazz, true, null));
			}

		} else {
			//Explicit constructor invocation
			ExplicitConsCallStmtNode explicitInvocation = (ExplicitConsCallStmtNode) stmts.elementAt(0);
			explicitInvocation.childrenAccept(this);
			boolean useSuper = explicitInvocation.getUseSuper();
			if(useSuper&&superClass==null)
				throw Problem.ofType(ProblemId.SUPER_CONSTRUCTOR_IN_TOP_CLASS).at(explicitInvocation)
							.raiseUnrecoverable();
 			
			//Resolve the constructor call
			
			ConstructorInvocation inv = resolveConstructorCall(explicitInvocation, clazz, new Signature(explicitInvocation.getArguments()), useSuper?Visibility.PROTECTED:Visibility.PRIVATE, false,useSuper);
			explicitInvocation.setSemantics(inv);
			c.setInvokedConstructor(inv);
		}
	}
	
	@Override
	public void visit(MethodDeclNode functionDeclaration) {
		
		//Get function body, if this function has none (abstract/inteface) we don't have to do anything
		StmtNode body = functionDeclaration.getBody();
		if(body == null||(!options.getParamBool(Parameter.TEST_CHECK_NATIVE_FUNCTION_BODIES)&&curScope.getInclusionType()==InclusionType.NATIVE)) return;
		
		//Remember old function (in case we have nested functions or local classes inside a function)
		Function functionBefore = curFunction;
		LoopSemantics loopBefore = curLoop;
		boolean inMemberBefore = inMember;
		
		
		//A function begins not in a loop
		curLoop = null;
		
		//We are on top of the function
		isOnTop = true;
		
		//Set current function
		curFunction = (Function)functionDeclaration.getSemantics();
		
		//Are we in a member (i.e. allowed to access member fields and functions)
		inMember = curFunction.isMember();
		
		//Register parameters as local variables
		for(VarDecl v: curFunction.getParams()){
			nameResolver.registerLocalVar(v);
		}
		
		//If this is a con-/destructor, do checks for it
		int functionType = curFunction.getFunctionType();
		switch(functionType){
		case Function.TYPE_CONSTRUCTOR:
			checkConstructor((Constructor)curFunction,body);
			break;
		case Function.TYPE_DESTRUCTOR:
			checkDestructor((Destructor)curFunction);
			break;
		}

		
		//Analyze the body
		body.accept(this);
			
		//Get the local variable result
		curFunction.setLocals(nameResolver.methodFinished(curFunction.getParams().length));

		//If this is a non void function, check if the exec path does not end without a return
		if(curFunction.getReturnType()!=SpecialType.VOID&&functionType!=Function.TYPE_CONSTRUCTOR){
			if(!execPathStack.isTopFrameEmpty())
				throw Problem.ofType(ProblemId.MISSING_RETURN).at(functionDeclaration)
						.raiseUnrecoverable();
		}
		//Check if the control flow reaches the end of the function
		curFunction.setFlowReachesEnd(!execPathStack.isTopFrameEmpty());
		
		//Pop the last frame from the method and check if the stack is empty
		execPathStack.popFrameAndDiscard();
		if(!execPathStack.isStackEmpty()){
			throw new InternalProgramError(functionDeclaration,"Execution path stack is not empty at the end of a function. Please file a bug report!");
		}
		
		//Restore function before
		curFunction = functionBefore;
		curLoop = loopBefore;
		inMember = inMemberBefore;
				
	}
	
	//************** MISC STRUCTURES **************
	@Override
	public void visit(ExprListNode expressionList) {
		expressionList.childrenAccept(this);
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
			stmt = statementList.elementAt(i);
			
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
				statementList.removeElementAt(i);
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
		//Do not allow a non block statement as body
		if(!(thenStmt instanceof BlockStmtNode)){
			doWhileStatement.setThenStatement(new BlockStmtNode(new StmtListNode(thenStmt)));
		}
		
		LoopSemantics loopBefore = curLoop;
		doWhileStatement.setSemantics(curLoop = new LoopSemantics());
		doWhileStatement.childrenAccept(this);		
		
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
		
		
		if(!doWhileStatement.getCondition().getInferedType().canImplicitCastTo(BasicType.BOOL))
			throw Problem.ofType(ProblemId.TYPE_ERROR_NONBOOL_LOOP_CONDITION).at(doWhileStatement.getCondition())
				.details(doWhileStatement.getCondition().getInferedType().getUid())
				.raiseUnrecoverable();
	
		curLoop = loopBefore;		
}
	
	@Override
	public void visit(WhileStmtNode whileStatement) {
		StmtNode thenStmt = whileStatement.getThenStatement();
		//Do not allow a non block statment as body
		if(!(thenStmt instanceof BlockStmtNode)){
			whileStatement.setThenStatement(new BlockStmtNode(new StmtListNode(thenStmt)));
		}
		
		//Body
		LoopSemantics loopBefore = curLoop;
		whileStatement.setSemantics(curLoop = new LoopSemantics());
		whileStatement.childrenAccept(this);		
		
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
		if(!whileStatement.getCondition().getInferedType().canImplicitCastTo(BasicType.BOOL))
			throw Problem.ofType(ProblemId.TYPE_ERROR_NONBOOL_LOOP_CONDITION).at(whileStatement.getCondition())
					.details(whileStatement.getCondition().getInferedType().getUid())
					.raiseUnrecoverable();
		
		curLoop = loopBefore;	
	}
	
	@Override
	public void visit(ExplicitConsCallStmtNode e) {
		e.childrenAccept(this);
		execPathStack.pushSingleStatementFrame(e);
	}
	
	@Override
	public void visit(ForEachStmtNode forEachStmt) {
		StmtNode thenStmt = forEachStmt.getThenStatement();
		//Do not allow a non block statement as body
		if(!(thenStmt instanceof BlockStmtNode)){
			forEachStmt.setThenStatement(new BlockStmtNode(new StmtListNode(thenStmt)));
		}
		
		//Body
		LoopSemantics loopBefore = curLoop;
		ForeachSemantics semantics;
		forEachStmt.setSemantics(curLoop = semantics = new ForeachSemantics());	
		//There is a var definition in the header, so create a new local block
		nameResolver.pushLocalBlock();		
		
		//Check init expression
		ExprNode iteree = forEachStmt.getExpression();
		iteree.accept(this);
		
		//Register local var for iterator
		Type iterVarType = typeProvider.resolveType(forEachStmt.getIteratorType());
		LocalVarDecl iterVarDecl = new LocalVarDecl(null, iterVarType, forEachStmt.getIterator(), false);
		nameResolver.registerLocalVar(iterVarDecl);
		semantics.setIterVarDecl(iterVarDecl);
		
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
		Type itereeType = iteree.getInferedType();
		
		
		switch(itereeType.getCategory()){
		case Type.ARRAY:
			
			
			throw new Error("array foreach not yet supported");
		case Type.GENERIC_CLASS://XPilot: added so that generic classes can be iterated
		case Type.BASIC:
		case Type.CLASS:
		case Type.INTERFACE:
			//Get iterator method
			Invocation iv;
			try {
				iv = nameResolver.resolvePrefixedFunctionCall(curScope, curRecordType, iteree, itereeType, Signature.EMPTY_SIGNATURE, "getIterator", false);
			} catch (CompilationError e) {
				Problem.ofType(ProblemId.FOREACH_NO_GET_ITERATOR_METHOD).at(forEachStmt)
					.details(itereeType.getUid())
					.raise();
			}
			semantics.setGetIterator(iv);
			
			//Get iterator type and check if it has all needed methods
			Type iteratorType = iv.getWhichFunction().getReturnType();
			
			// hasNext()
			try {
				iv = nameResolver.resolvePrefixedFunctionCall(curScope, curRecordType, iteree, iteratorType, Signature.EMPTY_SIGNATURE, "hasNext", false);
			} catch (CompilationError e) {
				Problem.ofType(ProblemId.FOREACH_NO_HAS_NEXT_METHOD).at(forEachStmt)
					.details(iteratorType)
					.raiseUnrecoverable();
			}
			if(iv.getWhichFunction().getReturnType()!=BasicType.BOOL){
				Problem.ofType(ProblemId.FOREACH_HAS_NEXT_DOES_NOT_RETURN_BOOL).at(forEachStmt)
					.details(iv.getWhichFunction().getReturnType().getUid())
					.raiseUnrecoverable();
			}
			semantics.setHasNext(iv);
			
			//next()
			try {
				iv = nameResolver.resolvePrefixedFunctionCall(curScope, curRecordType, iteree, iteratorType, Signature.EMPTY_SIGNATURE, "next", false);
			} catch (CompilationError e) {
				Problem.ofType(ProblemId.FOREACH_NO_NEXT_METHOD).at(forEachStmt)
					.details(iteratorType.getUid())
					.raiseUnrecoverable();
			}
			Type nextType = iv.getWhichFunction().getReturnType();			
			if(!nextType.canImplicitCastTo(iterVarType)){
				Problem.ofType(ProblemId.FOREACH_INCOMPATIBLE_ITERATION_TYPE).at(forEachStmt)
					.details(iv.getWhichFunction().getReturnType().getUid(),iterVarType.getUid())
					.raiseUnrecoverable();
			}
			semantics.setNext(iv);
			
			boolean destroyAfter = false;
			//destroy after foreach
			if(iteratorType instanceof Class){
				Class c = (Class)iteratorType;
				if(!c.hasAnnotation("KeepAfterForeach")){
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
		//Do not allow a non block statement as body
		if(!(thenStmt instanceof BlockStmtNode)){
			forStatement.setThenStatement(new BlockStmtNode(new StmtListNode(thenStmt)));
		}
		
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
		if(e!=null) e.accept(this);
		thenStmt.accept(this);
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
			cond.setInferedType(BasicType.BOOL);
		}
		if(!cond.getInferedType().canImplicitCastTo(BasicType.BOOL))
			Problem.ofType(ProblemId.TYPE_ERROR_NONBOOL_LOOP_CONDITION).at(forStatement.getCondition())
				.details(forStatement.getCondition().getInferedType().getUid())
				.raiseUnrecoverable();
		curLoop = loopBefore;
	}
	

	@Override
	public void visit(ExprStmtNode expressionStatement) {
		expressionStatement.childrenAccept(this);
		execPathStack.pushSingleStatementFrame(expressionStatement);
	}
	
	@Override
	public void visit(EmptyStmtNode emptyStatement) {
		execPathStack.pushSingleStatementFrame(emptyStatement);
	}
	
	@Override
	public void visit(DeleteStmtNode deleteStatement) {
		ExprNode expr = deleteStatement.getExpression();
		expr.accept(this);
		Type type = expr.getInferedType();
		//made generic class delete-able
		if(type.getCategory()!=Type.CLASS && type.getCategory()!=Type.GENERIC_CLASS){
			Problem.ofType(ProblemId.DELETE_NON_CLASS).at(expr)
				.details(type.getUid(),type.getDescription())
				.raiseUnrecoverable();
		}

		//Register destructor invocation
		Invocation in = nameResolver.registerDelete((Class)type,deleteStatement);
		deleteStatement.setSemantics(in);
		
		execPathStack.pushSingleStatementFrame(deleteStatement);
		
		
	}
	
	@Override
	public void visit(IfStmtNode ifThenElseStatement) {
		//Do not allow a non block statement as body, (in the else case possible if an ifthenelse follows)
		StmtNode thenStmt = ifThenElseStatement.getThenStatement();
		if(!(thenStmt instanceof BlockStmtNode)){
			ifThenElseStatement.setThenStatement(new BlockStmtNode(new StmtListNode(thenStmt)));
		}
		StmtNode elseStmt = ifThenElseStatement.getElseStatement();
		if(elseStmt != null){
			if(!(elseStmt instanceof BlockStmtNode)&&!(elseStmt instanceof IfStmtNode)){
				ifThenElseStatement.setElseStatement(new BlockStmtNode(new StmtListNode(elseStmt)));
			}				
		}
		
		//Infer condition
		ifThenElseStatement.getCondition().accept(this);
			
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
		Type t = ifThenElseStatement.getCondition().getInferedType();
		if(!t.canImplicitCastTo(BasicType.BOOL)&&!t.canBeNull())
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
		Type t = typeProvider.resolveType(l.getVarDeclaration().getType());
		
		//Register and init all variables in the correct order
		int size = decls.size();
		for(int i=0;i<size;i++){
			VarDeclNode decl = decls.elementAt(i);
			
			//Register the local var
			nameResolver.registerLocalVar(new LocalVarDecl(l.getVarDeclaration().getModifiers(),t, decl, isOnTop));
			
			//Check the init expression
			decl.accept(this);
			
		
		}
	}
	
	@Override
	public void visit(ReturnStmtNode returnStatement) {
		//Pushes an empty frame, since the following statement will have no predecessor!
		execPathStack.pushFrame();
		
		//Register
		curFunction.addReturnStmt(returnStatement);
		
		//For later use, we set the function as semantics of this return stmt
		returnStatement.setSemantics(curFunction);
		
		ExprNode result = returnStatement.getResult();
		
		if(result!=null){
			//Evaluate the expression
			returnStatement.childrenAccept(this);
			
			//Void function? No return expression allowed!
			if(curFunction.getReturnType()==SpecialType.VOID)
				throw Problem.ofType(ProblemId.RETURNING_VALUE_IN_VOID_METHOD).at(returnStatement)
							.raiseUnrecoverable();
				
			//Check if the return type is correct
			if(!result.getInferedType().canImplicitCastTo(curFunction.getReturnType())){
				throw Problem.ofType(ProblemId.TYPE_ERROR_INCOMPATIBLE_RETURN_TYPE).at(returnStatement)
							.details(curFunction.getReturnType().getFullName(),result.getInferedType().getFullName())
							.raiseUnrecoverable();
				
			}
		
		} else {
			//Void return
			if(curFunction.getReturnType()!=SpecialType.VOID)
				throw Problem.ofType(ProblemId.RETURN_WITHOUT_VALUE).at(returnStatement)
							.raiseUnrecoverable();
		}

	}
	
	@Override
	public void visit(ThrowStmtNode throwStatement) {
		throw new InternalProgramError(throwStatement, "throw not (yet?) supported!");
	}
	

	
	//************** EXPRESSIONS (infere types, do local var stuff, resolve invocations and field accesses) **************
	
	
	@Override
	public void visit(ArrayTypeNode arrayType) {
		arrayType.getDimensions().accept(this);
	}
	
	@Override
	public void visit(AssignmentExprNode assignment) {	
			
		//Visit left side as lValue
		ExprNode lExpr = assignment.getLeftExpression();
		lExpr.accept(this);
		ExprNode rExpr = assignment.getRightExpression();
		rExpr.accept(this);
		
		//Is the assignment type valid?
		if(!rExpr.getInferedType().canImplicitCastTo(lExpr.getInferedType()))
			throw Problem.ofType(ProblemId.TYPE_ERROR_INCOMPATIBLE_ASSIGNMENT).at(assignment)
							.details(rExpr.getInferedType(),lExpr.getInferedType())
							.raiseUnrecoverable();
		
		//The type of an assignment is the type of its left side
		assignment.setInferedType(lExpr.getInferedType());
		
		//Constants cannot be assigned
		if(lExpr.getConstant()){
			throw Problem.ofType(ProblemId.CONST_VAR_REASSIGNED).at(lExpr)
					.raiseUnrecoverable();
		}
	}
	
	@Override
	public void visit(UnOpExprNode unaryExpression){
		unaryExpression.childrenAccept(this);
		exprResolver.analyze(unaryExpression);	
	}
	
	@Override
	public void visit(BinOpExprNode binaryExpression) {
		//Visit children
		binaryExpression.childrenAccept(this);

		exprResolver.analyze(binaryExpression);
	}
	
	@Override
	public void visit(LiteralExprNode le){
		le.setSimple(true);
		LiteralNode l = le.getLiteral();
		int type = l.getType();
		
		//Literals are constant
		le.setConstant(true);
		
		switch(type){
		case LiteralTypeSE.BOOL:
			le.setInferedType(BasicType.BOOL);
			break;
		case LiteralTypeSE.STRING:
			le.setInferedType(BasicType.STRING);
			break;
		case LiteralTypeSE.INT:
			le.setInferedType(BasicType.INT);
			break;
		case LiteralTypeSE.CHAR:
			le.setInferedType(BasicType.CHAR);
			break;
		case LiteralTypeSE.FLOAT:
			le.setInferedType(BasicType.FLOAT);
			break;
		case LiteralTypeSE.NULL:
			le.setInferedType(SpecialType.NULL);
			break;
		case LiteralTypeSE.TEXT:
			le.setInferedType(BasicType.TEXT);
			break;
		default:
			throw new InternalProgramError(l,"Literal of unknown literal type!");
		}
		
		//Get constant value
		le.accept(constResolve);
	}
	
	@Override
	public void visit(CastExprNode castExpression) {
		//Get the type to cast to
		Type type = typeProvider.resolveType(castExpression.getType());
		
		//Infere right expression type
		castExpression.getRightExpression().accept(this);
		Type rightType = castExpression.getRightExpression().getInferedType();
		
		//Is cast possible
		if(rightType.canExplicitCastTo(type)){
			castExpression.setInferedType(type);
		} else {
			throw Problem.ofType(ProblemId.TYPE_ERROR_FORBIDDEN_CAST).at(castExpression)
						.details(rightType,type)
						.raiseUnrecoverable();
		}		
		
		//If the operand is constant, the value is constant
		if(castExpression.getRightExpression().getConstant()){
			castExpression.setConstant(true);
			castExpression.accept(constResolve);
		}
		
	}
	
	@Override
	public void visit(ArrayAccessExprNode arrayAccess) {
		arrayAccess.childrenAccept(this);
		//Check if an array access is possible
		Type t = arrayAccess.getLeftExpression().getInferedType();
		if(t.getCategory()!=Type.ARRAY){
			Problem.ofType(ProblemId.ARRAY_ACCESS_ON_NONARRAY).at(arrayAccess)
					.details(t.getUid())
					.raiseUnrecoverable();
		}
		
		//Type is the type of the array
		arrayAccess.setInferedType(t.getWrappedType());
		
		//The array access accesses the wrapped decl
		arrayAccess.setSemantics(arrayAccess.getLeftExpression().getSemantics());
		
	}
	
	@Override
	public void visit(ArrayCreationExprNode arrayCreationExpression) {
		throw new InternalProgramError(arrayCreationExpression,"Array creation is not yet possible!");
	}
	
	@Override
	public void visit(NewExprNode c) {
		c.childrenAccept(this);
		Type t;
		c.setInferedType(t = typeProvider.resolveType(c.getType()));
		
		
		switch (t.getCategory()) {
		case Type.GENERIC_CLASS:
		case Type.CLASS:
			Class cl = (Class) t;
			int maxVisibility = Visibility.PUBLIC;
			if (cl == curType)
				maxVisibility = Visibility.PRIVATE;
			else {
				if (curType instanceof Class)
					if (((Class) curType).isInstanceof(cl))
						maxVisibility = Visibility.PROTECTED;
			}
			ConstructorInvocation ci = resolveConstructorCall(c, cl,
					new Signature(c.getArguments()), maxVisibility, false,
					false);
			c.setSemantics(ci);
			break;
		default:
			throw Problem.ofType(ProblemId.NEW_NON_CLASS).at(c)
					.raiseUnrecoverable();
		}

	}
	
	@Override
	public void visit(ConditionalExprNode conditionalExpression) {
		//Infere subexpression types
		conditionalExpression.childrenAccept(this);
		
		//Check type validity
		if (conditionalExpression.getLeftExpression().getInferedType() != BasicType.BOOL) {
			throw Problem.ofType(ProblemId.TYPE_ERROR_NONBOOL_CONDITIONAL).at(conditionalExpression.getLeftExpression())
							.details(conditionalExpression.getLeftExpression().getInferedType())
							.raiseUnrecoverable();
		}
		Type type1 = conditionalExpression.getRightExpression().getInferedType();
		Type type2 = conditionalExpression.getRightExpression2().getInferedType();
		if(!type2.canImplicitCastTo(type1)){
			throw Problem.ofType(ProblemId.TYPE_ERROR_INCOMPATIBLE_CONDITIONAL).at(conditionalExpression)
				.details(type1,type2)
				.raiseUnrecoverable();
		}
		conditionalExpression.setInferedType(type1);
	}
	
	@Override
	public void visit(FieldAccessExprNode fieldAccess) {
		fieldAccess.childrenAccept(this);
		
		VarDecl va = nameResolver.resolveVariable(curScope,curRecordType,fieldAccess, inMember);
		Type type = va.getType();
		fieldAccess.setInferedType(type);
		fieldAccess.setSemantics(va);
		
		//Is it simple?
		fieldAccess.setSimple(SimplicityDecider.isSimple(fieldAccess));
		
		
		//In global declaration? If so, test order
		if(inGlobalVarDecl){
			if(va.getIndex()>=varDeclIndex)
				throw Problem.ofType(ProblemId.GLOBAL_VAR_ACCESS_BEFORE_DECL).at(fieldAccess)
						.raiseUnrecoverable();
		}
		
		//Field accesses are correct lValues
		fieldAccess.setLValue(true);
		
		//Is it const?
		if(va.isConst()){
			fieldAccess.setConstant(true);
			fieldAccess.accept(constResolve);
		}
	}
	
	@Override
	public void visit(KeyOfExprNode keyOfExpression) {
		Type t = typeProvider.resolveType(keyOfExpression.getType());
		keyOfExpression.setInferedType(t);
		keyOfExpression.setConstant(true);
		keyOfExpression.accept(constResolve);
	}
	
	@Override
	public void visit(AccessorDeclNode accessorDeclaration) {
		accessorDeclaration.childrenAccept(this);
	}
	
	@Override
	public void visit(InstanceofExprNode instanceofExpression) {
		throw new InternalProgramError(instanceofExpression,"Instanceof not implemented yet!");
	}
	
	@Override
	public void visit(MetaClassExprNode metaClassExpression) {
		throw new InternalProgramError(metaClassExpression,".class not implemented yet!");
	}
	
	@Override
	public void visit(MethodInvocationExprNode methodInvocation) {
		
		methodInvocation.childrenAccept(this);
		Invocation in = nameResolver.resolveFunctionCall(curScope,curRecordType,methodInvocation,inMember);
		Type type = in.getWhichFunction().getReturnType();
		methodInvocation.setSemantics(in);
		methodInvocation.setInferedType(type);
	}
	
	@Override
	public void visit(ThisExprNode thisExpression) {
		if(curType==null) 
			throw Problem.ofType(ProblemId.THIS_OUTSIDE_CLASS_OR_ENRICHMENT).at(thisExpression)
					.raiseUnrecoverable();
		if(curFunction.isStatic())
			throw Problem.ofType(ProblemId.THIS_IN_STATIC_MEMBER).at(thisExpression)
					.raiseUnrecoverable();
		thisExpression.setInferedType(curType);
		thisExpression.setSimple(true);
	}
	
	@Override
	public void visit(ParenthesisExprNode parenthesisExpression) {
		ExprNode e = parenthesisExpression.getExpression();
		e.accept(this);
		parenthesisExpression.setInferedType(e.getInferedType());
		
		if(e.getConstant()){
			parenthesisExpression.setConstant(true);
			parenthesisExpression.setValue(e.getValue());
		}
	}

}
