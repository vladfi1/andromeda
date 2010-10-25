package com.sc2mod.andromeda.test.junit.unittests;

import org.junit.Test;

import com.sc2mod.andromeda.test.junit.AndromedaSingleRunTest;

public class BasicTests extends AndromedaSingleRunTest{

	@Test
	public void test1(){
		//Call andromeda without any parameters invokes andromeda onto the file which
		//is located in the same folder and named like this class (with .a extension instead of .java)
		callAndromeda();
		
		assertNoMoreProblems();
		checkOutput();
	}
	
	@Test
	public void test2(){
		//Call andromeda without any parameters invokes andromeda onto the file which
		//is located in the same folder and named like this class (with .a extension instead of .java)
		callAndromeda();
		
		assertNoMoreProblems();
		checkOutput();
	}
}
