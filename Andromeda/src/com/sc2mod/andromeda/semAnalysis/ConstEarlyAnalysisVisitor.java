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

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Scope;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.GlobalVarDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.syntaxNodes.AndromedaFile;
import com.sc2mod.andromeda.syntaxNodes.BinaryExpression;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclaration;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclaration;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.ExpressionList;
import com.sc2mod.andromeda.syntaxNodes.FieldAccess;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclaration;
import com.sc2mod.andromeda.syntaxNodes.FileContent;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclaration;
import com.sc2mod.andromeda.syntaxNodes.IncludedFile;
import com.sc2mod.andromeda.syntaxNodes.Literal;
import com.sc2mod.andromeda.syntaxNodes.LiteralExpression;
import com.sc2mod.andromeda.syntaxNodes.LiteralType;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExpression;
import com.sc2mod.andromeda.syntaxNodes.StructDeclaration;
import com.sc2mod.andromeda.syntaxNodes.UnaryExpression;
import com.sc2mod.andromeda.syntaxNodes.VariableAssignDecl;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclarator;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclarators;

/**
 * Tries to resolve the value of constant global variables early (before the type inference)
 * so that the size of array types is known in the type inference.
 * @author J. 'gex' Finis
 *
 */
public class ConstEarlyAnalysisVisitor extends AnalysisVisitor{

	private TypeProvider tprov;
	private Scope scope;
	private ConstantResolveVisitor constResolve;
	private NameResolver nameResolver;
	private RecordType curType;
	private ExpressionAnalyzer exprAnalyzer;

	
	public ConstEarlyAnalysisVisitor(ExpressionAnalyzer exprResolver, NameResolver r, ConstantResolveVisitor constResolve, Environment env) {
		super(env);
		this.exprAnalyzer = exprResolver;
		this.nameResolver = r;
		this.constResolve = constResolve;
		tprov = env.typeProvider;
	}
	
	//************** GLOBAL STRUCTURES *************
	
	@Override
	public void visit(AndromedaFile andromedaFile) {
		Scope oldScope = scope;
		scope = andromedaFile.getScope();
		andromedaFile.childrenAccept(this);
		scope = oldScope;
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
	public void visit(ClassDeclaration classDeclaration) {
		RecordType typeBefore = curType;
		curType = (RecordType) classDeclaration.getSemantics();
		boolean generic = curType.isGeneric();
		if(generic)tprov.pushTypeParams(curType.getTypeParams());
		classDeclaration.getBody().childrenAccept(this);
		if(generic)tprov.popTypeParams(curType.getTypeParams());
		curType = typeBefore;
	}
	
	@Override
	public void visit(StructDeclaration classDeclaration) {
		RecordType typeBefore = curType;
		curType = (RecordType) classDeclaration.getSemantics();
		classDeclaration.getBody().childrenAccept(this);
		curType = typeBefore;
	}
	
	@Override
	public void visit(EnrichDeclaration enrichDeclaration) {
		RecordType typeBefore = curType;
		curType = (RecordType) enrichDeclaration.getSemantics();
		enrichDeclaration.getBody().childrenAccept(this);
		curType = typeBefore;
	}
	
	@Override
	public void visit(FieldDeclaration fieldDeclaration) {
		//Visit field type (in case we have an array)
		fieldDeclaration.getType().childrenAccept(this);
		
		int vsize = fieldDeclaration.getDeclaredVariables().size();
		for(int j=0;j<vsize;j++){
			FieldDecl field = new FieldDecl(fieldDeclaration,curType,j);
			curType.getFields().addField(field);
			field.resolveType(tprov);
			if(field.isConst()&&field.isStatic()){		
				field.getDeclarator().accept(this);
			}
		}
	}
	
	
	@Override
	public void visit(GlobalVarDeclaration g) {
		//Visit field type (in case we have an array)
		g.getFieldDecl().getType().childrenAccept(this);
		
		VariableDeclarators dvs = g.getFieldDecl().getDeclaredVariables();
		int size = dvs.size();
		for(int i=0;i<size;i++){
			VariableDeclarator vd = dvs.elementAt(i);
			GlobalVarDecl vdecl = (GlobalVarDecl) vd.getSemantics();
			vdecl.resolveType(tprov);
			vdecl.assignIndex();
			if(vdecl.isConst()){				
				vd.accept(this);
			}
		}
	}
	
	@Override
	public void visit(BinaryExpression binaryExpression) {
		binaryExpression.childrenAccept(this);
		exprAnalyzer.analyze(binaryExpression);
	}
	
	@Override
	public void visit(UnaryExpression unaryExpression) {
		unaryExpression.childrenAccept(this);
		exprAnalyzer.analyze(unaryExpression);
	}
	
	@Override
	public void visit(VariableAssignDecl variableAssignDecl) {
		variableAssignDecl.getInitializer().accept(this);
		variableAssignDecl.setInferedType(variableAssignDecl.getInitializer().getInferedType());
		VarDecl decl = (VarDecl)variableAssignDecl.getName().getSemantics();
		
		//Constants must be initialized with constant values
		if(decl.isConst()){
			variableAssignDecl.accept(constResolve);
		}
	}
	
	@Override
	public void visit(ExpressionList expressionList) {
		//Array dimensions are an expression list, so resolve them!
		expressionList.childrenAccept(this);
	}
	
	@Override
	public void visit(FieldAccess nameExpression) {
		//XPilot: added (this is what ExpressionAnalysisVisitor does)
		nameExpression.childrenAccept(this);
		
		VarDecl va = nameResolver.resolveVariable(scope,curType,nameExpression,false);
		nameExpression.setSemantics(va);

		Type type = va.getType();
		nameExpression.setInferedType(type);
	
		//Is it const?
		if(va.isConst()){
			nameExpression.setConstant(true);
			nameExpression.accept(constResolve);
		}
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
		default:
			throw new CompilationError(l,"Literal of unknown literal type!");
		}
		
		//Get constant value
		le.accept(constResolve);
	}
	
	/**
	 * XPilot: Copied from ExpressionAnalysisVisitor
	 */
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
