package com.sc2mod.andromeda.test.sc2tester;

public class SC2TestResultsFailDecider {

	
	private SC2TestResults results;

	public SC2TestResultsFailDecider(SC2TestResults results) {
		this.results = results;
	}
	
	public boolean isFail(){
		switch(results.getResult()){
		case FAILED: return true;
		case INVALID_OUTPUT: return true;
		case NOT_FINISHED: return true;
		case SUCCEEDED: return false;
		case TIMED_OUT: return true;
		}
		return true;
	}
	
	public String getFailMessage(){
		switch(results.getResult()){
		case FAILED: return "Following test cases failed:\n" + getFailingTestCaseStr();
		case INVALID_OUTPUT: return "An invalid bank file was produced";
		case NOT_FINISHED: return "The test did not finish up to the time-out. The following test case ran up to the time out: " + results.getTimeoutTestCase();
		case SUCCEEDED: return null; 
		case TIMED_OUT: return "The test did not even start until the time-out (probably syntax error, or no suite created)";
		}
		return null;
	}

	private String getFailingTestCaseStr() {
		StringBuilder sb = new StringBuilder();
		for( SC2TestCaseResult t : results.getFailingTestCases()){
			sb.append(t.getName() + ": " + t.getFailMessage()+ "\n");
		}
		return sb.toString();		
			
	}
	
	
}
