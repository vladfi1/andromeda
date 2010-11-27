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

import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.IDeclaredType;
import com.sc2mod.andromeda.environment.types.IExtension;
import com.sc2mod.andromeda.environment.types.IRecordType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.Language;
import com.sc2mod.andromeda.syntaxNodes.SourceListNode;
import com.sc2mod.andromeda.syntaxNodes.TypeAliasDeclNode;
import com.sc2mod.andromeda.syntaxNodes.util.VoidVisitor;
import com.sc2mod.andromeda.util.Pair;

/**
 * Handles the workflow of the semantic analysis.
 * @author J. 'gex' Finis
 *
 */
public class SemanticAnalysisWorkflow {

	
	/**
	 * Resolves the type alias relation:
	 * Resolves the aliased type and then entries the alias into the 
	 * @param alias
	 * @param scope
	 */
	private static void resolveTypeAlias(TypeProvider tprov, TypeAliasDeclNode alias, IScope scope) {
		IType t = tprov.resolveType(alias.getEnrichedType(),scope);
		scope.addContent(alias.getName(),t);
	}

	private static void resolveClassTypes(TypeProvider tprov, ResolveAndCheckTypesVisitor resolver){
			ArrayList<Pair<TypeAliasDeclNode, IScope>> typeAliases = tprov.getTypeAliases();
			for(Pair<TypeAliasDeclNode, IScope> i : typeAliases){
				resolveTypeAlias(tprov, i._1,i._2);
			}
			
			List<IDeclaredType> declaredTypes = tprov.getDeclaredTypes();
			for(IDeclaredType r : declaredTypes){
				r.accept(resolver);
			}
		

	
	}
	
	
	public static Environment analyze(CompilationEnvironment compEnv, Language lang){
		
		//Create the semantics environment
		Environment env = new Environment(lang);
		compEnv.setSemanticEnvironment(env);
		SourceListNode syntax = compEnv.getSyntaxTree();
		TransientAnalysisData analysisData = new TransientAnalysisData();
				
		//--- ConstantResolveVisitor constResolve = new ConstantResolveVisitor();
		
		//+++++++++++++++++++++++++++++++++++++++++++
		//+++ 1.) Analyze and register types
		syntax.accept( new TypeRegistryTreeScanner(env), null );

		//+++++++++++++++++++++++++++++++++++++++++++
		//+++ 2.) Resolve and check class hierarchy
		
		//Resolve inheritance (extends, implements and type parameters)
		resolveClassTypes(env.typeProvider,new ResolveAndCheckTypesVisitor(env));
		
		//Build and check type hierarchy
		new TypeHierarchySVisitor(env).execute();
		
		// 3.) Register fields, methods and other constructs (also resolve their types and signatures)
		syntax.accept( new StructureRegistryTreeScanner(env, analysisData), null );
		
		
		//Copy down members from super to subclasses
		new CopyDownVisitor(env).execute();
		
		//Check some additional things for classes and other record types, which can only be
		//checked after their type hierarchy was built and their members have been registered and copied down
		SemanticsElementCheckVisitor classChecks = new SemanticsElementCheckVisitor(env);
		for(IDeclaredType c : env.typeProvider.getDeclaredTypes()){
			c.accept(classChecks);
		}
		
		
		//Resolve system classes and types
		env.typeProvider.resolveSystemTypes();
		
		
		// 4.) Analyze statements and expressions
		//Infer expression types, resolve function calls and field accesses
		StatementAnalysisVisitor codeAnalysis = 
			new StatementAnalysisVisitor(env,compEnv.getConfig());
		syntax.accept(codeAnalysis);	
		
		//Do additional checks, if desired by the language
		VoidVisitor additionalVisitor = lang.getImpl().getAdditionalAnalysisVisitor(env, compEnv.getConfig());
		if(additionalVisitor != null)
			syntax.accept(additionalVisitor);
		
		//Resolve remaining constants
		codeAnalysis.constResolve.resolveRemainingExprs();
		
		//Check class instance limits
		new InstanceLimitChecker(analysisData, env).doChecks();
		
		return env;
		
		
	}

}
