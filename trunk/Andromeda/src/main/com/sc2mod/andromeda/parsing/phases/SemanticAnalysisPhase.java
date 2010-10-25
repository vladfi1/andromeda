package com.sc2mod.andromeda.parsing.phases;

import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.Workflow;
import com.sc2mod.andromeda.semAnalysis.SemanticAnalysisWorkflow;

public class SemanticAnalysisPhase extends Phase {

	public SemanticAnalysisPhase() {
		super(PhaseRunPolicy.IF_NO_ERRORS, "Checking semantics", true);
	}

	@Override
	public void execute(CompilationEnvironment env, Workflow workflow) {
		SemanticAnalysisWorkflow.analyze(env);
	}

}
