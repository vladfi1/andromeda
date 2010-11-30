package com.sc2mod.andromeda.test.sc2tester;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.sc2mod.andromeda.test.sc2tester.SC2TestCaseResult.CaseResult;
import com.sc2mod.andromeda.test.sc2tester.SC2TestResults.SuiteResult;

public class SC2TestResultProcessor {

	
	public SC2TestResults process(BankContent bc){
		String progress = bc.getValue("misc", "progress");
		boolean finished = false;
		List<SC2TestCaseResult> succeeded = new ArrayList<SC2TestCaseResult>();
		List<SC2TestCaseResult> fails = new ArrayList<SC2TestCaseResult>();
		List<SC2TestCaseResult> results = new ArrayList<SC2TestCaseResult>();
		SC2TestCaseResult aborted = null;
		if("finished".equals(progress)){
			finished = true;
		}
		
		for ( Entry<String, BankSection> entry : bc.entrySet() ) {
			String sectionName = entry.getKey();
			if(sectionName.startsWith("test_")){
				SC2TestCaseResult caseResult = processTestCase(sectionName.substring(5),entry.getValue());
				switch(caseResult.getResult()){
				case ABORTED: aborted = caseResult; break;
				case FAILED: fails.add(caseResult); break;
				case SUCCEEDED: succeeded.add(caseResult); break;
				}
				results.add(caseResult);
			}
		}
		
		SuiteResult suiteResult;
		if(!finished){
			suiteResult = SuiteResult.NOT_FINISHED;
		} else {
			if(fails.isEmpty()){
				suiteResult = SuiteResult.SUCCEEDED;
			} else {
				suiteResult = SuiteResult.FAILED;
			}
		}
		return new SC2TestResults(suiteResult,results,succeeded,fails,aborted);
		
		
	}

	private SC2TestCaseResult processTestCase(String name,
			BankSection value) {
		int id = Integer.parseInt(value.get("id"));
		String progress = value.get("progess");
		if("started".equals(progress)){
			return new SC2TestCaseResult(name, id, CaseResult.ABORTED);
		} else if("failed".equals(progress)){
			return new SC2TestCaseResult(name, id, value.get("failMsg"));
		} else {
			return new SC2TestCaseResult(name, id, CaseResult.SUCCEEDED);
		}
	}

	public boolean isFinished(BankContent bc) {
		String progress = bc.getValue("misc", "progress");
		
		return "finished".equals(progress);
				
	}
	
	

	
}
