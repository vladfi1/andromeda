package com.sc2mod.andromeda.environment.types;

import java.util.HashMap;

import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.Visibility;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;

public abstract class SystemTypes {

	private TypeProvider typeProvider;
	private HashMap<String, Type> resolvedSystemTypes = new HashMap<String, Type>();
	private HashMap<String, AbstractFunction> resolvedSystemFuncs = new HashMap<String, AbstractFunction>();
	
	private boolean resolved = false;
	
	public SystemTypes(TypeProvider t){
		this.typeProvider = t;
	}
	

	
	/**
	 * This method resolves all necessary types and fields.
	 * It must be overriden to do that.
	 * It should only be called by the type resolver in the appropriate moment.
	 */
	protected abstract void onResolveSystemTypes();
	
	void resolveSystemTypes(){
		onResolveSystemTypes();
		resolved = true;
	}
	
	public Class getSystemClass(String identifier){
		checkResolved();
		return (Class) resolvedSystemTypes.get(identifier);
	}

	public Type getSystemType(String identifier){
		checkResolved();
		return resolvedSystemTypes.get(identifier);
	}

	public AbstractFunction getSystemFunction(String id) {
		checkResolved();
		return resolvedSystemFuncs.get(id);
	}
	
	private void checkResolved() {
		if(!resolved)
			throw new InternalProgramError("Trying to get a system type before system types are resolved");
	}

	
	/**
	 * Method used to get special system classes like Object, Class or System.
	 * The method will raise a compiler error if the class does not exist or is not a class.
	 * @param name
	 * @return
	 */
	protected Class resolveSystemClass(String identifier, String name){
		Type t = resolveSystemType(name, identifier);
		if(!(t instanceof Class)){
			throw Problem.ofType(ProblemId.SYSTEM_CLASS_NOT_DEFINED_AS_CLASS).at(t.getDefinitionIfExists())
						.details(t.getUid())
						.raiseUnrecoverable();
		}
		return (Class)t;
	}
	
	/**
	 * Method used to get internal system types that are not classes (like funcName).
	 * TODO: This method should check if the system type is of the correct category
	 * @param name
	 * @return
	 */
	protected Type resolveSystemType(String identifier, String name){
		Type t = typeProvider.resolveTypeName(name);
		if(t == null) 
			throw Problem.ofType(ProblemId.SYSTEM_TYPE_MISSING)
					.raiseUnrecoverable();
		//Add entry to resolved system types
		resolvedSystemTypes.put(identifier, t);
		return t;
	}
	
	
	protected AbstractFunction resolveSystemMethod(String identifier, Class clas, String name, Signature sig){
		AbstractFunction func = clas.getMethods().getMethod(name,sig);
		if(func == null){
			throw Problem.ofType(ProblemId.SYSTEM_TYPE_MISSING_MEMBER).at(clas.getDeclaration())
				.details(clas.getFullName(),"method", name + "(" + sig.getFullName() + ")")	
				.raiseUnrecoverable();
		}
		resolvedSystemFuncs.put(identifier, func);
		return func;
	}
	
	protected AbstractFunction resolveSystemConstructor(String identifier, Class clas, Signature sig){
		AbstractFunction func = clas.resolveConstructorCall(clas.getDeclaration(), sig, Visibility.PRIVATE);
		if(func == null){
			throw Problem.ofType(ProblemId.SYSTEM_TYPE_MISSING_MEMBER).at(clas.getDeclaration())
				.details(clas.getFullName(),"constructor", clas.getName() + "(" + sig.getFullName() + ")")	
				.raiseUnrecoverable();
		}
		resolvedSystemFuncs.put(identifier, func);
		return func;
	}
	
	


	
}
