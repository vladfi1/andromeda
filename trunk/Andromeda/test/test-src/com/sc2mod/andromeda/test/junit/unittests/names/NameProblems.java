package com.sc2mod.andromeda.test.junit.unittests.names;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class NameProblems extends AndromedaSingleRunTest {

	@Test
	public void accessorNotVisible(){
		callAndromeda("accessorNotVisible.a","accessorNotVisible_2.a");
		assertOnlyProblem(ProblemId.ACCESSOR_NOT_VISIBLE);
	}
	
	@Test
	public void accessorWrongSig(){
		callAndromeda("accessorWrongSig.a");
		assertOnlyProblem(ProblemId.ACCESSOR_WRONG_SIGNATURE);
	}
	
	@Test
	public void accessorWrongSig2(){
		callAndromeda("accessorWrongSig2.a");
		assertOnlyProblem(ProblemId.ACCESSOR_WRONG_SIGNATURE);
	}
	
	@Test
	public void nameNotVisible(){
		callAndromeda("nameNotVisible.a","nameNotVisible_2.a");
		assertOnlyProblem(ProblemId.FIELD_NOT_VISIBLE);
	}
	
	@Test
	public void nameNotFound(){
		callAndromeda("nameNotFound.a");
		assertOnlyProblem(ProblemId.FIELD_NAME_NOT_FOUND);
	}
	
	@Test
	public void prefixNameNotFound(){
		callAndromeda("prefixNameNotFound.a");
		assertOnlyProblem(ProblemId.FIELD_NAME_NOT_FOUND);
	}
	
	
	
}
