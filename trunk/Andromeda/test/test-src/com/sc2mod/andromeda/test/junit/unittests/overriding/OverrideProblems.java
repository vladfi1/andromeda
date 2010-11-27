package com.sc2mod.andromeda.test.junit.unittests.overriding;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class OverrideProblems extends AndromedaSingleRunTest {

	@Test
	public void legalOverrides(){
		callAndromeda("legalOverrides.a");
		assertNoMoreProblems();
	}
	
	@Test
	public void missingOverrideModifier(){
		callAndromeda("missingOverrideModifier.a");
		assertOnlyProblem(ProblemId.OVERRIDE_WITHOUT_OVERRIDE_MODIFIER);
	}
	
	@Test
	public void missingOverrideModifier2(){
		callAndromeda("missingOverrideModifier2.a");
		assertOnlyProblem(ProblemId.OVERRIDE_WITHOUT_OVERRIDE_MODIFIER);
	}
	
	@Test
	public void missingOverrideModifier3(){
		callAndromeda("missingOverrideModifier3.a");
		assertOnlyProblem(ProblemId.OVERRIDE_WITHOUT_OVERRIDE_MODIFIER);
	}
	
	@Test
	public void superfluousOverrideModifier(){
		callAndromeda("superfluousOverrideModifier.a");
		assertOnlyProblem(ProblemId.OVERRIDE_DECL_DOES_NOT_OVERRIDE);
	}
	
	@Test
	public void overrideFinalMethod(){
		callAndromeda("overrideFinalMethod.a");
		assertOnlyProblem(ProblemId.OVERRIDE_FINAL_METHOD);
	}
	
	@Test
	public void overrideField(){
		callAndromeda("overrideField.a");
		assertOnlyProblem(ProblemId.OVERRIDE_FORBIDDEN_ELEMENT);
	}
	
	@Test
	public void overrideField2(){
		callAndromeda("overrideField2.a");
		assertOnlyProblem(ProblemId.OVERRIDE_FORBIDDEN_ELEMENT);
	}
	
	@Test
	public void wrongReturnType(){
		callAndromeda("wrongReturnType.a");
		assertOnlyProblem(ProblemId.OVERRIDE_RETURN_TYPE_MISMATCH);
	}
	
	@Test
	public void staticNonStatic1(){
		callAndromeda("staticNonStatic1.a");
		assertOnlyProblem(ProblemId.OVERRIDE_STATIC_NON_STATIC);
	}
	
	@Test
	public void staticNonStatic2(){
		callAndromeda("staticNonStatic2.a");
		assertOnlyProblem(ProblemId.OVERRIDE_STATIC_NON_STATIC);
	}
	
	@Test
	public void visibilityReduced(){
		callAndromeda("visibilityReduced.a");
		assertOnlyProblem(ProblemId.OVERRIDE_REDUCED_VISIBILITY);
	}
	
	@Test
	public void visibilityReduced2(){
		callAndromeda("visibilityReduced2.a");
		assertOnlyProblem(ProblemId.OVERRIDE_REDUCED_VISIBILITY);
	}
	
	@Test
	public void visibilityReduced3(){
		callAndromeda("visibilityReduced3.a");
		assertOnlyProblem(ProblemId.OVERRIDE_REDUCED_VISIBILITY);
	}
	
	@Test
	public void overridePrivate(){
		callAndromeda("overridePrivateMethod.a");
		assertOnlyProblem(ProblemId.OVERRIDE_DECL_DOES_NOT_OVERRIDE);
	}
	
	@Test
	public void useSuper(){
		callAndromeda("useSuper.a");
		assertNoMoreProblems();
		checkOutput();
	}
	
	@Test
	public void superInStaticContext(){
		callAndromeda("superInStaticContext.a");
		assertOnlyProblem(ProblemId.STATIC_MEMBER_MISUSE);
	}
	
	@Test
	public void superInStaticContext2(){
		callAndromeda("superInStaticContext2.a");
		assertOnlyProblem(ProblemId.STATIC_MEMBER_MISUSE);
	}
	
	
	
}
