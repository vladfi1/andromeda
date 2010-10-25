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

import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeParamMapping;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class MethodSet implements Cloneable {

	private LinkedHashMap<String, LinkedHashMap<Signature, AbstractFunction>> methods = new LinkedHashMap<String, LinkedHashMap<Signature, AbstractFunction>>();
	private ArrayList<AbstractFunction> myMethods = new ArrayList<AbstractFunction>();

	private int numUnimplementedMethods;
	private RecordType containingType;
	
	public ArrayList<AbstractFunction> getMyMethods() {
		return myMethods;
	}
	
	public MethodSet(RecordType containingType) {
		this.containingType = containingType;
	}

	public boolean containsUnimplementedMethods() {
		return numUnimplementedMethods > 0;
	}

	void addMethod(AbstractFunction m, boolean addToList) {
		String uid = m.getUid();
		
		//Check that it is no constructor
		if(uid.equals(containingType.getName()))
			throw Problem.ofType(ProblemId.CONSTRUCTOR_WITH_RETURN_TYPE).at(m.getDefinition())
					.raiseUnrecoverable();
		
		LinkedHashMap<Signature, AbstractFunction> meths = methods.get(uid);
		if (meths == null) {
			meths = new LinkedHashMap<Signature, AbstractFunction>();
			methods.put(uid, meths);
		}
		addMethod(m, meths,addToList);
	}
	
	public void addMethod(AbstractFunction m){
		addMethod(m,true);
	}

	private void addMethod(AbstractFunction m, LinkedHashMap<Signature, AbstractFunction> meths,boolean addToList) {
		
		//Add to list
		if(addToList)myMethods.add(m);
		
		//Add to hash
		AbstractFunction old = meths.put(m.getSignature(), m);
		
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
			HashMap<Signature, AbstractFunction> meths = m.methods.get(methodName);

			for (Signature signature : meths.keySet()) {
				AbstractFunction meth = meths.get(signature);

				// Private methods are not inherited
				if (meth.getVisibility() == Visibility.PRIVATE)
					continue;

				addMethod(meth,false);

			}

		}
	}
	
	public AbstractFunction resolveMethodInvocation(SyntaxNode where, String name, Signature s){
		LinkedHashMap<Signature, AbstractFunction> meths = methods.get(name);
		if(meths==null) return null;
		//No signature specified? Only one method with the name allowed
		if(s == null){
			if(meths.size()>1) 
				throw Problem.ofType(ProblemId.AMBIGUOUS_METHOD_ACCESS).at(where)
							.details("method",name,assembleFuncList(meths))
							.raiseUnrecoverable();
			return meths.entrySet().iterator().next().getValue();			
		}
		AbstractFunction m = meths.get(s);
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
			LinkedHashMap<Signature, AbstractFunction> funcs) {
		StringBuilder sb = new StringBuilder(128);
		for(Entry<Signature, AbstractFunction> e : funcs.entrySet()){
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
			LinkedHashMap<Signature, AbstractFunction> funcs = methods.get(s);
			for(Signature sig: funcs.keySet()){
				AbstractFunction meth = funcs.get(sig);

				if(meth.hasBody()&&meth.getContainingType()==container) meth.setIndex(index++);

			}
		}
	}

	public LinkedHashMap<String, LinkedHashMap<Signature, AbstractFunction>> getMethodTable() {
		return methods;
	}

	public AbstractFunction getMethod(String name, Signature signature){
		LinkedHashMap<Signature, AbstractFunction> meths = methods.get(name);
		if(meths == null) return null;
		return meths.get(signature);
	}
	
	public List<AbstractFunction> getUnimplementedMethods(){
		ArrayList<AbstractFunction> result = new ArrayList<AbstractFunction>();
		for(Entry<String, LinkedHashMap<Signature, AbstractFunction>> f: methods.entrySet()){
			LinkedHashMap<Signature, AbstractFunction> funcs = f.getValue();
			for(Entry<Signature, AbstractFunction> f2: funcs.entrySet()){
				AbstractFunction m = f2.getValue();
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
		
		result.methods = new LinkedHashMap<String, LinkedHashMap<Signature, AbstractFunction>>();
		for(Entry<String, LinkedHashMap<Signature, AbstractFunction>> e: methods.entrySet()){
			
			LinkedHashMap<Signature, AbstractFunction> meths;
			result.methods.put(e.getKey(),meths = new LinkedHashMap<Signature, AbstractFunction>());
			
			for(Entry<Signature, AbstractFunction> e2: e.getValue().entrySet()){
				
				AbstractFunction vd = e2.getValue();
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
