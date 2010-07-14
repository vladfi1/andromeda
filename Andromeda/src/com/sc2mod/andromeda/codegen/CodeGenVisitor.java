/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.codegen;

import java.io.IOException;
import java.util.List;

import com.sc2mod.andromeda.classes.ClassGenerator;
import com.sc2mod.andromeda.codegen.buffers.GlobalVarBuffer;
import com.sc2mod.andromeda.codegen.buffers.SimpleBuffer;
import com.sc2mod.andromeda.codetransform.SyntaxGenerator;
import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Constructor;
import com.sc2mod.andromeda.environment.ConstructorInvocation;
import com.sc2mod.andromeda.environment.Destructor;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.Invocation;
import com.sc2mod.andromeda.environment.Method;
import com.sc2mod.andromeda.environment.StaticInit;
import com.sc2mod.andromeda.environment.Visibility;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Struct;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.FieldOrAccessorDecl;
import com.sc2mod.andromeda.environment.variables.FieldSet;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.NonParamDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.parsing.AndromedaFileInfo;
import com.sc2mod.andromeda.parsing.OutputStats;
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.semAnalysis.ForeachSemantics;
import com.sc2mod.andromeda.syntaxNodes.AccessType;
import com.sc2mod.andromeda.syntaxNodes.AccessorDeclaration;
import com.sc2mod.andromeda.syntaxNodes.AndromedaFile;
import com.sc2mod.andromeda.syntaxNodes.BlockStatement;
import com.sc2mod.andromeda.syntaxNodes.BreakStatement;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclaration;
import com.sc2mod.andromeda.syntaxNodes.ContinueStatement;
import com.sc2mod.andromeda.syntaxNodes.DeleteStatement;
import com.sc2mod.andromeda.syntaxNodes.DoWhileStatement;
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
import com.sc2mod.andromeda.syntaxNodes.LocalVariableDeclaration;
import com.sc2mod.andromeda.syntaxNodes.LocalVariableDeclarationStatement;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclaration;
import com.sc2mod.andromeda.syntaxNodes.ReturnStatement;
import com.sc2mod.andromeda.syntaxNodes.Statement;
import com.sc2mod.andromeda.syntaxNodes.StatementList;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclaration;
import com.sc2mod.andromeda.syntaxNodes.StructDeclaration;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.VariableDecl;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclarator;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclarators;
import com.sc2mod.andromeda.syntaxNodes.WhileStatement;

public class CodeGenVisitor extends CodeGenerator {

	private boolean noCodeGenerated;
	public CodeGenExpressionVisitor expressionVisitor;

	public SimpleBuffer curBuffer = null;
	// SimpleBuffer extraStatementBuffer = new SimpleBuffer(64);
	public int curIndent;
	private Function curFunction;
	private RecordType curType;
	private boolean inLib;
	private int bytesOut;
	
	
	private void invokeRValueVisitor(SyntaxNode toVisit,
			boolean flushBuffers,
			boolean terminateExpression) {
		toVisit.accept(expressionVisitor);
		if (flushBuffers) {
			if (!expressionVisitor.curExprBuffer.isEmpty()) {
				expressionVisitor.curExprBuffer.flushTo(curBuffer, false);
				if (terminateExpression)
					curBuffer.append(";");
			}

		}
	}

	@Override
	public void setClassGenerator(ClassGenerator classGen) {
		super.setClassGenerator(classGen);
		expressionVisitor.classGen = classGen;
	}

	public void invokeRValueVisitor(SyntaxNode toVisit, boolean flushBuffers) {
		invokeRValueVisitor(toVisit, flushBuffers, false);
	}
	
	public void invokeExprSurroundCast(Expression toVisit, Type castTo, boolean flushBuffers){
		expressionVisitor.surroundTypeCastIfNecessary(toVisit,castTo);
		if (flushBuffers) {
			if (!expressionVisitor.curExprBuffer.isEmpty()) {
				expressionVisitor.curExprBuffer.flushTo(curBuffer, false);

			}

		}
	}

	public CodeGenVisitor(Environment env, Options options, INameProvider nameProvider) {
		super(env, options, nameProvider);
		this.expressionVisitor = new CodeGenExpressionVisitor(this, env,
				options);
	}

	

	public void flushOutCode(CodeWriter w) throws IOException {
		fileBuffer.flush(this,w);
		bytesOut = w.getBytesOut();
		
	}


	public int getBytesOut() {
		return bytesOut;
	}

	public void generateCode(NameGenerationVisitor ngv, AndromedaFile af) {

		ngv.writeTypedefs(typedefBuffer);
		typedefBuffer.flushTo(fileBuffer.typedefs, true);
		visit(af);
	}

	// *********** GLOBAL STRUCTURES **************

	@Override
	public void visit(AndromedaFile andromedaFile) {
		AndromedaFileInfo afi = andromedaFile.getFileInfo();
		// No names are generated for native libs
		int inclType = afi.getInclusionType();
		if (inclType == AndromedaFileInfo.TYPE_NATIVE)
			return;

		boolean inLibBefore = inLib;
		inLib = inclType == AndromedaFileInfo.TYPE_LIBRARY;
		andromedaFile.childrenAccept(this);
		inLib = inLibBefore;
	}

	@Override
	public void visit(FileContent fileContent) {
		fileContent.childrenAccept(this);
	}

	@Override
	public void visit(IncludedFile includedFile) {
		includedFile.getIncludedContent().accept(this);
	}

	@Override
	public void visit(FunctionDeclaration functionDeclaration) {
		functionDeclaration.childrenAccept(this);
	}
	
	@Override
	public void visit(GlobalInitDeclaration globalInitDeclaration) {
		globalInitDeclaration.getInitDecl().accept(this);
	}

	@Override
	public void visit(EnrichDeclaration enrichDeclaration) {
		enrichDeclaration.getBody().childrenAccept(this);
	}

	@Override
	public void visit(AccessorDeclaration a) {
		MethodDeclaration m = a.getGetMethod();
		if (m != null)
			m.accept(this);
		m = a.getSetMethod();
		if (m != null)
			m.accept(this);
	}

	@Override
	public void visit(ExplicitConstructorInvocationStatement e) {
		ExpressionList arguments = e.getArguments();
		ConstructorInvocation c = (ConstructorInvocation) e.getSemantics();

		curBuffer.append(c.getClassToAlloc().getGeneratedName()).append(" ")
				.append(classGen.getThisName()).append("=");
		classGen.generateConstructorInvocation(c, arguments, null);
		curBuffer.append(";");
	}

	@Override
	public void visit(ClassDeclaration classDeclaration) {
		RecordType typeBefore = curType;
		curType = (Class) classDeclaration.getSemantics();
		classDeclaration.getBody().childrenAccept(this);
		curType = typeBefore;
	}

	@Override
	public void visit(StructDeclaration structDeclaration) {
		Struct struct = (Struct) structDeclaration.getSemantics();
		Options options = this.options;
		int curIndent = this.curIndent;
		SimpleBuffer structBuffer = this.structBuffer;

		structBuffer.append("struct ");
		structBuffer.append(struct.getGeneratedName());
		if (options.ownLineForOpenBraces)
			structBuffer.newLine(curIndent);
		structBuffer.append("{");

		if (options.useIndent)
			curIndent++;
		FieldSet f = struct.getFields();
		for (String name : f.getFieldNames()) {
			VarDecl field = f.getFieldByName(name);
			if (options.newLines)
				structBuffer.newLine(curIndent);
			structBuffer.append(field.getType().getGeneratedName());
			structBuffer.append(" ");
			structBuffer.append(field.getGeneratedName());
			structBuffer.append(";");
			;
		}
		if (options.useIndent)
			curIndent--;
		if (options.newLines) {
			structBuffer.newLine(curIndent);
			structBuffer.append("};");
			structBuffer.newLine(curIndent);
			structBuffer.newLine(curIndent);
		} else {
			structBuffer.append("};");
		}
		structBuffer.flushTo(fileBuffer.types, true);
	}

	
	private void generateFunctionBody(Function m, Statement body, boolean isConstructor){
	
		// Do a forward declaration
		functionBuffer.appendTo(fileBuffer.forwardDeclarations, true);
		fileBuffer.forwardDeclarations.append(";", true);
		if (options.newLines)
			fileBuffer.forwardDeclarations.newLine();

		curBuffer = functionBuffer;

		if(options.ownLineForOpenBraces) functionBuffer.newLine();
		functionBuffer.append("{");
		if (options.useIndent)
			curIndent++;

		// Constructor? Insert beginning stuff
		if (isConstructor)
			classGen.generateConstructorHead((Constructor) m);

		LocalVarDecl[] locals = m.getLocals();
		boolean skipInit = false;
		for (int i = 0, size = locals.length; i < size; i++) {
			LocalVarDecl local = locals[i];

			// Inlined constants are not generated
			if (local.isConst() && local.getNumReadAccesses() == 0)
				continue;

			if (options.newLines)
				functionBuffer.newLine(curIndent);
			functionBuffer.append(local.getType().getGeneratedName());
			functionBuffer.append(" ");
			functionBuffer.append(local.getGeneratedName());

			// Was this variable defined at the top of the function?
			// If so, we init it here. (Otherwise it is inited where it was
			// actually defined)
			if (local.isOnTop()) {
				VariableDeclarator decl = local.getDeclarator();
				
				// Has this variable before-statements? If so, we must stop initialization here
				if(local.getInitCode()!=null) skipInit = true;
				
				if(skipInit){
					//Since we have to skip init, this variable is no longer considered on top
					local.setOnTop(false);
				} else if (!(decl instanceof VariableDecl)) {
					// Initialization, if this decl has one
					invokeRValueVisitor(decl, true);
				}

			}
			
			

			functionBuffer.append(";");
		}

		body.childrenAccept(this);

		// For some special functions (constructor, destructor, static init)
		// we have to add returns if the control flow reaches the end of the function
		if (m.flowReachesEnd()) {
			switch(m.getFunctionType()){
			case Function.TYPE_CONSTRUCTOR:
				functionBuffer.newLine(curIndent);
				functionBuffer.append("return ").append(classGen.getThisName())
						.append(";");
				break;
			case Function.TYPE_DESTRUCTOR:
				functionBuffer.newLine(curIndent);
				Destructor d = (Destructor)curFunction;
				AbstractFunction overridden = d.getOverridenMethod();
				String destrName = overridden==null?((Class)d.getContainingType()).getDeallocatorName():overridden.getGeneratedName();
				curBuffer.append(destrName).append("(").append(classGen.getThisName()).append(");");
				if(options.newLines) curBuffer.newLine(curIndent);
				break;
			case Function.TYPE_STATIC_INIT:
				functionBuffer.newLine(curIndent);
				functionBuffer.append("return true;");
				break;
			}
			
		}

		if (options.useIndent)
			curIndent--;
		if (options.newLines) {
			functionBuffer.newLine(curIndent);
			functionBuffer.append("}");
			functionBuffer.newLine(curIndent);
		} else {
			functionBuffer.append("}");
		}

	}
	
	
	@Override
	public void visit(StaticInitDeclaration initDecl) {
		StaticInit init = (StaticInit) initDecl.getSemantics();

		curFunction = init;

		// Cache in locals for speed
		Options options = this.options;
		SimpleBuffer functionBuffer = this.functionBuffer;

		String comment = null;
		if (options.insertDescriptionComments) {
			functionBuffer.append("//Static init");
			functionBuffer.newLine();
		}

		// Init header
		String boolName = BasicType.BOOL.getGeneratedName();
		String param1 = nameProvider.getLocalNameRaw("A__1", init.getLocals().length);
		String param2 = nameProvider.getLocalNameRaw("A__2", init.getLocals().length+1);
		functionBuffer.append(boolName + " ").append(init.getGeneratedName())
			.append("(").append(boolName).append(" ");
		functionBuffer.append(param1).append(",").append(boolName).append(" ").append(param2);
		functionBuffer.append(")");
		
		generateFunctionBody(init, initDecl.getBody(), false);
		

		if (options.newLines)
			functionBuffer.newLine(curIndent);
	
		functionBuffer.flushTo(fileBuffer.functions, true);
		
		curFunction = null;

	}

	@Override
	public void visit(MethodDeclaration methodDeclaration) {
		Function m = (Function) methodDeclaration.getSemantics();

		// Forward declarations are not written since all functions are forward
		// declared anyway.
		if (!m.hasBody() && !m.isNative())
			return;

		// Uncalled library funcitons are not written
		if (inLib && m.getInvocationCount() == 0)
			return;

		curFunction = m;

		// Cache in locals for speed
		Options options = this.options;
		SimpleBuffer functionBuffer = this.functionBuffer;

		int functionType = m.getFunctionType();
		String comment = null;
		if(options.insertDescriptionComments){
			switch(functionType){
			case Function.TYPE_CONSTRUCTOR:
				comment = "//Constructor for class ".concat(m.getContainingType()
						.getName());
				break;
			case Function.TYPE_DESTRUCTOR:
				comment = "//Destructor for class ".concat(m.getContainingType()
						.getName());
				break;
			}
		}
		
		writeFuncHeader(m, functionBuffer, comment);

		// Only for functions with body
		Statement body = methodDeclaration.getBody();
		if (body != null) {
			generateFunctionBody(m, body, Function.TYPE_CONSTRUCTOR == functionType);
		} else
			functionBuffer.append(";");

		if (options.newLines)
			functionBuffer.newLine(curIndent);

		// Natives are appended to the forward declarations
		if (m.isNative()) {
			functionBuffer.flushTo(fileBuffer.forwardDeclarations, true);
		} else {
			functionBuffer.flushTo(fileBuffer.functions, true);
		}

		curFunction = null;
	}
	
	private String generateGlobalInitFunction(NonParamDecl g) {
		List<Statement> initCode = g.getInitCode();
		boolean newLines = options.newLines;
		curBuffer = functionBuffer;
		
		//Comment
		if(options.insertDescriptionComments){
			curBuffer.append("//Variable init for " + g.getUid());
			curBuffer.newLine();
		}
		
		curBuffer.append("static ").append(g.getType().getGeneratedName()).append(" ");
		String funcName = nameProvider.getGlobalNameRawNoPrefix("gInit___".concat(g.getGeneratedName()));
		curBuffer.append(funcName).append("()");
		
		curBuffer.appendTo(fileBuffer.forwardDeclarations,true);
		fileBuffer.forwardDeclarations.append(";").newLine();
		
		curBuffer.append("{");

		if(options.useIndent)curIndent++;

		//Generate init code
		for(Statement s: initCode){
			if(newLines){
				curBuffer.newLine(curIndent);
			}
			s.accept(this);
		}
		
		if(newLines){
			curBuffer.newLine(curIndent);
		}
		//Generate return
		curBuffer.append("return ");
		invokeRValueVisitor(g.getDeclarator().getInitializer(), true, true);
		
		if(options.useIndent)curIndent--;		
		generateMethodFooter(curBuffer);
		
		//Flush to functions
		curBuffer.flushTo(fileBuffer.functions, true);
		
		//Return function name so it can be called in the init 
		return funcName;		
	}

	private void generateGlobalDecl(NonParamDecl g, boolean isPrivate) {
		GlobalVarBuffer globalVarBuffer = this.globalVarBuffer;
		if (options.newLines)
			globalVarBuffer.newLine();

		// Modifiers
		if (isPrivate)
			globalVarBuffer.append("static ");
		if (g.isConst())
			globalVarBuffer.append("const ");

		// Type and name
		globalVarBuffer.beginVarDecl(g.getType(), g.getGeneratedName());
		// Initialization
		
		// Were init statements generated? If so, we need an initializer
		
		if(g.getInitCode() != null){
			String initFuncName = generateGlobalInitFunction(g);
			globalVarBuffer.append("=").append(initFuncName).append("();");
		} else {

			curBuffer = globalVarBuffer;
			invokeRValueVisitor(g.getDeclarator(), true, true);
			if (g.getDeclarator() instanceof VariableDecl)
				curBuffer.append(";");
	
		}
		

		globalVarBuffer.flushTo(fileBuffer.variables, true);
	}

	public void generateFieldInit(SimpleBuffer buffer,Class c,FieldDecl f) {
		
		SimpleBuffer curBufferBefore = curBuffer;
		curBuffer = buffer;

		//Explicit init side effects?
		if(f.getInitCode() != null){
			for(Statement s: f.getInitCode()){
				s.accept(this);
			}
		}
		
		//Name
		curBuffer.append(c.getNameProvider().getMemoryName()).append("[").append(classGen.getThisName()).append("].").append(f.getGeneratedName());
		
		// Initialization
		invokeRValueVisitor(f.getDeclarator(), true, true);
		curBuffer = curBufferBefore;
	}

	@Override
	public void visit(GlobalVarDeclaration gvd) {
		VariableDeclarators f = gvd.getFieldDecl().getDeclaredVariables();
		int size = f.size();
		for (int i = 0; i < size; i++) {
			NonParamDecl field = (NonParamDecl) f.elementAt(i).getSemantics();
			if (inLib && field.getNumReadAccesses() == 0)
				continue;
			generateGlobalDecl(field,
					field.getVisibility() == Visibility.PRIVATE);
		}
	}

	@Override
	public void visit(FieldDeclaration fieldDeclaration) {
		VariableDeclarators f = fieldDeclaration.getDeclaredVariables();
		int size = f.size();
		for (int i = 0; i < size; i++) {
			FieldDecl field = (FieldDecl) f.elementAt(i).getSemantics();
			if (inLib && field.getNumReadAccesses() == 0)
				continue;
			if (field.isStatic()) {
				// Static fields are treated like globals (but they are never
				// private)
				generateGlobalDecl(field, false);
			} else {
				// Non static fields just produce code in the alloc method of
				// the class
			}
		}
	}

	// ******************* STATEMENTS *****************
	public void visitExt(BlockStatement blockStatement,
			SimpleBuffer codeBefore, SimpleBuffer codeAfter, boolean doBraces) {
		SimpleBuffer curBuffer = this.curBuffer;
		if(doBraces){
			curBuffer.append("{");
			if (options.useIndent)
				curIndent++;
		}
		
		if (codeBefore != null && !codeBefore.isEmpty()) {
			if (options.newLines)
				curBuffer.newLine(curIndent);
			codeBefore.flushTo(curBuffer, false);
		}

		blockStatement.childrenAccept(this);

		if (codeAfter != null)
			codeAfter.flushTo(curBuffer, false);

		if(doBraces){
			if (options.useIndent)
				curIndent--;
	
			if (options.newLines)
				curBuffer.newLine(curIndent);
			curBuffer.append("}");
		}
	}

	@Override
	public void visit(BlockStatement blockStatement) {
		visitExt(blockStatement, null, null,false);
	}

	@Override
	public void visit(StatementList statementList) {
		int size = statementList.size();

		// Remember if code was generated
		boolean noCodeGeneratedBefore = noCodeGenerated;
		noCodeGenerated = false;

		for (int i = 0; i < size; i++) {
			if (options.newLines) {
				if (!noCodeGenerated)
					curBuffer.newLine(curIndent);
				noCodeGenerated = false;
			}
			statementList.elementAt(i).accept(this);
		}

		// Restore generated code
		noCodeGenerated = noCodeGeneratedBefore;

	}

	@Override
	public void visit(ExpressionStatement expressionStatement) {
		invokeRValueVisitor(expressionStatement.getExpression(), true, 
				true);
	}

	@Override
	public void visit(IfThenElseStatement ifThenElseStatement) {
		SimpleBuffer curBuffer = this.curBuffer;

		// if / condition
		invokeRValueVisitor(ifThenElseStatement.getCondition(), false, 
				true);
		curBuffer.append("if(");
		expressionVisitor.curExprBuffer.flushTo(curBuffer, false);
		curBuffer.append(")");

		// then statement
		Statement stmt = ifThenElseStatement.getThenStatement();
		if (options.ownLineForOpenBraces)
			curBuffer.newLine(curIndent);
		visitExt((BlockStatement) stmt, null, null, true);
		
//		if (options.newLines && !(stmt instanceof BlockStatement)) {
//			curBuffer.newLine(curIndent + 1);
//			stmt.accept(this);
//			curBuffer.newLine(curIndent);
//		} else {
//			if (options.ownLineForOpenBraces)
//				curBuffer.newLine(curIndent);
//			
//			stmt.accept(this);
//		}

		// else statement
		stmt = ifThenElseStatement.getElseStatement();
		if (stmt != null) {

			if (options.newLines && options.ownLineForOpenBraces) {
				if (options.whitespacesInExprs)
					curBuffer.append(" else");
				else
					curBuffer.append("else");
				curBuffer.newLine(curIndent);
			} else if (options.whitespacesInExprs)
				curBuffer.append(" else ");
			else
				curBuffer.append("else ");
			
			if(stmt instanceof BlockStatement){
				visitExt((BlockStatement) stmt, null, null, true);
			} else {
				stmt.accept(this);
			}
		}
	}

	
	@Override
	public void visit(ForEachStatement forEachStatement) {
		SimpleBuffer curBuffer = this.curBuffer;

		ForeachSemantics semantics = (ForeachSemantics) forEachStatement.getSemantics();
		FieldAccess iter = semantics.getIterator();
		
		
		//Comment
		if(options.insertDescriptionComments){
			curBuffer.append("//Generated for each loop");
			curBuffer.newLine(curIndent);
		}
		
		//Init (iterator = leftside.getIterator();)
		curBuffer.append(((VarDecl)iter.getSemantics()).getGeneratedName()).append("=");		
		expressionVisitor.generateMethodInvocation(curBuffer, semantics.getGetIterator(), AccessType.EXPRESSION, forEachStatement.getExpression(), SyntaxGenerator.EMPTY_EXPRESSIONS);
		curBuffer.append(";");
		
		if(options.newLines) curBuffer.newLine(curIndent);
		
		//Condition: (while(iterator.hasNext()))
		curBuffer.append("while(");
		expressionVisitor.generateMethodInvocation(curBuffer, semantics.getHasNext(), AccessType.EXPRESSION, iter, SyntaxGenerator.EMPTY_EXPRESSIONS);
		curBuffer.append("){");
		
		if(options.newLines){
			if(options.useIndent)curIndent++;
			curBuffer.newLine(curIndent);
		}
		
		//Update: itervar = iterator.next();
		LocalVarDecl iterVarDecl = semantics.getIterVarDecl();
		curBuffer.append(iterVarDecl.getGeneratedName()).append("=");
		expressionVisitor.generateMethodInvocation(curBuffer, semantics.getNext(), AccessType.EXPRESSION, iter, SyntaxGenerator.EMPTY_EXPRESSIONS);
		curBuffer.append(";").newLine(curIndent);		

		// loop body
		BlockStatement stmt = (BlockStatement) forEachStatement.getThenStatement();
		visitExt(stmt, null, null,false);
		
		// loop end and delete afterwards, if desired
		if(options.newLines){
			if(options.useIndent)curIndent--;
			curBuffer.newLine(curIndent);
		}
		curBuffer.append("}");
		
		if(semantics.doDestroyAfter()){
			curBuffer.newLine(curIndent);
			semantics.getDelStatement().accept(this);
		}
		
		
		

	}
	
	@Override
	public void visit(WhileStatement whileStatement) {
		SimpleBuffer curBuffer = this.curBuffer;

		curIndent++;
		invokeRValueVisitor(whileStatement.getCondition(), false, false);
		curIndent--;
		
		curBuffer.append("while(");
		expressionVisitor.curExprBuffer.flushTo(curBuffer, false);
		curBuffer.append(")");


		// loop body
		BlockStatement stmt = (BlockStatement) whileStatement
				.getThenStatement();
		visitExt(stmt, null, null,true);

	}

	@Override
	public void visit(DoWhileStatement doWhileStatement) {
		SimpleBuffer curBuffer = this.curBuffer;
		Options options = this.options;

		// do
		curBuffer.append("do");

		// loop body
		BlockStatement stmt = (BlockStatement) doWhileStatement.getThenStatement();
		if (options.newLines) {
			if (options.ownLineForOpenBraces)
				curBuffer.newLine(curIndent);
			curBuffer.append("{");
			if (options.useIndent)
				curIndent++;
			stmt.childrenAccept(this);
			if (options.useIndent)
				curIndent--;
		} else {
			curBuffer.append("{");
			stmt.childrenAccept(this);
		}

		// while
		Expression cond = doWhileStatement.getCondition();
		if (options.useIndent)
			curIndent++;
		invokeRValueVisitor(cond, false, false);

		if (options.useIndent)
			curIndent--;

		// Block end
		if (options.newLines) {
			curBuffer.newLine(curIndent);
			curBuffer.append("}");
		} else {
			curBuffer.append("}");
		}
		// actual while
		curBuffer.append("while(");
		expressionVisitor.curExprBuffer.flushTo(curBuffer, false);
		curBuffer.append(");");

	}

	@Override
	public void visit(ForStatement forStatement) {
		SimpleBuffer curBuffer = this.curBuffer;
		Options options = this.options;

		// comment
		if (options.newLines && options.insertDescriptionComments) {
			curBuffer.append("//generated for-loop");
			curBuffer.newLine(curIndent);
		}

		// init
		Statement s = forStatement.getForInit();
		s.accept(this);

		// for
		if (options.newLines)
			curBuffer.newLine(curIndent);
		curIndent++;
		invokeRValueVisitor(forStatement.getCondition(), false, false);
		curIndent--;

		curBuffer.append("while(");
		expressionVisitor.curExprBuffer.flushTo(curBuffer, false);
		curBuffer.append(")");

		// loop body
		Statement stmt = forStatement.getThenStatement();
		if (options.newLines) {
			if (options.ownLineForOpenBraces)
				curBuffer.newLine(curIndent);
			curBuffer.append("{");
			if (options.useIndent)
				curIndent++;
			stmt.childrenAccept(this);
			curBuffer.newLine(curIndent);
		} else {
			curBuffer.append("{");
			stmt.childrenAccept(this);
		}

		// for-update
		forStatement.getForUpdate().getStatements().accept(this);
		if (options.useIndent)
			curIndent--;

		// Block end
		if (options.newLines) {
			curBuffer.newLine(curIndent);
			curBuffer.append("}");
		} else {
			curBuffer.append("}");
		}


	}



	@Override
	public void visit(ExpressionList el) {
		SimpleBuffer curBuffer = this.curBuffer;
		Options options = this.options;

		int size = el.size();
		for (int i = 0; i < size; i++) {
			if (options.newLines && i != 0)
				curBuffer.newLine(curIndent);
			invokeRValueVisitor(el.elementAt(i), true, true);
		}
	}

	@Override
	public void visit(LocalVariableDeclarationStatement l) {
		SimpleBuffer curBuffer = this.curBuffer;

		LocalVariableDeclaration def = l.getVarDeclaration();
		VariableDeclarators decls = def.getDeclarators();
		int size = decls.size();

		int numGenerated = 0;
		for (int i = 0; i < size; i++) {
			VariableDeclarator decl = decls.elementAt(i);

			// Get semantics
			LocalVarDecl v = (LocalVarDecl) decl.getName().getSemantics();

			// Inlined constants are not generated
			if (v.isConst() && v.getNumReadAccesses() == 0)
				continue;

			// Is this just a declaration without init?
			// Skip if the variable was not overridden, otherwise
			// make a default init
			boolean isOnlyDecl = decl instanceof VariableDecl;
			if (isOnlyDecl && !v.doesOverride()) {
				continue;
			}

			// Was the variable defined at the top of the function? If so, we do
			// not have
			// to init it here, since it was inited at the top. -> skip
			if (v.isOnTop())
				continue;

			// Use overriden variables
			numGenerated++;
			if (v.doesOverride())
				v = v.getOverride();
			
			//Does this local var have explicit init code? If so, generate it!
			List<Statement> initCode = v.getInitCode();
			if(initCode != null){
				for(Statement s: initCode){
					s.accept(this);
				}
			}
			
			curBuffer.append(v.getGeneratedName());

			// Initialization
			if (isOnlyDecl) {
				// An overriding declaration must be inited with the default
				// value of the type
				if (options.whitespacesInExprs)
					curBuffer.append(" = ");
				else
					curBuffer.append("=");
				curBuffer.append(v.getType().getDefaultValueStr());
				curBuffer.append(";");
			} else
				invokeRValueVisitor(decl, true, true);

		}
		if (numGenerated == 0) {
			noCodeGenerated = true;
		}
	}

	@Override
	public void visit(BreakStatement breakStatement) {
		curBuffer.append("break;");
	}

	@Override
	public void visit(ContinueStatement continueStatement) {
		curBuffer.append("continue;");
	}

	@Override
	public void visit(ReturnStatement returnStatement) {
		Expression result = returnStatement.getResult();
		if (result == null) {
			switch(curFunction.getFunctionType()){
			case Function.TYPE_CONSTRUCTOR:
				curBuffer.append("return ").append(classGen.getThisName())
				.append(";");
				break;
			case Function.TYPE_STATIC_INIT:
				curBuffer.append("return true;");
				break;
			case Function.TYPE_DESTRUCTOR:
				Destructor d = (Destructor)curFunction;
				AbstractFunction overridden = d.getOverridenMethod();
				String destrName = overridden==null?((Class)d.getContainingType()).getDeallocatorName():overridden.getGeneratedName();
				curBuffer.append(destrName).append("(").append(classGen.getThisName()).append(");");
				if(options.newLines) curBuffer.newLine(curIndent);
				curBuffer.append("return;");
				break;
			default:
				curBuffer.append("return;");
			}
		} else {
			curBuffer.append("return ");
			invokeExprSurroundCast(result, curFunction.getReturnType(), true);
			curBuffer.append(";");
		}
	}

	@Override
	public void visit(DeleteStatement deleteStatement) {
		Expression e = deleteStatement.getExpression();
//		Class c = (Class) e.getInferedType();
//		c = c.getTopClass();
//		
//		curBuffer.append(c.getDeallocatorName()).append("(");
//		invokeRValueVisitor(e, true);
//		curBuffer.append(");");

		Invocation i = (Invocation) deleteStatement.getSemantics();
		int accessType = i.getAccessType();
		if(accessType==Invocation.TYPE_VIRTUAL){
			curBuffer.append(((Method) i.getWhichFunction()).getVirtualCaller());
		} else {
			curBuffer.append(i.getWhichFunction().getGeneratedName());
		}
		curBuffer.append("(");
		invokeRValueVisitor(e, true);	
		curBuffer.append(")");	
		curBuffer.append(";");
	}

	

}
