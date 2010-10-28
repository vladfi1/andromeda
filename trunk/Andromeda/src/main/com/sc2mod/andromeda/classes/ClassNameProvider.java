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

import com.sc2mod.andromeda.environment.operations.Deallocator;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.types.Class;
public abstract class ClassNameProvider {

	protected Class clazz;
	protected String memoryName;
	protected String allocatorName;
	protected String deallocatorName;
	
	public String getDeallocatorName() {
		return deallocatorName;
	}

	public void setDeallocatorName(String deallocatorName) {
		this.deallocatorName = deallocatorName;
		Destructor destructor = clazz.getDestructor();
		if(destructor instanceof Deallocator){
			destructor.setGeneratedName(deallocatorName);
		}
	}

	public String getAllocatorName() {
		return allocatorName;
	}

	public void setAllocatorName(String allocatorName) {
		this.allocatorName = allocatorName;
	}

	public String getMemoryName() {
		return memoryName;
	}

	public void setMemoryName(String memoryName) {
		this.memoryName = memoryName;
	}

	public ClassNameProvider(Class c){
		this.clazz = c;
	}
	
	
	
	public abstract void setName(String name);
	
	public abstract String getName();	
	
	public abstract String getStructName();
}
