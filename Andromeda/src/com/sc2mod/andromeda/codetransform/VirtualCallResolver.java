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
import java.util.HashSet;

import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.Invocation;
import com.sc2mod.andromeda.environment.Method;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.syntaxNodes.MethodDeclaration;

/**
 * Resolves virtual calls.
 * @author geX, XPilot
 */
public class VirtualCallResolver {

	private Environment env;
	private CallHierarchyVisitor chv;
	
	public VirtualCallResolver(Environment env, CallHierarchyVisitor chv) {
		this.env = env;
		this.chv = chv;
	}

	/**
	 * Tries to resolve all virtual method calls as efficiently as possible
	 * (with as few virtual calls as possible).
	 */
	public void tryResolve() {
		while(true) {
			HashSet<Method> more = new HashSet<Method>(8);
			for(Invocation inv : env.getVirtualInvocations()) {
				Method method = (Method)inv.getWhichFunction();
				if(calledVirtually(method)) {
					registerOverriders(method, more);
					if(!method.isCalledVirtually()) {
						method.registerVirtualCall();
					}
				} else {
					inv.setAccessType(Invocation.TYPE_METHOD);
				}
			}

			//no more virtual calls
			if(more.isEmpty()) break;
			
			//loop through methods that are called virtually
			for(Method m : more) {
				if(m.getInvocationCount() == 0) {
					m.addInvocation();
					chv.visit((MethodDeclaration)m.getDefinition());
				}
			}
		}
	}
	
	/**
	 * Determines whether a method needs to be called virtually.
	 * @param method The method.
	 * @return Whether the method needs to be called virtually.
	 */
	private boolean calledVirtually(Method method) {
		for(AbstractFunction f : method.getOverridingMethods()) {
			if(((Class)f.getContainingType()).isUsed()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Recursive helper to {@link #tryResolve()}.
	 * @param method Method whose overriders are to be registered.
	 * @param methods Registered methods.
	 */
	private void registerOverriders(Method method, HashSet<Method> methods) {
		ArrayList<AbstractFunction> overriders = method.getOverridingMethods();
		if(overriders != null) {
			for(AbstractFunction f : overriders) {
				Method m = (Method)f;
				Class cl = (Class)m.getContainingType();
				if(!cl.isUsed()) {
//					System.out.println(cl + " is not used.");
					continue;
				}
				
				if(!m.isCalledVirtually()) {
					m.registerVirtualCall();
					methods.add(m);
				}
				registerOverriders(m, methods);
			}
		}
	}
	
	/* XPilot: old tryResolve()
	public void tryResolve() {
		ArrayList<Invocation> invocations = env.getVirtualInvocations();
		ArrayList<Invocation> result = new ArrayList<Invocation>(128);
		int size = invocations.size();
		outer: for(int i=0;i<size;i++) {
			
			Invocation inv = invocations.get(i);
//			System.out.println(inv.getWhichFunction().getDescription() + ": " + inv.isUsed());
			//Unused? Skip!
			if(!inv.isUsed()) continue;
			
			Method meth = (Method) inv.getWhichFunction();
			
			
			//If none of the classes containing any overrider is ever instantiated, we can make it non-virtual
			ArrayList<AbstractFunction> overriders = meth.getOverridingMethods();
			for(AbstractFunction m: overriders) {
				Class cl = (Class)m.getContainingType();
//				System.out.println(cl.getDescription() + ": " + cl.isUsed());
				//Found a subclass that is used, so we cannot resolve this call :(
				if(cl.isUsed()) {
					//The method is really called virtually! Register this
					//meth.registerVirtualCall();
					HashSet<Method> methods = registerVirtualCall(meth);
					result.add(inv);
					continue outer;
				} else {
//					System.out.println(cl + " is not used.");
				}
			}
			
			//No used class was found, we can resolve this call!
			inv.setAccessType(Invocation.TYPE_METHOD);
			
		}
		
		env.setVirtualInvocations(result);
	}
	*/
	
}
