package com.sc2mod.andromeda.environment.scopes;

import com.sc2mod.andromeda.environment.operations.StaticInit;


public final class ScopeUtil {

	private ScopeUtil(){}

	public static boolean isChildOf(IScope tis, IScope target){
		if(tis == null) return false;
		if(target == tis) return true;
		return isChildOf(tis.getParentScope(),target);
	}
	
	
	
	public static boolean isInSubpackageOf(IScope tis, IScope target){
		return tis.getPackage().isSubpackageOf(target.getPackage());
		
	}
	
	public static void addStaticInit(IScope scope, StaticInit si){
		scope.getContent().addStaticInit(si);
	}
	
	/**
	 * Returns the file scope in which this scope is embedded.
	 * 
	 * If this is called for a scope that is not in a file (like packages and the global scope)
	 * then null is returned.
	 * @param scope Scope for which to get the file scope
	 * @return the file scope in which this scope is embedded or null
	 */
	public static FileScope getFileScopeOfScope(IScope scope){
		if(scope == null) return null;
		if(scope instanceof FileScope) return (FileScope)scope;
		return getFileScopeOfScope(scope.getParentScope());
	}
	
}
