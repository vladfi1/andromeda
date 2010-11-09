package com.sc2mod.andromeda.test.junit.unittests.visibility;

import org.junit.Test;

import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class VisibilityProblems extends AndromedaSingleRunTest {

	@Test
	public void visibility1(){
		callAndromeda("PrivateVisibility.a");
		assertOnlyProblem(ProblemId.FIELD_NAME_NOT_FOUND);
	}
	
}
