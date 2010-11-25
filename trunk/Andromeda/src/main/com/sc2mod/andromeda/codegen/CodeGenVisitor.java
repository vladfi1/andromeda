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
import com.sc2mod.andromeda.codegen.buffers.AdvancedBuffer;
import com.sc2mod.andromeda.codegen.buffers.GlobalVarBuffer;
import com.sc2mod.andromeda.codegen.buffers.SimpleBuffer;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.access.ConstructorInvocation;
import com.sc2mod.andromeda.environment.access.Invocation;
import com.sc2mod.andromeda.environment.access.InvocationType;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.OperationType;
import com.sc2mod.andromeda.environment.operations.StaticInit;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IRecordType;
import com.sc2mod.andromeda.environment.types.IStruct;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeUtil;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.SourceInfo;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.syntaxNodes.BlockStmtNode;
import com.sc2mod.andromeda.syntaxNodes.BreakStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ContinueStmtNode;
import com.sc2mod.andromeda.syntaxNodes.DeleteStmtNode;
import com.sc2mod.andromeda.syntaxNodes.DoWhileStmtNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ExplicitConsCallStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprStmtNode;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ForEachStmtNode;
import com.sc2mod.andromeda.syntaxNodes.ForStmtNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalFuncDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureListNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.IfStmtNode;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.LocalVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.LocalVarDeclStmtNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ReturnStmtNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.SourceListNode;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.StmtListNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.UninitedVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.WhileStmtNode;

public class CodeGenVisitor extends CodeGenerator {

	private boolean noCodeGenerated;
	public CodeGenExpressionVisitor expressionVisitor;

	public AdvancedBuffer curBuffer = null;
	// SimpleBuffer extraStatementBuffer = new SimpleBuffer(64);
	private Function curFunction;
	private IRecordType curType;
	private boolean inLib;
	
	
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
	
	public void invokeExprSurroundCast(ExprNode toVisit, IType castTo, boolean flushBuffers){
		expressionVisitor.surroundTypeCastIfNecessary(toVisit,castTo);
		if (flushBuffers) {
			if (!expressionVisitor.curExprBuffer.isEmpty()) {
				expressionVisitor.curExprBuffer.flushTo(curBuffer, false);

			}

		}
	}

	public CodeGenVisitor(Environment env, Configuration options, INameProvider nameProvider) {
		super(env, options, nameProvider);
		this.expressionVisitor = new CodeGenExpressionVisitor(this, env.typeProvider,options);
	}

	

	public void flushOutCode(CodeWriter w) throws IOException {
		fileBuffer.flush(this,w);
		addBytesOut(w.getBytesOut());
		
	}

	public void generateCode(NameGenerationVisitor ngv, SourceListNode af) {

		ngv.writeTypedefs(typedefBuffer);
		typedefBuffer.flushTo(fileBuffer.typedefs, true);
		visit(af);
	}

	// *********** GLOBAL STRUCTURES **************

	@Override
	public void visit(SourceFileNode andromedaFile) {
		SourceInfo afi = andromedaFile.getSourceInfo();
		// No names are generated for native libs
		InclusionType inclType = afi.getType();
		if (inclType == InclusionType.NATIVE)
			return;

		boolean inLibBefore = inLib;
		inLib = inclType == InclusionType.LIBRARY;
		andromedaFile.childrenAccept(this);
		inLib = inLibBefore;
	}

	@Override
	public void visit(GlobalStructureListNode fileContent) {
		fileContent.childrenAccept(this);
	}

	@Override
	public void visit(IncludeNode includedFile) {
		includedFile.getIncludeContent().accept(this);
	}

	@Override
	public void visit(GlobalFuncDeclNode functionDeclaration) {
		functionDeclaration.childrenAccept(this);
	}
	
	@Override
	public void visit(GlobalStaticInitDeclNode globalInitDeclaration) {
		globalInitDeclaration.getInitDecl().accept(this);
	}

	@Override
	public void visit(EnrichDeclNode enrichDeclaration) {
		enrichDeclaration.getBody().childrenAccept(this);
	}

	@Override
	public void visit(ExplicitConsCallStmtNode e) {
		ExprListNode arguments = e.getArguments();
		ConstructorInvocation c = (ConstructorInvocation) e.getSemantics();

		curBuffer.append(c.getClassToAlloc().getGeneratedName()).append(" ")
				.append(classGen.getThisName()).append("=");
		classGen.generateConstructorInvocation(c, arguments, null);
		curBuffer.append(";");
	}

	@Override
	public void visit(ClassDeclNode classDeclaration) {
		IRecordType typeBefore = curType;
		curType = classDeclaration.getSemantics();
		classDeclaration.getBody().childrenAccept(this);
		curType = typeBefore;
	}

	@Override
	public void visit(StructDeclNode structDeclaration) {
		IStruct struct = (IStruct) structDeclaration.getSemantics();
		AdvancedBuffer structBuffer = this.structBuffer;

		structBuffer.append("struct ");
		structBuffer.append(struct.getGeneratedName());
		structBuffer.nlBeforeBrace();
		structBuffer.append("{");

		structBuffer.indent();
		Iterable<Variable> fields = TypeUtil.getNonStaticTypeFields(struct, false);
		for (Variable field : fields) {
			structBuffer.nl();
			structBuffer.append(field.getType().getGeneratedName());
			structBuffer.append(" ");
			structBuffer.append(field.getGeneratedName());
			structBuffer.append(";");
			;
		}
		structBuffer.unindent();
		structBuffer.nl().append("}").nl().nl();
		
		structBuffer.flushTo(fileBuffer.types, true);
	}

	
	private void generateFunctionBody(Function m, StmtNode body, boolean isConstructor){
	
		// Do a forward declaration
		functionBuffer.appendTo(fileBuffer.forwardDeclarations, true);
		fileBuffer.forwardDeclarations.append(";", true);
		
		fileBuffer.forwardDeclarations.nl();

		curBuffer = functionBuffer;
		
		
		functionBuffer.nlBeforeBrace();
		functionBuffer.append("{");
		functionBuffer.indent();

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

			functionBuffer.nl();
			functionBuffer.append(local.getType().getGeneratedName());
			functionBuffer.append(" ");
			functionBuffer.append(local.getGeneratedName());

			// Was this variable defined at the top of the function?
			// If so, we init it here. (Otherwise it is inited where it was
			// actually defined)
			if (local.isOnTop()) {
				VarDeclNode decl = local.getDeclarator();
				
				// Has this variable before-statements? If so, we must stop initialization here
				if(local.getInitCode()!=null) skipInit = true;
				
				if(skipInit){
					//Since we have to skip init, this variable is no longer considered on top
					local.setOnTop(false);
				} else if (!(decl instanceof UninitedVarDeclNode)) {
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
			switch(m.getOperationType()){
			case CONSTRUCTOR:
				functionBuffer.nl();
				functionBuffer.append("return ").append(classGen.getThisName())
						.append(";");
				break;
			case DESTRUCTOR:
				functionBuffer.nl();
				Destructor d = (Destructor)curFunction;
				Operation overridden = d.getOverrideInformation().getOverridenMethod();
				String destrName = overridden==null?((IClass)d.getContainingType()).getNameProvider().getDeallocatorName():overridden.getGeneratedName();
				curBuffer.append(destrName).append("(").append(classGen.getThisName()).append(");");
				curBuffer.nl();
				break;
			case STATIC_INIT:
				functionBuffer.nl();
				functionBuffer.append("return true;");
				break;
			}
			
		}

		generateMethodFooter(functionBuffer);

	}
	
	
	@Override
	public void visit(StaticInitDeclNode initDecl) {
		StaticInit init = (StaticInit) initDecl.getSemantics();

		curFunction = init;

		// Cache in locals for speed
		SimpleBuffer functionBuffer = this.functionBuffer;

		if (insertComments) {
			functionBuffer.append("//Static init").nl();
		}

		// Init header
		String boolName = env.typeProvider.BASIC.BOOL.getGeneratedName();
		String param1 = nameProvider.getLocalNameRaw("A__1", init.getLocals().length);
		String param2 = nameProvider.getLocalNameRaw("A__2", init.getLocals().length+1);
		functionBuffer.append(boolName + " ").append(init.getGeneratedName())
			.append("(").append(boolName).append(" ");
		functionBuffer.append(param1).append(",").append(boolName).append(" ").append(param2);
		functionBuffer.append(")");
		
		generateFunctionBody(init, initDecl.getBody(), false);
		

		functionBuffer.nl();
		
		functionBuffer.flushTo(fileBuffer.functions, true);
		
		curFunction = null;

	}

	@Override
	public void visit(MethodDeclNode methodDeclaration) {
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
		SimpleBuffer functionBuffer = this.functionBuffer;

		OperationType functionType = m.getOperationType();
		String comment = null;
		if(insertComments){
			switch(functionType){
			case CONSTRUCTOR:
				comment = "//Constructor for class ".concat(m.getContainingType()
						.getUid());
				break;
			case DESTRUCTOR:
				comment = "//Destructor for class ".concat(m.getContainingType()
						.getUid());
				break;
			}
		}
		
		writeFuncHeader(m, functionBuffer, comment);

		// Only for functions with body
		StmtNode body = methodDeclaration.getBody();
		if (body != null) {
			generateFunctionBody(m, body, OperationType.CONSTRUCTOR == functionType);
		} else 
			functionBuffer.append(";").nl();

		
		// Natives are appended to the forward declarations
		if (m.isNative()) {
			functionBuffer.flushTo(fileBuffer.forwardDeclarations, true);
		} else {
			functionBuffer.flushTo(fileBuffer.functions, true);
		}

		curFunction = null;
	}
	
	private String generateGlobalInitFunction(Variable g) {
		List<StmtNode> initCode = g.getInitCode();
		
		curBuffer = functionBuffer;
		
		//Comment
		if(insertComments){
			curBuffer.append("//Variable init for " + g.getUid()).nl();
		}
		
		curBuffer.append("static ").append(g.getType().getGeneratedName()).append(" ");
		String funcName = nameProvider.getGlobalNameRawNoPrefix("gInit___".concat(g.getGeneratedName()));
		curBuffer.append(funcName).append("()");
		
		curBuffer.appendTo(fileBuffer.forwardDeclarations,true);
		fileBuffer.forwardDeclarations.append(";").nl();
		
		curBuffer.append("{");

		curBuffer.indent();
		
		//Generate init code
		for(StmtNode s: initCode){
			curBuffer.nl();
			s.accept(this);
		}
		
		curBuffer.nl();
		
		//Generate return
		curBuffer.append("return ");
		invokeRValueVisitor(g.getDeclarator().getInitializer(), true, true);
		
		generateMethodFooter(curBuffer);
		
		//Flush to functions
		curBuffer.flushTo(fileBuffer.functions, true);
		
		//Return function name so it can be called in the init 
		return funcName;		
	}

	private void generateGlobalDecl(Variable g, boolean isPrivate) {
		GlobalVarBuffer globalVarBuffer = this.globalVarBuffer;
		globalVarBuffer.nl();
		
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
			if (g.getDeclarator() instanceof UninitedVarDeclNode)
				curBuffer.append(";");
	
		}
		

		globalVarBuffer.flushTo(fileBuffer.variables, true);
	}

	public void generateFieldInit(AdvancedBuffer buffer,IClass c,Variable f) {
		
		AdvancedBuffer curBufferBefore = curBuffer;
		curBuffer = buffer;

		//Explicit init side effects?
		if(f.getInitCode() != null){
			for(StmtNode s: f.getInitCode()){
				s.accept(this);
				buffer.nl();
			}
		}
		
		//Name
		curBuffer
			.append(c.getNameProvider().getMemoryName())
			.append("[")
			.append(classGen.getThisName())
			.append("].")
			.append(f.getGeneratedName());
		
		// Initialization
		invokeRValueVisitor(f.getDeclarator(), true, true);
		curBuffer = curBufferBefore;
	}


	@Override
	public void visit(GlobalVarDeclNode gvd) {
		VarDeclListNode f = gvd.getFieldDecl().getDeclaredVariables();
		int size = f.size();
		for (int i = 0; i < size; i++) {
			Variable field = f.get(i).getName().getSemantics();
			if (inLib && field.getNumReadAccesses() == 0)
				continue;
			generateGlobalDecl(field,
					field.getVisibility() == Visibility.PRIVATE);
		}
	}

	@Override
	public void visit(FieldDeclNode fieldDeclaration) {
		VarDeclListNode f = fieldDeclaration.getDeclaredVariables();
		int size = f.size();
		for (int i = 0; i < size; i++) {
			Variable field = f.get(i).getName().getSemantics();
			//XPilot: don't remove a variable if it is written to
			if (inLib && field.getNumReadAccesses() == 0 && field.getNumWriteAccesses() == 0)
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
	public void visitExt(BlockStmtNode blockStatement,
			SimpleBuffer codeBefore, SimpleBuffer codeAfter, boolean doBraces) {
		SimpleBuffer curBuffer = this.curBuffer;
		if(doBraces){
			curBuffer.append("{");
			curBuffer.indent();
		}
		
		if (codeBefore != null && !codeBefore.isEmpty()) {
			curBuffer.nl();
			codeBefore.flushTo(curBuffer, false);
		}

		blockStatement.childrenAccept(this);

		if (codeAfter != null)
			codeAfter.flushTo(curBuffer, false);

		if(doBraces){
			curBuffer.unindent();
			curBuffer.nl();
			curBuffer.append("}");
		}
	}

	@Override
	public void visit(BlockStmtNode blockStatement) {
		visitExt(blockStatement, null, null,false);
	}

	@Override
	public void visit(StmtListNode statementList) {
		int size = statementList.size();

		// Remember if code was generated
		boolean noCodeGeneratedBefore = noCodeGenerated;
		noCodeGenerated = false;

		for (int i = 0; i < size; i++) {
			if (!noCodeGenerated)
				curBuffer.nl();
			noCodeGenerated = false;
		
			statementList.get(i).accept(this);
		}

		// Restore generated code
		noCodeGenerated = noCodeGeneratedBefore;

	}

	@Override
	public void visit(ExprStmtNode expressionStatement) {
		invokeRValueVisitor(expressionStatement.getExpression(), true, 
				true);
	}

	@Override
	public void visit(IfStmtNode ifThenElseStatement) {
		AdvancedBuffer curBuffer = this.curBuffer;

		// if / condition
		invokeRValueVisitor(ifThenElseStatement.getCondition(), false, 
				true);
		curBuffer.append("if(");
		expressionVisitor.curExprBuffer.flushTo(curBuffer, false);
		curBuffer.append(")");

		// then statement
		StmtNode stmt = ifThenElseStatement.getThenStatement();
		curBuffer.nlBeforeBrace();
		visitExt((BlockStmtNode) stmt, null, null, true);

		// else statement
		stmt = ifThenElseStatement.getElseStatement();
		if (stmt != null) {
	
			curBuffer.exprWhitespace();
			curBuffer.append("else");
			curBuffer.exprWhitespace();
			curBuffer.nlBeforeBrace();
		
			if(stmt instanceof BlockStmtNode){
				visitExt((BlockStmtNode) stmt, null, null, true);
			} else {
				stmt.accept(this);
			}
		}
	}

	
	@Override
	public void visit(ForEachStmtNode forEachStatement) {
		throw new InternalProgramError("ForEach loops cannot exist in the code generation phase!");
	}
	
	@Override
	public void visit(ForStmtNode forStatement) {
		throw new InternalProgramError("For loops cannot exist in the code gen phase");
	}
	
	
	
	@Override
	public void visit(WhileStmtNode whileStatement) {
		SimpleBuffer curBuffer = this.curBuffer;

		
		expressionVisitor.curExprBuffer.indent();
		invokeRValueVisitor(whileStatement.getCondition(), false, false);
		expressionVisitor.curExprBuffer.unindent();
		
		curBuffer.append("while(");
		expressionVisitor.curExprBuffer.flushTo(curBuffer, false);
		curBuffer.append(")");


		// loop body
		BlockStmtNode stmt = (BlockStmtNode) whileStatement
				.getThenStatement();
		visitExt(stmt, null, null,true);

	}

	@Override
	public void visit(DoWhileStmtNode doWhileStatement) {
		AdvancedBuffer curBuffer = this.curBuffer;
		
		// do
		curBuffer.append("do");

		// loop body
		BlockStmtNode stmt = (BlockStmtNode) doWhileStatement.getThenStatement();
		curBuffer.nlBeforeBrace().append("{");
		curBuffer.indent();
		stmt.childrenAccept(this);
		curBuffer.unindent();
	
		// while
		ExprNode cond = doWhileStatement.getCondition();
		expressionVisitor.curExprBuffer.indent();
		invokeRValueVisitor(cond, false, false);
		expressionVisitor.curExprBuffer.unindent();
		
		//block end
		curBuffer.nl().append("}");
		
		// actual while
		curBuffer.append("while(");
		expressionVisitor.curExprBuffer.flushTo(curBuffer, false);
		curBuffer.append(");");

	}

	



	@Override
	public void visit(ExprListNode el) {
		SimpleBuffer curBuffer = this.curBuffer;
		
		int size = el.size();
		for (int i = 0; i < size; i++) {
			curBuffer.nl();
			invokeRValueVisitor(el.get(i), true, true);
		}
	}

	@Override
	public void visit(LocalVarDeclStmtNode l) {
		AdvancedBuffer curBuffer = this.curBuffer;

		LocalVarDeclNode def = l.getVarDeclaration();
		VarDeclListNode decls = def.getDeclarators();
		int size = decls.size();

		int numGenerated = 0;
		for (int i = 0; i < size; i++) {
			VarDeclNode decl = decls.get(i);

			// Get semantics
			LocalVarDecl v = (LocalVarDecl) decl.getName().getSemantics();

			// Inlined constants are not generated
			if (v.isConst() && v.getNumReadAccesses() == 0)
				continue;

			// Is this just a declaration without init?
			// Skip if the variable was not overridden, otherwise
			// make a default init
			boolean isOnlyDecl = decl instanceof UninitedVarDeclNode;
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
			List<StmtNode> initCode = v.getInitCode();
			if(initCode != null){
				for(StmtNode s: initCode){
					s.accept(this);
				}
			}
			
			curBuffer.append(v.getGeneratedName());

			// Initialization
			if (isOnlyDecl) {
				// An overriding declaration must be inited with the default
				// value of the type
				curBuffer.exprWhitespace();
				curBuffer.append("=");
				curBuffer.exprWhitespace();
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
	public void visit(BreakStmtNode breakStatement) {
		curBuffer.append("break;");
	}

	@Override
	public void visit(ContinueStmtNode continueStatement) {
		curBuffer.append("continue;");
	}

	@Override
	public void visit(ReturnStmtNode returnStatement) {
		ExprNode result = returnStatement.getResult();
		if (result == null) {
			switch(curFunction.getOperationType()){
			case CONSTRUCTOR:
				curBuffer.append("return ").append(classGen.getThisName())
				.append(";");
				break;
			case STATIC_INIT:
				curBuffer.append("return true;");
				break;
			case DESTRUCTOR:
				Destructor d = (Destructor)curFunction;
				Operation overridden = d.getOverrideInformation().getOverridenMethod();
				String destrName = overridden==null?((IClass)d.getContainingType()).getNameProvider().getDeallocatorName():overridden.getGeneratedName();
				curBuffer.append(destrName).append("(").append(classGen.getThisName()).append(");");
				curBuffer.nl();
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
	public void visit(DeleteStmtNode deleteStatement) {
		ExprNode e = deleteStatement.getExpression();
//		Class c = (Class) e.getInferedType();
//		c = c.getTopClass();
//		
//		curBuffer.append(c.getDeallocatorName()).append("(");
//		invokeRValueVisitor(e, true);
//		curBuffer.append(");");

		Invocation i = (Invocation) deleteStatement.getSemantics();
		InvocationType accessType = i.getInvocationType();
		if(accessType==InvocationType.VIRTUAL){
			curBuffer.append(i.getWhichFunction().getOverrideInformation().getVirtualCaller());
		} else {
			curBuffer.append(i.getWhichFunction().getGeneratedName());
		}
		curBuffer.append("(");
		invokeRValueVisitor(e, true);	
		curBuffer.append(")");	
		curBuffer.append(";");
	}

	

}
