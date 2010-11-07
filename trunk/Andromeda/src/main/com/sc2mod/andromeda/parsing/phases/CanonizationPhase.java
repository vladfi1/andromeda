package com.sc2mod.andromeda.parsing.phases;

import com.sc2mod.andromeda.codetransform.CanonizeStmtVisitor;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.Workflow;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class CanonizationPhase extends Phase {

	public CanonizationPhase() {
		super(PhaseRunPolicy.IF_NO_ERRORS, "Canonizing code", true);
	}
	
	@Override
	public boolean wantExecute(CompilationEnvironment env) {
		//Simplification has only to be done if code is to be generated.
		if(env.getConfig().getParamBool(Parameter.MISC_NO_CODE_GEN)) return false;
		return true;
	}
	
	private void workflow(Environment env, Configuration options, SyntaxNode code){
		CanonizeStmtVisitor trans = new CanonizeStmtVisitor(options,env.typeProvider);
		code.accept(trans);
	}

	@Override
	public void execute(CompilationEnvironment env, Workflow workflow) {
		workflow(env.getSemanticEnvironment(), env.getConfig(),env.getSyntaxTree());
		
	}

}
