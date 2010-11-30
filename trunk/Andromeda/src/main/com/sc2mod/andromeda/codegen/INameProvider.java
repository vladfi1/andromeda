/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.codegen;

import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.types.IStruct;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;

/**
 * A NameProvider assigns names to variables and types for code generation.
 * Of course, it should somehow ensure that these names are valid galaxy names
 * and do not collide with each other.
 * 
 * Note that some methods of this interface may have an internal state
 * to eliminate duplicates.
 * So two calls of the same method with the same parameter might yield
 * different results, so be sure to call these methods only once.
 * 
 * @author J. 'gex' Finis
 *
 */
public interface INameProvider {
	
	public static class Factory{
		public static INameProvider createProvider(CompilationEnvironment env, Configuration o){
			boolean shortenVarNames = o.getParamBool(Parameter.CODEGEN_SHORTEN_VAR_NAMES);
			IndexInformation indexInfo = env.getTransientData().getIndexInformation();
			if(shortenVarNames){
				return new ShortNameProvider(indexInfo);
			} else {
				return new LongNameProvider(indexInfo,env.getSemanticEnvironment());
			}
		}
	}
	
	/**
	 * Retrieves a global name from a variable declaration.
	 * May have internal state.
	 * @param decl Declaration for which to generate the name.
	 * @return the generated name.
	 */
	public String getGlobalName(Variable decl);
	
	/**
	 * Retrieves a function name from a function (also method, constructor, static_init,...)
	 * May have internal state.
	 * @param function Function for which to generate the name.
	 * @return the generated name.
	 */
	public String getFunctionName(Function function);
	
	/**
	 * Retrieves a type name from a type
	 * May have internal state.
	 * @param type Type for which to generate the name.
	 * @return the generated name
	 */
	public String getTypeName(IType type);
	
	/**
	 * Retrieves a field name from a field declaration and its containing type
	 * May have internal state.
	 * @param decl the declaration for which to create the name 
	 * @param containingType the type for which the field was declared
	 * @return
	 */
	public String getFieldName(FieldDecl decl, IType containingType);
	
	/**
	 * Assigns names for parameters and local variables of a function / method
	 * Should assign a name (setGeneratedName) to all local variables and parameters
	 * of the function
	 * @param function the function for which to assign local names
	 */
	void assignLocalNamesForMethod(Function function);
	
	/**
	 * Assigns field names for a struct.
	 * Should assign a name to each field of the struct
	 * @param struct the struct for which to assign the field names
	 */
	void assignFieldNames(IStruct struct);
	
	/**
	 * Generates a global name for a name.
	 * 
	 * This method is only used for generated globals for which
	 * no Variable exists!
	 * 
	 * May have internal state.
	 * @param name the name for which to generate a name.
	 * @return the generated name.
	 */
	public String getGlobalNameRaw(String name);
	
	/**
	 * Generates a global name for a name.
	 * 
	 * This method is only used for generated globals for which
	 * no Variable exists!
	 * 
	 * May have internal state.
	 * 
	 * In contrast to the getGlobalNameRaw method, this method
	 * must not add a prefix to ensure that the resulting name
	 * is unique. So the given input name must already be somehow
	 * unique to avoid collisions.
	 * 
	 * @param name the name for which to generate a name.
	 * @return the generated name.
	 */
	public String getGlobalNameRawNoPrefix(String name);
	
	/**
	 * Generates a local variable name from a desired
	 * name of a variable and its index (position) in the function.
	 * 
	 * The index enumerates all locals and parameters of a function
	 * and is normally generated before.
	 * 
	 * This method is only used for generated parameters for which
	 * no Variable exists!
	 * 
	 * May NOT have internal state, must return the same result
	 * on each invocation with the same parameters.
	 * @param name the desired name
	 * @param index the index
	 * @return the generated name
	 */
	public String getLocalNameRaw(String name, int index);
}
