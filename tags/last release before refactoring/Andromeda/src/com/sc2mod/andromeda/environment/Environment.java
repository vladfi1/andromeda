/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.variables.GlobalVarSet;
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.semAnalysis.NameResolver;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclaration;
import com.sc2mod.andromeda.syntaxNodes.FunctionDeclaration;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclaration;
import com.sc2mod.andromeda.syntaxNodes.InstanceLimitSetter;
import com.sc2mod.andromeda.syntaxNodes.StaticInitDeclaration;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.IntObject;

public final class Environment {

	
	/**
	 * Temporary storage for functions until types are resolved and
	 * functions can be sorted by Signature.
	 */
	private LinkedList<Function> functionsTrans = new LinkedList<Function>();
	

	private GlobalVarSet globals = new GlobalVarSet();
	private FunctionSet functions = new FunctionSet();
	public TypeProvider typeProvider = new TypeProvider();
	private ArrayList<Invocation> virtualInvocations = new ArrayList<Invocation>(128);
	private ArrayList<StaticInit> globalInitializers = new ArrayList<StaticInit>();
	private ArrayList<InstanceLimitSetter> instanceLimits = new ArrayList<InstanceLimitSetter>();
	

	public void registerInstanceLimitSetter(InstanceLimitSetter ils) {
		instanceLimits.add(ils);
	}
	
	private int maxVCTSize;


	public NameResolver nameResolver;
	
	public int getMaxVCTSize() {
		return maxVCTSize;
	}

	public void setVirtualInvocations(ArrayList<Invocation> virtualInvocations) {
		this.virtualInvocations = virtualInvocations;
	}

	public ArrayList<Invocation> getVirtualInvocations() {
		return virtualInvocations;
	}

	public GlobalVarSet getGlobalVariables() {
		return globals;
	}

	public FunctionSet getFunctions() {
		return functions;
	}

	public void registerFunction(FunctionDeclaration functionDeclaration, Scope scope) {
		functionsTrans.add(new Function(functionDeclaration,scope));
	}

	public void registerGlobalVar(GlobalVarDeclaration globalVarDeclaration, Scope scope) {
		globals.add(globalVarDeclaration,scope);
	}
	
	public void registerGlobalInit(StaticInitDeclaration staticInit, Scope scope) {
		globalInitializers.add(new StaticInit(staticInit,scope));
	}
	
	public void resolveClassHierarchy() {
		
		//Register the system class Object
		typeProvider.registerObjectClass();
		
		//Resolve inheritance
		typeProvider.resolveInheritance();
		
		//Check type hierarchy
		typeProvider.checkHierarchy();
		
		//Generate class indices
		typeProvider.generateClassAndInterfaceIndex();
		
		//Resolve enrichments (We can now merge enrichments of one type)
		typeProvider.resolveEnrichments();

	}
	
	public void resolveTypes() {
		

		//Global vars already resolved from const early analysis
		
		//Resolve members
		typeProvider.resolveMemberTypes();				

		typeProvider.resolveEnrichmentMethods();
		
		typeProvider.checkImplicitConstructors();

		
		//Resolve global functions
		for(Function f: functionsTrans) {
			f.resolveTypes(typeProvider,null);
			functions.addFunction(f);
		}
		
		for(StaticInit i: globalInitializers) {
			i.resolveTypes(typeProvider, null);
		}
		
		//We do no longer need the trans list
		functionsTrans.clear();


	}
	

	public void generateFunctionIndex() {
		functions.generateFunctionIndex();
		typeProvider.generateMethodIndex();
	}

	public void registerEnrichment(EnrichDeclaration enrichDeclaration, Scope scope) {
		typeProvider.addEnrichment(enrichDeclaration, scope);
	}
	
	public void addVirtualInvocation(Invocation inv) {
		//System.out.println("Virtual Invocation: " + inv.getWhichFunction());
		virtualInvocations.add(inv);
	}

	public void registerMaxVCTSize(int size) {
		if(maxVCTSize >= size) return;
		maxVCTSize = size;
	}

	public  ArrayList<StaticInit> getGlobalInits() {
		return globalInitializers;
	}

	public void adjustClassInstanceLimit() {
		//HashSet<Type> instanceLimits = new HashSet<Type>();
		for(InstanceLimitSetter ils: this.instanceLimits) {
			Type t = typeProvider.resolveType(ils.getEnrichedType());
			DataObject instanceLimit = ils.getInstanceLimit().getValue();
			if(instanceLimit == null) {
				throw new CompilationError(ils.getInstanceLimit(), "Could not determine the instance limit. Only constant expressions that can be resolved during compilation may be used here.");
			}
			if(!(instanceLimit instanceof IntObject)) {
				throw new CompilationError(ils.getInstanceLimit(), "The instance limit did not resolve to an integer number but to " + ils.getInstanceLimit().getInferedType());
			}
			if(t.getCategory() != Type.CLASS) {
				throw new CompilationError(ils.getEnrichedType(),"The type in a setinstancelimit clause must be a class.");
			}
			Class c = (Class)t;
			if(!c.isTopClass()) {
				throw new CompilationError(ils.getEnrichedType(),"The class in a setinstancelimit clause must be a top class (a class extending no other class).");
			}
			int i = instanceLimit.getIntValue();
			if(i <= 0) {
				throw new CompilationError(ils.getInstanceLimit(),"Only positive numbers are not allowed as instance limit. The specified instance limit was " + i );
			}
			c.setInstanceLimit(i);
		}
	}


}
