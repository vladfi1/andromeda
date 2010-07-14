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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.parsing.AndromedaReader;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocation;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class FunctionSet {

	private LinkedHashMap<String, LinkedHashMap<Signature, LinkedList<Function>>> functions = new LinkedHashMap<String, LinkedHashMap<Signature, LinkedList<Function>>>();

	public LinkedHashMap<String, LinkedHashMap<Signature, LinkedList<Function>>> getFunctionTable() {
		return functions;
	}

	void addFunction(Function f) {
		String uid = f.getUid();
		LinkedHashMap<Signature, LinkedList<Function>> meths = functions.get(uid);
		if (meths == null) {
			meths = new LinkedHashMap<Signature, LinkedList<Function>>();
			functions.put(uid, meths);
		}
		addFunction(f, meths);
	}

	private void addFunction(Function f, LinkedHashMap<Signature, LinkedList<Function>> meths) {
		LinkedList<Function> list = meths.get(f.getSignature());

				
		// No duplicate method? Everything fine!
		if (list == null){
			list = new LinkedList<Function>();
			meths.put(f.getSignature(), list);
			list.add(f);
			return;
		}
		
		//Function is just a forward declaration and there is already such a function? Skip!
		if(f.isForwardDeclaration()) return;
		
		//Test for duplicate functions
		ListIterator<Function> iter = list.listIterator();
		while(iter.hasNext()){
			Function f2 = iter.next();
			
			//Forward declaration? Remove!
			if(f2.isForwardDeclaration()){
				iter.remove();
				continue;
			}
			
			//Duplicate?
			if(	f2.getScope().equals(f.getScope())||(f.getVisibility()!=Visibility.PRIVATE&&f2.getVisibility()!=Visibility.PRIVATE)){
				
				if(f2.isOverride()||f.isOverride()){
					if(f2.isOverride()&&f.isOverride())
						throw new CompilationError(f.getDefinition(),f2.getDefinition(),
								"Duplicate override definition of function '" + f.getName() + "'.",
								"First defined here");
					
				} else 
					throw new CompilationError(f.getDefinition(),f2.getDefinition(),
								"Duplicate definition of function '" + f.getName() + "'.",
								"First defined here");
			}
		}
		list.add(f);
	
	}
	
	private static Function chooseFunctionForInvocation(LinkedList<Function> functions, Scope scope,boolean wantNative){
		Function candidate = null;
		int candidatePriority = 0;
		int curPriority;
		for(Function f: functions){				
			curPriority = 0;
			
			//Scoped private functions override global ones
			if(f.getScope()==scope){
				curPriority = 2;
				if(wantNative&&f.isNative()) curPriority = 10;
				if(f.isOverride()){
					curPriority = 4;
				}
			}else if(f.getVisibility()!=Visibility.PRIVATE){
				curPriority = 1;
				if(wantNative&&f.isNative()) curPriority = 10;
				if(f.isOverride()){
					curPriority = 3;
				}
			}
			if(curPriority>candidatePriority){
				candidate = f;
				candidatePriority = curPriority;
			}
		}
		if(wantNative&&!candidate.isNative()) return null;
		return candidate;
	}


	public Function resolveInvocation(Scope scope,SyntaxNode where, String name, Signature s, boolean wantNative) {
		LinkedHashMap<Signature, LinkedList<Function>> funcs = functions.get(name);
		if(funcs==null) return null;
		//No signature specified? Get one function, if not ambiguous
		if(s == null){
			if(funcs.size()>1) throw new CompilationError(where,"Ambiguous access! There is more than one function with the name " + name);
			return chooseFunctionForInvocation(funcs.entrySet().iterator().next().getValue(),scope,wantNative);
		}
		LinkedList<Function> funcList = funcs.get(s);
		if(funcList == null){
			//No direct hit :(
			
			Function globalCandidate = null;
			Function candidate = null;
			boolean error = false;
			for(Signature sig: funcs.keySet()){
				if(s.canImplicitCastTo(sig)){
					funcList = funcs.get(sig);
					candidate = chooseFunctionForInvocation(funcList,scope,wantNative);
					if(candidate != null && globalCandidate != null){
						error = true;
					}
					globalCandidate = candidate;

				}
				if(error) throw new CompilationError(where,candidate.getDefinition(),globalCandidate.getDefinition(),
										"This function call is ambiguous.",
										"Functions that could be called:\nFirst",
										"Second");				
				globalCandidate = candidate;				
				
			}
			return globalCandidate;
		}
		
		//Direct signature hit!
		return chooseFunctionForInvocation(funcList, scope, wantNative);		
	}
	
	public void generateFunctionIndex(){
		for(String s: functions.keySet()){
			int index = 1;
			LinkedHashMap<Signature, LinkedList<Function>> funcs = functions.get(s);
			for(Signature sig: funcs.keySet()){
				LinkedList<Function> sameFuncs = funcs.get(sig);
				boolean danglingDeclaration = true;
				
				//If we have only one function, we need no function indices.
				//However, we still mus go through the loop to check for dangling forward-declarations.
				if(sameFuncs.size()==1&&funcs.size()==1){
					index = 0;
				}
				for(Function f: sameFuncs){
					if(f.hasBody()){
						f.setIndex(index++);
						danglingDeclaration = false;
					} else if(f.isNative()){
						danglingDeclaration = false;
					}
				}
				if(danglingDeclaration){
					throw new CompilationError(sameFuncs.getFirst().getDefinition(), "No definition found for forward-declared function '" + sameFuncs.getFirst().getName() + "'.");
				}
			}
		}
	}
}
