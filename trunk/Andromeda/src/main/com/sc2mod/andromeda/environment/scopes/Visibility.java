/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.scopes;

import com.sc2mod.andromeda.environment.types.IType;

/**
 * Visibility modifier for a class, field, or function.
 * @author J. 'gex' Finis
 */
public abstract class Visibility {
	
	public static final Visibility PUBLIC = new Visibility("public",0){
		@Override
		public boolean checkAccessible(IScope from, IScope target) {
			return true;
		}
	};
	public static final Visibility PROTECTED = new Visibility("protected",1){
		@Override
		public boolean checkAccessible(IScope from, IScope target) {
			if(ScopeUtil.isInSubpackageOf(from, target))
				return true;
			if(from instanceof IType){
				//TODO: Insert checks that protected is not used outside of types
				if(((IType)from).isSubtypeOf((IType)target)){
					return true;
				}
			}
			return false;
			
		}
	};
	public static final Visibility INTERNAL = new Visibility("internal",2){
		public boolean checkAccessible(IScope from, IScope target) {
			return ScopeUtil.isInSubpackageOf(from, target);
		}
	};
	public static final Visibility PRIVATE = new Visibility("private",3){
		public boolean checkAccessible(IScope from, IScope target) {
			return ScopeUtil.isChildOf(from,target);
		}
		
		@Override
		public boolean isInherited() {
			return false;
		}
	};
	
	public static final Visibility DEFAULT = PUBLIC;
	
	private final int lvl;
	private final String name;
	protected Visibility(String name,int lvl){
		this.lvl = lvl;
		this.name = name;
	}
	
	public abstract boolean checkAccessible(IScope from, IScope target);
//	public static final int DEFAULT = 0;
//	public static final int PUBLIC = 1;
//	public static final int PROTECTED = 2;
//	public static final int PRIVATE = 3;
	
	/**
	 * Determines the relationship between two visibilities.
	 * @param visibility1 The first visibility.
	 * @param visibility2 The second visibility.
	 * @return 
	 */
	public boolean isLessVisibleThan(Visibility otherVisibility){
		return this.lvl > otherVisibility.lvl;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean isInherited(){
		return true;
	}
	
	
	
}
