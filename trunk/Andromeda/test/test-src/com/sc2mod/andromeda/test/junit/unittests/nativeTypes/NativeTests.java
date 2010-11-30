package com.sc2mod.andromeda.test.junit.unittests.nativeTypes;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class NativeTests extends AndromedaSingleRunTest {
	
	@Test
	public void setMissing(){
		callAndromeda("setMissing.a");
		assertOnlyProblem(ProblemId.ACCESSOR_GET_OR_SET_MISSING);
	}
	
	
	@Test
	public void getMissing(){
		callAndromeda("getMissing.a");
		assertOnlyProblem(ProblemId.ACCESSOR_GET_OR_SET_MISSING);
	}
	
	@Test
	public void getSetStaticConflict(){
		callAndromeda("getSetStaticConflict.a");
		assertOnlyProblem(ProblemId.ACCESSOR_STATIC_NON_STATIC_GET_SET);
	}
	
	@Test
	public void getSetStaticConflict2(){
		callAndromeda("getSetStaticConflict2.a");
		assertOnlyProblem(ProblemId.ACCESSOR_STATIC_NON_STATIC_GET_SET);
	}
	
	@Test
	public void getNotVisible(){
		callAndromeda("getNotVisible.a","getNotVisible_2.a");
		assertOnlyProblem(ProblemId.ACCESSOR_GET_OR_SET_NOT_VISIBLE);
	}
	
	@Test
	public void setNotVisible(){
		callAndromeda("setNotVisible.a","setNotVisible_2.a");
		assertOnlyProblem(ProblemId.ACCESSOR_GET_OR_SET_NOT_VISIBLE);
	}
	
	@Test
	public void getWrongSignature(){
		callAndromeda("getWrongSignature.a");
		assertOnlyProblem(ProblemId.ACCESSOR_GET_OR_SET_WRONG_SIGNATURE);
	}
	
	@Test
	public void setWrongSignature(){
		callAndromeda("setWrongSignature.a");
		assertOnlyProblem(ProblemId.ACCESSOR_GET_OR_SET_WRONG_SIGNATURE);
	}
	
	
}
