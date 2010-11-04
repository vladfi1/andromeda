/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.scopes.content;


import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Invocation;
import com.sc2mod.andromeda.environment.scopes.AccessType;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.variables.LocalVarDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.semAnalysis.LocalVarStack;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

/**
 * Gets a field access or method invocation and resolves which field or method
 * was called by it. 
 * @author J. 'gex' Finis
 *
 */
public class NameResolver {
	
	private LocalVarStack localVars;

	public NameResolver(LocalVarStack localVars) {
		this.localVars = localVars;
	}
	
	/**
	 * Resolves a name from a specified scope.
	 * It first checks the local variable stack and then searches recursively
	 * in all parent scopes starting from the originating scope.
	 * 
	 * Returns a value of type ScopedElement, since every kind of element could be returned.
	 * 
	 * Returns null if the name cannot be resolved.
	 * 
	 * @param name the name of the operation
	 * @param from the scope from which to resolve the operation
	 * @param accessType how the name is accessed
	 * @param where a syntax node to be mentioned in problems raised by this method
	 * @return the resolved ScopedElement or null
	 */
	public IScopedElement resolveName(String name, IScope from, AccessType accessType, SyntaxNode where){
		return ResolveUtil.resolveUnprefixedName(localVars, name, from, accessType, where);
	}
	
	/**
	 * Resolves an operation with a specified signature from a specified scope.
	 * It first checks the local variable stack and then searches recursively
	 * in all parent scopes starting from the originating scope.
	 * 
	 * Returns a value of type ScopedElement, not Operation.
	 * This is because it does not check if the name is really an operation or something
	 * else. This has to be done afterwards! Note that it could also a VarDecl to a function
	 * pointer could be a valid return value if the signature of the pointer type matches.
	 * 
	 * Returns null if the operation name cannot be resolved.
	 * 
	 * @param name the name of the operation
	 * @param sig the signature of the operation
	 * @param from the scope from which to resolve the operation
	 * @param where a syntax node to be mentioned in problems raised by this method
	 * @return the resolved ScopedElement or null
	 */
	public Invocation resolveInvocation(String name, Signature sig, IScope from, SyntaxNode where, boolean allowFuncPointer){
		return ResolveUtil.resolveUnprefixedInvocation(localVars, name, sig, from, where, allowFuncPointer);
	}
	

	
	public void registerLocalVar(VarDecl decl){
		localVars.addLocalVar(decl);
	}
	
	public void pushLocalBlock(){
		localVars.pushLocalBlock();
	}
	
	public void popLocalBlock(){
		localVars.popLocalBlock();
	}

	public LocalVarDecl[] methodFinished(int numParams) {
		return localVars.methodFinished(numParams);
	}


	

}
