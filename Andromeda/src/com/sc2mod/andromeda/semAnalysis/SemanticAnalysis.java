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
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.syntaxNodes.AndromedaFile;

/**
 * Handles the workflow of the semantic analysis.
 * @author J. 'gex' Finis
 *
 */
public class SemanticAnalysis {


	public static Environment analyze(AndromedaFile a, Options o){
		Environment env = new Environment();
		
		ConstantResolveVisitor constResolve = new ConstantResolveVisitor();
		
		//Analyze and register structure
		StructureAnalysisVisitor typeAnalysisVisitor = new StructureAnalysisVisitor(env);
		a.accept(typeAnalysisVisitor);
		
		//Resolve class hierarchy
		env.resolveClassHierarchy();
		
		//Resolve constant variables
		NameResolver r = new NameResolver(new ArrayLocalVarStack(), env);
		ExpressionAnalyzer exprAnalyzer = new ExpressionAnalyzer(constResolve,env.typeProvider);
		ConstEarlyAnalysisVisitor constResolver = new ConstEarlyAnalysisVisitor(exprAnalyzer,r, constResolve, env);
		a.accept(constResolver);
		
		//Determine signatures and non-local non-constant variable types
		env.resolveTypes();
		
		//Infere expression types, resolve function calls and field accesses
		ExpressionAnalysisVisitor expressionAnalysisVisitor = new ExpressionAnalysisVisitor(exprAnalyzer,r,constResolve,env,o);
		a.accept(expressionAnalysisVisitor);	
		
		//Resolve remaining constants
		expressionAnalysisVisitor.constResolve.resolveRemainingExprs();
		
		//Generate function indices
		env.generateFunctionIndex();
		
		//Adjust class instance bounds
		env.adjustClassInstanceLimit();
		
		return env;
		
		
	}
}
