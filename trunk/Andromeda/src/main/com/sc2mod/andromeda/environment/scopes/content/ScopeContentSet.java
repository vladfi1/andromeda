package com.sc2mod.andromeda.environment.scopes.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import javax.lang.model.element.ElementKind;

import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.StaticInit;
import com.sc2mod.andromeda.environment.scopes.AccessType;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.Package;
import com.sc2mod.andromeda.environment.variables.AccessorDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitorAdapter;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.program.ToDo;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public abstract class ScopeContentSet {
	
	public final IScope scope;
	
	
	
	
	protected HashMap<String, IScopedElement> contentSet = new LinkedHashMap<String, IScopedElement>();
	private ArrayList<StaticInit> staticInits = null;
	
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
	
	
	public Iterator<IScopedElement> getDeepIterator(boolean includeOperations, boolean includeSubpackaes){
		return new DeepIterator(includeOperations,includeSubpackaes);
	}
	
	public Iterable<IScopedElement> iterateDeep(final boolean includeOperations, final boolean includeSubpackaes){
		return new Iterable<IScopedElement>() {
			@Override
			public Iterator<IScopedElement> iterator() {
				return new DeepIterator(includeOperations,includeSubpackaes);
			}
		};
	}
	
	
	
	
	
	IScopedElement resolve(String name, IScope from, AccessType accessType, Signature sig, SyntaxNode where, EnumSet<ScopedElementType> allowedTypes){
		
		//Get the entry
		IScopedElement result = contentSet.get(name);
		if(result == null) return null;
		
		//Disallowed type found? Return null
		if(!allowedTypes.contains(result.getElementType())){
			return null;
		}
		
		ScopedElementType type = result.getElementType();
		if(type == ScopedElementType.OP_SET){
			//Op set? Do operation resolving stuff
			OperationSet oset = (OperationSet) result;
			result = oset.get(sig, where);
		}
		
		//Check if it is accessible
		if(!result.getVisibility().checkAccessible(from,result.getScope())){
			throw Problem.ofType(ProblemId.NOT_VISIBLE).at(where)
					.details(result.getElementTypeName(),name)
					.raiseUnrecoverable();
		}

		//Do readonly checks for accessors
		if(type == ScopedElementType.VAR){
			//If this is an accessor, do read write checks
			if(((VarDecl)result).isAccessor()){
				AccessorDecl ad = (AccessorDecl) result;
				ad.doAccessChecks(from,accessType,where);
			}			
		}
		
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


	public class DeepIterator implements Iterator<IScopedElement> {

		private Iterator<IScopedElement> it;
		private Iterator<? extends IScopedElement> nestedIterator;
		boolean inNestedIteration;
		private boolean doOps;
		private boolean doPackages;
		
		public DeepIterator(boolean includeOperations, boolean includeSubpackaes){
			this.it = contentSet.values().iterator();
			this.doOps = includeOperations;
			this.doPackages = includeSubpackaes;
		}
		
		@Override
		public boolean hasNext() {
			if(inNestedIteration){
				if(nestedIterator.hasNext()){
					return true;
				} else {
					inNestedIteration = false;
				}
			}
			return it.hasNext();
		}

		@Override
		public IScopedElement next() {
			if(inNestedIteration){
				if(nestedIterator.hasNext()){
					return nestedIterator.next();
				} else {
					inNestedIteration = false;
				}
			}
			if(!it.hasNext()){
				return null;
			}
			IScopedElement elem = it.next();
			switch(elem.getElementType()){
			case OP_SET:
				if(doOps){
					inNestedIteration = true;
					nestedIterator = ((OperationSet)elem).iterator();
					return next();
				}
				break;
			case PACKAGE:
				if(doPackages){
					inNestedIteration = true;
					nestedIterator = ((Package)elem).getContent().getDeepIterator(doOps, doPackages);
					return next();
				}
				break;
			}
			
			return elem;
		}

		@Override
		public void remove() {
			throw new InternalProgramError("Remove not supported!");
		}

	}
}
