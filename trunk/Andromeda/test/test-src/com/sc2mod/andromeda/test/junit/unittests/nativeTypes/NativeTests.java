package com.sc2mod.andromeda.test.junit.unittests.nativeTypes;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class NativeTests extends AndromedaSingleRunTest {
	
	@Test
	public void nativeTypesInCondition(){
		callAndromeda("nativeTypesInCondition.a");
		assertOnlyProblem(ProblemId.ACCESSOR_GET_OR_SET_MISSING);
	}
	
	
	
}
