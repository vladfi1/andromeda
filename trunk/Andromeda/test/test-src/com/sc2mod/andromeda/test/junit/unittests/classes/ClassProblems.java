package com.sc2mod.andromeda.test.junit.unittests.classes;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class ClassProblems extends AndromedaSingleRunTest {
	
	@Test
	public void nonStaticMembersInStaticClass(){
		callAndromeda("nonStaticMembersInStaticClass.a");
		assertOnlyProblem(ProblemId.STATIC_CLASS_HAS_NON_STATIC_MEMBER);
	}
	
	
	@Test
	public void nonStaticMembersInStaticClass2(){
		callAndromeda("nonStaticMembersInStaticClass2.a");
		assertOnlyProblem(ProblemId.STATIC_CLASS_HAS_NON_STATIC_MEMBER);
	}
	
	@Test
	public void nonAbstractClassHasAbstractMembers(){
		callAndromeda("nonAbstractClassHasAbstractMembers.a");
		assertOnlyProblem(ProblemId.ABSTRACT_METHOD_IN_NON_ABSTRACT_CLASS);
	}
	
	@Test
	public void nonAbstractClassHasAbstractMembers2(){
		callAndromeda("nonAbstractClassHasAbstractMembers2.a");
		assertOnlyProblem(ProblemId.NON_ABSTRACT_CLASS_MISSES_IMPLEMENTATIONS);
	}
	
	@Test
	public void nonAbstractClassHasAbstractMembers3(){
		callAndromeda("nonAbstractClassHasAbstractMembers3.a");
		assertOnlyProblem(ProblemId.ABSTRACT_METHOD_IN_NON_ABSTRACT_CLASS);
	}
	
	@Test
	public void nonAbstractClassHasAbstractMembers4(){
		callAndromeda("nonAbstractClassHasAbstractMembers4.a");
		assertOnlyProblem(ProblemId.NON_ABSTRACT_CLASS_MISSES_IMPLEMENTATIONS);
	}
	
	
	
	
}
