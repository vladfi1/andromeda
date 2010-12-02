package com.sc2mod.andromeda.test.junit.unittests.closures;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class ClosureTests extends AndromedaSingleRunTest {
	
	@Test
	public void funcName(){
		callAndromeda("funcName.a");
		assertNoMoreProblems();
		checkOutput();
	}

	
	
	
}
