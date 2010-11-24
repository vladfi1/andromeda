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

import java.util.ArrayList;

import com.sc2mod.andromeda.classes.ClassNameProvider;
import com.sc2mod.andromeda.codegen.buffers.SimpleBuffer;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.StaticInit;
import com.sc2mod.andromeda.environment.types.Enrichment;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IStruct;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.TypeUtil;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.types.impl.RecordTypeImpl;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.SourceInfo;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalFuncDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureListNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MemberDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.SourceListNode;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclListNode;
import com.sc2mod.andromeda.syntaxNodes.util.VoidVisitorAdapter;

public class NameGenerationVisitor extends VoidVisitorAdapter{
	
	private INameProvider nameProvider;
	private Configuration options;

	private IType curType;
	private boolean inLib;
	
	private boolean shortenVarNames;
	private TypeProvider tprov;
	
	public INameProvider getNameProvider() {
		return nameProvider;
	}
	
	public NameGenerationVisitor(TypeProvider tp, INameProvider nameProvider) {
		this.nameProvider = nameProvider;
		this.tprov = tp;
	}

	public NameGenerationVisitor(TypeProvider tp, INameProvider nameProvider, Configuration o) {
		this.shortenVarNames = o.getParamBool(Parameter.CODEGEN_SHORTEN_VAR_NAMES);
		this.nameProvider = nameProvider;
		this.tprov = tp;
		options = o;
	}
	
	public void prepareNameGeneration() {		
		if(shortenVarNames){
			for(BasicType b: tprov.BASIC){
				b.setGeneratedName(nameProvider.getTypeName(b));
			}
		}
	}
	
	public void writeTypedefs(SimpleBuffer buffer) {
		if(shortenVarNames){
			for(BasicType b: tprov.BASIC){
				buffer.append("typedef ").append(b.getName()).append(" ").append(b.getGeneratedName()).append(";");
				buffer.nl();
			}
		}
	}
	
	@Override
	public void visit(SourceListNode sourceListNode) {
		sourceListNode.childrenAccept(this);
	}
	
	@Override
	public void visit(SourceFileNode andromedaFile) {
		SourceInfo afi = andromedaFile.getSourceInfo();
		//No names are generated for native libs
		InclusionType inclType = afi.getType();
		if(inclType==InclusionType.NATIVE) return;
		
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
		includedFile.childrenAccept(this);
	}
	
	private void visitTypedef(GlobalStructureNode g){
		RecordTypeImpl r = (RecordTypeImpl)g.getSemantics();
		r.setGeneratedName(nameProvider.getTypeName(r));
		IType typeBefore = curType;
		curType = r;
		g.childrenAccept(this);
		curType = typeBefore;
	}
	
	@Override
	public void visit(StructDeclNode structDeclaration) {
		RecordTypeImpl r = (RecordTypeImpl)structDeclaration.getSemantics();
		r.setGeneratedName(nameProvider.getTypeName(r));
		nameProvider.assignFieldNames((IStruct)structDeclaration.getSemantics());
	}
	
	@Override
	public void visit(ClassDeclNode classDeclaration) {
		IClass c = (IClass)classDeclaration.getSemantics();
		ClassNameProvider className = c.getNameProvider();
		//If this is a top class we need a name for its alloc method
		if(c.isTopType()){
			className.setAllocatorName(nameProvider.getGlobalNameRawNoPrefix("alloc___" + c.getName()));
			className.setDeallocatorName(nameProvider.getGlobalNameRawNoPrefix("dealloc___" + c.getName()));
		} else {
			//If it is not a top class we maybe need a name for its field init method
			if(TypeUtil.hasTypeFieldInits(c))
				className.setAllocatorName(nameProvider.getGlobalNameRawNoPrefix("init___" + c.getName()));
		}
		visitTypedef(classDeclaration);
	}
	
	@Override
	public void visit(MemberDeclListNode classBody) {
		classBody.childrenAccept(this);
	}
	
	@Override
	public void visit(EnrichDeclNode enrichDeclaration) {
		IType typeBefore = curType;
		curType = ((Enrichment)enrichDeclaration.getSemantics()).getEnrichedType();
		enrichDeclaration.childrenAccept(this);
		curType = typeBefore;
	}
	
	@Override
	public void visit(InterfaceDeclNode interfaceDeclaration) {
		visitTypedef(interfaceDeclaration);
	}
	
	@Override
	public void visit(GlobalFuncDeclNode functionDeclaration) {
		functionDeclaration.getFuncDecl().accept(this);
	}
	

	@Override
	public void visit(MethodDeclNode methodDeclaration) {
		Function m = (Function)methodDeclaration.getSemantics();
		if(inLib&&m.getInvocationCount()==0) return;
		nameProvider.assignLocalNamesForMethod(m);
		m.setGeneratedName(nameProvider.getFunctionName(m));
	}
	
	@Override
	public void visit(GlobalStaticInitDeclNode globalInitDeclaration) {
		globalInitDeclaration.getInitDecl().accept(this);
	}
	
	@Override
	public void visit(StaticInitDeclNode staticInitDeclaration) {
		StaticInit m = (StaticInit)staticInitDeclaration.getSemantics();
		nameProvider.assignLocalNamesForMethod(m);
		m.setGeneratedName(nameProvider.getFunctionName(m));
	}
	
	@Override
	public void visit(FieldDeclNode fieldDeclaration) {
		VarDeclListNode v = fieldDeclaration.getDeclaredVariables();
		int size = v.size();
		for(int i=0;i<size;i++){
			FieldDecl decl = (FieldDecl) v.get(i).getName().getSemantics();
			//XPilot: variables that are written to are also needed
			if(inLib&&decl.getNumReadAccesses()==0 && decl.getNumWriteAccesses() == 0) continue;
			decl.setGeneratedName(nameProvider.getFieldName(decl,curType));
		}
		
		
	}
	
	@Override
	public void visit(GlobalVarDeclNode globalVarDeclaration) {
		VarDeclListNode v = globalVarDeclaration.getFieldDecl().getDeclaredVariables();
		int size = v.size();
		for(int i=0;i<size;i++){
			Variable decl = (Variable) v.get(i).getName().getSemantics();
			if(inLib&&decl.getNumReadAccesses()==0) continue;
			decl.setGeneratedName(nameProvider.getGlobalName(decl));
		}
	}


}
