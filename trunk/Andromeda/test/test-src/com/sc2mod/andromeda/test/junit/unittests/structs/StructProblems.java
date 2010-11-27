package com.sc2mod.andromeda.test.junit.unittests.structs;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class StructProblems extends AndromedaSingleRunTest {
	
	@Test
	public void structReferencingSelf(){
		callAndromeda("structReferencingSelf.a");
		assertProblem(ProblemId.STRUCT_MEMBER_REFERENCING_SELF);
		assertProblem(ProblemId.STRUCT_MEMBER_REFERENCING_SELF);
		assertProblem(ProblemId.STRUCT_MEMBER_REFERENCING_SELF);
		assertOnlyProblem(ProblemId.STRUCT_MEMBER_REFERENCING_SELF);
	}
	
	@Test
	public void structReferencingStructAfterwards(){
		callAndromeda("structReferencingStructAfterwards.a");
		assertProblem(ProblemId.STRUCT_MEMBER_REFERENCING_STRUCT_BELOW);
		assertProblem(ProblemId.STRUCT_MEMBER_REFERENCING_STRUCT_BELOW);
		assertProblem(ProblemId.STRUCT_MEMBER_REFERENCING_STRUCT_BELOW);
		assertOnlyProblem(ProblemId.STRUCT_MEMBER_REFERENCING_STRUCT_BELOW);
	}
	
	@Test
	public void emptyStruct(){
		callAndromeda("emptyStruct.a");
		assertOnlyProblem(ProblemId.SYNTAX_UNEXPECTED_TOKEN);
	}
	
	@Test
	public void genericStruct(){
		callAndromeda("genericStruct.a");
		assertNoMoreProblems();
		checkOutput();
	}
	
	@Test
	public void structWithInit(){
		callAndromeda("structWithInit.a");
		assertOnlyProblem(ProblemId.STRUCT_MEMBER_WITH_INIT);
	}
		
	@Test
	public void structWithModifiers(){
		callAndromeda("structWithModifiers.a");
		assertOnlyProblem(ProblemId.STRUCT_MEMBER_WITH_MODIFIER);
	}
	
	
	
}
