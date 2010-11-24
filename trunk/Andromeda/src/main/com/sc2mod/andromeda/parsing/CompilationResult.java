package com.sc2mod.andromeda.parsing;

import java.util.ArrayList;

import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.util.Debug;
import com.sc2mod.andromeda.util.logging.Log;

public class CompilationResult {

	private boolean printStackTraces;

	public CompilationResult(boolean printProblemStackTraces){
		this.printStackTraces = printProblemStackTraces;
	}
	
	private ArrayList<Problem> warnings = new ArrayList<Problem>();
	private ArrayList<Problem> errors = new ArrayList<Problem>();
	private ArrayList<Problem> others = new ArrayList<Problem>();
	private ArrayList<Problem> problems = new ArrayList<Problem>();
	
	public ArrayList<Problem> getWarnings() {
		return warnings;
	}
	public ArrayList<Problem> getErrors() {
		return errors;
	}
	public ArrayList<Problem> getOthers() {
		return others;
	}
	public ArrayList<Problem> getProblems() {
		return problems;
	}
	
	
	public boolean isSuccessful(){
		return errors.isEmpty();
	}
	
	
	/**
	 * Adds a problem. Internal method called by Problem.raise().
	 * Not to be called from anywhere else!
	 * @param problem the problem that occurred
	 */
	public synchronized void registerProblem(Problem problem) {
		problems.add(problem);
		switch(problem.getSeverity()){
		case ERROR:
		case FATAL_ERROR:
			errors.add(problem);
			break;
		case WARNING:
			warnings.add(problem);
			break;
		default:
			others.add(problem);
		}
		if(printStackTraces){
			problem.setStackTrace(Debug.getStackTrace(3, 0));
		}
		Log.printProblem(problem,printStackTraces);
	}
	
}
