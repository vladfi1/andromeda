package com.sc2mod.andromeda.notifications;

public class UnrecoverableProblem extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public final Problem problem;
	
	public UnrecoverableProblem(Problem problem){
		this.problem = problem;
	}

	
}
