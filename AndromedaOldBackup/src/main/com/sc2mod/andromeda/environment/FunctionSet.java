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
import java.util.Map.Entry;

import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
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
						throw Problem.ofType(ProblemId.DUPLICATE_FUNCTION).at(f.getDefinition(),f2.getDefinition())
									.details(f.getName(),f.getSignature().getFullName())
									.raiseUnrecoverable();
					
				} else 
					throw Problem.ofType(ProblemId.DUPLICATE_OVERRIDE_FUNCTION).at(f.getDefinition(),f2.getDefinition())
					.details(f.getName(),f.getSignature().getFullName())
					.raiseUnrecoverable();
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
			if(funcs.size()>1)
				throw Problem.ofType(ProblemId.AMBIGUOUS_METHOD_ACCESS).at(where)
							.details("function",name,assembleFuncList(funcs))
							.raiseUnrecoverable();
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
				if(error) 
					throw Problem.ofType(ProblemId.AMBIGUOUS_METHOD_CALL).at(where)
						.details("function",candidate.getNameAndSignature(),globalCandidate.getNameAndSignature())
						.raiseUnrecoverable();			
				globalCandidate = candidate;				
				
			}
			return globalCandidate;
		}
		
		//Direct signature hit!
		return chooseFunctionForInvocation(funcList, scope, wantNative);		
	}
	
	/**
	 * Helper method just for error message generation
	 * @param funcs
	 * @return
	 */
	private Object assembleFuncList(
			LinkedHashMap<Signature, LinkedList<Function>> funcs) {
		StringBuilder sb = new StringBuilder(128);
		for(Entry<Signature, LinkedList<Function>> e : funcs.entrySet()){
			sb.append(e.getValue().getFirst().getNameAndSignature()).append("\n");
		}
		//remove last newline
		sb.setLength(sb.length()-1);
		return sb.toString();
	}

	public void generateFunctionIndex(){
		for(String s: functions.keySet()){
			int index = 1;
			LinkedHashMap<Signature, LinkedList<Function>> funcs = functions.get(s);
			for(Signature sig: funcs.keySet()){
				LinkedList<Function> sameFuncs = funcs.get(sig);
				boolean danglingDeclaration = true;
				
				//If we have only one function, we need no function indices.
				//However, we still must go through the loop to check for dangling forward-declarations.
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
					throw Problem.ofType(ProblemId.DANGLING_FORWARD_DECLARATION).at(sameFuncs.getFirst().getDefinition())
							.raiseUnrecoverable();
				}
			}
		}
	}
}
