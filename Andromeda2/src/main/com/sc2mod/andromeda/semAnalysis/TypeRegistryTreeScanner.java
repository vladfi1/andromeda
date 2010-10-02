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
import com.sc2mod.andromeda.syntaxNodes.AccessorDeclNode;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureListNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalFuncDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclNode;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.InstanceLimitSetterNode;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclNode;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclNode;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeAliasDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeExtensionDeclNode;
import com.sc2mod.andromeda.syntaxNodes.util.Visitor;
import com.sc2mod.andromeda.util.visitors.NoResultTreeScanVisitor;
import com.sc2mod.andromeda.util.visitors.VoidTreeScanVisitor;

/**
 * This scanner does the first step of the semantic analysis by registering
 * all types in all files and registering their scope. These types are later
 * needed to type-check all kinds of expressions.
 * @author J. 'gex' Finis
 *
 */
public class TypeRegistryTreeScanner extends NoResultTreeScanVisitor<Scope>{

	private TypeProvider tprov;
	private Environment env;
	
	public TypeRegistryTreeScanner(Environment env) {
		this.env = env;
		tprov = env.typeProvider;
		
	}
	
	@Override
	public void visit(SourceFileNode andromedaFile, Scope s) {
		s = new Scope(andromedaFile.getFileInfo().getInclusionType());
		andromedaFile.setScope(s);
		andromedaFile.childrenAccept(this,s);
	}
	

	@Override
	public void visit(ClassDeclNode classDeclaration, Scope scope) {
		tprov.registerClass(classDeclaration,scope);
	}
	
	@Override
	public void visit(InterfaceDeclNode interfaceDeclaration, Scope scope) {
		tprov.registerInterface(interfaceDeclaration,scope);
	}
	
	@Override
	public void visit(StructDeclNode structDeclaration, Scope scope) {
		tprov.registerStruct(structDeclaration,scope);
	}
	

	

	
	@Override
	public void visit(TypeAliasDeclNode typeAlias, Scope scope) {
		tprov.registerTypeAlias(typeAlias);
	}
	
	@Override
	public void visit(TypeExtensionDeclNode typeExtension, Scope scope) {
		tprov.registerTypeExtension(typeExtension);
	}
	
	//Stop scan if one of these constructs is reached (since they cannot contain types at the moment)
	public void visit(MethodDeclNode mdecl, Scope scope){}
	public void visit(StaticInitDeclNode sdecl, Scope scope){}
	public void visit(FieldDeclNode mdecl, Scope scope){}
	public void visit(AccessorDeclNode mdecl, Scope scope){}
	
	
	//Does an enrich belong here? It forms no own type!
//	@Override
//	public void visit(EnrichDeclaration enrichDeclaration, Scope scope) {
//		env.registerEnrichment(enrichDeclaration, scope);
//	}
	
	//These do not belong in here, only types should be checked in this step.
	//So, they were removed (need to be processed in a later step.
	//Remove this comment and them, once the code is working again
//	@Override
//	public void visit(GlobalInitDeclaration globalInitDeclaration, Scope scope) {
//		env.registerGlobalInit(globalInitDeclaration.getInitDecl(), scope);
//	}
//	
//	@Override
//	public void visit(InstanceLimitSetter instanceLimitSetter, Scope scope) {
//		env.registerInstanceLimitSetter(instanceLimitSetter);
//	}
	
//	@Override
//	public void visit(FunctionDeclaration functionDeclaration, Scope scope) {
//		env.registerFunction(functionDeclaration,scope);
//	}
//	
//	@Override
//	public void visit(GlobalVarDeclaration g, Scope scope) {
//		env.registerGlobalVar(g,scope);
//	}	

}
