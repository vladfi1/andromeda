package com.sc2mod.andromeda.test.sc2tester;

import com.sc2mod.andromeda.util.Strings;

public class SC2TestCaseResult {

	public static enum CaseResult{
		SUCCEEDED,
		FAILED,
		ABORTED
	}

	private CaseResult result;
	private int id;
	private String failMessage;
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

	public CaseResult getResult() {
		return result;
	}

	public int getId() {
		return id;
	}

	public String getFailMessage() {
		return failMessage;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return Strings.pad(id + "", 3, true) + " " + Strings.pad(name, 30, false) + " " + result.name() ;
	}
}
