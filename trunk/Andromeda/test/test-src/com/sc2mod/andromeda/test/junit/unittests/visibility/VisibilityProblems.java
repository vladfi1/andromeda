package com.sc2mod.andromeda.test.junit.unittests.visibility;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class VisibilityProblems extends AndromedaSingleRunTest {

	@Test
	public void privateVisibility(){
		callAndromeda("PrivateVisibility.a","PrivateVisibility_2.a");
		assertOnlyProblem(ProblemId.FIELD_NOT_VISIBLE);
	}
	
	@Test
	public void protectedVisibility(){
		callAndromeda("ProtectedVisibility.a","ProtectedVisibility_2.a","ProtectedVisibility_3.a");
		assertNoMoreProblems();
		checkOutput();
	}
	
}
