package com.sc2mod.andromeda.test.sc2tester;

public class SC2TestCaseResult {

	public static enum CaseResult{
		SUCCEEDED,
		FAILED,
		ABORTED
	}

	private CaseResult result;
	private int id;
	private Object failMessage;
	private String name;
	
	public SC2TestCaseResult(String name, int id, CaseResult result){
		this.name = name;
		this.result = result;
		this.id = id;
		this.failMessage = null;
	}
	
	public SC2TestCaseResult(String name, int id, String failMessage){
		this.name = name;
		this.result = CaseResult.FAILED;
		this.id = id;
		this.failMessage = failMessage;
	}
}
