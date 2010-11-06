package com.sc2mod.andromeda.environment.types;

import java.util.Iterator;
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.types.generic.TypeParamIdentificationVisitor;
import com.sc2mod.andromeda.environment.types.generic.TypeParameter;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitorAdapter;
import com.sc2mod.andromeda.notifications.InternalProgramError;

public class TypeUtil {

	
	public static boolean hasTypeFieldInits(IType t){
		for(IScopedElement c : t.getContent().viewValues()){
			if(c.getElementType() == ScopedElementType.VAR){
				VarDecl vd = (VarDecl) c;
				if(vd.isStatic() && !vd.isAccessor())
					return true;
			}
		}
		return false;
	}
	
	public static Iterable<Operation> getMethods(IType t, boolean includeInherited){
		final ScopeContentSet content = t.getContent();
		
		//If inheritance is not supported, we auto include inherited fileds (since there are none)
		//This is a speed improvement.
		if(!content.supportsInheritance()) includeInherited = true;
		final boolean includeInherit = includeInherited;
		
		return new Iterable<Operation>() {
			
			@Override
			public Iterator<Operation> iterator() {
				return new FilterIterator<IScopedElement, Operation>(content.getDeepIterator(true,false)) {

					@Override
					protected boolean filter(
							IScopedElement it2) {
						if(it2.getElementType() != ScopedElementType.OPERATION)
							return false;
						
						if(it2.isStatic()) 
							return false;
						
						if(!includeInherit && content.isElementInherited(it2))
							return false;
						
						return true;
					}

					@Override
					protected Operation transform(
							IScopedElement it2) {
						return (Operation)it2;
					}
				};
			}
		};
		
		
	}
	
	
	public static Iterable<VarDecl> getNonStaticTypeFields(IType t, boolean includeInherited){
		final ScopeContentSet content = t.getContent();
		
		//If inheritance is not supported, we auto include inherited fileds (since there are none)
		//This is a speed improvement.
		if(!content.supportsInheritance()) includeInherited = true;
		final boolean includeInherit = includeInherited;
		
		return new Iterable<VarDecl>() {
			
			@Override
			public Iterator<VarDecl> iterator() {
				return new FilterIterator<IScopedElement, VarDecl>(content.viewValues().iterator()) {

					@Override
					protected boolean filter(IScopedElement it2) {
						if(it2.getElementType() != ScopedElementType.VAR) return false;
						
						VarDecl vd = (VarDecl) it2;
						if(vd.isStatic() || vd.isAccessor())
							return false;
						
						if(!includeInherit && content.isElementInherited(vd))
							return false;
						
						return true;
					}

					@Override
					protected VarDecl transform(IScopedElement it2) {
						return (VarDecl) it2;
					}
				};
			}
		};
	}
	
	private abstract static class FilterIterator<From, To> implements Iterator<To>{
		
		protected abstract boolean filter(From it2);
		protected abstract To transform(From it2);
		
		private Iterator<From> it;
		private From nextElemt;
		private boolean elementReady;
		public FilterIterator(Iterator<From> it){
			this.it= it;
		}
		@Override
		public boolean hasNext() {
			if(elementReady) return true;
			if(!it.hasNext()) return false;
			return getNextElement();
		}
		
		private boolean getNextElement() {
			if(elementReady) return true;
			do{
				if(!it.hasNext()) return false;
				From elem = it.next();
				if(filter(elem)){
					nextElemt = elem;
					elementReady = true;
					return true;
				}
			} while(true);
		}
		
		@Override
		public To next() {
			if(!elementReady){
				if(!getNextElement()){
					return null;
				}
			}
			elementReady = false;
			return transform(nextElemt);
		}
		
		@Override
		public void remove() {
			throw new InternalProgramError("Cannot remove from this iterator!");
		}
	}
	
	/**
	 * Returns true if and only if the two handed types are classes and
	 * one is instanceof the other or vice versa.
	 * @param left a class
	 * @param right another class
	 */
	public static boolean isHierarchyShared(IType left, IType right) {
		return left == right || left.isSubtypeOf(right) || right.isSubtypeOf(left);
	}
	
	
	private static TypeParamIdentificationVisitor paramIdentificationVisitor = new TypeParamIdentificationVisitor();
	public static boolean containsTypeParameters(IType t){
		return t.accept(paramIdentificationVisitor,null);
	}

	public static boolean isClass(IType t){
		return t.getCategory() == TypeCategory.CLASS;
	}
		
}
