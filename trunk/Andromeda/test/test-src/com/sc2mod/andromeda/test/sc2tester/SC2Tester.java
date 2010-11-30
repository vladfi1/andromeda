package com.sc2mod.andromeda.test.sc2tester;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;

import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.program.MapRunner;
import com.sc2mod.andromeda.program.Program;
import com.sc2mod.andromeda.test.sc2tester.SC2TestResults.SuiteResult;
import com.sc2mod.andromeda.util.StopWatch;
import com.sc2mod.andromeda.util.ThreadUtil;
import com.sc2mod.andromeda.util.WindowsProcessKiller;

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
	private File sc2Executable;
	private int testTimeout;
	private String sc2RunParams;
	private SC2TestResultProcessor resultProcessor = new SC2TestResultProcessor();

	public SC2Tester(File sc2Executable, String sc2RunParams, File bankFolder, int testTimeout){
		this.sc2Executable = sc2Executable;
		this.sc2RunParams = sc2RunParams;
		this.bankFolder = bankFolder;
		this.testTimeout = testTimeout;
		
	}
	
	public SC2TestResults execute(File mapFile, String testSuiteName){
		File bankFile = new File(bankFolder,"test" + testSuiteName + ".SC2Bank");
		if(bankFile.exists()){
			bankFile.delete();
		}
		startSC2(mapFile);
		StopWatch time = new StopWatch();
		time.start();
		SC2TestResults results = null;

		System.out.println("Test running, status (. = not started, ? = running, ! = finished):");
		while(true){
			ThreadUtil.sleepMillisec(CHECK_INTERVAL);
			TestStatus status = getTestStatus(bankFile);
			System.out.print(status.icon);
			if(status == TestStatus.FINISHED || time.getTime() > testTimeout){
				results = evaluateTestResults(bankFile,testSuiteName);
				break;
			}
		}
		stopSC2();
		return results;
		
		
	}

	private void stopSC2() {
		new WindowsProcessKiller().kill("SC2.exe",false);
		System.out.println("Test finished, SC2 terminated.");
	}

	private SC2TestResults evaluateTestResults(File bankFile,
			String testCaseName) {
		if(!bankFile.exists()){
			return new SC2TestResults(SuiteResult.TIMED_OUT);
		}
		
		BankContent bc = new BankReader(bankFile).read();
		
		return resultProcessor.process(bc);
	}

	private static enum TestStatus{
		NOT_STARTED("."), STARTED("?"), FINISHED("!\n");
		
		private String icon;

		private TestStatus(String icon){
			this.icon = icon;
		}
	}

	private TestStatus getTestStatus(File bankFile) {
		if(!bankFile.exists()){
			return TestStatus.NOT_STARTED;
		}
		
		BankContent bc = new BankReader(bankFile).read();
		
		if( resultProcessor.isFinished(bc)){
			return TestStatus.FINISHED;
		}
		return TestStatus.STARTED;
		
	}

	private void startSC2(File mapFile) {
		try {
			new MapRunner(Program.platform,sc2Executable.getAbsolutePath(),sc2RunParams).test(mapFile);
		} catch (IOException e) {
			throw new InternalProgramError(e);
		}
	}

}
