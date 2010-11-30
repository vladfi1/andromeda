package com.sc2mod.andromeda.semAnalysis;

import java.util.HashMap;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.access.Invocation;
import com.sc2mod.andromeda.environment.access.NameAccess;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.content.OperationSet;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.VarType;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.LocalVarDeclStmtNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocationExprNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.SimpleTypeNode;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.TypeAliasDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeNode;
import com.sc2mod.andromeda.util.visitors.TraceScopeScanVisitor;

public class GalaxyAnalysisVisitor extends TraceScopeScanVisitor implements Analyser {

	private HashMap<SyntaxNode, Integer> enumeration = new HashMap<SyntaxNode, Integer>();

	private int curNum = 0;
	private boolean inGlobalField;

	private int putEnum(SyntaxNode s) {
		int num = ++curNum;
		enumeration.put(s, num);
		return num;
	}

	private Integer getEnum(SyntaxNode s) {
		return enumeration.get(s);
	}

	private Configuration options;

	public GalaxyAnalysisVisitor(Configuration options) {
		this.options = options;
	}
	

	@Override
	public void analyse(Environment env, SyntaxNode ast) {
		//Trace the tree
		ast.accept(this);
		
		//Check for function overloading
		for(IScopedElement elem : env.iterateOverContent(false, false, false)){
			if(elem.getElementType() == ScopedElementType.OP_SET){
				OperationSet opSet = (OperationSet) elem;
				if(opSet.size() > 1){
					Problem p = Problem.ofType(ProblemId.GALAXY_FUNCTION_OVERLOADING_NOT_POSSIBLE);
					for(Operation o : opSet){
						p.at(o.getDefinition());
					}
					p.raise();
				}
				
			}
		}
		
	}

	@Override
	public void visit(MethodDeclNode functionDeclaration) {
		putEnum(functionDeclaration);

		if (!options.getParamBool(Parameter.TEST_CHECK_NATIVE_FUNCTION_BODIES)
				&& curSourceInfo.getType() == InclusionType.NATIVE) {
			return;
		}
		functionDeclaration.childrenAccept(this);
	}

	@Override
	public void visit(FieldDeclNode fieldDeclNode) {
		inGlobalField = true;
		putEnum(fieldDeclNode.getDeclaredVariables().get(0).getName());
		fieldDeclNode.childrenAccept(this);
		inGlobalField = false;
	}

	@Override
	public void visit(TypeAliasDeclNode typeAliasDeclNode) {
		putEnum(typeAliasDeclNode);
		typeAliasDeclNode.childrenAccept(this);
	}

	@Override
	public void visit(StructDeclNode structDeclNode) {
		putEnum(structDeclNode);
		structDeclNode.childrenAccept(this);
	}

	// ********** Expressions & Statemenst **********

	@Override
	public void visit(LocalVarDeclStmtNode l) {
		l.childrenAccept(this);
		LocalVarDecl v = (LocalVarDecl) l.getVarDeclaration().getDeclarators()
				.get(0).getName().getSemantics();
		if (v == null)
			throw new InternalProgramError(l, "Var decl without semantics");
		if (!v.isOnTop()) {
			Problem.ofType(ProblemId.GALAXY_LOCAL_VAR_NOT_ON_TOP).at(l).raise();
		}
	}

	@Override
	public void visit(SimpleTypeNode typeNode) {
		checkTypeAccess((IType) typeNode.getSemantics(), typeNode);
		typeNode.childrenAccept(this);
	}

	private void checkTypeAccess(IType semantics, TypeNode typeNode) {
		if (semantics.getCategory() == TypeCategory.STRUCT) {
			// Do the check
			Integer sourceId = enumeration.get(semantics.getDefinition());
			if (sourceId == null) {
				Problem
						.ofType(
								ProblemId.GALAXY_ACCESSING_ELEMENT_FROM_ABOVE_ITS_DECLARATION)
						.at(typeNode).details(semantics.getDescription())
						.raise();
			}
		}

	}

	@Override
	public void visit(NameExprNode nameExprNode) {
		nameExprNode.childrenAccept(this);
		NameAccess n = nameExprNode.getSemantics();
		checkNameAccess(n, nameExprNode);
	}

	@Override
	public void visit(FieldAccessExprNode fieldAccess) {
		fieldAccess.childrenAccept(this);
		NameAccess n = fieldAccess.getSemantics();
		checkNameAccess(n, fieldAccess);
	}

	@Override
	public void visit(MethodInvocationExprNode methodInvocation) {
		methodInvocation.childrenAccept(this);
		Invocation inv = methodInvocation.getSemantics();
		checkInvocation(inv, methodInvocation);
	}

	public void checkInvocation(Invocation inv, SyntaxNode where) {
		if (inv == null) {
			return;
		}
		Operation op = inv.getWhichFunction();

		// Get the forward declaration that is "highest"
		int highest = Integer.MAX_VALUE;
		Operation highestOp = op;
		while (true) {
			// Check if this one is higher
			Integer i = getEnum(op.getDefinition());
			if (i != null && i < highest) {
				highest = i;
				highestOp = op;
			}

			// Move up override hierarchy
			Operation overridden = op.getOverrideInformation()
					.getOverridenMethod();
			if (overridden == null)
				break;
			op = overridden;
		}
		op = highestOp;

		// Do the check
		Integer sourceId = enumeration.get(op.getDefinition());
		if (sourceId == null) {
			Problem
					.ofType(
							ProblemId.GALAXY_ACCESSING_ELEMENT_FROM_ABOVE_ITS_DECLARATION)
					.at(where).details(op.getDescription()).raise();
		}
	}

	public void checkNameAccess(NameAccess n, SyntaxNode where) {
		if( inGlobalField ){
			//Do not do name access checks for global fields
			//since these are already done by Andromeda
			return;
		}
		if (n == null) {
			return;
		}
		switch (n.getAccessType()) {
		case VAR:
			break;
		case ACCESSOR:
		case OP_POINTER:
		case PACKAGE:
		case TYPE:
			throw Problem.ofType(ProblemId.GALAXY_DISALLOWED_NAME_TYPE).at(
					where).details(n.getAccessType().name().toLowerCase())
					.raiseUnrecoverable();
		}

		// Check order
		Variable var = (Variable) n.getAccessedElement();
		if (!(var.getVarType() == VarType.GLOBAL)) {
			return;
		}
		Integer sourceId = enumeration.get(var.getDefinition());
		if (sourceId == null || sourceId == curNum) {
			Problem
					.ofType(
							ProblemId.GALAXY_ACCESSING_ELEMENT_FROM_ABOVE_ITS_DECLARATION)
					.at(where).details(var.getDescription()).raise();
		}
	}


}
