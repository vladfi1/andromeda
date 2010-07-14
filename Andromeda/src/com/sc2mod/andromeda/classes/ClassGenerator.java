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
import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Constructor;
import com.sc2mod.andromeda.environment.ConstructorInvocation;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.Method;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.Visibility;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.SpecialType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.ExpressionList;

/**
 * Generates class code (allocators, constructors, deallocators, class init, class structs,...)
 * @author J. 'gex' Finis
 */
public abstract class ClassGenerator {

	protected Options options;
	protected CodeGenVisitor codeGenVisitor;
	protected INameProvider nameProvider;
	protected int generationCount;
	protected String curClassIdName;
	protected String curThisName;
	protected Class metaClass;

	
	/**
	 * Gets the generated name of the this variable.
	 * @return the name of the this variable
	 */
	public String getThisName() {
		return curThisName;
	}
	
	/**
	 * Generates the this parameter. Should be used at the top of a function.
	 * @param m the function in which the this param is used
	 * @param isConstructor if the function is a constructor
	 */
	public void generateThisParam(AbstractFunction f) {
		int index = f.getLocals().length+f.getParams().length + (f.getFunctionType()==Function.TYPE_CONSTRUCTOR?1:0);
		curThisName = nameProvider.getLocalNameRaw("this", index);
	}

	public String getCidParam() {
		return curClassIdName;
	}

	public ClassGenerator(Environment env, CodeGenVisitor c,INameProvider nameProvider,Options options) {
		this.options = options;
		this.nameProvider = nameProvider;
		this.codeGenVisitor = c;
		c.setClassGenerator(this);
		
		metaClass = env.typeProvider.getClass("Class");
		if(metaClass == null){
			throw new CompilationError("Meta class \"Class\" is missing!");		
		}

	}
	
	public abstract void generateClasses(ArrayList<Class> arrayList);
	

	public abstract void generateFieldAccessPrefix(SimpleBuffer curExprBuffer, Class c);
	
	public abstract void generateFieldAccessSuffix(SimpleBuffer curExprBuffer, Class c);

	
	/**
	 * Creates a constructor head in the function buffer of the code generator.
	 * Note that this should not include explicit constructor invocations, since
	 * these are processed seperately.
	 * @param m
	 */
	public abstract void generateConstructorHead(Constructor m);
	
	/**
	 * Generates a constructor invocation in the curBuffer of the code generator.
	 * @param inv the semantics of the invocation statement
	 * @param arguments the argument list
	 * @param forClass the class for which the constructor was called
	 */
	public abstract void generateConstructorInvocation(ConstructorInvocation inv, ExpressionList arguments,Class forClass);

	public void generateImplicitCidParam(AbstractFunction m, int indexOffset) {		
		//Generate the implicit parameter
		int index = m.getLocals().length+m.getParams().length+indexOffset;
		curClassIdName = nameProvider.getLocalNameRaw("A__class", index);
		
	}


	
	/**
	 * Returns the generated name of the metaclass type.
	 * @return the generated name of the metaclass type.
	 */
	public abstract String getAllocParamType();

	public abstract String getInitFunctionName();


}
