/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.codetransform;

import java.util.ArrayList;

import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Invocation;
import com.sc2mod.andromeda.environment.Method;
import com.sc2mod.andromeda.environment.types.Class;

public class VirtualCallResolver {

	private Environment env;
	public VirtualCallResolver(Environment env) {
		this.env = env;
	}

	public void tryResolve() {
		ArrayList<Invocation> invocations = env.getVirtualInvocations();
		ArrayList<Invocation> result = new ArrayList<Invocation>(128);
		int size = invocations.size();
		outer: for(int i=0;i<size;i++){			
			
			Invocation inv = invocations.get(i);
			//Unused? Skip!
			if(!inv.isUsed()) continue;
			
			Method meth = (Method) inv.getWhichFunction();			
			//If none of the classes containing any overrider is ever instantiated, we can make it non-virtual
			ArrayList<AbstractFunction> overriders = meth.getOverridingMethods();
			for(AbstractFunction m: overriders){
				Class cl = (Class)m.getContainingType();
				//Found a subclass that is used, so we cannot resolve this call :(
				if(cl.isUsed()){
					//The method is really called virtually! Register this
					meth.registerVirtualCall();
					result.add(inv);
					continue outer;
				}
			}
			
			//No used class was found, we can resolve this call!
			inv.setAccessType(Invocation.TYPE_METHOD);
			
		}
		
		env.setVirtualInvocations(result);
	}

}
