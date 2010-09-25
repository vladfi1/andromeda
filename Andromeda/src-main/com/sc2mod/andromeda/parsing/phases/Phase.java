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
	
	
	public boolean canExecute(CompilationEnvironment env){
		switch (runPolicy) {
		case ALWAYS: return true;
		case IF_ERRORS: return !(env.getResult().isSuccessful());
		case IF_NO_ERRORS: return (env.getResult().isSuccessful());
		default: return false;
		}
	}
	
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
