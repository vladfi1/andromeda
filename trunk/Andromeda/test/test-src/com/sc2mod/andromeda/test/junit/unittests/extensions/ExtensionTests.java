package com.sc2mod.andromeda.test.junit.unittests.extensions;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
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
	public void extensionCircle(){
		callAndromeda("extensionCircle.a");
		assertOnlyProblem(ProblemId.INHERITANCE_CYCLE);
	}
	
	@Test
	public void distinctExtensionOfExtension(){
		callAndromeda("distinctExtensionOfExtension.a");
		assertOnlyProblem(ProblemId.DISJOINT_EXTENSION_BASED_ON_EXTENSION);
	}
	
	@Test
	public void enrichmentBase(){
		callAndromeda("enrichmentBase.a");
		assertNoMoreProblems();
		checkOutput();
	}
	
	@Test
	public void finalExtensionInherited(){
		callAndromeda("finalExtensionInherited.a");
		assertOnlyProblem(ProblemId.FINAL_TYPE_EXTENDED);
	}
	
	@Test
	public void invalidExtensionModifier(){
		callAndromeda("invalidExtensionModifier.a");
		assertProblem(ProblemId.INVALID_MODIFIER);
		assertProblem(ProblemId.INVALID_MODIFIER);
		assertProblem(ProblemId.INVALID_MODIFIER);
		assertProblem(ProblemId.INVALID_MODIFIER);
		assertOnlyProblem(ProblemId.INVALID_MODIFIER);
	}
	
	@Test
	public void typedefBase(){
		callAndromeda("typedefBase.a");
		assertNoMoreProblems();
		checkOutput();
	}
	
	@Test
	public void typedefCircle(){
		callAndromeda("typedefCircle.a");
		assertOnlyProblem(ProblemId.UNKNOWN_TYPE);
	}
	
	
	
}
