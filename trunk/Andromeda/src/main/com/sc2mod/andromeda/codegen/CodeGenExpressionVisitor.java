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

import com.sc2mod.andromeda.classes.ClassGenerator;
import com.sc2mod.andromeda.codegen.buffers.SimpleBuffer;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.access.AccessType;
import com.sc2mod.andromeda.environment.access.AccessUtil;
import com.sc2mod.andromeda.environment.access.ConstructorInvocation;
import com.sc2mod.andromeda.environment.access.Invocation;
import com.sc2mod.andromeda.environment.access.InvocationType;
import com.sc2mod.andromeda.environment.access.NameAccess;
import com.sc2mod.andromeda.environment.access.VarAccess;
import com.sc2mod.andromeda.environment.operations.Method;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.TypeUtil;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.types.basic.BasicTypeSet;
import com.sc2mod.andromeda.environment.types.basic.SpecialType;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.AssignmentExprNode;
import com.sc2mod.andromeda.syntaxNodes.BinOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.CastExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocationExprNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.NewExprNode;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExprNode;
import com.sc2mod.andromeda.syntaxNodes.ReturnStmtNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.ThisExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpSE;
import com.sc2mod.andromeda.syntaxNodes.VarAssignDeclNode;

public class CodeGenExpressionVisitor extends CodeGenerator {
	
	private CodeGenVisitor parent;
	ClassGenerator classGen;
	private Signature desiredSignature;
	private BasicTypeSet BASIC;
	private TypeCastCodeProvider typeCastProvider;
	
	public CodeGenExpressionVisitor(CodeGenVisitor codeGenVisitor, TypeProvider tp) {
		super(codeGenVisitor);
		this.parent = codeGenVisitor;
		this.classGen = codeGenVisitor.classGen;
		this.BASIC = tp.BASIC;
		this.typeCastProvider = new TypeCastCodeProvider(tp);
	}
	
	public Signature getDesiredSignature() {
		return desiredSignature;
	}

	public void setDesiredSignature(Signature desiredSignature) {
		this.desiredSignature = desiredSignature;
	}

	SimpleBuffer curExprBuffer = new SimpleBuffer(64);
	
	private void invokeSelf(SyntaxNode s){
		s.accept(this);
	}
	
	public void surroundTypeCastIfNecessary(ExprNode exp, IType to){
		IType from = exp.getInferedType();
		if(to==null||from==to){
			invokeSelf(exp);
			return;
		}

		
		//Special case for int to fixed if the value to be cast is a literal
		if(exp instanceof LiteralExprNode){
			if(from==BASIC.INT && to==BASIC.FLOAT){
				invokeSelf(exp);
				curExprBuffer.append(".0");
				return;
			} else if(from==BASIC.NULL){
				//For null literals we just set their type. This will trigger the correct code gen
				exp.setInferedType(to);
				invokeSelf(exp);
				return;				
			}
			
		}
		
		surround(curExprBuffer,exp,typeCastProvider.getCastOp(from, to));
	}
	


	
	protected void surround(SimpleBuffer buffer,SyntaxNode s, String left, String right){
		buffer.append(left);
		invokeSelf(s);
		buffer.append(right);
	}
	
	protected void surround(SimpleBuffer buffer,SyntaxNode s, StringPair toSurround){
		if(toSurround==null){
			invokeSelf(s);
			return;
		}
		buffer.append(toSurround.left);
		invokeSelf(s);
		buffer.append(toSurround.right);
	}
	
	
	@Override
	public void visit(LiteralExprNode literalExpression) {
		SimpleBuffer curBuffer = curExprBuffer;
		LiteralNode l = literalExpression.getLiteral();
		switch(l.getType()){
		case FLOAT:
		case BOOL:
		case INT:
			curBuffer.append(String.valueOf(l.getValue()));
			break;
		case STRING:
			String s = reEscape(String.valueOf(l.getValue()));
			curBuffer.appendStringLiteral(s, parent);
			break;
		case TEXT:
			curBuffer.append("StringToText(\"");
			curBuffer.append(reEscape(String.valueOf(l.getValue())));
			curBuffer.append("\")");
			break; 
		case CHAR:
			curBuffer.append("'");
			curBuffer.append(String.valueOf(l.getValue()));
			curBuffer.append("'");
			break;
		case NULL:
			curBuffer.append(literalExpression.getInferedType().getDefaultValueStr());
			break;
		default:
			throw new InternalProgramError(l,"Unknown literal type: " + l.getType());
		}
			
	}
	/**
	 * XPilot: re-escapes certain characters.
	 * @param s The string to re-escape.
	 * @return The re-escaped string.
	 */
	private static String reEscape(String s) {
		StringBuilder buffer = new StringBuilder(s.length());
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch(c) {
			case '\n': buffer.append("\\n");
				break;
			case '\r': buffer.append("\\r");
				break;
			case '\t': buffer.append("\\t");
				break;
			case '\f': buffer.append("\\f");
				break;
			case '\b': buffer.append("\\b");
				break;
			case '\"': case '\\': buffer.append('\\');
			default: buffer.append(c);
			}
		}
		return buffer.toString();
	}

	@Override
	public void visit(AssignmentExprNode assignment) {

		//This is a statement expression.
		invokeSelf(assignment.getLeftExpression());
		curExprBuffer.append(CodegenUtil.getAssignOp(assignment.getAssignOp(), whitespaceInExprs));
		surroundTypeCastIfNecessary(assignment.getRightExpression(),assignment.getLeftExpression().getInferedType());
		
	}
	
	@Override
	public void visit(BinOpExprNode binaryExpression) {

		surroundTypeCastIfNecessary(
				binaryExpression.getLeftExpression(),binaryExpression.getLeftExpectedType());
		
		curExprBuffer.append(CodegenUtil.getBinaryOp(binaryExpression.getBinOp(), whitespaceInExprs));
		
		surroundTypeCastIfNecessary(
				binaryExpression.getRightExpression(),binaryExpression.getRightExpectedType());
		
	}


	
	@Override
	public void visit(ThisExprNode thisExpression) {
		curExprBuffer.append(classGen.getThisName());
	}
	
	@Override
	public void visit(NameExprNode nameExprNode) {

		NameAccess access = nameExprNode.getSemantics();
		if(access.getAccessType() != AccessType.VAR)
			return;
		VarAccess vAccess = (VarAccess) access;
		
		boolean notStatic = !access.isStatic();
		if(AccessUtil.isClassFieldAccess(access)){
			IClass c = (IClass) access.getAccessedElement().getContainingType();
			classGen.generateFieldAccessPrefix(curExprBuffer,c);
			curExprBuffer.append(classGen.getThisName());
			classGen.generateFieldAccessSuffix(curExprBuffer,c);
		}
		try {
			curExprBuffer.append(vAccess.getAccessedElement().getGeneratedName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void visit(FieldAccessExprNode fieldAccess) {

		NameAccess access = fieldAccess.getSemantics();
		if(access.getAccessType() != AccessType.VAR)
			return;
		VarAccess vAccess = (VarAccess) access;
		
		boolean notStatic = !access.isStatic();
		
		//Infer left child expression (unless this is static)
		//if(notStatic)
		//	fieldAccess.childrenAccept(this);
		
		if(notStatic){
			IType t = fieldAccess.getLeftExpression().getInferedType();
			if(TypeUtil.isClass(t)){
				IClass c = (IClass)t;
				classGen.generateFieldAccessPrefix(curExprBuffer,c);
				fieldAccess.childrenAccept(this);
				classGen.generateFieldAccessSuffix(curExprBuffer,c);
			} else  {
				fieldAccess.childrenAccept(this);
				curExprBuffer.append(".");
			}
		}
		curExprBuffer.append(vAccess.getAccessedElement().getGeneratedName());
	}
	
	
	@Override
	public void visit(UnOpExprNode unaryExpression) {
		UnOpSE op = unaryExpression.getUnOp();
		String opStr = CodegenUtil.getUnaryOp(op);
		switch(op){
		case COMP:
		case MINUS:
		case NOT:
		case ADDRESSOF:
		case DEREFERENCE:
			curExprBuffer.append(opStr);
			invokeSelf(unaryExpression.getExpression());
			break;
		case PREPLUSPLUS:
		case PREMINUSMINUS:
		case POSTPLUSPLUS:
		case POSTMINUSMINUS:
		default: throw new InternalProgramError(unaryExpression,"Unkonwn unary operator type: " + op);
		}
	}
	
	@Override
	public void visit(ParenthesisExprNode parenthesisExpression) {
		curExprBuffer.append("(");
		invokeSelf(parenthesisExpression.getExpression());
		curExprBuffer.append(")");
	}
	
	public void generateMethodInvocation(SimpleBuffer curBuffer, Invocation i, ExprNode prefix, ExprListNode arguments){
		
		SimpleBuffer curExprBufferBefore = curExprBuffer;
		curExprBuffer = curBuffer;
		
		InvocationType accessType = i.getInvocationType();
		if(accessType==InvocationType.VIRTUAL){
			curBuffer.append(((Method) i.getWhichFunction()).getOverrideInformation().getVirtualCaller());
		} else {
			curBuffer.append(i.getWhichFunction().getGeneratedName());
		}
		curBuffer.append("(");
		
		boolean hasArguments = arguments!=null&&!arguments.isEmpty();
		
		switch(accessType){
		case VIRTUAL:
		case METHOD:
			if(prefix != null){
				invokeSelf(prefix);
			} else {
				curBuffer.append(classGen.getThisName());
			}
			if(hasArguments) curBuffer.append(",");
		}
		
		
		if(hasArguments){
			Signature sigBefore = desiredSignature;
			desiredSignature = i.getWhichFunction().getSignature();
			arguments.accept(this);
			desiredSignature = sigBefore;
		}
		curBuffer.append(")");
		
		curExprBuffer = curExprBufferBefore;
	}

	@Override
	public void visit(MethodInvocationExprNode methodInvocation) {
		Invocation i = (Invocation)methodInvocation.getSemantics();
		
		generateMethodInvocation(curExprBuffer, i, methodInvocation.getPrefix(), methodInvocation.getArguments());
	
		
	}
	
	@Override
	public void visit(CastExprNode castExpression) {
		IType t = castExpression.getInferedType();
		
		ExprNode e = castExpression.getRightExpression();
		IType et = e.getInferedType();
		if(t!=et){
			//If this is a cast to byte, check if we actually need it or if a cast to int is sufficient
			if(t.getBaseType()==BASIC.BYTE){
				SyntaxNode sn = castExpression.getParent();
				if(sn instanceof AssignmentExprNode){
					if(((AssignmentExprNode)sn).getInferedType().getBaseType()==BASIC.BYTE){
						t = BASIC.INT;
					}
				} else if(sn instanceof VarAssignDeclNode){
					if(((VarAssignDeclNode)sn).getName().getInferedType().getBaseType()==BASIC.BYTE){
						t = BASIC.INT;
					}
				} else if(sn instanceof ReturnStmtNode){
					if(((Operation)sn.getSemantics()).getReturnType().getBaseType()==BASIC.BYTE){
						t = BASIC.INT;
					}
				}
				
						
			}
			
			//If the argument of the cast is in parenthesis, we don't need these
			//since the cast adds parenthesis.
			StringPair op = typeCastProvider.getCastOp(et, t);
			if(op != null && e instanceof ParenthesisExprNode){
				e = e.getExpression();
			}
			surround(curExprBuffer,e,op);
		} else {
			invokeSelf(e);
		}
	}
	
	
	@Override
	public void visit(ExprListNode expressionList) {
		if(desiredSignature == null) throw new Error("No desired signature set!");
		int size = expressionList.size();
		SimpleBuffer curBuffer = curExprBuffer;
		Configuration options = this.options;
		
		for(int i=0;i<size;){
			ExprNode e = expressionList.elementAt(i);
			surroundTypeCastIfNecessary(e, desiredSignature.get(i));
			if(++i<size){
				if(whitespaceInExprs){
					curBuffer.append(", ");
				} else {
					curBuffer.append(",");
				}
			}
		}
	}
	
	@Override
	public void visit(VarAssignDeclNode variableAssignDecl) {
		SimpleBuffer buffer = curExprBuffer;
		if(whitespaceInExprs){
			buffer.append(" = ");
		} else {
			buffer.append("=");
		}
		
		surroundTypeCastIfNecessary(variableAssignDecl.getInitializer(), variableAssignDecl.getName().getInferedType());
	}
	
	@Override
	public void visit(ArrayAccessExprNode arrayAccess) {
		invokeSelf(arrayAccess.getLeftExpression());
		curExprBuffer.append("[");
		invokeSelf(arrayAccess.getRightExpression());
		curExprBuffer.append("]");
	}
	
	@Override
	public void visit(NewExprNode c) {
		ConstructorInvocation ci = (ConstructorInvocation) c.getSemantics();
		IClass clazz = (IClass)c.getInferedType();
		SimpleBuffer bufferBefore = parent.curBuffer;
		parent.curBuffer = curExprBuffer;
		classGen.generateConstructorInvocation(ci, c.getArguments(), clazz);
		parent.curBuffer = bufferBefore;
	}
	

}
