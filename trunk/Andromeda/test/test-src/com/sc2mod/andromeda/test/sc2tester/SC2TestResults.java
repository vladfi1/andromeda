package com.sc2mod.andromeda.test.sc2tester;

public class SC2TestResults {
	
	public static enum SuiteResult{
		
		/**
		 * All test cases succeeded
		 */
		SUCCEEDED,
		
		/**
		 * At least one test case failed
		 */
		FAILED,
		
		/**
		 * No result bank was written until the time out
		 */
		TIMED_OUT,
		
		/**
		 * The result was not written completely until the time out
		 * (so the test suite assumes that the test thread is crashed,
		 * contained no finish statement or needed to long to finish)
		 * 
		 */
		NOT_FINISHED,
		
		/**
		 * If the content of the output bank were invalid
		 */
		INVALID_OUTPUT
	}

	private SuiteResult result;
	private int numStartedTestCases;
	
	public SC2TestResults(SuiteResult result){
		this.result = result;
	}
	
	public SuiteResult getResult(){
		return result;
	}
	
	public int getNumStartedTestCases(){
		return numStartedTestCases;
	}
	
	

}
