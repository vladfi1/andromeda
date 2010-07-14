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

import com.sc2mod.andromeda.codegen.buffers.SimpleBuffer;
import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.Method;
import com.sc2mod.andromeda.environment.StaticInit;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.Enrichment;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Struct;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.parsing.AndromedaFileInfo;
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.syntaxNodes.AccessorDeclaration;
import com.sc2mod.andromeda.syntaxNodes.AndromedaFile;
import com.sc2mod.andromeda.syntaxNodes.ClassBody;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclaration;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclaration;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclaration;
import com.sc2mod.andromeda.syntaxNodes.FileContent;
import com.sc2mod.andromeda.syntaxNodes.FunctionDeclaration;
import com.sc2mod.andromeda.syntaxNodes.GlobalInitDeclaration;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructure;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclaration;
import com.sc2mod.andromeda.syntaxNodes.IncludedFile;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclaration;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclaration;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclaration;
import com.sc2mod.andromeda.syntaxNodes.StructDeclaration;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclarators;
import com.sc2mod.andromeda.syntaxNodes.VisitorAdaptor;

public class NameGenerationVisitor extends VisitorAdaptor{
	
	private INameProvider nameProvider;
	private Options options;

	private Type curType;
	private boolean inLib;
	
	public INameProvider getNameProvider() {
		return nameProvider;
	}
	
	public NameGenerationVisitor(INameProvider nameProvider) {
		this.nameProvider = nameProvider;
	}

	public NameGenerationVisitor(Options o) {
		if(o.shortenVarNames){
			nameProvider = new ShortNameProvider();
		} else {
			nameProvider = new LongNameProvider();
		}
		options = o;
	}
	
	public void prepareNameGeneration() {		
		if(options.shortenVarNames){
			ArrayList<BasicType> bt = BasicType.getBasicTypeList();
			for(BasicType b: bt){
				b.setGeneratedName(nameProvider.getTypeName(b));
			}
		}
	}
	
	public void writeTypedefs(SimpleBuffer buffer) {
		if(options.shortenVarNames){
			ArrayList<BasicType> bt = BasicType.getBasicTypeList();
			boolean newLines = options.newLines;
			for(BasicType b: bt){
				buffer.append("typedef ").append(b.getName()).append(" ").append(b.getGeneratedName()).append(";");
				if(newLines) buffer.newLine();
			}
		}
	}
	
	
	@Override
	public void visit(AndromedaFile andromedaFile) {
		AndromedaFileInfo afi = andromedaFile.getFileInfo();
		//No names are generated for native libs
		int inclType = afi.getInclusionType();
		if(inclType==AndromedaFileInfo.TYPE_NATIVE) return;
		
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
		includedFile.childrenAccept(this);
	}
	
	private void visitTypedef(GlobalStructure g){
		RecordType r = (RecordType)g.getSemantics();
		r.setGeneratedName(nameProvider.getTypeName(r));
		Type typeBefore = curType;
		curType = r;
		g.childrenAccept(this);
		curType = typeBefore;
	}
	
	@Override
	public void visit(StructDeclaration structDeclaration) {
		RecordType r = (RecordType)structDeclaration.getSemantics();
		r.setGeneratedName(nameProvider.getTypeName(r));
		nameProvider.assignFieldNames((Struct)structDeclaration.getSemantics());
	}
	
	@Override
	public void visit(ClassDeclaration classDeclaration) {
		Class c = (Class)classDeclaration.getSemantics();
		
		//If this is a top class we need a name for its alloc method
		if(c.isTopClass()){
			c.setAllocatorName(nameProvider.getGlobalNameRawNoPrefix("alloc___" + c.getName()));
			c.setDeallocatorName(nameProvider.getGlobalNameRawNoPrefix("dealloc___" + c.getName()));
		} else {
			//If it is not a top class we maybe need a name for its field init method
			if(c.hasFieldInit())
				c.setAllocatorName(nameProvider.getGlobalNameRawNoPrefix("init___" + c.getName()));
		}
		visitTypedef(classDeclaration);
	}
	
	@Override
	public void visit(ClassBody classBody) {
		classBody.childrenAccept(this);
	}
	
	@Override
	public void visit(EnrichDeclaration enrichDeclaration) {
		Type typeBefore = curType;
		curType = ((Enrichment)enrichDeclaration.getSemantics()).getEnrichedType();
		enrichDeclaration.childrenAccept(this);
		curType = typeBefore;
	}
	
	@Override
	public void visit(InterfaceDeclaration interfaceDeclaration) {
		visitTypedef(interfaceDeclaration);
	}
	
	@Override
	public void visit(FunctionDeclaration functionDeclaration) {
		functionDeclaration.getFuncDecl().accept(this);
	}
	

	@Override
	public void visit(MethodDeclaration methodDeclaration) {
		Function m = (Function)methodDeclaration.getSemantics();
		if(inLib&&m.getInvocationCount()==0) return;
		nameProvider.assignLocalNamesForMethod(m);
		m.setGeneratedName(nameProvider.getFunctionName(m));
	}
	
	@Override
	public void visit(GlobalInitDeclaration globalInitDeclaration) {
		globalInitDeclaration.getInitDecl().accept(this);
	}
	
	@Override
	public void visit(StaticInitDeclaration staticInitDeclaration) {
		StaticInit m = (StaticInit)staticInitDeclaration.getSemantics();
		nameProvider.assignLocalNamesForMethod(m);
		m.setGeneratedName(nameProvider.getFunctionName(m));
	}
	
	@Override
	public void visit(AccessorDeclaration accessorDeclaration) {
		accessorDeclaration.childrenAccept(this);
	}
	
	@Override
	public void visit(FieldDeclaration fieldDeclaration) {
		VariableDeclarators v = fieldDeclaration.getDeclaredVariables();
		int size = v.size();
		for(int i=0;i<size;i++){
			FieldDecl decl = (FieldDecl) v.elementAt(i).getName().getSemantics();
			if(inLib&&decl.getNumReadAccesses()==0) continue;
			decl.setGeneratedName(nameProvider.getFieldName(decl,curType));
		}
		
		
	}
	
	@Override
	public void visit(GlobalVarDeclaration globalVarDeclaration) {
		VariableDeclarators v = globalVarDeclaration.getFieldDecl().getDeclaredVariables();
		int size = v.size();
		for(int i=0;i<size;i++){
			VarDecl decl = (VarDecl) v.elementAt(i).getName().getSemantics();
			if(inLib&&decl.getNumReadAccesses()==0) continue;
			decl.setGeneratedName(nameProvider.getGlobalName(decl));
		}
	}


}
