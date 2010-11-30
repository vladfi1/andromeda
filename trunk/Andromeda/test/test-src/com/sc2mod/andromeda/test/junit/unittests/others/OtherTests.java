package com.sc2mod.andromeda.test.junit.unittests.others;

import static org.junit.Assert.fail;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;
import com.sc2mod.andromeda.test.junit.UnitTestRun;
import com.sc2mod.andromeda.test.sc2tester.BankContent;
import com.sc2mod.andromeda.test.sc2tester.BankReader;

public class OtherTests extends UnitTestRun {

	@Test
	public void bankTest(){
		File bank = getPackageFile("test.SC2Bank");
		try{
			BankReader br = new BankReader(bank);
			BankContent result = br.read();
			Assert.assertEquals(result.getValue("misc", "progress"), "1");
			Assert.assertEquals(result.getValue("test_foo", "failMsg"), "blub");
			
		} catch(Throwable t){
			failException(t);
		}
	}


	
}
