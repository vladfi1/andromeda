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
import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.ConstructorInvocation;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Invocation;
import com.sc2mod.andromeda.environment.Method;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.syntaxNodes.AccessType;
import com.sc2mod.andromeda.syntaxNodes.ArrayAccess;
import com.sc2mod.andromeda.syntaxNodes.Assignment;
import com.sc2mod.andromeda.syntaxNodes.BinaryExpression;
import com.sc2mod.andromeda.syntaxNodes.CastExpression;
import com.sc2mod.andromeda.syntaxNodes.ClassInstanceCreationExpression;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.ExpressionList;
import com.sc2mod.andromeda.syntaxNodes.FieldAccess;
import com.sc2mod.andromeda.syntaxNodes.Literal;
import com.sc2mod.andromeda.syntaxNodes.LiteralExpression;
import com.sc2mod.andromeda.syntaxNodes.LiteralType;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocation;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExpression;
import com.sc2mod.andromeda.syntaxNodes.ReturnStatement;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.ThisExpression;
import com.sc2mod.andromeda.syntaxNodes.UnaryExpression;
import com.sc2mod.andromeda.syntaxNodes.UnaryOperator;
import com.sc2mod.andromeda.syntaxNodes.VariableAssignDecl;
import com.sc2mod.andromeda.syntaxNodes.VisitorAdaptor;

public class CodeGenExpressionVisitor extends CodeGenerator {
	
	private CodeGenVisitor parent;
	ClassGenerator classGen;
	private Signature desiredSignature;
	
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
	
	public void surroundTypeCastIfNecessary(Expression exp, Type to){
		Type from = exp.getInferedType();
		if(to==null||from==to){
			invokeSelf(exp);
			return;
		}

		
		//Special case for int to fixed if the value to be cast is a literal
		if(exp instanceof LiteralExpression){
			if(from==BasicType.INT && to==BasicType.FLOAT){
				invokeSelf(exp);
				curExprBuffer.append(".0");
				return;
			} else if(from==SpecialType.NULL){
				//For null literals we just set their type. This will trigger the correct code gen
				exp.setInferedType(to);
				invokeSelf(exp);
				return;				
			}
			
		}
		
		surround(curExprBuffer,exp,CodegenUtil.getCastOp(from, to));
	}
	

	public CodeGenExpressionVisitor(CodeGenVisitor codeGenVisitor) {
		super(codeGenVisitor);
		this.parent = codeGenVisitor;
		this.classGen = codeGenVisitor.classGen;
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
	public void visit(LiteralExpression literalExpression) {
		SimpleBuffer curBuffer = curExprBuffer;
		Literal l = literalExpression.getLiteral();
		switch(l.getType()){
		case LiteralType.FLOAT:
		case LiteralType.BOOL:
		case LiteralType.INT:
			curBuffer.append(String.valueOf(l.getValue()));
			break;
		case LiteralType.STRING:
			String s = reEscape(String.valueOf(l.getValue()));
			curBuffer.appendStringLiteral(s, parent);
			break;
		case LiteralType.TEXT:
			curBuffer.append("StringToText(\"");
			curBuffer.append(reEscape(String.valueOf(l.getValue())));
			curBuffer.append("\")");
			break; 
		case LiteralType.CHAR:
			curBuffer.append("'");
			curBuffer.append(String.valueOf(l.getValue()));
			curBuffer.append("'");
			break;
		case LiteralType.NULL:
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
	public void visit(Assignment assignment) {

		//This is a statement expression.
		invokeSelf(assignment.getLeftExpression());
		curExprBuffer.append(CodegenUtil.getAssignOp(assignment.getOperator(), whitespaceInExprs));
		surroundTypeCastIfNecessary(assignment.getRightExpression(),assignment.getLeftExpression().getInferedType());
		
	}
	
	@Override
	public void visit(BinaryExpression binaryExpression) {

		surroundTypeCastIfNecessary(
				binaryExpression.getLeftExpression(),binaryExpression.getLeftExpectedType());
		
		curExprBuffer.append(CodegenUtil.getBinaryOp(binaryExpression.getOperator(), whitespaceInExprs));
		
		surroundTypeCastIfNecessary(
				binaryExpression.getRightExpression(),binaryExpression.getRightExpectedType());
		
	}


	
	@Override
	public void visit(ThisExpression thisExpression) {
		curExprBuffer.append(classGen.getThisName());
	}
	
	@Override
	public void visit(FieldAccess fieldAccess) {

		int accessType = fieldAccess.getAccessType();
		VarDecl decl = (VarDecl)fieldAccess.getSemantics();
		
		boolean notStatic = !decl.isStatic();
		
		//Infer left child expression (unless this is static)
		//if(notStatic)
		//	fieldAccess.childrenAccept(this);
		
		switch(accessType){
		case AccessType.SIMPLE:
			if(decl.getDeclType()==VarDecl.TYPE_FIELD){
				Class c = (Class) decl.getContainingType();
				classGen.generateFieldAccessPrefix(curExprBuffer,c);
				curExprBuffer.append(classGen.getThisName());
				classGen.generateFieldAccessSuffix(curExprBuffer,c);
			}
			try {
				curExprBuffer.append(decl.getGeneratedName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case AccessType.EXPRESSION:
			if(notStatic){
				Type t = fieldAccess.getLeftExpression().getInferedType();
				if(t.isClass()){
					Class c = (Class)t;
					classGen.generateFieldAccessPrefix(curExprBuffer,c);
					fieldAccess.childrenAccept(this);
					classGen.generateFieldAccessSuffix(curExprBuffer,c);
				} else  {
					fieldAccess.childrenAccept(this);
					curExprBuffer.append(".");
				}
			}
			curExprBuffer.append(decl.getGeneratedName());
			break;
		case AccessType.POINTER:
			curExprBuffer.append("->");
			curExprBuffer.append(decl.getGeneratedName());
			break;
		case AccessType.SUPER:
			if(notStatic)
				curExprBuffer.append("this->");
			curExprBuffer.append(decl.getGeneratedName());
			break;
		default:
			throw new InternalProgramError(fieldAccess,"Field access type " + accessType + " not supported.");
		}
	}
	
	
	@Override
	public void visit(UnaryExpression unaryExpression) {
		int op = unaryExpression.getOperator();
		String opStr = CodegenUtil.getUnaryOp(op);
		switch(op){
		case UnaryOperator.COMP:
		case UnaryOperator.MINUS:
		case UnaryOperator.NOT:
		case UnaryOperator.ADDRESSOF:
		case UnaryOperator.DEREFERENCE:
			curExprBuffer.append(opStr);
			invokeSelf(unaryExpression.getExpression());
			break;
		case UnaryOperator.PREPLUSPLUS:
		case UnaryOperator.PREMINUSMINUS:
		case UnaryOperator.POSTPLUSPLUS:
		case UnaryOperator.POSTMINUSMINUS:
		default: throw new InternalProgramError(unaryExpression,"Unkonwn unary operator type: " + op);
		}
	}
	
	@Override
	public void visit(ParenthesisExpression parenthesisExpression) {
		curExprBuffer.append("(");
		invokeSelf(parenthesisExpression.getExpression());
		curExprBuffer.append(")");
	}
	
	public void generateMethodInvocation(SimpleBuffer curBuffer, Invocation i, int invocationType, Expression prefix, ExpressionList arguments){
		
		SimpleBuffer curExprBufferBefore = curExprBuffer;
		curExprBuffer = curBuffer;
		
		int accessType = i.getAccessType();
		if(accessType==Invocation.TYPE_VIRTUAL){
			curBuffer.append(((Method) i.getWhichFunction()).getVirtualCaller());
		} else {
			curBuffer.append(i.getWhichFunction().getGeneratedName());
		}
		curBuffer.append("(");
		
		boolean hasArguments = arguments!=null&&!arguments.isEmpty();
		
		switch(accessType){
		case Invocation.TYPE_VIRTUAL:
		case Invocation.TYPE_METHOD:
			//Method call, add first parameter
			switch(invocationType){
				case AccessType.SUPER:
				case AccessType.SIMPLE:
					curBuffer.append(classGen.getThisName());
					if(hasArguments)curBuffer.append(",");
					break;
				case AccessType.EXPRESSION:
					invokeSelf(prefix);
					if(hasArguments)curBuffer.append(",");
					break;
				default:
					throw new InternalProgramError("Unsupported invocation type!");
			}
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
	public void visit(MethodInvocation methodInvocation) {
		Invocation i = (Invocation)methodInvocation.getSemantics();
		
		generateMethodInvocation(curExprBuffer, i, methodInvocation.getInvocationType(),methodInvocation.getPrefix(), methodInvocation.getArguments());
	
		
	}
	
	@Override
	public void visit(CastExpression castExpression) {
		Type t = castExpression.getInferedType();
		
		Expression e = castExpression.getRightExpression();
		Type et = e.getInferedType();
		if(t!=et){
			//If this is a cast to byte, check if we actually need it or if a cast to int is sufficient
			if(t.getBaseType()==BasicType.BYTE){
				SyntaxNode sn = castExpression.getParent();
				if(sn instanceof Assignment){
					if(((Assignment)sn).getInferedType().getBaseType()==BasicType.BYTE){
						t = BasicType.INT;
					}
				} else if(sn instanceof VariableAssignDecl){
					if(((VariableAssignDecl)sn).getInferedType().getBaseType()==BasicType.BYTE){
						t = BasicType.INT;
					}
				} else if(sn instanceof ReturnStatement){
					if(((AbstractFunction)sn.getSemantics()).getReturnType().getBaseType()==BasicType.BYTE){
						t = BasicType.INT;
					}
				}
				
						
			}
			
			//If the argument of the cast is in parenthesis, we don't need these
			//since the cast adds parenthesis.
			StringPair op = CodegenUtil.getCastOp(et, t);
			if(op != null && e instanceof ParenthesisExpression){
				e = e.getExpression();
			}
			surround(curExprBuffer,e,op);
		} else {
			invokeSelf(e);
		}
	}
	
	
	@Override
	public void visit(ExpressionList expressionList) {
		if(desiredSignature == null) throw new Error("No desired signature set!");
		int size = expressionList.size();
		SimpleBuffer curBuffer = curExprBuffer;
		Configuration options = this.options;
		
		for(int i=0;i<size;){
			Expression e = expressionList.elementAt(i);
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
	public void visit(VariableAssignDecl variableAssignDecl) {
		SimpleBuffer buffer = curExprBuffer;
		if(whitespaceInExprs){
			buffer.append(" = ");
		} else {
			buffer.append("=");
		}
		
		surroundTypeCastIfNecessary(variableAssignDecl.getInitializer(), variableAssignDecl.getName().getInferedType());
	}
	
	@Override
	public void visit(ArrayAccess arrayAccess) {
		invokeSelf(arrayAccess.getLeftExpression());
		curExprBuffer.append("[");
		invokeSelf(arrayAccess.getRightExpression());
		curExprBuffer.append("]");
	}
	
	@Override
	public void visit(ClassInstanceCreationExpression c) {
		ConstructorInvocation ci = (ConstructorInvocation) c.getSemantics();
		Class clazz = (Class)c.getInferedType();
		SimpleBuffer bufferBefore = parent.curBuffer;
		parent.curBuffer = curExprBuffer;
		classGen.generateConstructorInvocation(ci, c.getArguments(), clazz);
		parent.curBuffer = bufferBefore;
	}
	

}
