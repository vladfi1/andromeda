package com.sc2mod.andromeda.test.junit.unittests.names;

import org.junit.Test;

import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class NameProblems extends AndromedaSingleRunTest {

	@Test
	public void visi(){
		callAndromeda("IllegalEscape.a");
		assertOnlyProblem(ProblemId.SYNTAX_ILLEGAL_ESCAPE_SEQUENCE);
	}
	
	
	
}
