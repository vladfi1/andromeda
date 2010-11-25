package com.sc2mod.andromeda.test.sc2tester;

import java.io.File;

import org.w3c.dom.Document;

import com.sc2mod.andromeda.test.sc2tester.SC2TestResults.SuiteResult;
import com.sc2mod.andromeda.util.StopWatch;
import com.sc2mod.andromeda.util.ThreadUtil;

/**
 * This class allows to run tests on a map.
 * 
 * This is done by opening the map and checking the banks for test entries.
 * 
 * @author gex
 *
 */
public class SC2Tester {
	
	private static final int CHECK_INTERVAL = 400;
	private File bankFolder;
	private File sc2Folder;
	private int testTimeout;

	public SC2Tester(File sc2Folder, File bankFolder, int testTimeout){
		this.sc2Folder = sc2Folder;
		this.bankFolder = bankFolder;
		this.testTimeout = testTimeout;
		
	}
	
	public SC2TestResults execute(File mapFile, String testSuiteName){
		File bankFile = new File(bankFolder,"test_" + testSuiteName);
		if(bankFile.exists()){
			bankFile.delete();
		}
		startSC2();
		StopWatch time = new StopWatch();
		time.start();
		SC2TestResults results = null;
		while(true){
			ThreadUtil.sleepMillisec(CHECK_INTERVAL);
			if(isTestFinished(bankFile) || time.getTime() > testTimeout){
				results = evaluateTestResults(bankFile,testSuiteName);
				break;
			}
		}
		stopSC2();
		return results;
		
		
	}

	private void stopSC2() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	private SC2TestResults evaluateTestResults(File bankFile,
			String testCaseName) {
		if(!bankFile.exists()){
			return new SC2TestResults(SuiteResult.TIMED_OUT);
		}
		
		Document bankContent = new BankReader(bankFile).read();
		
		processBankContent(bankContent);
	}

	private void processBankContent(Document bankContent) {


	}

	private boolean isTestFinished(File bankFile) {
		if(!bankFile.exists()){
			return false;
		}
		
		//TODO implement check test finished
		return false;
		
	}

	private void startSC2() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

}
