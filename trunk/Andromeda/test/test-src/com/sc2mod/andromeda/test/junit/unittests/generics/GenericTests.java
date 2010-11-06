package com.sc2mod.andromeda.test.junit.unittests.generics;

import org.junit.Test;

import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class GenericTests extends AndromedaSingleRunTest {

	@Test
	public void genericBase(){
		callAndromeda("genericBase.a");
		assertNoMoreProblems();
		checkOutput();
	}
	
	@Test
	public void genericCasts1(){
		callAndromeda("genericCasts1.a");
		assertNoMoreProblems();
		checkOutput();
	}
	
	@Test
	public void invalidTypeBound(){
		callAndromeda("invalidTypeBound.a");
		assertOnlyProblem(ProblemId.INVALID_TYPE_BOUND);
	}
	
	
	
}
