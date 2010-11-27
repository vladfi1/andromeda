package com.sc2mod.andromeda.test.junit.unittests.galaxy;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class GalaxyTests extends AndromedaSingleRunTest {
	
	@Test
	public void globalUsedFromAbove(){
		callGalaxy("globalUsedFromAbove.galaxy");
		assertOnlyProblem(ProblemId.GLOBAL_VAR_ACCESS_BEFORE_DECL);
	}
	
	@Test
	public void globalUsedFromAbove2(){
		callGalaxy("globalUsedFromAbove2.galaxy");
		assertOnlyProblem(ProblemId.GALAXY_ACCESSING_ELEMENT_FROM_ABOVE_ITS_DECLARATION);
	}
	
	@Test
	public void globalUsedFromAbove3(){
		callGalaxy("globalUsedFromAbove3.galaxy");
		assertOnlyProblem(ProblemId.VAR_ACCESS_IN_OWN_DECL);
	}
	
	@Test
	public void funcUsedFromAbove(){
		callGalaxy("funcUsedFromAbove.galaxy");
		assertOnlyProblem(ProblemId.GALAXY_ACCESSING_ELEMENT_FROM_ABOVE_ITS_DECLARATION);
	}
	
	@Test
	public void funcUsedFromAbove2(){
		callGalaxy("funcUsedFromAbove2.galaxy");
		assertNoMoreProblems();
	}
	
	@Test
	public void invalidIdent(){
		callGalaxy("invalidIdent.galaxy");
		assertOnlyProblem(ProblemId.SYNTAX_ILLEGAL_CHARACTER);
	}
	
	@Test
	public void invalidIdent2(){
		callGalaxy("invalidIdent2.galaxy");
		assertOnlyProblem(ProblemId.SYNTAX_ILLEGAL_CHARACTER);
	}
	
	@Test
	public void invalidIdent3(){
		callGalaxy("invalidIdent3.galaxy");
		assertNoMoreProblems();
	}
	
	@Test
	public void structWithoutSemicolon(){
		callGalaxy("structWithoutSemicolon.galaxy");
		assertOnlyProblem(ProblemId.SYNTAX_UNEXPECTED_TOKEN);
	}
	
	@Test
	public void blockComment(){
		callGalaxy("blockComment.galaxy");
		assertOnlyProblem(ProblemId.SYNTAX_UNEXPECTED_TOKEN);
	}
	
	@Test
	public void nonTopLocal(){
		callGalaxy("nonTopLocal.galaxy");
		assertOnlyProblem(ProblemId.GALAXY_LOCAL_VAR_NOT_ON_TOP);
	}
	
	@Test
	public void ifWithoutBraces(){
		callGalaxy("ifWithoutBraces.galaxy");
		assertProblem(ProblemId.GALAXY_BLOCK_WITHOUT_BRACES);
		assertOnlyProblem(ProblemId.GALAXY_BLOCK_WITHOUT_BRACES);
	}
	
	@Test
	public void structUsedFromAbove(){
		callGalaxy("structUsedFromAbove.galaxy");
		assertProblem(ProblemId.GALAXY_ACCESSING_ELEMENT_FROM_ABOVE_ITS_DECLARATION);
		assertProblem(ProblemId.GALAXY_ACCESSING_ELEMENT_FROM_ABOVE_ITS_DECLARATION);
		assertOnlyProblem(ProblemId.GALAXY_ACCESSING_ELEMENT_FROM_ABOVE_ITS_DECLARATION);
	}
	
	@Test
	public void typedefUsedFromAbove(){
		callGalaxy("typedefUsedFromAbove.galaxy");
		assertOnlyProblem(ProblemId.GALAXY_ACCESSING_ELEMENT_FROM_ABOVE_ITS_DECLARATION);
	}
	
	
}
