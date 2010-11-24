package com.sc2mod.andromeda.test.junit.unittests.types;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class TypeErrors extends AndromedaSingleRunTest{
	
	@Test
	public void uncheckedCast(){
		callAndromeda("uncheckedCast.a");
		assertOnlyProblem(ProblemId.TYPE_ERROR_NEED_UNCHECKED_CAST);
	}
	
	@Test
	public void uncheckedCast2(){
		callAndromeda("uncheckedCast2.a");
		assertOnlyProblem(ProblemId.TYPE_ERROR_NEED_UNCHECKED_CAST);
	}
	
	@Test
	public void forbiddenExtensionCast(){
		callAndromeda("forbiddenExtensionCast.a");
		assertOnlyProblem(ProblemId.TYPE_ERROR_INCOMPATIBLE_ASSIGNMENT);
	}
	
	@Test
	public void forbiddenExtensionCast2(){
		callAndromeda("forbiddenExtensionCast2.a");
		assertOnlyProblem(ProblemId.TYPE_ERROR_INCOMPATIBLE_ASSIGNMENT);
	}
	
	@Test
	public void forbiddenComparison(){
		callAndromeda("forbiddenComparison.a");
		assertOnlyProblem(ProblemId.TYPE_ERROR_INCOMPATIBLE_COMPARISON);
	}
}
