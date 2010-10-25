package com.sc2mod.andromeda.parsing.phases;

import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.CompilationResult;
import com.sc2mod.andromeda.parsing.Workflow;

public abstract class Phase {

	private PhaseRunPolicy runPolicy;
	private String description;
	private Workflow workflow;
	private boolean doLog;
	
	protected Phase(PhaseRunPolicy runPolicy, String description, boolean doLog){
		this.description = description;
		this.doLog = doLog;
		this.runPolicy = runPolicy;
	}
	
	
	public final boolean canExecute(CompilationEnvironment env){
		boolean doRun = false;
		switch (runPolicy) {
		case ALWAYS: doRun = true; break;
		case IF_ERRORS: doRun = !(env.getResult().isSuccessful()); break;
		case IF_NO_ERRORS: doRun = (env.getResult().isSuccessful()); break;
		default: doRun = false;
		}
		if(!doRun) return false;
		
		return wantExecute(env);
	}
	
	protected boolean wantExecute(CompilationEnvironment env){ return true; }
	
	public abstract void execute(CompilationEnvironment env, Workflow workflow);
	
	public PhaseRunPolicy getRunPolicy(){
		return PhaseRunPolicy.ALWAYS;
	}
	
	public String getDescription(){
		return description;
	}


	public boolean doLog() {
		return doLog;
	}
	
}
