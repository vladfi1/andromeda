package com.sc2mod.andromeda.test.junit.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.sc2mod.andromeda.test.junit.unittests.ExampleTest;
import com.sc2mod.andromeda.test.junit.unittests.generics.GenericTests;
import com.sc2mod.andromeda.test.junit.unittests.problems.visibility.VisibilityProblems;
import com.sc2mod.andromeda.test.junit.unittests.syntax.SyntaxProblems;
import com.sc2mod.andromeda.test.junit.unittests.types.TypeErrors;

import junit.framework.Test;
import junit.framework.TestSuite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        SyntaxProblems.class,
        VisibilityProblems.class,
        GenericTests.class,
        TypeErrors.class
})

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.sc2mod.andromeda.test.suites");
		//$JUnit-BEGIN$

		//$JUnit-END$
		return suite;
	}

}
