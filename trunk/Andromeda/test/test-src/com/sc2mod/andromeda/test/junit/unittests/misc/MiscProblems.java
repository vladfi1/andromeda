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
	
	
	
	
}
