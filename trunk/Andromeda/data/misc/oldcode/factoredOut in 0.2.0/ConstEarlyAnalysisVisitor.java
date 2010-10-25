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
import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.content.NameResolver;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.GlobalVarDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.BinOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureListNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralExprNode;
import com.sc2mod.andromeda.syntaxNodes.LiteralTypeSE;
import com.sc2mod.andromeda.syntaxNodes.ParenthesisExprNode;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;
import com.sc2mod.andromeda.syntaxNodes.UnOpExprNode;
import com.sc2mod.andromeda.syntaxNodes.VarAssignDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclListNode;

/**
 * Tries to resolve the value of constant global variables early (before the type inference)
 * so that the size of array types is known in the type inference.
 * @author J. 'gex' Finis
 *
 */
public class ConstEarlyAnalysisVisitor extends AnalysisVisitor{

	private TypeProvider tprov;
	private FileScope scope;
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
	public void visit(SourceFileNode andromedaFile) {
		FileScope oldScope = scope;
		scope = andromedaFile.getScope();
		andromedaFile.childrenAccept(this);
		scope = oldScope;
	}
	
	@Override
	public void visit(GlobalStructureListNode fileContent) {
		fileContent.childrenAccept(this);
	}
	
	@Override
	public void visit(IncludeNode includedFile) {
		includedFile.getIncludedContent().accept(this);
	}

	@Override
	public void visit(ClassDeclNode classDeclaration) {
		RecordType typeBefore = curType;
		curType = (RecordType) classDeclaration.getSemantics();
		boolean generic = curType.isGeneric();
		if(generic)tprov.pushTypeParams(curType.getTypeParams());
		classDeclaration.getBody().childrenAccept(this);
		if(generic)tprov.popTypeParams(curType.getTypeParams());
		curType = typeBefore;
	}
	
	@Override
	public void visit(StructDeclNode classDeclaration) {
		RecordType typeBefore = curType;
		curType = (RecordType) classDeclaration.getSemantics();
		classDeclaration.getBody().childrenAccept(this);
		curType = typeBefore;
	}
	
	@Override
	public void visit(EnrichDeclNode enrichDeclaration) {
		RecordType typeBefore = curType;
		curType = (RecordType) enrichDeclaration.getSemantics();
		enrichDeclaration.getBody().childrenAccept(this);
		curType = typeBefore;
	}
	
	@Override
	public void visit(FieldDeclNode fieldDeclaration) {
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
	public void visit(GlobalVarDeclNode g) {
		//Visit field type (in case we have an array)
		g.getFieldDecl().getType().childrenAccept(this);
		
		VarDeclListNode dvs = g.getFieldDecl().getDeclaredVariables();
		int size = dvs.size();
		for(int i=0;i<size;i++){
			VarDeclNode vd = dvs.elementAt(i);
			GlobalVarDecl vdecl = (GlobalVarDecl) vd.getSemantics();
			vdecl.resolveType(tprov);
			vdecl.assignIndex();
			if(vdecl.isConst()){				
				vd.accept(this);
			}
		}
	}
	
	@Override
	public void visit(BinOpExprNode binaryExpression) {
		binaryExpression.childrenAccept(this);
		exprAnalyzer.analyze(binaryExpression);
	}
	
	@Override
	public void visit(UnOpExprNode unaryExpression) {
		unaryExpression.childrenAccept(this);
		exprAnalyzer.analyze(unaryExpression);
	}
	
	@Override
	public void visit(VarAssignDeclNode variableAssignDecl) {
		variableAssignDecl.getInitializer().accept(this);
		variableAssignDecl.setInferedType(variableAssignDecl.getInitializer().getInferedType());
		VarDecl decl = (VarDecl)variableAssignDecl.getName().getSemantics();
		
		//Constants must be initialized with constant values
		if(decl.isConst()){
			variableAssignDecl.accept(constResolve);
		}
	}
	
	@Override
	public void visit(ExprListNode expressionList) {
		//Array dimensions are an expression list, so resolve them!
		expressionList.childrenAccept(this);
	}
	
	@Override
	public void visit(FieldAccessExprNode nameExpression) {
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
		default:
			throw new InternalError("Literal of unknown literal type!");
		}
		
		//Get constant value
		le.accept(constResolve);
	}
	
	/**
	 * XPilot: Copied from ExpressionAnalysisVisitor
	 */
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
