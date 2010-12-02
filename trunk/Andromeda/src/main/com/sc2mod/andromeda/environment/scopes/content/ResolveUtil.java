package com.sc2mod.andromeda.environment.scopes.content;

import java.util.EnumSet;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.access.AccessorAccess;
import com.sc2mod.andromeda.environment.access.Invocation;
import com.sc2mod.andromeda.environment.access.InvocationType;
import com.sc2mod.andromeda.environment.access.NameAccess;
import com.sc2mod.andromeda.environment.access.OperationAccess;
import com.sc2mod.andromeda.environment.access.PackageAccess;
import com.sc2mod.andromeda.environment.access.TypeAccess;
import com.sc2mod.andromeda.environment.access.VarAccess;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.Package;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.UsageType;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.problems.ErrorUtil;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.problems.UnrecoverableProblem;
import com.sc2mod.andromeda.semAnalysis.LocalVarStack;
import com.sc2mod.andromeda.syntaxNodes.DeleteStmtNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

/**
 * Utility class for resolving different types of names.
 * @author gex
 *
 */
public final class ResolveUtil {
	
	private static final EnumSet<ScopedElementType> RESOLVE_SCOPES = EnumSet.of(ScopedElementType.TYPE, ScopedElementType.ERROR);
	private static final EnumSet<ScopedElementType> RESOLVE_ONLY_TYPES = EnumSet.of(ScopedElementType.TYPE,ScopedElementType.ERROR);
	private static final EnumSet<ScopedElementType> RESOLVE_NAMES = EnumSet.allOf(ScopedElementType.class);
	private static final EnumSet<ScopedElementType> RESOLVE_OPS_AND_VARS = EnumSet.of(ScopedElementType.ERROR,ScopedElementType.OP_SET,ScopedElementType.VAR);
	static final EnumSet<ScopedElementType> RESOLVE_OPS = EnumSet.of(ScopedElementType.ERROR,ScopedElementType.OP_SET);
	private static final EnumSet<ScopedElementType> RESOLVE_ONLY_VARS = EnumSet.of(ScopedElementType.VAR, ScopedElementType.ERROR);
	
	
	//Util
	private ResolveUtil(){}
	
	//======= PREFIXED RESOLVING ========
	
	public static VarDecl rawResolveField(IScope scope, String name, SyntaxNode where, boolean staticAccess){
		IScopedElement elem = scope.getContent().resolve(name, scope, UsageType.OTHER, null, where, RESOLVE_ONLY_VARS);
		//TODO: check what happens if elem is null
		checkStaticAccess(staticAccess, elem, where);
		
		return (VarDecl) elem;
	}
	
	public static NameAccess resolvePrefixedName(IScope prefix, String name, IScope from, UsageType accessType, SyntaxNode where, boolean staticAccess, IType setType){
		IScopedElement elem = prefix.getContent().resolve(name,from,accessType,null,where,RESOLVE_NAMES);
		ResolveResult error = ResolveResult.NOT_FOUND; 
		
		//Not found? Try get and set methods, respectively
		NameAccess ac;
		if(elem == null){
			error = rememberError(error, prefix.getContent().getLastResolveResult());
			ac = ResolveGetSetUtil.resolveGetSet(prefix,name,from,accessType,where, setType);
			if(ac == null){
				error = rememberError(error, prefix.getContent().getLastResolveResult().toAccessorResult());
				throw emitNameError(name,error,where);
			}
			elem = ac.getAccessedElement();
		} else {
			ac = createAccess(elem);
		}
		
		//Check for static/non-static misuse
		checkStaticAccess(staticAccess, elem, where);
		
		return ac;
	}
	


	private static UnrecoverableProblem emitNameError(String name, ResolveResult error, SyntaxNode where) {
		switch(error){
		case ACCESSOR_NOT_VISIBLE:
			throw Problem.ofType(ProblemId.ACCESSOR_NOT_VISIBLE).at(where)
				.details(name)
				.raiseUnrecoverable();
		case ACCESSOR_WRONG_SIGNATURE:
			throw Problem.ofType(ProblemId.ACCESSOR_WRONG_SIGNATURE).at(where)
				.details(name)
				.raiseUnrecoverable();
		case NOT_VISIBLE:
			throw Problem.ofType(ProblemId.FIELD_NOT_VISIBLE).at(where)
				.details(name)
				.raiseUnrecoverable();
		}
		throw Problem.ofType(ProblemId.FIELD_NAME_NOT_FOUND).at(where)
			.details(name)
			.raiseUnrecoverable();
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
		Operation op = (Operation) prefix.getContent().resolve(name,from,UsageType.OTHER,sig,where,RESOLVE_OPS);
		if(op == null)
			return null;
		
		//Check for static/non-static misuse
		checkStaticAccess(staticAccess, op, where);
	
		return op;
	}
	
	public static Invocation resolvePrefixedInvocation(IScope prefix, String name, Signature sig, IScope from, SyntaxNode where, boolean allowFuncPointer, boolean staticAccess, boolean disallowVirtualInvocation){
		IScopedElement elem = prefix.getContent().resolve(name,from,UsageType.OTHER,sig,where,allowFuncPointer?RESOLVE_OPS_AND_VARS:RESOLVE_OPS);
		if(elem == null)
			return null;
		
		//Check for static/non-static misuse
		checkStaticAccess(staticAccess, elem, where);
	
		return createInvocation(elem, disallowVirtualInvocation);
	}
	
	public static IType resolvePrefixedType(IScope prefix, String name, IScope from, SyntaxNode where){
		return (IType) prefix.getContent().resolve(name,from,UsageType.OTHER,null,where,RESOLVE_ONLY_TYPES);
	}
	
	public static IScope resolvePrefixedScope(IScope prefix, String name, IScope from, SyntaxNode where){
		IScope result = (IScope) prefix.getContent().resolve(name,from,UsageType.OTHER,null,where,RESOLVE_SCOPES);
		//check packages
		if(result == null){
			if(prefix instanceof Package){
				return ((Package) prefix).resolveSubpackage(name, where);
			}
		}
		return result;
	}
	


	//======= UNPREFIXED RESOLVING ========

	
	static NameAccess resolveUnprefixedName(LocalVarStack localContext, String name, IScope from, UsageType accessType, SyntaxNode where, IType setType, boolean staticContext){
		//First check locals
		IScopedElement result = localContext.resolveVar(name);
		if(result != null)
			return createAccess(result);
		
		NameAccess res = recursiveNameResolve(name,from,accessType,setType,where,RESOLVE_NAMES);
		
		if(res != null)
			checkStaticContext(res.isStatic(),staticContext,where);
		
		return res;
	}
	
	private static void checkStaticContext(boolean elemIsStatic,
			boolean staticContext, SyntaxNode where) {
		if(!elemIsStatic && staticContext){
			Problem.ofType(ProblemId.NON_STATIC_ACCESS_FROM_STATIC_CONTEXT).at(where)
				.raise();
		}
	}

	static Invocation resolveUnprefixedInvocation(LocalVarStack localContext, String name, Signature sig, IScope from, SyntaxNode where, boolean allowFuncPointer, boolean disallowVirtualInvocation, boolean staticContext){
		IScopedElement elem = null;
		//TODO: Nice error messages for invocations, just like for names
		
		//First check locals, if func pointers are allowed (since these could be a local variable)
		if(allowFuncPointer){
			elem = localContext.resolveVar(name);
		}
		
		if(elem == null)
			elem = recursiveResolve(name,from,UsageType.OTHER,sig,where,allowFuncPointer?RESOLVE_OPS_AND_VARS:RESOLVE_OPS);
		
		if(elem != null)
			checkStaticContext(elem.isStaticElement(),staticContext,where);
		
		return createInvocation(elem, disallowVirtualInvocation);
	}
	
	public static IType resolveUnprefixedType(String name, IScope from, SyntaxNode where, boolean staticContext){
		IType result = (IType) recursiveResolve(name,from,UsageType.OTHER,null,where,RESOLVE_ONLY_TYPES);
		
		if(result != null)
			checkStaticContext(result.isStaticElement(), staticContext, where);
		
		return result;
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
		if (staticAccess ^ elem.isStaticElement()){
			Problem p;
			if(elem.isStaticElement())
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
	
	private static IScopedElement recursiveResolve(String name, IScope from, UsageType accessType, Signature sig, SyntaxNode where, EnumSet<ScopedElementType> allowedTypes){
		IScope lookup = from;
		while(lookup != null){
			IScopedElement result = lookup.getContent().resolve(name, from, accessType, sig, where, allowedTypes);
			if(result != null) 
				return result;
			
			//try parent
			lookup = lookup.getParentScope();
		}
		
		//Not found in any scope :(
		return null;
	}
	
	private static NameAccess recursiveNameResolve(String name, IScope from, UsageType accessType, IType setType, SyntaxNode where, EnumSet<ScopedElementType> allowedTypes){
		IScope lookup = from;
		ResolveResult error = ResolveResult.NOT_FOUND;
		while(lookup != null){
			ScopeContentSet content = lookup.getContent();
			IScopedElement result = content.resolve(name, from, accessType, null, where, allowedTypes);
			if(result != null) 
				return createAccess(result);
			error = rememberError(error, content.getLastResolveResult());
			
			//try get and set methods
			AccessorAccess op = ResolveGetSetUtil.resolveGetSet(lookup,name,from,accessType,where,setType);
			if(op != null)
				return op;
			error = rememberError(error, content.getLastResolveResult());
				
			
			//try parent
			lookup = lookup.getParentScope();
		}
		
		//Not found in any scope :(
		throw emitNameError(name, error, where);
	}
	
	public static Invocation createInvocation(IScopedElement elem, boolean disallowVirtual) {
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
			
			if(op.getModifiers().isNative()){
				return new Invocation(op, InvocationType.NATIVE);
			}
			if(op.isStaticElement()){
				return new Invocation(op, InvocationType.STATIC);
			}
			if(!disallowVirtual && op.getOverrideInformation().isOverridden())
				return new Invocation(op,InvocationType.VIRTUAL);
			return new Invocation(op,InvocationType.METHOD);
			
		default: //FIXME: Type Error handling
			throw new Error("!");
		}
		
	
	}
	
	private static NameAccess createAccess(IScopedElement elem) {
		switch(elem.getElementType()){
		case OPERATION:
			return new OperationAccess((Operation) elem);
		case VAR:
			return new VarAccess((Variable) elem);
		case TYPE:
			return new TypeAccess((IType) elem);
		default:
			//TODO error handling
		}
		throw ErrorUtil.defaultInternalError();
	}
	
	private static ResolveResult rememberError(ResolveResult result, ResolveResult newResult){
		if(result == ResolveResult.NOT_FOUND || result == ResolveResult.DISALLOWED_TYPE)
			return newResult;
		
		return result;
	}


}
