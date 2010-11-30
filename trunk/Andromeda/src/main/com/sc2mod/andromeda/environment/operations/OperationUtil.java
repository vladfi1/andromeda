package com.sc2mod.andromeda.environment.operations;

import java.util.List;

import com.sc2mod.andromeda.environment.variables.ImplicitParamDecl;

public final class OperationUtil {

	/**
	 * Utility class.
	 */
	private OperationUtil(){}
	
	public static int countParams(Operation op){
		List<ImplicitParamDecl> l = op.getImplicitParams();
		if(l == null) return op.getParams().length;
		return op.getParams().length + l.size();
	}
	
	public static boolean isForwardDeclaration(Operation op){
		return !op.hasBody() && op.isStaticElement() && !op.getModifiers().isNative();
	}
	
	public static boolean usesThis(Operation op){
		switch(op.getOperationType()){
		case METHOD:
		case DESTRUCTOR:
			return true;
		}
		return false;
	}
	
	public static String getNameAndSignature(Operation op){
		return new StringBuilder(64)
			.append(op.getName().toString())
			.append("(")
			.append(op.getSignature().toString())
			.append(")")
			.toString();
	}
	
	public static String getTypeAndNameAndSignature(Operation op){
		if(op.getContainingType() != null){
			return new StringBuilder(64)
			.append(op.getContainingType().getUid())
			.append(".")
			.append(getNameAndSignature(op))
			.toString();
		} else {
			return getNameAndSignature(op);
		}
	}
}
