package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.ConstructorInvocation;
import com.sc2mod.andromeda.environment.operations.Invocation;
import com.sc2mod.andromeda.environment.operations.InvocationType;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.AccessType;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.scopes.ScopedElement;
import com.sc2mod.andromeda.environment.scopes.content.NameResolver;
import com.sc2mod.andromeda.environment.scopes.content.ResolveUtil;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.ArrayCreationExprNode;
import com.sc2mod.andromeda.syntaxNodes.ArrayTypeNode;
import com.sc2mod.andromeda.syntaxNodes.AssignmentExprNode;
import com.sc2mod.andromeda.syntaxNodes.BinOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.CastExprNode;
import com.sc2mod.andromeda.syntaxNodes.ConditionalExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.InstanceofExprNode;
import com.sc2mod.andromeda.syntaxNodes.KeyOfExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;
import com.sc2mod.andromeda.syntaxNodes.MetaClassExprNode;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocationExprNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.NewExprNode;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExprNode;
import com.sc2mod.andromeda.syntaxNodes.SpecialInvocationSE;
import com.sc2mod.andromeda.syntaxNodes.SuperExprNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.ThisExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpExprNode;
import com.sc2mod.andromeda.util.visitors.VoidResultErrorVisitor;

public class ExpressionAnalysisVisitor extends VoidResultErrorVisitor<ExpressionContext>{


	private final StatementAnalysisVisitor parent;
	private final NameResolver nameResolver;
	private final ExpressionAnalyzer exprResolver;
	
	private Scope staticPrefixScope;
	
	public ExpressionAnalysisVisitor(StatementAnalysisVisitor parent){
		this.parent = parent;
		this.nameResolver = parent.nameResolver;
		this.exprResolver = new ExpressionAnalyzer(new ConstantResolveVisitor(), parent.typeProvider);
	}
	//************** EXPRESSIONS (infere types, do local var stuff, resolve invocations and field accesses) **************
	
	
	@Override
	public void visit(ArrayTypeNode arrayType, ExpressionContext context) {
		arrayType.getDimensions().accept(this,ExpressionContext.DEFAULT);
	}
	
	@Override
	public void visit(AssignmentExprNode assignment, ExpressionContext context) {	
			
		
		//Visit left side as lValue
		ExprNode lExpr = assignment.getLeftExpression();
		switch(assignment.getAssignOp()){
		case EQ:
			lExpr.accept(this,ExpressionContext.LVALUE);
			break;
		default:
			lExpr.accept(this,ExpressionContext.LRVALUE);
			break;
		}
		
		ExprNode rExpr = assignment.getRightExpression();
		rExpr.accept(this,ExpressionContext.DEFAULT);
		
		//Is the assignment type valid?
		if(!rExpr.getInferedType().canImplicitCastTo(lExpr.getInferedType()))
			throw Problem.ofType(ProblemId.TYPE_ERROR_INCOMPATIBLE_ASSIGNMENT).at(assignment)
							.details(rExpr.getInferedType(),lExpr.getInferedType())
							.raiseUnrecoverable();
		
		//The type of an assignment is the type of its left side
		assignment.setInferedType(lExpr.getInferedType());
		
		//Constants cannot be assigned
		if(lExpr.isConstant()){
			throw Problem.ofType(ProblemId.CONST_VAR_REASSIGNED).at(lExpr)
					.raiseUnrecoverable();
		}
	}
	
	@Override
	public void visit(UnOpExprNode unaryExpression, ExpressionContext context){
		switch(unaryExpression.getUnOp()){
		case POSTMINUSMINUS:
		case PREMINUSMINUS:
		case POSTPLUSPLUS:
		case PREPLUSPLUS:
			//For increments/decrements, the operand is treated as L and RValue
			context = ExpressionContext.LRVALUE;
			break;
		default:
			context = ExpressionContext.DEFAULT;
		}
		unaryExpression.childrenAccept(this,context);
		exprResolver.analyze(unaryExpression);	
	}
	
	@Override
	public void visit(BinOpExprNode binaryExpression, ExpressionContext context) {
		//Visit children
		binaryExpression.childrenAccept(this,ExpressionContext.DEFAULT);

		exprResolver.analyze(binaryExpression);
	}
	
	@Override
	public void visit(LiteralExprNode le, ExpressionContext context){
		LiteralNode l = le.getLiteral();
		LiteralTypeSE type = l.getType();
		
		//Literals are constant
		le.setConstant(true);
		
		switch(type){
		case BOOL:
			le.setInferedType(BasicType.BOOL);
			break;
		case STRING:
			le.setInferedType(BasicType.STRING);
			break;
		case INT:
			le.setInferedType(BasicType.INT);
			break;
		case CHAR:
			le.setInferedType(BasicType.CHAR);
			break;
		case FLOAT:
			le.setInferedType(BasicType.FLOAT);
			break;
		case NULL:
			le.setInferedType(SpecialType.NULL);
			break;
		case TEXT:
			le.setInferedType(BasicType.TEXT);
			break;
		default:
			throw new InternalProgramError(l,"Literal of unknown literal type!");
		}
		
		//Get constant value
		le.accept(parent.constResolve);
	}
	
	@Override
	public void visit(CastExprNode castExpression, ExpressionContext context) {
		//Get the type to cast to
		Type type = parent.typeProvider.resolveType(castExpression.getType(),parent.curScope);
		
		//Infere right expression type
		castExpression.getRightExpression().accept(this,ExpressionContext.DEFAULT);
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
		if(castExpression.getRightExpression().isConstant()){
			castExpression.setConstant(true);
			castExpression.accept(parent.constResolve);
		}
		
	}
	
	@Override
	public void visit(ArrayAccessExprNode arrayAccess, ExpressionContext context) {
		arrayAccess.childrenAccept(this,ExpressionContext.DEFAULT);
		//Check if an array access is possible
		Type t = arrayAccess.getLeftExpression().getInferedType();
		if(t.getCategory()!=TypeCategory.ARRAY){
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
	public void visit(ArrayCreationExprNode arrayCreationExpression, ExpressionContext context) {
		throw new InternalProgramError(arrayCreationExpression,"Array creation is not yet possible!");
	}
	
	@Override
	public void visit(NewExprNode c, ExpressionContext context) {
		c.getArguments().accept(this,ExpressionContext.DEFAULT);
		Type t;
		c.setInferedType(t = parent.typeProvider.resolveType(c.getType(),parent.curScope));
		
		
		switch (t.getCategory()) {
		case GENERIC_CLASS:
		case CLASS:
			
			ConstructorInvocation ci = parent.resolveConstructorCall(c,(Class) t,
					new Signature(c.getArguments()), parent.curScope, false,
					false);
			c.setSemantics(ci);
			break;
		default:
			throw Problem.ofType(ProblemId.NEW_NON_CLASS).at(c)
					.raiseUnrecoverable();
		}

	}
	
	@Override
	public void visit(ConditionalExprNode conditionalExpression, ExpressionContext context) {
		//Infere subexpression types
		conditionalExpression.childrenAccept(this,ExpressionContext.DEFAULT);
		
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
	

	
	private boolean isPrefixStatic(ExprNode prefix){
		if(prefix.getInferedType() == null){
			/* 
			 * If the infered type was null, then the prefix
			 * was either a package or a type name. In both cases,
			 * the access is considered static.
			 */
			return true;
		} else {
			//If the prefix has an infered type, then this is no static access
			return false;
		}
	}
	
	private Scope getPrefixScope(ExprNode prefix){
		Scope prefixScope = prefix.getInferedType();
		
		if(prefixScope == null){
			/* 
			 * The prefix has no infered type. This means that it must be a package
			 * or type.
			 * 
			 * The scope for the prefix can be set to the staticPrefixScope variable,
			 * since the prefix sets this variable for packages and types.
			 */
			prefixScope = staticPrefixScope;
		}
		
		return prefixScope;
	}
	
	private VarDecl checkResolvedVar(ScopedElement elem, ExprNode where, ExpressionContext context){
		VarDecl vd = null;
		Type t = null;
		switch(elem.getElementType()){
		case ERROR: //FIXME: Error handling
			throw new Error("!");
		case VAR:
			vd = (VarDecl) elem;
			t = vd.getType();
			break;
		case OPERATION:
			vd = ((Operation)elem).getPointerDecl(parent.typeProvider);
			t = vd.getType();
			break;
		case TYPE:
		case PACKAGE:
			//If this is a type or package prefix, we set it as static
			//field scope so it can be used to look up its content in a field access
			//or method invocation expression.
			staticPrefixScope = (Scope) elem;
		}
		
		//Set semantics
		if(vd == null){
			//If we have found a package or type, we must be in a prefix. Otherwise, these make no sense
			if(context != ExpressionContext.SCOPE_PREFIX){
				throw Problem.ofType(ProblemId.NAME_EXPR_IS_NOT_A_VAR_OR_OP).at(where)
					.details(elem.getElementType().name().toLowerCase())
					.raiseUnrecoverable();
			}
		} else {
			where.setSemantics(vd);
			
			//Is it const?
			if(vd.isConst()){
				where.setConstant(true);
				where.accept(parent.constResolve);
			}
		}
		
		//Set infered type
		if(t != null){
			where.setInferedType(t);
		}
		return vd;
	}
	
	@Override
	public void visit(NameExprNode nameExprNode, ExpressionContext context) {

		//FIXME: Use correct access type
		
		//Resolve the name
		ScopedElement elem = nameResolver.resolveName(nameExprNode.getName(), parent.curScope, AccessType.RVALUE, nameExprNode);

		if(elem == null) 
			throw Problem.ofType(ProblemId.VAR_NAME_NOT_FOUND).at(nameExprNode).details(nameExprNode.getName())
				.raiseUnrecoverable();
		
		//Do checks, set semantics and infered type
		checkResolvedVar(elem, nameExprNode,context);
		
	
	}
	
	@Override
	public void visit(FieldAccessExprNode fieldAccess, ExpressionContext context) {
		
		//Process prefix
		ExprNode prefix = fieldAccess.getLeftExpression();
		prefix.accept(this,ExpressionContext.SCOPE_PREFIX);
		
		boolean staticAccess = isPrefixStatic(prefix);
		Scope prefixScope = getPrefixScope(prefix);
		
		//FIXME: Use correct access type
		
		//Resolve the name
		ScopedElement elem = ResolveUtil.resolvePrefixedName(prefixScope, fieldAccess.getName(), parent.curScope, AccessType.RVALUE, fieldAccess,staticAccess);
		
		//FIXME correct error response
		if(elem == null) throw new Error("Could not resolve (see copyOfNameResolver)");
		
		//Do checks, set semantics and infered type
		VarDecl va = checkResolvedVar(elem, fieldAccess,context);
	
	
		
		//In global declaration? If so, test order
		//TODO: Reimplement order checking
//		if(inGlobalVarDecl){
//			if(va.getIndex()>=varDeclIndex)
//				throw Problem.ofType(ProblemId.GLOBAL_VAR_ACCESS_BEFORE_DECL).at(fieldAccess)
//						.raiseUnrecoverable();
//		}
	}
	
	@Override
	public void visit(ExprListNode exprListNode, ExpressionContext state) {
		exprListNode.childrenAccept(this,state);
	}
	
	@Override
	public void visit(MethodInvocationExprNode methodInvocation, ExpressionContext context) {
		//FIXME: Hier check dass alle Besonderheiten aus dem CopyOfNameResolver beruecksichtigt wurden
		if(methodInvocation.getSpecial() == SpecialInvocationSE.NATIVE){
			//FIXME handle native prefix function calls
			throw new InternalProgramError(methodInvocation,"Native prefix not yet supported");
		}
		
		//Visit arguments and build call signature from them
		methodInvocation.getArguments().accept(this,ExpressionContext.DEFAULT);
		Signature sig = new Signature(methodInvocation.getArguments());
		
		//Visit prefix, if there is any
		ExprNode prefix = methodInvocation.getPrefix();
		
		//Resolve the method/function name
		Invocation inv;
		boolean disallowVirtualInvocation = false;
		if(prefix != null){
			
			//Visit prefix
			prefix.accept(this,ExpressionContext.SCOPE_PREFIX);
			boolean staticAccess = isPrefixStatic(prefix);
			Scope prefixScope = getPrefixScope(prefix);
			
			//Do we have a super prefix? If so, the invocation cannot be virtual
			disallowVirtualInvocation = prefix instanceof SuperExprNode;
			
			//Resolve the prefixed operation
			inv = ResolveUtil.resolvePrefixedInvocation(prefixScope, methodInvocation.getFuncName(), sig, parent.curScope, methodInvocation, true, staticAccess);
			
			//Nothing found?
			if(inv == null)
				throw Problem.ofType(ProblemId.NO_SUCH_METHOD).at(methodInvocation)
					.details(methodInvocation.getFuncName(),sig.getFullName(),prefixScope)
					.raiseUnrecoverable();
				
			
		} else {
			
			//Resolve the non-prefixed operation
			inv = nameResolver.resolveInvocation(methodInvocation.getFuncName(), sig, parent.curScope, methodInvocation, true);
			
			//Nothing found?
			if(inv == null)
				throw Problem.ofType(ProblemId.NO_SUCH_FUNCTION).at(methodInvocation)
					.details(methodInvocation.getFuncName(),sig.getFullName())
					.raiseUnrecoverable();
				
		}
		
		//Set semantics and type
		Type type = inv.getReturnType();
		methodInvocation.setSemantics(inv);
		methodInvocation.setInferedType(type);
	}
	

	@Override
	public void visit(KeyOfExprNode keyOfExpression, ExpressionContext context) {
		Type t = parent.typeProvider.resolveType(keyOfExpression.getType(),parent.curScope);
		keyOfExpression.setInferedType(t);
		keyOfExpression.setConstant(true);
		keyOfExpression.accept(parent.constResolve);
	}
	
	@Override
	public void visit(InstanceofExprNode instanceofExpression, ExpressionContext context) {
		throw new InternalProgramError(instanceofExpression,"Instanceof not implemented yet!");
	}
	
	@Override
	public void visit(MetaClassExprNode metaClassExpression, ExpressionContext context) {
		throw new InternalProgramError(metaClassExpression,".class not implemented yet!");
	}
	

	
	@Override
	public void visit(ThisExprNode thisExpression, ExpressionContext context) {
		if(parent.curType==null) 
			throw Problem.ofType(ProblemId.THIS_OUTSIDE_CLASS_OR_ENRICHMENT).at(thisExpression)
					.raiseUnrecoverable();
		if(parent.curOperation.isStatic())
			throw Problem.ofType(ProblemId.THIS_IN_STATIC_MEMBER).at(thisExpression)
					.raiseUnrecoverable();
		thisExpression.setInferedType(parent.curType);
	}
	
	@Override
	public void visit(SuperExprNode superExprNode, ExpressionContext context) {
		Type curType = parent.curType;
		if(curType == null  || !curType.isClass())
			throw Problem.ofType(ProblemId.SUPER_OUTSIDE_OF_CLASS).at(superExprNode)
				.raiseUnrecoverable();
		Class curClass = ((Class) curType).getSuperClass();
		if(curClass == null)
			throw Problem.ofType(ProblemId.SUPER_IN_TOP_CLASS).at(superExprNode)
				.raiseUnrecoverable();
		superExprNode.setInferedType(curClass);
	}
	
	@Override
	public void visit(ParenthesisExprNode parenthesisExpression, ExpressionContext context) {
		ExprNode e = parenthesisExpression.getExpression();
		
		//The context of the expression in the parenthesis is the same as the context of the
		//parenthesis expression itself!
		//This is the only situation where the context is directly passed on.
		e.accept(this,context);
		
		parenthesisExpression.setInferedType(e.getInferedType());
		
		if(e.isConstant()){
			parenthesisExpression.setConstant(true);
			parenthesisExpression.setValue(e.getValue());
		}
	}
	
}
