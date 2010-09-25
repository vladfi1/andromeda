package com.sc2mod.andromeda.test.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.sc2mod.andromeda.test.unittests.ExampleTest;

import junit.framework.Test;
import junit.framework.TestSuite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        ExampleTest.class
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
