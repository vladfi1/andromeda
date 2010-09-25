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
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.syntaxNodes.SourceFile;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclaration;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclaration;
import com.sc2mod.andromeda.syntaxNodes.FileContent;
import com.sc2mod.andromeda.syntaxNodes.FunctionDeclaration;
import com.sc2mod.andromeda.syntaxNodes.GlobalInitDeclaration;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclaration;
import com.sc2mod.andromeda.syntaxNodes.IncludedFile;
import com.sc2mod.andromeda.syntaxNodes.InstanceLimitSetter;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclaration;
import com.sc2mod.andromeda.syntaxNodes.StructDeclaration;
import com.sc2mod.andromeda.syntaxNodes.TypeAlias;
import com.sc2mod.andromeda.syntaxNodes.TypeExtension;

/**
 * Analyzes the coarse syntax structure (finds global structures like functions, global variables and classes)
 * @author J. 'gex' Finis
 *
 */
public class StructureAnalysisVisitor extends AnalysisVisitor{

	private TypeProvider tprov;
	private Scope scope;

	
	public StructureAnalysisVisitor(Environment env) {
		super(env);
		tprov = env.typeProvider;
	}
	
	@Override
	public void visit(SourceFile andromedaFile) {
		Scope oldScope = scope;
		scope = new Scope(andromedaFile.getFileInfo().getInclusionType());
		andromedaFile.setScope(scope);
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
		tprov.registerClass(classDeclaration,scope);
	}
	
	@Override
	public void visit(InterfaceDeclaration interfaceDeclaration) {
		tprov.registerInterface(interfaceDeclaration,scope);
	}
	
	@Override
	public void visit(StructDeclaration structDeclaration) {
		tprov.registerStruct(structDeclaration,scope);
	}
	
	@Override
	public void visit(FunctionDeclaration functionDeclaration) {
		env.registerFunction(functionDeclaration,scope);
	}
	
	@Override
	public void visit(GlobalVarDeclaration g) {
		env.registerGlobalVar(g,scope);
	}	

	@Override
	public void visit(EnrichDeclaration enrichDeclaration) {
		env.registerEnrichment(enrichDeclaration, scope);
	}
	
	@Override
	public void visit(GlobalInitDeclaration globalInitDeclaration) {
		env.registerGlobalInit(globalInitDeclaration.getInitDecl(), scope);
	}
	
	@Override
	public void visit(InstanceLimitSetter instanceLimitSetter) {
		env.registerInstanceLimitSetter(instanceLimitSetter);
	}
	
	@Override
	public void visit(TypeAlias typeAlias) {
		tprov.registerTypeAlias(typeAlias);
	}
	
	@Override
	public void visit(TypeExtension typeExtension) {
		tprov.registerTypeExtension(typeExtension);
	}
}
