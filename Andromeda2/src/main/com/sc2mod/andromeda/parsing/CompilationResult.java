package com.sc2mod.andromeda.parsing;

import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.notifications.Message;
import com.sc2mod.andromeda.notifications.MessageSeverity;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.util.logging.Log;

public class CompilationResult {

	private ArrayList<Problem> warnings = new ArrayList<Problem>();
	private ArrayList<Problem> errors = new ArrayList<Problem>();
	private ArrayList<Problem> others = new ArrayList<Problem>();
	
	public ArrayList<Problem> getWarnings() {
		return warnings;
	}
	public ArrayList<Problem> getErrors() {
		return errors;
	}
	
	public boolean isSuccessful(){
		return errors.isEmpty();
	}
	
	
	/**
	 * Adds a problem. Internal method called by Problem.raise().
	 * Not to be called from anywhere else!
	 * @param problem the problem that occurred
	 */
	public void registerProblem(Problem problem) {
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
		
		Log.printProblem(problem);
	}
	
}
