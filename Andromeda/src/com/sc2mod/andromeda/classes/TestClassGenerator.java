/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.classes;

import java.util.ArrayList;

import com.sc2mod.andromeda.codegen.CodeGenVisitor;
import com.sc2mod.andromeda.codegen.INameProvider;
import com.sc2mod.andromeda.codegen.buffers.SimpleBuffer;
import com.sc2mod.andromeda.environment.Constructor;
import com.sc2mod.andromeda.environment.ConstructorInvocation;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.syntaxNodes.ExpressionList;

public class TestClassGenerator extends ClassGenerator {

	public TestClassGenerator(Environment env, CodeGenVisitor c,
			INameProvider nameProvider, Options o) {
		super(env,c,nameProvider,o);
	}

	@Override
	public void generateClasses(ArrayList<Class> arrayList) {

	}

	@Override
	public void generateConstructorHead(Constructor m) {

	}

	@Override
	public void generateConstructorInvocation(ConstructorInvocation inv,
			ExpressionList arguments, Class forClass) {

	}

	@Override
	public void generateFieldAccessPrefix(SimpleBuffer curExprBuffer, Class c) {

	}

	@Override
	public void generateFieldAccessSuffix(SimpleBuffer curExprBuffer, Class c) {

	}

	@Override
	public String getAllocParamType() {
		return "abc";
	}
	
	@Override
	public String getThisName() {
		return "this";
	}

	@Override
	public String getInitFunctionName() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

}
