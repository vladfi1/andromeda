package com.sc2mod.andromeda.test.junit.unittests.extensions;

import org.junit.Test;

import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class ExtensionTests extends AndromedaSingleRunTest {
	
	@Test
	public void constructorInExtension(){
		callAndromeda("constructorInExtension.a");
		assertOnlyProblem(ProblemId.CONSTRUCTOR_OUTSIDE_OF_CLASS);
	}
	
	@Test
	public void destructorInExtension(){
		callAndromeda("destructorInExtension.a");
		assertOnlyProblem(ProblemId.DESTRUCTOR_OUTSIDE_OF_CLASS);
	}
	
	@Test
	public void fieldInExtension(){
		callAndromeda("fieldInExtension.a");
		assertOnlyProblem(ProblemId.NATIVE_ENRICHMENT_HAS_FIELD);
	}
	
	@Test
	public void extensionOfClass(){
		callAndromeda("extensionOfClass.a");
		assertOnlyProblem(ProblemId.EXTENSION_TYPE_INVALID);
	}
	
	@Test
	public void enrichmentBase(){
		callAndromeda("enrichmentBase.a");
		assertNoMoreProblems();
		checkOutput();
	}
	
	
	
	
}
