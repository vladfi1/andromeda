package com.sc2mod.andromeda.test.junit.unittests.misc;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class MiscProblems extends AndromedaSingleRunTest {
	
	@Test
	public void danglingForwardDecl(){
		callAndromeda("danglingForwardDecl.a");
		assertOnlyProblem(ProblemId.DANGLING_FORWARD_DECLARATION);
	}
	
	@Test
	public void fieldAccessFromAbove(){
		callAndromeda("fieldAccessFromAbove.a");
		assertProblem(ProblemId.GLOBAL_VAR_ACCESS_BEFORE_DECL);
		assertProblem(ProblemId.GLOBAL_VAR_ACCESS_BEFORE_DECL);
		assertProblem(ProblemId.GLOBAL_VAR_ACCESS_BEFORE_DECL);
		assertOnlyProblem(ProblemId.GLOBAL_VAR_ACCESS_BEFORE_DECL);
	}
	
	@Test
	public void fieldAccessFromAbove2(){
		callAndromeda("fieldAccessFromAbove2.a");
		assertProblem(ProblemId.FIELD_ACCESS_BEFORE_DECL);
		assertOnlyProblem(ProblemId.FIELD_ACCESS_BEFORE_DECL);
	}
	
	@Test
	public void varAccessInOwnDecl(){
		callAndromeda("varAccessInOwnDecl.a");
		assertProblem(ProblemId.VAR_ACCESS_IN_OWN_DECL);
		assertProblem(ProblemId.VAR_ACCESS_IN_OWN_DECL);
		assertProblem(ProblemId.VAR_ACCESS_IN_OWN_DECL);
		assertOnlyProblem(ProblemId.VAR_ACCESS_IN_OWN_DECL);
	}
	
	@Test
	public void arrays(){
		callAndromeda("arrays.a");
		assertProblem(ProblemId.NEGATIVE_ARRAY_DIMENSION);
		assertProblem(ProblemId.NON_CONSTANT_ARRAY_DIMENSION);
		assertProblem(ProblemId.INVALID_ARRAY_DIMENSION_TYPE);
		assertProblem(ProblemId.ARRAY_OR_STRUCT_AS_PARAMETER);
		assertOnlyProblem(ProblemId.ARRAY_OR_STRUCT_RETURNED);
	}
	
	@Test
	public void arrays2(){
		callAndromeda("arrays2.a");
		assertNoMoreProblems();
		checkOutput();
	}
	
	
	
}
