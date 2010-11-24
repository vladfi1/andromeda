package com.sc2mod.andromeda.environment.scopes.content;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.OperationUtil;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.parsing.CompilerThread;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.util.Debug;

/**
 * An operation set is a special entry in a ScopeContentSet denoting
 * that there are one or many methods/functions with that name but with
 * different signatures (overloading).
 * It implements the ScopedElement interface to be able to be stored in
 * the table of a ScopeContentSet. However, the ScopeContentSet must
 * ensure that no OperationSet ever gets returned by one of its get methods.
 * Instead, an operation of that set or an error in case of ambiguities
 * is returned.
 * 
 * @author gex
 *
 */
public abstract class OperationSet implements IScopedElement, Iterable<Operation>{

	private final String uid;
	protected final IScope scope;
	
	
	public OperationSet(IScope scope, String uid){
		this.uid = uid;
		this.scope = scope;
	}
	
	public OperationSet(IScope scope, Operation op){
		this.uid = op.getUid();
		this.scope = scope;
		add(op);
	}

	public OperationSet(IScope scope, OperationSet set) {
		this.uid = set.getUid();
		this.scope = scope;
		addAllInherited(set);
	}

	//TODO: Use better map here, since most operation sets have only one operation. An implementation where no array is used if only one op exists would be better
	private LinkedHashMap<Signature, Operation> opSet = new LinkedHashMap<Signature, Operation>(2);
	private int numUnimplementedMethods;
	
	/**
	 * Adds the non-private contents of an operation set as
	 * inherited content to this operation set.
	 * @param set
	 */
	public void addAllInherited(OperationSet set) {
		for(Entry<Signature, Operation> e : set.opSet.entrySet()){
			Operation o = e.getValue();
			if(o.getVisibility().isInherited())
				add(o);
		}
	}
	
	public Iterator<Operation> iterator(){
		return opSet.values().iterator();
	}
	
	public Iterable<Entry<Signature,Operation>> viewEntries(){
		return opSet.entrySet();
	}
	
	/**
	 * Returns true, if the method value in this op set is inherited in it.
	 * If the method is not contained in this op set an internal error is thrown.
	 * @param value
	 * @return
	 */
	
	protected boolean isOpInherited(Operation value) {
		return false;
	}

	public void add(Operation m) {
		
			//Callback for each new op
			if(!doHandleNewOp(m)) return;
		
			//Add to hash
			Operation old = opSet.put(m.getSignature(), m);
			
			//Abstract?
			if(!m.hasBody()) numUnimplementedMethods++;
				
			// No duplicate method? Everything fine!
			if (old == null){
				return;
			}
			
			//Exchange to the old, if we want to keep the old one
			if(doHandleDuplicate(old,m) != m){
				opSet.put(m.getSignature(), old);
			}
			
		}

	@Override
	public ScopedElementType getElementType() {
		return ScopedElementType.OP_SET;
	}


	@Override
	public String getUid() {
		return uid;
	}

	/**
	 * Returns an operation in this set. It is not specified which one.
	 * Hence, it is only useful if this set's size == 1
	 * @return
	 */
	public Operation getAny() {
		return opSet.entrySet().iterator().next().getValue();
	}
	
	/**
	 * Helper method just for error message generation
	 * @param funcs
	 * @return
	 */
	private String assembleFuncList() {
		StringBuilder sb = new StringBuilder(128);
		for(Entry<Signature, Operation> e : opSet.entrySet()){
			sb.append(OperationUtil.getNameAndSignature(e.getValue())).append("\n");
		}
		//remove last newline
		sb.setLength(sb.length()-1);
		return sb.toString();
	}

	public Operation get(Signature s, SyntaxNode where) {
		//If no sig is set, it only works if there is only one op with that name.
		if(s == null){
			if(opSet.size()>1) 
				throw Problem.ofType(ProblemId.AMBIGUOUS_METHOD_ACCESS).at(where)
							.details(uid,assembleFuncList())
							.raiseUnrecoverable();
			return getAny();
		}
		
		Operation m = opSet.get(s);
		
		//Direct signature hit, lucky :)
		if(m != null) return m;
		
		
		Signature candidate = null;
		for(Signature sig: opSet.keySet()){
			if(s.canImplicitCastTo(sig)){
				if(candidate != null) 
					throw Problem.ofType(ProblemId.AMBIGUOUS_METHOD_CALL).at(where)
								.details(OperationUtil.getNameAndSignature(opSet.get(candidate)),OperationUtil.getNameAndSignature(opSet.get(sig)))
								.raiseUnrecoverable();
				candidate = sig;					
			}
		}
		return opSet.get(candidate);

	}
	
	/**
	 * Returns a method with a specific signature.
	 * Does visibility checking and returns null if an operation is present but not visible
	 * @return
	 */
	public Operation get(Signature s, SyntaxNode where, IScope from) {
		Operation op = get(s,where);
		if(op == null) return null;
		
		if(!op.getVisibility().checkAccessible(from, op.getScope())) return null;
		return op;
	}
	
	/**
	 * Callback method to handle the situation where two methods of the same
	 * name and signature appear.
	 * 
	 * This method returns the operation which should be kept. The other one is
	 * discarded.
	 */
	protected abstract Operation doHandleDuplicate(Operation oldOp, Operation newOp);
	
	/**
	 * Callback method which is called for every new operation in this set.
	 * If the method returns false, the operation is NOT added to the set.
	 * 
	 * Function sets for example use this method to discard forward declarations.
	 * @param m 
	 * 
	 * @return if the method should be added to this set.
	 */
	protected boolean doHandleNewOp(Operation m){ return true; }
	
	/**
	 * Returns the name of content elements, like "function".
	 * Used for error messages.
	 * @return
	 */
	protected abstract String getContentTypeName();
	
	/*
	 * Superfluous methods:
	 * These methods are implemented from ScopedElement but should never be called!
	 * Since an OpSet may not escape from an ScopeContentSet, only the content set must ensure that these are not called.
	 */
	
	@Override public IScope getScope() { return scope; }
	@Override public Visibility getVisibility() { throw new InternalProgramError("Cannot get the visibility of an op set!"); }
	@Override public boolean isStatic() {	throw new InternalProgramError("Cannot decide if a operation set is static"); }
	@Override public SyntaxNode getDefinition() {	throw new InternalProgramError("Cannot get the definition of an operation set"); }
	@Override public IType getContainingType() { return getAny().getContainingType(); }
	@Override public String getElementTypeName() { return "operation set"; }
	
	public boolean isEmpty() {
		return opSet.isEmpty();
	}

	public int size() {
		return opSet.size();
	}



}
