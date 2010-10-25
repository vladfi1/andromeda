package com.sc2mod.andromeda.environment.types;

import java.util.HashMap;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.Scope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.scopes.content.NameResolver;
import com.sc2mod.andromeda.environment.scopes.content.ResolveUtil;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.FieldAccessExprNode;
import com.sc2mod.andromeda.syntaxNodes.QualifiedTypeNode;
import com.sc2mod.andromeda.util.Strings;

public abstract class SystemTypes {

	private TypeProvider typeProvider;
	private Environment env;
	private Scope rootScope;
	private HashMap<String, Type> resolvedSystemTypes = new HashMap<String, Type>();
	private HashMap<String, Operation> resolvedSystemFuncs = new HashMap<String, Operation>();
	
	private boolean resolved = false;
	
	public SystemTypes(Environment env){
		this.env = env;
		this.typeProvider = env.typeProvider;
		this.rootScope = env.getDefaultPackage();
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

	public Operation getSystemFunction(String id) {
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
	protected Class resolveSystemClass(String identifier, String[] qualifiedName){
		Type t = resolveSystemType(identifier, qualifiedName);
		if(!(t instanceof Class)){
			throw Problem.ofType(ProblemId.SYSTEM_CLASS_NOT_DEFINED_AS_CLASS).at(t.getDefinition())
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
	protected Type resolveSystemType(String identifier, String[] qualifiedName){
		Type t = ResolveUtil.resolveQualifiedType(rootScope, qualifiedName);
		if(t == null) 
			throw Problem.ofType(ProblemId.SYSTEM_TYPE_MISSING)
					.details(Strings.mkString(qualifiedName, "."))
					.raiseUnrecoverable();
		//Add entry to resolved system types
		resolvedSystemTypes.put(identifier, t);
		return t;
	}
	
	
	protected Operation resolveSystemMethod(String identifier, Class clas, String name, Signature sig, boolean isStatic){
		Operation func = ResolveUtil.resolvePrefixedOperation(clas, name, sig, clas, null, isStatic);
		if(func == null){
			throw Problem.ofType(ProblemId.SYSTEM_TYPE_MISSING_MEMBER).at(clas.getDefinition())
				.details(clas.getFullName(),"method", name + "(" + sig.getFullName() + ")")	
				.raiseUnrecoverable();
		}
		resolvedSystemFuncs.put(identifier, func);
		return func;
	}
	
	protected Operation resolveSystemConstructor(String identifier, Class clas, Signature sig){
		Operation func = clas.getConstructors().get(sig, clas.getDefinition());
		if(func == null){
			throw Problem.ofType(ProblemId.SYSTEM_TYPE_MISSING_MEMBER).at(clas.getDefinition())
				.details(clas.getFullName(),"constructor", clas.getName() + "(" + sig.getFullName() + ")")	
				.raiseUnrecoverable();
		}
		resolvedSystemFuncs.put(identifier, func);
		return func;
	}
	
	


	
}
