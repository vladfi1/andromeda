package com.sc2mod.andromeda.environment.scopes.content;

import java.util.EnumSet;

import org.apache.tools.ant.IntrospectionHelper.Creator;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Invocation;
import com.sc2mod.andromeda.environment.operations.InvocationType;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.AccessType;
import com.sc2mod.andromeda.environment.scopes.Package;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.semAnalysis.LocalVarStack;
import com.sc2mod.andromeda.syntaxNodes.DeleteStmtNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

/**
 * Utility class for resolving different types of names.
 * @author gex
 *
 */
public final class ResolveUtil {
	
	private static final EnumSet<ScopedElementType> RESOLVE_SCOPES = EnumSet.of(ScopedElementType.TYPE, ScopedElementType.PACKAGE,ScopedElementType.ERROR);
	private static final EnumSet<ScopedElementType> RESOLVE_ONLY_TYPES = EnumSet.of(ScopedElementType.TYPE,ScopedElementType.ERROR);
	private static final EnumSet<ScopedElementType> RESOLVE_NAMES = EnumSet.allOf(ScopedElementType.class);
	private static final EnumSet<ScopedElementType> RESOLVE_OPS_AND_VARS = EnumSet.of(ScopedElementType.ERROR,ScopedElementType.OP_SET,ScopedElementType.VAR);
	private static final EnumSet<ScopedElementType> RESOLVE_OPS = EnumSet.of(ScopedElementType.ERROR,ScopedElementType.OP_SET);
	private static final EnumSet<ScopedElementType> RESOLVE_ONLY_PACKAGES = EnumSet.of(ScopedElementType.PACKAGE,ScopedElementType.ERROR);
	private static final EnumSet<ScopedElementType> RESOLVE_ONLY_VARS = EnumSet.of(ScopedElementType.VAR, ScopedElementType.ERROR);
	
	
	//Util
	private ResolveUtil(){}
	
	//======= PREFIXED RESOLVING ========
	
	public static VarDecl rawResolveField(IScope scope, String name, SyntaxNode where, boolean staticAccess){
		IScopedElement elem = scope.getContent().resolve(name, scope, AccessType.OTHER, null, where, RESOLVE_ONLY_VARS);
	
		checkStaticAccess(staticAccess, elem, where);
		
		return (VarDecl) elem;
	}
	
	public static IScopedElement resolvePrefixedName(IScope prefix, String name, IScope from, AccessType accessType, SyntaxNode where, boolean staticAccess){
		IScopedElement elem = prefix.getContent().resolve(name,from,accessType,null,where,RESOLVE_NAMES);
		
		//Check for static/non-static misuse
		checkStaticAccess(staticAccess, elem, where);
		
		return elem;
		
	}
	
	/**
	 * Does not resolve function pointers! The operation must be a real function or method.
	 * @param prefix
	 * @param name
	 * @param sig
	 * @param from
	 * @param where
	 * @param allowFuncPointer
	 * @param staticAccess
	 * @return
	 */
	public static Operation resolvePrefixedOperation(IScope prefix, String name, Signature sig, IScope from, SyntaxNode where, boolean staticAccess){
		Operation op = (Operation) prefix.getContent().resolve(name,from,AccessType.RVALUE,sig,where,RESOLVE_OPS);
	
		//Check for static/non-static misuse
		checkStaticAccess(staticAccess, op, where);
	
		return op;
	}
	
	public static Invocation resolvePrefixedInvocation(IScope prefix, String name, Signature sig, IScope from, SyntaxNode where, boolean allowFuncPointer, boolean staticAccess){
		IScopedElement elem = prefix.getContent().resolve(name,from,AccessType.RVALUE,sig,where,allowFuncPointer?RESOLVE_OPS_AND_VARS:RESOLVE_OPS);
	
		//Check for static/non-static misuse
		checkStaticAccess(staticAccess, elem, where);
	
		return createInvocation(elem, sig, false);
	}
	
	public static IType resolvePrefixedType(IScope prefix, String name, IScope from, SyntaxNode where){
		return (IType) prefix.getContent().resolve(name,from,AccessType.OTHER,null,where,RESOLVE_ONLY_TYPES);
	}
	
	public static Package resolvePrefixedPackage(IScope prefix, String name, SyntaxNode where) {
		return (Package) prefix.getContent().resolve(name,null,AccessType.OTHER,null,where,RESOLVE_ONLY_PACKAGES);
	}
	
	public static IScope resolvePrefixedScope(IScope prefix, String name, IScope from, SyntaxNode where){
		return (IScope) prefix.getContent().resolve(name,from,AccessType.OTHER,null,where,RESOLVE_SCOPES);
	}
	


	//======= UNPREFIXED RESOLVING ========

	
	static IScopedElement resolveUnprefixedName(LocalVarStack localContext, String name, IScope from, AccessType accessType, SyntaxNode where){
		//First check locals
		IScopedElement result = localContext.resolveVar(name);
		if(result != null)
			return result;
		
		return recursiveResolve(name,from,accessType,null,where,RESOLVE_NAMES);
	}
	
	static Invocation resolveUnprefixedInvocation(LocalVarStack localContext, String name, Signature sig, IScope from, SyntaxNode where, boolean allowFuncPointer){
		IScopedElement elem = null;
		
		//First check locals, if func pointers are allowed (since these could be a local variable)
		if(allowFuncPointer){
			elem = localContext.resolveVar(name);
		}
		
		if(elem == null)
			elem = recursiveResolve(name,from,AccessType.RVALUE,sig,where,allowFuncPointer?RESOLVE_OPS_AND_VARS:RESOLVE_OPS);
		
		return createInvocation(elem, sig, false);
	}
	
	public static IType resolveUnprefixedType(String name, IScope from, SyntaxNode where){
		return (IType) recursiveResolve(name,from,AccessType.OTHER,null,where,RESOLVE_ONLY_TYPES);
	}
	
	//======= OTHER RESOLVING ========
	
	public static IType resolveQualifiedType(IScope start, String[] qualifiedName){
		IScope parent = start;
		int i=0;
		for(int size=qualifiedName.length-1 ; i<size ; i++ ){
			String name = qualifiedName[i];
			parent = resolveQTInternal(start, parent, name);
			if(parent == null) {
				return null;
			}
		}
		return resolvePrefixedType(parent,qualifiedName[i], start, null);
	}
	
	public static Invocation registerDelete(IClass class1, DeleteStmtNode deleteStatement) {
		Destructor destructor = class1.getDestructor();
		if(destructor.getOverrideInformation().isOverridden())
			return new Invocation(destructor,InvocationType.VIRTUAL);
		return new Invocation(destructor,InvocationType.METHOD);
	}
	
	//======= PRIVATE HELPERS ========
	
	private static void checkStaticAccess(boolean staticAccess, IScopedElement elem, SyntaxNode where){
		if (staticAccess ^ elem.isStatic()){
			Problem p;
			if(elem.isStatic())
				p = Problem.ofType(ProblemId.STATIC_MEMBER_MISUSE);
			else 
				p = Problem.ofType(ProblemId.NONSTATIC_MEMBER_MISUSE);
			throw p.at(where).details(elem.getUid()).
				raiseUnrecoverable();
		}
	}
	
	private static IScope resolveQTInternal(IScope from, IScope parent, String subname){
		return resolvePrefixedScope(parent,subname, from, null);
	}
	
	private static IScopedElement recursiveResolve(String name, IScope from, AccessType accessType, Signature sig, SyntaxNode where, EnumSet<ScopedElementType> allowedTypes){
		IScope lookup = from;
		while(lookup != null){
			IScopedElement result = lookup.getContent().resolve(name, from, accessType, sig, where, allowedTypes);
			if(result != null) return result;
			lookup = lookup.getParentScope();
		}
		
		//Not found in any scope :(
		return null;
	}
	
	private static Invocation createInvocation(IScopedElement elem, Signature sig, boolean disallowVirtual) {
		//No element found? No invocation!
		if(elem == null)
			return null;
		
		//Check that the resolved result is of the correct type (an operation)
		Operation op;
		switch(elem.getElementType()){
		case VAR:
			//A var declaration is still okay, if it is a function pointer with a matching signature
			VarDecl vd = (VarDecl) elem;
			//FIXME: Implement function pointer invocations
			throw new Error("Func pointers not implemneted");
		case OPERATION:
			//This is a normal operation
			op = (Operation)elem;
			
			if(op.isNative()){
				return new Invocation(op, InvocationType.NATIVE);
			}
			if(op.isStatic()){
				return new Invocation(op, InvocationType.STATIC);
			}
			if(!disallowVirtual && op.getOverrideInformation().isOverridden())
				return new Invocation(op,InvocationType.VIRTUAL);
			return new Invocation(op,InvocationType.METHOD);
			
		default: //FIXME: Type Error handling
			throw new Error("!");
		}
		
	
	}
}
