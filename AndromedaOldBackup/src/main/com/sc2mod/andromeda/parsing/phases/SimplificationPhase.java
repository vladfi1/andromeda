package com.sc2mod.andromeda.parsing.phases;

import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.Workflow;

public class SimplificationPhase extends Phase {

	protected SimplificationPhase(PhaseRunPolicy runPolicy, String description,
			boolean doLogs) {
		super(PhaseRunPolicy.IF_NO_ERRORS, "Simplifying code...", true);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(CompilationEnvironment env, Workflow workflow) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

}
