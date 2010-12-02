package com.sc2mod.andromeda.test.junit.suites;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.sc2mod.andromeda.test.junit.unittests.accessors.AccessorTests;
import com.sc2mod.andromeda.test.junit.unittests.classes.ClassProblems;
import com.sc2mod.andromeda.test.junit.unittests.closures.ClosureTests;
import com.sc2mod.andromeda.test.junit.unittests.extensions.ExtensionTests;
import com.sc2mod.andromeda.test.junit.unittests.galaxy.GalaxyTests;
import com.sc2mod.andromeda.test.junit.unittests.generics.GenericTests;
import com.sc2mod.andromeda.test.junit.unittests.misc.MiscProblems;
import com.sc2mod.andromeda.test.junit.unittests.names.NameProblems;
import com.sc2mod.andromeda.test.junit.unittests.nativeTypes.NativeTests;
import com.sc2mod.andromeda.test.junit.unittests.others.OtherTests;
import com.sc2mod.andromeda.test.junit.unittests.overriding.OverrideProblems;
import com.sc2mod.andromeda.test.junit.unittests.structs.StructProblems;
import com.sc2mod.andromeda.test.junit.unittests.syntax.SyntaxProblems;
import com.sc2mod.andromeda.test.junit.unittests.types.TypeErrors;
import com.sc2mod.andromeda.test.junit.unittests.visibility.VisibilityProblems;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        SyntaxProblems.class,
        VisibilityProblems.class,
        GenericTests.class,
        TypeErrors.class,
        OverrideProblems.class,
        MiscProblems.class,
        ClassProblems.class,
        AccessorTests.class,
        NameProblems.class,
        ExtensionTests.class,
        GalaxyTests.class,
        StructProblems.class,
        OtherTests.class,
        NativeTests.class,
        ClosureTests.class
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
