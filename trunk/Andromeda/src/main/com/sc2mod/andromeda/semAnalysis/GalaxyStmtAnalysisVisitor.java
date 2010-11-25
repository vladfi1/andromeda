package com.sc2mod.andromeda.semAnalysis;

import java.util.HashMap;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class GalaxyStmtAnalysisVisitor extends StatementAnalysisVisitor{

	private HashMap<SyntaxNode, Integer> enumeration = new HashMap<SyntaxNode, Integer>();
	
	public GalaxyStmtAnalysisVisitor(Environment env, Configuration options) {
		super(env, options);
	}
	
	@Override
	public void visit(MethodDeclNode functionDeclaration) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}
	

}
