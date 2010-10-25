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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.GenericMethodProxy;
import com.sc2mod.andromeda.environment.operations.Method;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeParamMapping;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

@Deprecated
public class MethodSet implements Cloneable {

	private LinkedHashMap<String, LinkedHashMap<Signature, Operation>> methods = new LinkedHashMap<String, LinkedHashMap<Signature, Operation>>();
	private ArrayList<Operation> myMethods = new ArrayList<Operation>();

	private int numUnimplementedMethods;
	private RecordType containingType;
	
	public ArrayList<Operation> getMyMethods() {
		return myMethods;
	}
	
	public MethodSet(RecordType containingType) {
		this.containingType = containingType;
	}

	public boolean containsUnimplementedMethods() {
		return numUnimplementedMethods > 0;
	}

	void addMethod(Operation m, boolean addToList) {
		String uid = m.getUid();
		
		//Check that it is no constructor
		if(uid.equals(containingType.getName()))
			throw Problem.ofType(ProblemId.CONSTRUCTOR_WITH_RETURN_TYPE).at(m.getDefinition())
					.raiseUnrecoverable();
		
		LinkedHashMap<Signature, Operation> meths = methods.get(uid);
		if (meths == null) {
			meths = new LinkedHashMap<Signature, Operation>();
			methods.put(uid, meths);
		}
		addMethod(m, meths,addToList);
	}
	
	public void addMethod(Operation m){
		addMethod(m,true);
	}

	private void addMethod(Operation m, LinkedHashMap<Signature, Operation> meths,boolean addToList) {
		
		//Add to list
		if(addToList)myMethods.add(m);
		
		//Add to hash
		Operation old = meths.put(m.getSignature(), m);
		
		//Abstract?
		if(m.isAbstract()) numUnimplementedMethods++;
			
		// No duplicate method? Everything fine!
		if (old == null){
			//Unless override was specified
			if(addToList&&m.isOverride())
				throw Problem.ofType(ProblemId.OVERRIDE_DECL_DOES_NOT_OVERRIDE).at(m.getDefinition())
						.raiseUnrecoverable();
			return;
		}
		
		// Both functions defined in the same type? Fail!
		if (old.getContainingType() == m.getContainingType())
			throw Problem.ofType(ProblemId.DUPLICATE_METHOD).at(m.getDefinition(),old.getDefinition())
						.raiseUnrecoverable();

		// Different return types? Fail!
		if (old.getReturnType() != m.getReturnType())
			throw Problem.ofType(ProblemId.OVERRIDE_RETURN_TYPE_MISMATCH).at(m.getDefinition(),old.getDefinition())
							.raiseUnrecoverable();

		// Exactly one of them is static? Fail!
		if (old.isStatic() ^ m.isStatic())
			throw Problem.ofType(ProblemId.OVERRIDE_STATIC_NON_STATIC).at(m.getDefinition(),old.getDefinition())
						.raiseUnrecoverable();
		
		// Top method final? Fail!
		if (old.isFinal())
			throw Problem.ofType(ProblemId.OVERRIDE_FINAL_METHOD).at(m.getDefinition(),old.getDefinition())
					.raiseUnrecoverable();
		
		//Visibility reduced?
		if(!old.isStatic()&&Visibility.isLessVisibleThan(m.getVisibility(), old.getVisibility()))
			throw Problem.ofType(ProblemId.OVERRIDE_REDUCED_VISIBILITY).at(m.getDefinition(),old.getDefinition())
					.raiseUnrecoverable();
		
		//Override permitted, add it!
		old.addOverride(m);
		
		//Was the old one without body? Reduce abstract count
		if(old.isAbstract()){
			numUnimplementedMethods--;
		}
	}

	public void addInheritedMethods(MethodSet m) {
		for (String methodName : m.methods.keySet()) {
			HashMap<Signature, Operation> meths = m.methods.get(methodName);

			for (Signature signature : meths.keySet()) {
				Operation meth = meths.get(signature);

				// Private methods are not inherited
				if (meth.getVisibility() == Visibility.PRIVATE)
					continue;

				addMethod(meth,false);

			}

		}
	}
	
	public Operation resolveMethodInvocation(SyntaxNode where, String name, Signature s){
		LinkedHashMap<Signature, Operation> meths = methods.get(name);
		if(meths==null) return null;
		//No signature specified? Only one method with the name allowed
		if(s == null){
			if(meths.size()>1) 
				throw Problem.ofType(ProblemId.AMBIGUOUS_METHOD_ACCESS).at(where)
							.details("method",name,assembleFuncList(meths))
							.raiseUnrecoverable();
			return meths.entrySet().iterator().next().getValue();			
		}
		Operation m = meths.get(s);
		if(m == null){
			Signature candidate = null;
			for(Signature sig: meths.keySet()){
				if(s.canImplicitCastTo(sig)){
					if(candidate != null) 
						throw Problem.ofType(ProblemId.AMBIGUOUS_METHOD_CALL).at(where)
									.details("method",meths.get(candidate).getNameAndSignature(),meths.get(sig).getNameAndSignature())
									.raiseUnrecoverable();
					candidate = sig;					
				}
			}
			return meths.get(candidate);
		}
		return m;
	}
	
	/**
	 * Helper method just for error message generation
	 * @param funcs
	 * @return
	 */
	private String assembleFuncList(
			LinkedHashMap<Signature, Operation> funcs) {
		StringBuilder sb = new StringBuilder(128);
		for(Entry<Signature, Operation> e : funcs.entrySet()){
			sb.append(e.getValue().getNameAndSignature()).append("\n");
		}
		//remove last newline
		sb.setLength(sb.length()-1);
		return sb.toString();
	}

	public void generateMethodIndex() {
		RecordType container = this.containingType;
		for(String s: methods.keySet()){
			int index = 0;
			LinkedHashMap<Signature, Operation> funcs = methods.get(s);
			for(Signature sig: funcs.keySet()){
				Operation meth = funcs.get(sig);

				if(meth.hasBody()&&meth.getContainingType()==container) meth.setIndex(index++);

			}
		}
	}

	public LinkedHashMap<String, LinkedHashMap<Signature, Operation>> getMethodTable() {
		return methods;
	}

	public Operation getMethod(String name, Signature signature){
		LinkedHashMap<Signature, Operation> meths = methods.get(name);
		if(meths == null) return null;
		return meths.get(signature);
	}
	
	public List<Operation> getUnimplementedMethods(){
		ArrayList<Operation> result = new ArrayList<Operation>();
		for(Entry<String, LinkedHashMap<Signature, Operation>> f: methods.entrySet()){
			LinkedHashMap<Signature, Operation> funcs = f.getValue();
			for(Entry<Signature, Operation> f2: funcs.entrySet()){
				Operation m = f2.getValue();
				if(m.getBody() == null) result.add(m);
			}
		}
		return result;
	}


	public MethodSet getAlteredMethodSet(TypeParamMapping paramMap) {
		MethodSet result = null;
		try {
			result = (MethodSet)this.clone();
		} catch (CloneNotSupportedException e) {}
		
		result.methods = new LinkedHashMap<String, LinkedHashMap<Signature, Operation>>();
		for(Entry<String, LinkedHashMap<Signature, Operation>> e: methods.entrySet()){
			
			LinkedHashMap<Signature, Operation> meths;
			result.methods.put(e.getKey(),meths = new LinkedHashMap<Signature, Operation>());
			
			for(Entry<Signature, Operation> e2: e.getValue().entrySet()){
				
				Operation vd = e2.getValue();
				Type t = vd.getReturnType();
				Type t2 = t.replaceTypeParameters(paramMap);
				
				Signature newSignature = e2.getKey().replaceTypeParameters(paramMap);
				
				//XPilot: changed to GenericMethodProxy
				meths.put(newSignature, new GenericMethodProxy((Method)vd, newSignature, t2));
				
				/*
				if(t != t2){
					//Type has changed we need a generic proxy
					meths.put(newSignature, new GenericFunctionProxy(vd,t2));
				} else {
					//Type has not changed, just use the old field
					meths.put(newSignature, vd);
				}
				*/
			}
			
		}
		return result;
	}

}
