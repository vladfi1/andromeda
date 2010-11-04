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
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.ConstructorInvocation;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;

public class TestClassGenerator extends ClassGenerator {

	public TestClassGenerator(Environment env, CodeGenVisitor c,
			INameProvider nameProvider, Configuration o) {
		super(env,c,nameProvider,o);
	}

	@Override
	public void generateClasses(ArrayList<IClass> arrayList) {

	}

	@Override
	public void generateConstructorHead(Constructor m) {

	}

	@Override
	public void generateConstructorInvocation(ConstructorInvocation inv,
			ExprListNode arguments, IClass forClass) {

	}

	@Override
	public void generateFieldAccessPrefix(SimpleBuffer curExprBuffer, IClass c) {

	}

	@Override
	public void generateFieldAccessSuffix(SimpleBuffer curExprBuffer, IClass c) {

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
