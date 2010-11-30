package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.access.AccessorAccess;
import com.sc2mod.andromeda.environment.access.ConstructorInvocation;
import com.sc2mod.andromeda.environment.access.Invocation;
import com.sc2mod.andromeda.environment.access.InvocationType;
import com.sc2mod.andromeda.environment.access.NameAccess;
import com.sc2mod.andromeda.environment.access.VarAccess;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.OperationUtil;
import com.sc2mod.andromeda.environment.scopes.UsageType;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.content.NameResolver;
import com.sc2mod.andromeda.environment.scopes.content.ResolveUtil;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeUtil;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.types.basic.BasicTypeSet;
import com.sc2mod.andromeda.environment.types.basic.SpecialType;
import com.sc2mod.andromeda.environment.types.casting.CastUtil;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.variables.VarType;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
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
	private IType rightSideType;
	
	//private IScope staticPrefixScope;
	
	public ExpressionAnalysisVisitor(StatementAnalysisVisitor parent){
		this.parent = parent;
		this.nameResolver = parent.nameResolver;
		this.exprResolver = new ExpressionAnalyzer(new ConstantResolveVisitor(), parent.typeProvider);
	}
	//************** EXPRESSIONS (infere types, do local var stuff, resolve invocations and field accesses) **************
	
	
//	@Override
//	public void visit(ArrayTypeNode arrayType, ExpressionContext context) {
//		arrayType.getDimension().accept(this,ExpressionContext.DEFAULT);
//	}
	
	@Override
	public void visit(AssignmentExprNode assignment, ExpressionContext context) {	
			
		
		
		//check right side
		ExprNode rExpr = assignment.getRightExpression();
		rExpr.accept(this,ExpressionContext.DEFAULT);
		
		IType rightSideTypeBefore = rightSideType;
		rightSideType = rExpr.getInferedType();
		
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
		rightSideType = rightSideTypeBefore;
		
		//Is the assignment type valid?
		if(!rExpr.getInferedType().canImplicitCastTo(lExpr.getInferedType())){
			throw Problem.ofType(ProblemId.TYPE_ERROR_INCOMPATIBLE_ASSIGNMENT).at(assignment)
							.details(rExpr.getInferedType(),lExpr.getInferedType())
							.raiseUnrecoverable();
		}
			
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
		BasicTypeSet BASIC = parent.typeProvider.BASIC;
		
		//Literals are constant
		le.setConstant(true);
		
		switch(type){
		case BOOL:
			le.setInferedType(BASIC.BOOL);
			break;
		case STRING:
			le.setInferedType(BASIC.STRING);
			break;
		case INT:
			le.setInferedType(BASIC.INT);
			break;
		case CHAR:
			le.setInferedType(BASIC.CHAR);
			break;
		case FLOAT:
			le.setInferedType(BASIC.FLOAT);
			break;
		case NULL:
			le.setInferedType(BASIC.NULL);
			break;
		case TEXT:
			le.setInferedType(BASIC.TEXT);
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
		IType type = parent.typeProvider.resolveType(castExpression.getType(),parent.curScope);
		
		//Infere right expression type
		castExpression.getRightExpression().accept(this,ExpressionContext.DEFAULT);
		IType rightType = castExpression.getRightExpression().getInferedType();
		
		//Is cast possible
		boolean unchecked = castExpression.isUnchecked();
		if(CastUtil.canExplicitCast(rightType, type, unchecked)){
			castExpression.setInferedType(type);
		} else {
			//If we do a checked cast but only unchecked is possible, give a more
			//concise error message
			if(!unchecked && CastUtil.canExplicitCast(rightType, type, true)){
				throw Problem.ofType(ProblemId.TYPE_ERROR_NEED_UNCHECKED_CAST).at(castExpression)
					.details(rightType,type)
					.raiseUnrecoverable();
			}
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
		IType t = arrayAccess.getLeftExpression().getInferedType();
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
		IType t;
		c.setInferedType(t = parent.typeProvider.resolveType(c.getType(),parent.curScope));
		
		
		if(t.getCategory() == TypeCategory.CLASS){
			ConstructorInvocation ci = parent.resolveConstructorCall(c,(IClass) t,
					new Signature(c.getArguments()), parent.curScope, false,
					false);
			c.setSemantics(ci);
		} else {
			throw Problem.ofType(ProblemId.NEW_NON_CLASS).at(c)
					.raiseUnrecoverable();
		}

	}
	
	@Override
	public void visit(ConditionalExprNode conditionalExpression, ExpressionContext context) {
		//Infere subexpression types
		conditionalExpression.childrenAccept(this,ExpressionContext.DEFAULT);
		
		//Check type validity
		if (conditionalExpression.getLeftExpression().getInferedType() != parent.typeProvider.BASIC.BOOL) {
			throw Problem.ofType(ProblemId.TYPE_ERROR_NONBOOL_CONDITIONAL).at(conditionalExpression.getLeftExpression())
							.details(conditionalExpression.getLeftExpression().getInferedType())
							.raiseUnrecoverable();
		}
		IType type1 = conditionalExpression.getRightExpression().getInferedType();
		IType type2 = conditionalExpression.getRightExpression2().getInferedType();
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
	
	private IScope getPrefixScope(ExprNode prefix){
		IScope prefixScope = prefix.getInferedType();
		
		if(prefixScope == null){
			/* 
			 * The prefix has no infered type. This means that it must be a package
			 * or type.
			 * 
			 * The scope for the prefix can be set to the staticPrefixScope variable,
			 * since the prefix sets this variable for packages and types.
			 */
			prefixScope = ((NameAccess)prefix.getSemantics()).getPrefixScope();
		}
		
		return prefixScope;
	}
	
	private void checkResolvedVar(NameAccess elem, ExprNode where, ExpressionContext context){
		where.setSemantics(elem);
		switch(elem.getAccessType()){
		case PACKAGE:
		case TYPE:
			//If we have found a package or type, we must be in a prefix. Otherwise, these make no sense
			if(context != ExpressionContext.SCOPE_PREFIX){
				throw Problem.ofType(ProblemId.NAME_EXPR_IS_NOT_A_VAR_OR_OP).at(where)
					.details(elem.getAccessedElement().getElementType().name().toLowerCase())
					.raiseUnrecoverable();
			}
			break;
		case OP_POINTER:
			//TODO: Op pointer const handling
		case VAR:
		
			
			//Is it const?
			Variable vd = ((VarAccess)elem).getAccessedElement();
			if(vd.isConst()){
				where.setConstant(true);
				where.accept(parent.constResolve);
			}
			
			//Set infered type
			where.setInferedType(vd.getType());
			
			//In field declaration? If so, test order
			if(parent.curField != null){
				boolean possibleMisuse = false;
				Variable curField = parent.curField;
				if(!curField.isStatic()){
					//For non static fields, only test order if
					//the accessed field is also non static
					if(vd.isStatic())
						break;
					
					//Only error, if either simple name, or field access with this prefix
					//(other instances can be accessed, of course)
					if(where instanceof NameExprNode){
						possibleMisuse = true;
					} else {
						ExprNode prefix = where.getLeftExpression();
						possibleMisuse = (prefix instanceof ThisExprNode);
					}
					
					//Only error if the field is not from a super class
					if(possibleMisuse){
						if(!curField.getContainingType().equals(vd.getContainingType()))
							break;
					}
					
					
				} else {
					//For static fields, ordering always counts
					possibleMisuse = true;
				}
				
				//Check if this is a misuse
				if(!possibleMisuse)
					break;
				if(curField.getDeclarationIndex()<=vd.getDeclarationIndex()){
					if(curField.getDeclarationIndex()<vd.getDeclarationIndex()){
						Problem.ofType(curField.isStatic() ? ProblemId.GLOBAL_VAR_ACCESS_BEFORE_DECL : ProblemId.FIELD_ACCESS_BEFORE_DECL).at(where)
							.raise();
					} else {
						Problem.ofType(ProblemId.VAR_ACCESS_IN_OWN_DECL).at(where)
							.raise();
					}
				}
				
			} else {
				//Otherwise, we are in a method. So we need to do local self access checks
				if(vd.getVarType() == VarType.LOCAL){
					//If we re in the init of a local var decl and this name references
					//the var itself, we got the error
					if(parent.curLocal != null && vd == parent.curLocal){
						Problem.ofType(ProblemId.VAR_ACCESS_IN_OWN_DECL).at(where)
							.raise();
					}
				}
			}
			
			break;
		case ACCESSOR:
			AccessorAccess ac = (AccessorAccess) elem;
			where.setInferedType(ac.getType());
		}
		

	}
	
	@Override
	public void visit(NameExprNode nameExprNode, ExpressionContext context) {

		UsageType accessType;
		IType rightSideType;
		switch(context){
		case LRVALUE:	accessType = UsageType.LRVALUE; rightSideType = null; break;
		case LVALUE:	accessType = UsageType.LVALUE; rightSideType = this.rightSideType; break;
		default: 		accessType = UsageType.RVALUE; rightSideType = this.rightSideType; break;
		}
		
		//Resolve the name
		NameAccess elem = nameResolver.resolveName(nameExprNode.getName(), parent.curScope, accessType, nameExprNode, rightSideType);

		//Do checks, set semantics and infered type
		checkResolvedVar(elem, nameExprNode,context);
		
	
	}
	
	@Override
	public void visit(FieldAccessExprNode fieldAccess, ExpressionContext context) {
		
		//Process prefix
		ExprNode prefix = fieldAccess.getLeftExpression();
		prefix.accept(this,ExpressionContext.SCOPE_PREFIX);
		
		boolean staticAccess = isPrefixStatic(prefix);
		IScope prefixScope = getPrefixScope(prefix);
		
		UsageType accessType;
		IType rightSideType;
		switch(context){
		case LRVALUE:	accessType = UsageType.LRVALUE; rightSideType = null; break;
		case LVALUE:	accessType = UsageType.LVALUE; rightSideType = this.rightSideType; break;
		default: 		accessType = UsageType.RVALUE; rightSideType = this.rightSideType; break;
		}
		
		//Resolve the name
		NameAccess elem = ResolveUtil.resolvePrefixedName(prefixScope, fieldAccess.getName(), parent.curScope, accessType, fieldAccess,staticAccess, rightSideType);
		
		//Do checks, set semantics and infered type
		checkResolvedVar(elem, fieldAccess,context);
	}
	
	@Override
	public void visit(ExprListNode exprListNode, ExpressionContext state) {
		exprListNode.childrenAccept(this,state);
	}
	
	@Override
	public void visit(MethodInvocationExprNode methodInvocation, ExpressionContext context) {
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
			IScope prefixScope = getPrefixScope(prefix);
			
			//Do we have a super prefix? If so, the invocation cannot be virtual
			disallowVirtualInvocation = prefix instanceof SuperExprNode;
			
			//Resolve the prefixed operation
			inv = ResolveUtil.resolvePrefixedInvocation(prefixScope, methodInvocation.getFuncName(), sig, parent.curScope, methodInvocation, true, staticAccess, disallowVirtualInvocation);
			
			//Nothing found?
			if(inv == null)
				throw Problem.ofType(ProblemId.NO_SUCH_METHOD).at(methodInvocation)
					.details(methodInvocation.getFuncName(),sig.getFullName(),prefixScope)
					.raiseUnrecoverable();
			
		} else {
			
			//Resolve the non-prefixed operation
			inv = nameResolver.resolveInvocation(methodInvocation.getFuncName(), sig, parent.curScope, methodInvocation, true, disallowVirtualInvocation);
			
			//Nothing found?
			if(inv == null)
				throw Problem.ofType(ProblemId.NO_SUCH_FUNCTION).at(methodInvocation)
					.details(methodInvocation.getFuncName(),sig.getFullName())
					.raiseUnrecoverable();
			
		}	
		
		
		//Dangling forward decl check
		if(OperationUtil.isForwardDeclaration(inv.getWhichFunction())){
			Problem.ofType(ProblemId.DANGLING_FORWARD_DECLARATION).at(methodInvocation)
				.details(OperationUtil.getNameAndSignature(inv.getWhichFunction()))
				.raise();
		}
		
		//Set semantics and type
		IType type = inv.getReturnType();
		methodInvocation.setSemantics(inv);
		methodInvocation.setInferedType(type);
	}
	

	@Override
	public void visit(KeyOfExprNode keyOfExpression, ExpressionContext context) {
		IType t = parent.typeProvider.resolveType(keyOfExpression.getType(),parent.curScope);
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
		
		boolean misuse = false;
		if(parent.curField == null) {
			misuse = parent.curOperation.isStatic();
		} else {
			misuse = parent.curField.isStatic();
			
		}
		if(misuse)
			throw Problem.ofType(ProblemId.THIS_IN_STATIC_MEMBER).at(thisExpression)
					.raiseUnrecoverable();
		thisExpression.setInferedType(parent.curType);
	}
	
	@Override
	public void visit(SuperExprNode superExprNode, ExpressionContext context) {
		IType curType = parent.curType;
		if(curType == null  || !TypeUtil.isClass(curType))
			throw Problem.ofType(ProblemId.SUPER_OUTSIDE_OF_CLASS).at(superExprNode)
				.raiseUnrecoverable();
		IClass curClass = ((IClass) curType).getSuperClass();
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
