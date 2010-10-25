package com.sc2mod.andromeda.parsing.phases;

import com.sc2mod.andromeda.codetransform.SimplificationStmtVisitor;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.Workflow;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class SimplificationPhase extends Phase {

	public SimplificationPhase() {
		super(PhaseRunPolicy.IF_NO_ERRORS, "Simplifying code", true);
	}
	
	@Override
	public boolean wantExecute(CompilationEnvironment env) {
		//Simplification has only to be done if code is to be generated.
		if(env.getConfig().getParamBool(Parameter.MISC_NO_CODE_GEN)) return false;
		return true;
	}
	
	private void workflow(Environment env, Configuration options, SyntaxNode code){
		SimplificationStmtVisitor trans = new SimplificationStmtVisitor(options,env.typeProvider);
		code.accept(trans);
	}

	@Override
	public void execute(CompilationEnvironment env, Workflow workflow) {
		workflow(env.getSemanticEnvironment(), env.getConfig(),env.getSyntaxTree());
		
	}

}
