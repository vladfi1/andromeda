package com.sc2mod.andromeda.test.junit;

import java.io.File;
import org.junit.Assert;

public class UnitTestRun {
	protected File getPackageFile(String name){
		return new File(packageToString(this.getClass().getPackage()),name).getAbsoluteFile();
	}
	
	protected String packageToString(Package p){
		String result = "test/test-src/" + p.getName().replace('.', '/');
		return result;
	}
	
	protected void failException(Throwable t) {
		t.printStackTrace();
		Assert.fail("An exception occurred: " + t.getMessage());
	}
}
