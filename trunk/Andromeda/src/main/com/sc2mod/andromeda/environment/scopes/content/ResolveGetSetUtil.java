package com.sc2mod.andromeda.environment.scopes.content;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.access.AccessorAccess;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.OperationUtil;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.UsageType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.notifications.ErrorUtil;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.notifications.UnrecoverableProblem;
import com.sc2mod.andromeda.semAnalysis.AccessorTransformer;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public final class ResolveGetSetUtil {

	private ResolveGetSetUtil(){}
	
	static AccessorAccess resolveGetSet(IScope prefix, String name, IScope from, UsageType usageType, SyntaxNode where, IType setType) {
		switch(usageType){
		case LRVALUE:
			Operation get = resolveGet(prefix,name,from,where);
			ResolveResult getResult = prefix.getContent().getLastResolveResult();
			//we use the type of the getter as signature type for the setter
			if(get != null){
				setType = get.getReturnType();
			}
			Operation set = resolveSet(prefix,name,from,where,setType);
			ResolveResult setResult = prefix.getContent().getLastResolveResult();
			
			if(get == null && set == null){
				return null;
			}
			
			//Only get or set present? Error
			if(get == null || set == null){
				
				throw createGetSetMissingError(get, getResult, set, setResult, where);
			
			}
			
			//One of them is static
			if(get.isStatic() ^ set.isStatic()){
				throw Problem.ofType(ProblemId.ACCESSOR_STATIC_NON_STATIC_GET_SET)
					.at(where)
					.raiseUnrecoverable();
			}
			
			return new AccessorAccess(usageType, where, get, set, setType);
		case RVALUE:
			get = resolveGet(prefix,name,from,where);
			if(get == null)
				return null;
			return new AccessorAccess(usageType, where, get, null, get.getReturnType());
			
		case LVALUE:
			set = resolveSet(prefix,name,from,where,setType);
			if(set == null)
				return null;
			return new AccessorAccess(usageType, where, null, set,setType);
		}
		return null;
	}

	private static UnrecoverableProblem createGetSetMissingError(Operation get, ResolveResult getResult, Operation set, ResolveResult setResult, SyntaxNode where) {
		ProblemId pid;
		String meth, m1, m2;
		ResolveResult errorResult;
		if(get == null){		
			meth = OperationUtil.getTypeAndNameAndSignature(set);
			errorResult = getResult;
			m1 = "set";
			m2 = "get";
		} else {
			meth = OperationUtil.getTypeAndNameAndSignature(get);
			errorResult = setResult;
			m1 = "set";
			m2 = "get";
		}
		
		switch(errorResult){
		case DISALLOWED_TYPE:
		case NOT_FOUND:
			pid = ProblemId.ACCESSOR_GET_OR_SET_MISSING;
			break;
		case NOT_VISIBLE:
			pid = ProblemId.ACCESSOR_GET_OR_SET_NOT_VISIBLE;
			break;
		case WRONG_SIGNATURE:
			pid = ProblemId.ACCESSOR_GET_OR_SET_WRONG_SIGNATURE;
			break;
		default: throw ErrorUtil.defaultInternalError();
		}
		
		return Problem.ofType(pid).at(where)
			.details(m1,meth,m2)
			.raiseUnrecoverable();
	}

	private static Operation resolveSet(IScope prefix, String name, IScope from, SyntaxNode where, IType setType) {
		Signature sig = null;
		if(setType != null){
			sig = new Signature(setType);
		}
		return (Operation) prefix.getContent().resolve(AccessorTransformer.createAccessMethodName("set",name), from, UsageType.OTHER, sig, where, ResolveUtil.RESOLVE_OPS);
	}

	private static Operation resolveGet(IScope prefix, String name, IScope from, SyntaxNode where) {
		return (Operation) prefix.getContent().resolve(AccessorTransformer.createAccessMethodName("get",name), from, UsageType.OTHER, Signature.EMPTY_SIGNATURE, where, ResolveUtil.RESOLVE_OPS);
	}
}
