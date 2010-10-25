package com.sc2mod.andromeda.parsing.phases;

import java.io.File;
import java.io.IOException;

import com.sc2mod.andromeda.notifications.ErrorUtil;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.Workflow;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.program.MapRunner;
import com.sc2mod.andromeda.program.Program;

public class RunMapPhase extends Phase{

	public RunMapPhase() {
		super(PhaseRunPolicy.IF_NO_ERRORS, "Running created map", false);
	}

	@Override
	public void execute(CompilationEnvironment env, Workflow workflow) {
		try {
			File mapOut = env.getConfig().getParamFile(Parameter.FILES_MAP_OUT);
			new MapRunner(Program.platform,env.getConfig(),Program.config).test(mapOut);
		} catch (IOException e) {
			ErrorUtil.raiseIOProblem(e, false);
			System.err.println("--- Map run unsuccessful! ---");
		}
	}

}
