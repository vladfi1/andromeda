package com.sc2mod.andromeda.environment.scopes.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.StaticInit;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.Package;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.UsageType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.util.ArrayStack;

public abstract class ScopeContentSet implements Iterable<IScopedElement>{
	
	public final IScope scope;
	

	
	
	
	private ResolveResult lastResolveResult;
	private HashMap<String, IScopedElement> contentSet = new LinkedHashMap<String, IScopedElement>();
	private ArrayList<StaticInit> staticInits = null;
	
	protected HashMap<String, IScopedElement> getContentSet(){
		return contentSet;
	}
	
	/**
	 * Factory method to create new operation sets.
	 */
	protected abstract OperationSet createOperationSet(Operation start);
	protected abstract OperationSet createInheritedOperationSet(OperationSet copyFrom);
	
	
	public ScopeContentSet(IScope scope){
		this.scope = scope;
	}
	
	public Set<Entry<String, IScopedElement>> viewEntries(){
		return Collections.unmodifiableSet(contentSet.entrySet());
	}
	
	public Collection<IScopedElement> viewValues(){
		return Collections.unmodifiableCollection(contentSet.values());
	}
	
	
	public Iterator<IScopedElement> getDeepIterator(TraversalPolicies policies){
		return new DeepIterator(policies);
	}
	
	public Iterable<IScopedElement> iterateDeep(final TraversalPolicies policies){
		return new Iterable<IScopedElement>() {
			@Override
			public Iterator<IScopedElement> iterator() {
				return new DeepIterator(policies);
			}
		};
	}
	
	
	
	public ResolveResult getLastResolveResult(){
		return lastResolveResult;
	}
	
	
	IScopedElement resolve(String name, IScope from, UsageType accessType, Signature sig, SyntaxNode where, EnumSet<ScopedElementType> allowedTypes){

		//Get the entry
		IScopedElement result = contentSet.get(name);
		if(result == null){
			lastResolveResult = ResolveResult.NOT_FOUND;
			return null;
		}
		
		//Disallowed type found? Return null
		if(!allowedTypes.contains(result.getElementType())){
			lastResolveResult = ResolveResult.DISALLOWED_TYPE;
			return null;
		}
		
		ScopedElementType type = result.getElementType();
		if(type == ScopedElementType.OP_SET){
			//Op set? Do operation resolving stuff
			OperationSet oset = (OperationSet) result;
			result = oset.get(sig, where);
			
			if(result == null){
				lastResolveResult = ResolveResult.WRONG_SIGNATURE;
				return null;
			}
		}
		
		//Check if it is accessible
		if(!result.getVisibility().checkAccessible(from,result.getScope())){
			lastResolveResult = ResolveResult.NOT_VISIBLE;
			return null;
		}

		lastResolveResult = ResolveResult.SUCCESSFUL;
		return result;
	}
	

	

	
	protected abstract IScopedElement doHandleDuplicate(IScopedElement oldElem, IScopedElement newElem);
	
	/**
	 * An own add method for operations, since these might have more 
	 * than one with one name (overloading)
	 * @param name
	 * @param elem
	 */
	private void addOperation(String name, Operation elem){
		
		IScopedElement o = contentSet.get(name);
		if(o != null){
			//Op set present, add the method to it
			if(o.getElementType() != ScopedElementType.OP_SET){
				throw new InternalProgramError("Trying to override non op set with operation");
			}
			OperationSet os = (OperationSet) o;
			os.add(elem);
		} else {
			//Op set not present, create new one
			contentSet.put(name, createOperationSet(elem));
		}
	}
	

	
	public void add(String name, IScopedElement elem){
		switch(elem.getElementType()){
		case OPERATION:
			addOperation(name, (Operation) elem);
			break;
		case OP_SET:
			throw new InternalProgramError("Cannot add an operation set.");
			//TODO: Error handling
		case PACKAGE:
			throw new InternalProgramError("Cannot add packages to a content set.");
		case ERROR: throw new Error("Error handling!");
		default: 
			addElement(name, elem);
		}
	}
	

	
	private void addElement(String uid, IScopedElement elem){
		
	
	
		IScopedElement old = contentSet.put(uid, elem);
		if(old != null){
			//Handle duplicate elements
			if(doHandleDuplicate(old, elem)!=elem){
				//If we want the old one instead of the new one, put it in.
				
				contentSet.put(uid, old); 
			}
			
		}

	}
	


	public void addStaticInit(StaticInit si) {
		if(staticInits == null){
			staticInits = new ArrayList<StaticInit>();
		}
		staticInits.add(si);
	}
	
	public boolean supportsInheritance(){
		return false;
	}
	
	public boolean isElementInherited(IScopedElement value) {
		return false;
	}
	
	
	@Override
	public Iterator<IScopedElement> iterator() {
		return contentSet.values().iterator();
	}
	
	public static enum TraversalPolicy{
		IGNORE, RECURSE, GET, GET_AND_RECURSE;
	}

	//FIXME test deep iterator
	private class DeepIterator implements Iterator<IScopedElement> {

		private ArrayStack<Iterator<? extends IScopedElement>> iterators = new ArrayStack<Iterator<? extends IScopedElement>>();
		private IScopedElement lookahead;
		private EnumMap<ScopedElementType, TraversalPolicy> traversalPolicies;
		
		public DeepIterator(EnumMap<ScopedElementType, TraversalPolicy> traversalPolicies){
			if(scope instanceof Package && traversalPolicies.get(ScopedElementType.PACKAGE) != TraversalPolicy.IGNORE){
				iterators.push(((Package)scope).subpackageIterator());
			}
			iterators.push(iterator());
			this.traversalPolicies = traversalPolicies;
			lookahead = doLookahead();
		}
		
		@Override
		public boolean hasNext() {
			return lookahead != null;
		}
		
		@Override
		public IScopedElement next() {
			IScopedElement result = lookahead;
			lookahead = doLookahead();
			return result;
		}

		
	
		private boolean hasLookahead() {
			if(iterators.isEmpty()){
				return false;
			} else if(iterators.peek().hasNext()){
				return true;
			} else {
				//topmost iterator has no more elements, pop it and try again
				iterators.pop();
				return hasLookahead();
			}
		}

		private IScopedElement doLookahead(){
			if(!hasLookahead()){
				return null;
			}
			Iterator<? extends IScopedElement> topIterator = iterators.peek();
			
			IScopedElement elem = topIterator.next();
			ScopedElementType elemType = elem.getElementType();
			TraversalPolicy policy = traversalPolicies.get(elemType);
			switch(policy){					
			case IGNORE:
				//Ignore? Then do next one
				return doLookahead();
			case GET:
				//Return it right away
				return elem;
			}
			
			//If we are here, then recurse is requested
			switch(elemType){
			case OP_SET:
				//Go into op by pushing its iterator onto the stack and trying again
				iterators.push(((OperationSet)elem).iterator());
				if(policy == TraversalPolicy.RECURSE){
					return doLookahead();
				} else {
					return elem;
				}
			case PACKAGE:
				//push packages and subpackages
				iterators.push(((Package)elem).subpackageIterator());
				iterators.push(((Package)elem).getContent().iterator());
				if(policy == TraversalPolicy.RECURSE){
					return doLookahead();
				} else {
					return elem;
				}
			case TYPE:
				iterators.push(((IType)elem).getContent().iterator());
				if(policy == TraversalPolicy.RECURSE){
					return doLookahead();
				} else {
					return elem;
				}
			}
			
			return elem;
		}

		@Override
		public void remove() {
			throw new InternalProgramError("Remove not supported!");
		}

	}

}
