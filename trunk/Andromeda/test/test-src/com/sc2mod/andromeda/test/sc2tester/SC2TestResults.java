package com.sc2mod.andromeda.test.sc2tester;

import java.util.List;

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
	
	public List<SC2TestCaseResult> failingTestCases;
	public SC2TestCaseResult timeoutTestCase;
	private List<SC2TestCaseResult> results;
	private List<SC2TestCaseResult> succeeded;
	
	
	
	public List<SC2TestCaseResult> getFailingTestCases() {
		return failingTestCases;
	}

	public void setFailingTestCases(List<SC2TestCaseResult> failingTestCases) {
		this.failingTestCases = failingTestCases;
	}

	public SC2TestCaseResult getTimeoutTestCase() {
		return timeoutTestCase;
	}

	public void setTimeoutTestCase(SC2TestCaseResult timeoutTestCase) {
		this.timeoutTestCase = timeoutTestCase;
	}

	public SC2TestResults(SuiteResult result){
		this.result = result;
	}
	
	public SC2TestResults(SuiteResult suiteResult,
			List<SC2TestCaseResult> results, List<SC2TestCaseResult> succeeded,
			List<SC2TestCaseResult> fails, SC2TestCaseResult aborted) {
		this(suiteResult);
		this.results = results;
		this.succeeded = succeeded;
		failingTestCases = fails;
		timeoutTestCase = aborted;
	}

	public SuiteResult getResult(){
		return result;
	}
	
	public int getNumStartedTestCases(){
		return numStartedTestCases;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("==============================================\n");
		sb.append("Overall result: >>> ").append(result.name()).append(" <<<\n");
		if(results == null || results.isEmpty()){
			sb.append("----------- No finished test cases -----------\n");
		} else {
			sb.append("----------------- Test cases -----------------\n");
			for(SC2TestCaseResult result : results){
				sb.append(result.toString()).append("\n");
			}
		}
		sb.append("==============================================\n");
		return sb.toString();
	}
	
	

}
