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
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;

/**
 * Handles the workflow of the semantic analysis.
 * @author J. 'gex' Finis
 *
 */
public class SemanticAnalysisWorkflow {


	public static Environment analyze(CompilationEnvironment compEnv){
		
		//Create the semantics environment
		Environment env = new Environment();
		compEnv.setSemanticEnvironment(env);
		SourceFileNode syntax = compEnv.getSyntaxTree();
		
		//--- ConstantResolveVisitor constResolve = new ConstantResolveVisitor();
		
		// 1.) Analyze and register types
		syntax.accept( new TypeRegistryTreeScanner(env), null );
		
		// 2.) Resolve and check class hierarchy
		syntax.accept( new ClassHierachyCheckVisitor(env) );
		
		//---Resolve class hierarchy (if there are any classes)
		//---if(!env.typeProvider.getClasses().isEmpty()){
		//---	env.resolveClassHierarchy();
		//---}
		
		//---//Resolve constant variables
		//---NameResolver r = new NameResolver(new ArrayLocalVarStack(), env);
		//---ExpressionAnalyzer exprAnalyzer = new ExpressionAnalyzer(constResolve,env.typeProvider);
		//---ConstEarlyAnalysisVisitor constResolver = new ConstEarlyAnalysisVisitor(exprAnalyzer,r, constResolve, env);
		//---syntax.accept(constResolver);
		
		//---Determine signatures and non-local non-constant variable types
		//---env.resolveTypes();
		
		// 3.) Register fields, methods and other constructs (also resolve their types and signatures)
		syntax.accept( new StructureRegistryTreeScanner(env) );
		
		// 4.) Analyze statements and expressions
		//Infer expression types, resolve function calls and field accesses
		ExpressionAnalysisVisitor expressionAnalysisVisitor = new ExpressionAnalysisVisitor(exprAnalyzer,r,constResolve,env,compEnv.getConfig());
		syntax.accept(expressionAnalysisVisitor);	
		
		//Resolve remaining constants
		expressionAnalysisVisitor.constResolve.resolveRemainingExprs();
		
		//Generate function indices
		env.generateFunctionIndex();
		
		//Adjust class instance bounds
		env.adjustClassInstanceLimit();
		
		return env;
		
		
	}
}
