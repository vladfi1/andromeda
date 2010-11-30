package com.sc2mod.andromeda.test.junit.unittests.generics;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
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
	
	@Test
	public void genericHierarchy(){
		callAndromeda("genericHierarchy.a");
		assertOnlyProblem(ProblemId.TYPE_ERROR_INCOMPATIBLE_ASSIGNMENT);
	}
	
	@Test
	public void genericHierarchy2(){
		callAndromeda("genericHierarchy2.a");
		assertNoMoreProblems();
		checkOutput();
	}
	
	@Test
	public void genericHierarchy3(){
		callAndromeda("genericHierarchy3.a");
		assertOnlyProblem(ProblemId.TYPE_ERROR_INCOMPATIBLE_ASSIGNMENT);
	}
	
	@Test
	public void genericHierarchy4(){
		callAndromeda("genericHierarchy4.a");
		assertNoMoreProblems();
		checkOutput();
	}
	
	@Test
	public void genericExtensions(){
		callAndromeda("genericExtensions.a");
		assertNoMoreProblems();
		checkOutput();
	}
	
	@Test
	public void genericClassEnrichment(){
		callAndromeda("genericClassEnrichment.a");
		assertNoMoreProblems();
		checkOutput();
	}
	
	@Test
	public void typeParamVisibility(){
		callAndromeda("typeParamVisibility.a");
		assertOnlyProblem(ProblemId.UNKNOWN_TYPE);
	}
	
	@Test
	public void typeParamInEnrichment(){
		callAndromeda("typeParamInEnrichment.a");
		assertOnlyProblem(ProblemId.UNKNOWN_TYPE);
	}
	
	@Test
	public void typeParamInEnrichment2(){
		callAndromeda("typeParamInEnrichment2.a");
		assertNoMoreProblems();
		checkOutput();
	}
	
}
