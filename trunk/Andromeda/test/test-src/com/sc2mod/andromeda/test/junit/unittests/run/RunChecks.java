package com.sc2mod.andromeda.test.junit.unittests.run;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class RunChecks extends AndromedaSingleRunTest {

	@Test
	public void baseRun(){
		parseAndDoIngameTest("base", 25000, "baseRun.a");
	}
		
	@Test
	public void fulltest(){
		parseAndDoIngameTest("full", 25000, "fulltest.a");
	}
}
