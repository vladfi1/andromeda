package com.sc2mod.andromeda.test.unittests;

import org.junit.Test;

import com.sc2mod.andromeda.test.AndromedaSingleRunTest;

public class BasicTests extends AndromedaSingleRunTest{

	@Test
	public void test1(){
		//Call andromeda without any parameters invokes andromeda onto the file which
		//is located in the same folder and named like this class (with .a extension instead of .java)
		callAndromeda();
		
		assertNoMoreErrors();
		checkOutput();
	}
	
	@Test
	public void test2(){
		//Call andromeda without any parameters invokes andromeda onto the file which
		//is located in the same folder and named like this class (with .a extension instead of .java)
		callAndromeda();
		
		assertNoMoreErrors();
		checkOutput();
	}
}
