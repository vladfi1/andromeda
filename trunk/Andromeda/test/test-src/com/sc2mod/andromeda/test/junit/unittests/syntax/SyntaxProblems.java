package com.sc2mod.andromeda.test.junit.unittests.syntax;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class SyntaxProblems extends AndromedaSingleRunTest {

	@Test
	public void illegalEscape(){
		callAndromeda("IllegalEscape.a");
		assertOnlyProblem(ProblemId.SYNTAX_ILLEGAL_ESCAPE_SEQUENCE);
	}
	
	@Test
	public void backslashAtEndOfString(){
		callAndromeda("BackslashAtEnd.a");
		assertOnlyProblem(ProblemId.SYNTAX_UNTERMINATED_STRING);
	}
	
	@Test
	public void unterminatedString(){
		callAndromeda("UnterminatedString.a");
		assertOnlyProblem(ProblemId.SYNTAX_UNTERMINATED_STRING);
	}
	
	@Test
	public void unexpectedToken(){
		callAndromeda("UnexpectedToken.a");
		assertOnlyProblem(ProblemId.SYNTAX_UNEXPECTED_TOKEN);
	}
	
}
