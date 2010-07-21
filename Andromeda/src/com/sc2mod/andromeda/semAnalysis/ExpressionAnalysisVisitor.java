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
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.parsing.AndromedaFileInfo;
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.program.Program;
import com.sc2mod.andromeda.syntaxNodes.AccessorDeclaration;
import com.sc2mod.andromeda.syntaxNodes.AndromedaFile;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccess;
import com.sc2mod.andromeda.syntaxNodes.ArrayCreationExpression;
import com.sc2mod.andromeda.syntaxNodes.ArrayInitializer;
import com.sc2mod.andromeda.syntaxNodes.ArrayType;
import com.sc2mod.andromeda.syntaxNodes.Assignment;
import com.sc2mod.andromeda.syntaxNodes.BinaryExpression;
import com.sc2mod.andromeda.syntaxNodes.BlockStatement;
import com.sc2mod.andromeda.syntaxNodes.BreakStatement;
import com.sc2mod.andromeda.syntaxNodes.CastExpression;
import com.sc2mod.andromeda.syntaxNodes.ClassBody;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclaration;
import com.sc2mod.andromeda.syntaxNodes.ClassInstanceCreationExpression;
import com.sc2mod.andromeda.syntaxNodes.ConditionalExpression;
import com.sc2mod.andromeda.syntaxNodes.ContinueStatement;
import com.sc2mod.andromeda.syntaxNodes.DeleteStatement;
import com.sc2mod.andromeda.syntaxNodes.DoWhileStatement;
import com.sc2mod.andromeda.syntaxNodes.EmptyStatement;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclaration;
import com.sc2mod.andromeda.syntaxNodes.ExplicitConstructorInvocationStatement;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.ExpressionList;
import com.sc2mod.andromeda.syntaxNodes.ExpressionStatement;
import com.sc2mod.andromeda.syntaxNodes.FieldAccess;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclaration;
import com.sc2mod.andromeda.syntaxNodes.FileContent;
import com.sc2mod.andromeda.syntaxNodes.ForEachStatement;
import com.sc2mod.andromeda.syntaxNodes.ForStatement;
import com.sc2mod.andromeda.syntaxNodes.FunctionDeclaration;
import com.sc2mod.andromeda.syntaxNodes.GlobalInitDeclaration;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclaration;
import com.sc2mod.andromeda.syntaxNodes.IfThenElseStatement;
import com.sc2mod.andromeda.syntaxNodes.IncludedFile;
import com.sc2mod.andromeda.syntaxNodes.InstanceLimitSetter;
import com.sc2mod.andromeda.syntaxNodes.InstanceofExpression;
import com.sc2mod.andromeda.syntaxNodes.KeyOfExpression;
import com.sc2mod.andromeda.syntaxNodes.Literal;
import com.sc2mod.andromeda.syntaxNodes.LiteralExpression;
import com.sc2mod.andromeda.syntaxNodes.LiteralType;
import com.sc2mod.andromeda.syntaxNodes.LocalTypeDeclarationStatement;
import com.sc2mod.andromeda.syntaxNodes.LocalVariableDeclarationStatement;
import com.sc2mod.andromeda.syntaxNodes.MetaClassExpression;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclaration;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocation;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExpression;
import com.sc2mod.andromeda.syntaxNodes.ReturnStatement;
import com.sc2mod.andromeda.syntaxNodes.Statement;
import com.sc2mod.andromeda.syntaxNodes.StatementList;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclaration;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.ThisExpression;
import com.sc2mod.andromeda.syntaxNodes.ThrowStatement;
import com.sc2mod.andromeda.syntaxNodes.UnaryExpression;
import com.sc2mod.andromeda.syntaxNodes.VariableAssignDecl;
import com.sc2mod.andromeda.syntaxNodes.VariableDecl;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclarator;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclarators;
import com.sc2mod.andromeda.syntaxNodes.VisitorAdaptor;
import com.sc2mod.andromeda.syntaxNodes.WhileStatement;
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
	private Options options;
	private ExpressionAnalyzer exprResolver;

	
	public ExpressionAnalysisVisitor(ExpressionAnalyzer exprResolver, NameResolver nr, ConstantResolveVisitor constResolve, Environment env, Options options) {
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
	public void visit(ClassDeclaration classDeclaration) {
		//Remember old type (in case we have nested classes)
		RecordType recordTypeBefore = curRecordType;
		Type typeBefore = curType;
		
		//Set current type
		curRecordType = (RecordType)classDeclaration.getSemantics();
		curType = curRecordType;
		
		//Instancelimit
		Expression instanceLimit = classDeclaration.getInstanceLimit();
		if(instanceLimit!=null){
			if(curRecordType.isStatic())
				throw new CompilationError(instanceLimit,"Static classes may not specify an instance limit (they have no instances).");
			instanceLimit.accept(this);
			if(instanceLimit.getInferedType()!=BasicType.INT)
				throw new CompilationError(instanceLimit,"The instancelimit of a class must be of type int, but it is " + instanceLimit.getInferedType().getUid());
			DataObject val = instanceLimit.getValue();
			if(val == null)
				throw new CompilationError(instanceLimit, "Could not determine the instancelimit. It must be constant, so it can be determined during compilation.");
			int v = val.getIntValue();
			if(v < 0)
				throw new CompilationError(instanceLimit,"Negative instancelimit (" + v + ").");
			if(v == 0)
				throw new CompilationError(instanceLimit,"Zero instancelimit. If you want a class without any instance, declare the class 'util'.");
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
	public void visit(ClassBody classBody) {
		
		//Visit all class members
		classBody.childrenAccept(this);
	}
	
	@Override
	public void visit(FunctionDeclaration functionDeclaration) {
		functionDeclaration.childrenAccept(this);
	}
	
	@Override
	public void visit(GlobalVarDeclaration globalVarDeclaration) {
		globalVarDeclaration.childrenAccept(this);
	}
	
	@Override
	public void visit(GlobalInitDeclaration globalInitDeclaration) {
		globalInitDeclaration.getInitDecl().accept(this);
	}
	
	
	public void visit(StaticInitDeclaration d) {
		//Remember old function (in case we have nested functions or local classes inside a function)
		Function functionBefore = curFunction;
		LoopSemantics loopBefore = curLoop;
		
		//A function begins not in a loop
		curLoop = null;
		
		//We are on top of the function
		isOnTop = true;
		
		//Set current function
		curFunction = (Function)d.getSemantics();
		Statement body = d.getBody();
		
		//Analyze the body
		body.accept(this);
			
		//Get the local variable result
		curFunction.setLocals(nameResolver.methodFinished(0));

		//Check if the control flow reaches the end of the function
		curFunction.setFlowReachesEnd(!execPathStack.isTopFrameEmpty());
		
		//Pop the last frame from the method and check if the stack is empty
		execPathStack.popFrameAndDiscard();
		if(!execPathStack.isStackEmpty()){
			throw new CompilationError(d,"Execution path stack is not empty at the end of a function. Please contact gex!");
		}
		
		//Restore function before
		curFunction = functionBefore;
		curLoop = loopBefore;
	}
	
	
	@Override
	public void visit(EnrichDeclaration enrichDeclaration) {
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
	public void visit(FieldDeclaration fieldDeclaration) {

		VariableDeclarators v = fieldDeclaration.getDeclaredVariables();
		boolean inGlobalVarBefore = inGlobalVarDecl;
		boolean inMemberBefore = inMember;
		VarDecl vd0 = ((VarDecl) v.elementAt(0).getSemantics());
		inGlobalVarDecl = vd0.isGlobalField();
		inMember = vd0.isMember();
		
		int size = v.size();
		for(int i=0;i<size;i++){
			VariableDeclarator vd = v.elementAt(i);
			VarDecl decl = (VarDecl) vd.getName().getSemantics();
			varDeclIndex = decl.getIndex();
			vd.accept(this);
		}
		inGlobalVarDecl = inGlobalVarBefore;
		inMember = inMemberBefore;
	}
	
	@Override
	public void visit(VariableDecl variableDecl) {
		VarDecl decl = (VarDecl)variableDecl.getName().getSemantics();
		
		//Constants must be defined at declaration
		if(decl.isConst()){
			throw new CompilationError(variableDecl,"Constant variables must be initialized in their declaration.");
		}
		
		//The type for var decls is always correct
		variableDecl.setInferedType(((VarDecl)variableDecl.getName().getSemantics()).getType());
	}

	@Override
	public void visit(VariableAssignDecl variableAssignDecl) {
		variableAssignDecl.getInitializer().accept(this);
		Expression init = variableAssignDecl.getInitializer();
		
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
				throw new CompilationError(variableAssignDecl,"Cannot assign a value of type " + t.getFullName()
					+ " to a variable of type " + decl.getType().getFullName());
		}
			
		//Constants must be initialized with constant values
		if(decl.isConst()){
			if(!variableAssignDecl.getInitializer().getConstant())
				throw new CompilationError(variableAssignDecl,"Constant variables must be initialized with constant values.");
			variableAssignDecl.accept(constResolve);
		}
	}
	
	
	@Override
	public void visit(AndromedaFile andromedaFile) {
		//An included file brings a new scope
		Scope scopeBefore = curScope;
		curScope = andromedaFile.getScope();
		
		andromedaFile.childrenAccept(this);
		
		curScope = scopeBefore;
	}
	
	@Override
	public void visit(FileContent fileContent) {
		fileContent.childrenAccept(this);
	}
	
	@Override
	public void visit(IncludedFile includedFile) {		
		includedFile.childrenAccept(this);
	}
	
	@Override
	public void visit(InstanceLimitSetter instanceLimitSetter) {
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
				throw new CompilationError(CompilationError.CODE_NO_CONSTRUCTOR_WITH_THAT_SIGNATURE, def,"The class " + c.getFullName() + " specifies no constructor with signature (" + sig.getFullName() + ").");
			return new ConstructorInvocation(con, implicit, wrappedFieldInits);
		}
		
		//Class has no explicit constructors
		if(sig.size()>0)
			throw new CompilationError(CompilationError.CODE_ONLY_IMPLICIT_CONSTRUCTOR_AVAILABLE,def,"The class " + c.getFullName() + " specifies no constructors, so it can be only invoked with no arguments (implicit default constructor).");
		
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
						throw new CompilationError(CompilationError.IMPLICIT_CONSTRUCTOR_INVOCATION_IMPOSSIBLE,def,"The super class " + superClass.getName() + " specifies no parameterless constructor, so no implicit super constructor invocation is possible.\n"
													+ "Please specify an explicit constructor invocation.");
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
	
	private void checkConstructor(Constructor c, Statement body){
		Class clazz = (Class) c.getContainingType();
		Class superClass = clazz.getSuperClass();
		
		StatementList stmts = body.getStatements();
			
		
		if(stmts.isEmpty()||!(stmts.elementAt(0) instanceof ExplicitConstructorInvocationStatement)){
			//No explicit constructor invocation
			
			if(superClass!=null){
				c.setInvokedConstructor(resolveConstructorCall(body, superClass,Signature.EMPTY_SIGNATURE,Visibility.PROTECTED, true,false));
			} else {
				c.setInvokedConstructor(new ConstructorInvocation(clazz, true, null));
			}

		} else {
			//Explicit constructor invocation
			ExplicitConstructorInvocationStatement explicitInvocation = (ExplicitConstructorInvocationStatement) stmts.elementAt(0);
			explicitInvocation.childrenAccept(this);
			boolean useSuper = explicitInvocation.getUseSuper();
			if(useSuper&&superClass==null)
				throw new CompilationError(explicitInvocation,"Cannot invoke a super constructor, because this class has no super class.");
			
			//Resolve the constructor call
			
			ConstructorInvocation inv = resolveConstructorCall(explicitInvocation, clazz, new Signature(explicitInvocation.getArguments()), useSuper?Visibility.PROTECTED:Visibility.PRIVATE, false,useSuper);
			explicitInvocation.setSemantics(inv);
			c.setInvokedConstructor(inv);
		}
	}
	
	@Override
	public void visit(MethodDeclaration functionDeclaration) {
		
		//Get function body, if this function has none (abstract/inteface) we don't have to do anything
		Statement body = functionDeclaration.getBody();
		if(body == null||(!options.checkNativeFunctionBodies&&curScope.getInclusionType()==AndromedaFileInfo.TYPE_NATIVE)) return;
		
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
				throw new CompilationError(functionDeclaration,"Missing return. (the control flow of non-void functions may not reach the end of the function body)");
		}
		//Check if the control flow reaches the end of the function
		curFunction.setFlowReachesEnd(!execPathStack.isTopFrameEmpty());
		
		//Pop the last frame from the method and check if the stack is empty
		execPathStack.popFrameAndDiscard();
		if(!execPathStack.isStackEmpty()){
			throw new CompilationError(functionDeclaration,"Execution path stack is not empty at the end of a function. Please contact gex!");
		}
		
		//Restore function before
		curFunction = functionBefore;
		curLoop = loopBefore;
		inMember = inMemberBefore;
				
	}
	
	//************** MISC STRUCTURES **************
	@Override
	public void visit(ExpressionList expressionList) {
		expressionList.childrenAccept(this);
	}
	
	@Override
	public void visit(ArrayInitializer arrayInitializer) {
		throw new CompilationError(arrayInitializer,"Array initializers not yet supported!");
	}
	
	//************** STATEMENTS (walk through, check blocks, return types, ...) **************
	@Override
	public void visit(StatementList statementList) {
		int size = statementList.size();
		Statement stmt = null;
		boolean deadCode = false;
		boolean remove = false;
		for(int i=0;i<size;i++){
			stmt = statementList.elementAt(i);
			
			//Top frame empty? Dead code!
			if(execPathStack.isTopFrameEmpty()&&!deadCode){
				switch(options.handleDeadCode){
				case Options.EXCEPTION_ERROR:
					throw new CompilationError(stmt, "Unreachable code.");
				case Options.EXCEPTION_WARNING:
					Program.log.warning(stmt, "Unreachable code.");
					break;
				case Options.EXCEPTION_REMOVE:
					remove = true;
					break;
				case Options.EXCEPTION_IGNORE: 
				default:
				}
				deadCode = true;
			}
			
			//Link statements for the exec path
			execPathStack.popFrameAndConnect(stmt);			
			
			//Check if on top
			if(isOnTop){
				if(!(stmt instanceof LocalVariableDeclarationStatement)){
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
	public void visit(BlockStatement blockStatement) {
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
	public void visit(BreakStatement breakStatement) {
		//Pushes an empty frame, since the following statement will have no predecessor!
		execPathStack.pushFrame();
		
		//Check if we are in a loop
		if(curLoop==null) throw new CompilationError(breakStatement, "Found 'break' outside of loop.");
		
		//Add break
		curLoop.addBreak(breakStatement);
	}
	
	@Override
	public void visit(ContinueStatement continueStatement) {
		//Pushes an empty frame, since the following statement will have no predecessor!
		execPathStack.pushFrame();
		
		//Check if we are in a loop
		if(curLoop==null) throw new CompilationError(continueStatement, "Found 'continue' outside of loop.");
		
		//Set the use continue flag
		curLoop.addContinue(continueStatement);
	}
	
	@Override
	public void visit(DoWhileStatement doWhileStatement) {
		Statement thenStmt = doWhileStatement.getThenStatement();
		//Do not allow a non block statement as body
		if(!(thenStmt instanceof BlockStatement)){
			doWhileStatement.setThenStatement(new BlockStatement(new StatementList(thenStmt)));
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
		for(Statement s: curLoop.getContinues()) s.setSuccessor(eolStmt);
		//All break statements are added onto the stack, so they get bound to the next stmt
		for(Statement s: curLoop.getBreaks()) execPathStack.pushStatement(s);
		
		
		if(!doWhileStatement.getCondition().getInferedType().canImplicitCastTo(BasicType.BOOL))
			throw new CompilationError(doWhileStatement.getCondition(),"The condition of a while loop must be of type bool, but it is of type " + doWhileStatement.getCondition().getInferedType().getUid());

		curLoop = loopBefore;		
}
	
	@Override
	public void visit(WhileStatement whileStatement) {
		Statement thenStmt = whileStatement.getThenStatement();
		//Do not allow a non block statment as body
		if(!(thenStmt instanceof BlockStatement)){
			whileStatement.setThenStatement(new BlockStatement(new StatementList(thenStmt)));
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
		for(Statement s: curLoop.getContinues()) s.setSuccessor(whileStatement);
		//All break statements are added onto the stack, so they get bound to the next stmt
		for(Statement s: curLoop.getBreaks()) execPathStack.pushStatement(s);

		//Check condition type
		if(!whileStatement.getCondition().getInferedType().canImplicitCastTo(BasicType.BOOL))
			throw new CompilationError(whileStatement.getCondition(),"The condition of a while loop must be of type bool, but it is of type " + whileStatement.getCondition().getInferedType().getUid());

		curLoop = loopBefore;	
	}
	
	@Override
	public void visit(ExplicitConstructorInvocationStatement e) {
		e.childrenAccept(this);
		execPathStack.pushSingleStatementFrame(e);
	}
	
	@Override
	public void visit(ForEachStatement forEachStmt) {
		Statement thenStmt = forEachStmt.getThenStatement();
		//Do not allow a non block statement as body
		if(!(thenStmt instanceof BlockStatement)){
			forEachStmt.setThenStatement(new BlockStatement(new StatementList(thenStmt)));
		}
		
		//Body
		LoopSemantics loopBefore = curLoop;
		ForeachSemantics semantics;
		forEachStmt.setSemantics(curLoop = semantics = new ForeachSemantics());	
		//There is a var definition in the header, so create a new local block
		nameResolver.pushLocalBlock();		
		
		//Check init expression
		Expression iteree = forEachStmt.getExpression();
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
		for(Statement s: curLoop.getContinues()) s.setSuccessor(forEachStmt);
		//All break statements are added onto the stack, so they get bound to the next stmt
		for(Statement s: curLoop.getBreaks()) execPathStack.pushStatement(s);


		
		//Check iter type
		Type itereeType = iteree.getInferedType();
		
		
		switch(itereeType.getCategory()){
		case Type.ARRAY:
			
			
			throw new Error("array foreach not yet supported");
			
		case Type.BASIC:
		case Type.CLASS:
		case Type.INTERFACE:
			//Get iterator method
			Invocation iv;
			try {
				iv = nameResolver.resolvePrefixedFunctionCall(curScope, curRecordType, iteree, itereeType, Signature.EMPTY_SIGNATURE, "getIterator", false);
			} catch (CompilationError e) {
				throw new CompilationError("The expression over which a for each loop iterates must provide an accessible method .getIterator(). No such method found for type " + itereeType.getUid());
			}
			semantics.setGetIterator(iv);
			
			//Get iterator type and check if it has all needed methods
			Type iteratorType = iv.getWhichFunction().getReturnType();
			
			// hasNext()
			try {
				iv = nameResolver.resolvePrefixedFunctionCall(curScope, curRecordType, iteree, iteratorType, Signature.EMPTY_SIGNATURE, "hasNext", false);
			} catch (CompilationError e) {
				throw new CompilationError("The iterator returned by the getIterator() method in a for each loop must provide the method bool hasNext().\nThis method is not provided by the iterator type  " + iteratorType.getUid());
			}
			if(iv.getWhichFunction().getReturnType()!=BasicType.BOOL){
				throw new CompilationError("The hasNext() method of the iterator in a for each loop must return a value of type 'bool' but it returns a value of type " + iv.getWhichFunction().getReturnType().getUid() );		
			}
			semantics.setHasNext(iv);
			
			//next()
			try {
				iv = nameResolver.resolvePrefixedFunctionCall(curScope, curRecordType, iteree, iteratorType, Signature.EMPTY_SIGNATURE, "next", false);
			} catch (CompilationError e) {
				throw new CompilationError("The iterator returned by the getIterator() method in a for each loop must provide the method next().\nThis method is not provided by the iterator type  " + iteratorType.getUid());
			}
			Type nextType = iv.getWhichFunction().getReturnType();			
			if(!nextType.canImplicitCastTo(iterVarType)){
				throw new CompilationError("The return type of the next() method of the iterator in a for each loop must be compatible with the type of the iterating variable, but it isn't. next() return type: " + iv.getWhichFunction().getReturnType().getUid() 
											+ "\niterating variable type: " + iterVarType.getUid());		
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
	public void visit(ForStatement forStatement) {
		Statement thenStmt = forStatement.getThenStatement();
		//Do not allow a non block statement as body
		if(!(thenStmt instanceof BlockStatement)){
			forStatement.setThenStatement(new BlockStatement(new StatementList(thenStmt)));
		}
		
		//Body
		LoopSemantics loopBefore = curLoop;
		forStatement.setSemantics(curLoop = new LoopSemantics());	
		//There could be a var definition in the header, so create a new local block
		nameResolver.pushLocalBlock();		
		
		//Since the init can contain statements, we have to cut those statements from the execution path
		int execPathFrame = execPathStack.getCurFrame();
		Statement s = forStatement.getForInit();
		if(s!=null) s.accept(this);
		execPathStack.cutToFrame(execPathFrame);
		//The rest can be processed normally
		Expression e = forStatement.getCondition();
		if(e!=null) e.accept(this);
		thenStmt.accept(this);
		Statement el = forStatement.getForUpdate();
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
		for(Statement ss: curLoop.getContinues()) ss.setSuccessor(eolStmt);
		//All break statements are added onto the stack, so they get bound to the next stmt
		for(Statement ss: curLoop.getBreaks()) execPathStack.pushStatement(ss);
			
		//Check condition type
		Expression cond = forStatement.getCondition();
		if(cond == null){
			forStatement.setCondition(cond = new LiteralExpression(new Literal(BoolObject.getBool(true), LiteralType.BOOL)));
			cond.setInferedType(BasicType.BOOL);
		}
		if(!cond.getInferedType().canImplicitCastTo(BasicType.BOOL))
			throw new CompilationError(forStatement.getCondition(),"The condition of a for loop must be of type bool, but it is of type " + forStatement.getCondition().getInferedType().getUid());

		curLoop = loopBefore;
	}
	

	@Override
	public void visit(ExpressionStatement expressionStatement) {
		expressionStatement.childrenAccept(this);
		execPathStack.pushSingleStatementFrame(expressionStatement);
	}
	
	@Override
	public void visit(EmptyStatement emptyStatement) {
		execPathStack.pushSingleStatementFrame(emptyStatement);
	}
	
	@Override
	public void visit(DeleteStatement deleteStatement) {
		Expression expr = deleteStatement.getExpression();
		expr.accept(this);
		Type type = expr.getInferedType();
		//made generic class delete-able
		if(type.getCategory()!=Type.CLASS && type.getCategory()!=Type.GENERIC_CLASS){
			throw new CompilationError(expr, "The argument of a delete statement must be a class, but it is " 
										+ type.getUid() + " (" + type.getDescription() + ").");
		}

		//Register destructor invocation
		Invocation in = nameResolver.registerDelete((Class)type,deleteStatement);
		deleteStatement.setSemantics(in);
		
		execPathStack.pushSingleStatementFrame(deleteStatement);
		
		
	}
	
	@Override
	public void visit(IfThenElseStatement ifThenElseStatement) {
		//Do not allow a non block statement as body, (in the else case possible if an ifthenelse follows)
		Statement thenStmt = ifThenElseStatement.getThenStatement();
		if(!(thenStmt instanceof BlockStatement)){
			ifThenElseStatement.setThenStatement(new BlockStatement(new StatementList(thenStmt)));
		}
		Statement elseStmt = ifThenElseStatement.getElseStatement();
		if(elseStmt != null){
			if(!(elseStmt instanceof BlockStatement)&&!(elseStmt instanceof IfThenElseStatement)){
				ifThenElseStatement.setElseStatement(new BlockStatement(new StatementList(elseStmt)));
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
			throw new CompilationError(ifThenElseStatement.getCondition(),"The condition of an if block must be of type bool or of a referential type, but it is of type " + ifThenElseStatement.getCondition().getInferedType().getUid());

		
	}
	
	@Override
	public void visit(
			LocalTypeDeclarationStatement localTypeDeclarationStatement) {
		execPathStack.pushSingleStatementFrame(localTypeDeclarationStatement);
		throw new CompilationError(localTypeDeclarationStatement,"Local type declarations not yet supported!");
	}
	
	@Override
	public void visit(LocalVariableDeclarationStatement l) {
		execPathStack.pushSingleStatementFrame(l);
		
		//Get and resolve local var type
		l.getVarDeclaration().getType().accept(this);
		
		VariableDeclarators decls = l.getVarDeclaration().getDeclarators();
		Type t = typeProvider.resolveType(l.getVarDeclaration().getType());
		
		//Register and init all variables in the correct order
		int size = decls.size();
		for(int i=0;i<size;i++){
			VariableDeclarator decl = decls.elementAt(i);
			
			//Register the local var
			nameResolver.registerLocalVar(new LocalVarDecl(l.getVarDeclaration().getModifiers(),t, decl, isOnTop));
			
			//Check the init expression
			decl.accept(this);
			
		
		}
	}
	
	@Override
	public void visit(ReturnStatement returnStatement) {
		//Pushes an empty frame, since the following statement will have no predecessor!
		execPathStack.pushFrame();
		
		//Register
		curFunction.addReturnStmt(returnStatement);
		
		//For later use, we set the function as semantics of this return stmt
		returnStatement.setSemantics(curFunction);
		
		Expression result = returnStatement.getResult();
		
		if(result!=null){
			//Evaluate the expression
			returnStatement.childrenAccept(this);
			
			//Void function? No return expression allowed!
			if(curFunction.getReturnType()==SpecialType.VOID)
				throw new CompilationError(returnStatement, "Returning a value in a function that should return 'void'.");
				
			//Check if the return type is correct
			if(!result.getInferedType().canImplicitCastTo(curFunction.getReturnType())){
				throw new CompilationError(returnStatement,
						"Return type of function not compatible with the returned type.\nReturn type: "
								+ curFunction.getReturnType().getFullName()
								+ "\nReturned type: " + result.getInferedType().getFullName());
			}
		
		} else {
			//Void return
			if(curFunction.getReturnType()!=SpecialType.VOID)
				throw new CompilationError(returnStatement, "Returning no value is only allowed for 'void' functions.");
		}

	}
	
	@Override
	public void visit(ThrowStatement throwStatement) {
		throw new CompilationError(throwStatement, "throw not yet supported!");
	}
	

	
	//************** EXPRESSIONS (infere types, do local var stuff, resolve invocations and field accesses) **************
	
	
	@Override
	public void visit(ArrayType arrayType) {
		arrayType.getDimensions().accept(this);
	}
	
	@Override
	public void visit(Assignment assignment) {	
			
		//Visit left side as lValue
		Expression lExpr = assignment.getLeftExpression();
		lExpr.accept(this);
		Expression rExpr = assignment.getRightExpression();
		rExpr.accept(this);
		
		//Is the assignment type valid?
		if(!rExpr.getInferedType().canImplicitCastTo(lExpr.getInferedType()))
			throw new CompilationError(assignment,"Cannot assign a value of type " + rExpr.getInferedType().getFullName()
												+ " to a variable of type " + lExpr.getInferedType().getFullName());
		
		//The type of an assignment is the type of its left side
		assignment.setInferedType(lExpr.getInferedType());
		
		//Constants cannot be assigned
		if(lExpr.getConstant()){
			throw new CompilationError(lExpr, "Cannot assign to this variable, because it is constant!");
		}
	}
	
	@Override
	public void visit(UnaryExpression unaryExpression){
		unaryExpression.childrenAccept(this);
		exprResolver.analyze(unaryExpression);	
	}
	
	@Override
	public void visit(BinaryExpression binaryExpression) {
		//Visit children
		binaryExpression.childrenAccept(this);

		exprResolver.analyze(binaryExpression);
	}
	
	@Override
	public void visit(LiteralExpression le){
		le.setSimple(true);
		Literal l = le.getLiteral();
		int type = l.getType();
		
		//Literals are constant
		le.setConstant(true);
		
		switch(type){
		case LiteralType.BOOL:
			le.setInferedType(BasicType.BOOL);
			break;
		case LiteralType.STRING:
			le.setInferedType(BasicType.STRING);
			break;
		case LiteralType.INT:
			le.setInferedType(BasicType.INT);
			break;
		case LiteralType.CHAR:
			le.setInferedType(BasicType.CHAR);
			break;
		case LiteralType.FLOAT:
			le.setInferedType(BasicType.FLOAT);
			break;
		case LiteralType.NULL:
			le.setInferedType(SpecialType.NULL);
			break;
		case LiteralType.TEXT:
			le.setInferedType(BasicType.TEXT);
			break;
		default:
			throw new CompilationError(l,"Literal of unknown literal type!");
		}
		
		//Get constant value
		le.accept(constResolve);
	}
	
	@Override
	public void visit(CastExpression castExpression) {
		//Get the type to cast to
		Type type = typeProvider.resolveType(castExpression.getType());
		
		//Infere right expression type
		castExpression.getRightExpression().accept(this);
		Type rightType = castExpression.getRightExpression().getInferedType();
		
		//Is cast possible
		if(rightType.canExplicitCastTo(type)){
			castExpression.setInferedType(type);
		} else {
			throw new CompilationError(castExpression,"Cannot cast from " + rightType.getUid() + " to " + type.getUid());
		}		
		
		//If the operand is constant, the value is constant
		if(castExpression.getRightExpression().getConstant()){
			castExpression.setConstant(true);
			castExpression.accept(constResolve);
		}
		
	}
	
	@Override
	public void visit(ArrayAccess arrayAccess) {
		arrayAccess.childrenAccept(this);
		//Check if an array access is possible
		Type t = arrayAccess.getLeftExpression().getInferedType();
		if(t.getCategory()!=Type.ARRAY){
			throw new CompilationError(arrayAccess,"Cannot use array access, because the operand is no array type, but '" + t.getUid() + "' (" + t.getDescription() +  ").");
		}
		
		//Type is the type of the array
		arrayAccess.setInferedType(t.getWrappedType());
		
		//The array access accesses the wrapped decl
		arrayAccess.setSemantics(arrayAccess.getLeftExpression().getSemantics());
		
	}
	
	@Override
	public void visit(ArrayCreationExpression arrayCreationExpression) {
		throw new CompilationError(arrayCreationExpression,"Array creation is not yet possible!");
	}
	
	@Override
	public void visit(ClassInstanceCreationExpression c) {
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
			throw new CompilationError(c,
					"Only classes can be instanced by a constructor invocation.");
		}

	}
	
	@Override
	public void visit(ConditionalExpression conditionalExpression) {
		//Infere subexpression types
		conditionalExpression.childrenAccept(this);
		
		//Check type validity
		if (conditionalExpression.getLeftExpression().getInferedType() != BasicType.BOOL) {
			throw new CompilationError(
					conditionalExpression.getLeftExpression(),
					"The condition of a conditional expression must be of type bool, but it is of type "
							+ conditionalExpression.getLeftExpression().getInferedType().getUid());
		}
		Type type1 = conditionalExpression.getRightExpression().getInferedType();
		Type type2 = conditionalExpression.getRightExpression2().getInferedType();
		if(!type2.canImplicitCastTo(type1)){
			throw new CompilationError(conditionalExpression, "The types of the two alternatives in a conditional expression do not match!");
		}
		conditionalExpression.setInferedType(type1);
	}
	
	@Override
	public void visit(FieldAccess fieldAccess) {
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
				throw new CompilationError(fieldAccess,"Accessing a global variable from above its declaration.");
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
	public void visit(KeyOfExpression keyOfExpression) {
		Type t;
		keyOfExpression.setInferedType(t = typeProvider.resolveType(keyOfExpression.getType()));
		keyOfExpression.setConstant(true);
		keyOfExpression.accept(constResolve);
	}
	
	@Override
	public void visit(AccessorDeclaration accessorDeclaration) {
		accessorDeclaration.childrenAccept(this);
	}
	
	@Override
	public void visit(InstanceofExpression instanceofExpression) {
		throw new CompilationError(instanceofExpression,"Instanceof not implemented yet!");
	}
	
	@Override
	public void visit(MetaClassExpression metaClassExpression) {
		throw new CompilationError(metaClassExpression,".class not implemented yet!");
	}
	
	@Override
	public void visit(MethodInvocation methodInvocation) {
		
		methodInvocation.childrenAccept(this);
		Invocation in = nameResolver.resolveFunctionCall(curScope,curRecordType,methodInvocation,inMember);
		Type type = in.getWhichFunction().getReturnType();
		methodInvocation.setSemantics(in);
		methodInvocation.setInferedType(type);
	}
	
	@Override
	public void visit(ThisExpression thisExpression) {
		if(curType==null) throw new CompilationError(thisExpression,"'this' can only be used inside of classes, enrichments or interfaces");
		if(curFunction.isStatic()) throw new CompilationError(thisExpression,"'this' cannot be used in static methods.");
		thisExpression.setInferedType(curType);
		thisExpression.setSimple(true);
	}
	
	@Override
	public void visit(ParenthesisExpression parenthesisExpression) {
		Expression e = parenthesisExpression.getExpression();
		e.accept(this);
		parenthesisExpression.setInferedType(e.getInferedType());
		
		if(e.getConstant()){
			parenthesisExpression.setConstant(true);
			parenthesisExpression.setValue(e.getValue());
		}
	}

}
