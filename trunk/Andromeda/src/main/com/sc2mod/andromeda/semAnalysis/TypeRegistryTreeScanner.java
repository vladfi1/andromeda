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
import com.sc2mod.andromeda.environment.scopes.Package;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.syntaxNodes.AccessorDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.FieldDeclNode;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.NameExprNode;
import com.sc2mod.andromeda.syntaxNodes.PackageDeclNode;
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
	
	/**
	 * Creates a package and registers it in its parent package, or gets
	 * the package if it already exists.
	 * @param packageName
	 * @return
	 */
	private Package buildPackageFromName(ExprNode packageName) {
		if(packageName == null) return env.getDefaultPackage();
		String name = packageName.getName();
		
		//Recursively build or fetch parent package
		//We can cast to field access here safely since a package decl only consists of field accesses
		Package parent;
		if(packageName instanceof NameExprNode){
			parent = env.getDefaultPackage();
		} else {
			parent = buildPackageFromName(packageName.getLeftExpression());
		}
		
		//Get or create the package in its parent
		return parent.addOrGetSubpackage(name,packageName);
	}
		
	/**
	 * Builds a package scope from a package declaration.
	 * @param packageDecl
	 * @return
	 */
	private Package buildPackageFromDecl(PackageDeclNode packageDecl) {
		if(packageDecl==null){
			return env.getDefaultPackage();
		}
		
		ExprNode packageName = packageDecl.getPackageName();
		return buildPackageFromName(packageName);
	}


	@Override
	public void visit(SourceFileNode andromedaFile, Scope s) {
		PackageDeclNode packageDecl = andromedaFile.getPackageDecl();
		Package p;
		if(packageDecl == null){
			p = env.getDefaultPackage();
		} else {
			p = buildPackageFromName(andromedaFile.getPackageDecl().getPackageName());
		}
		s = new FileScope(andromedaFile.getFileInfo().getFileId()+"",andromedaFile.getFileInfo().getInclusionType(),p);
		andromedaFile.setSemantics(s);
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
		tprov.registerTypeAlias(typeAlias,scope);
	}
	
	@Override
	public void visit(TypeExtensionDeclNode typeExtension, Scope scope) {
		tprov.registerTypeExtension(typeExtension,scope);
	}
	
	//Stop scan if one of these constructs is reached (since they cannot contain types at the moment)
	public void visit(MethodDeclNode mdecl, FileScope scope){}
	public void visit(StaticInitDeclNode sdecl, FileScope scope){}
	public void visit(FieldDeclNode mdecl, FileScope scope){}
	public void visit(AccessorDeclNode mdecl, FileScope scope){}
	


}
