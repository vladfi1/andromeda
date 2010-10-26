package com.sc2mod.andromeda.environment.operations;

import java.util.List;

import com.sc2mod.andromeda.environment.variables.ImplicitParamDecl;

public class OperationUtil {

	public static int countParams(Operation op){
		List<ImplicitParamDecl> l = op.getImplicitParams();
		if(l == null) return op.getParams().length;
		return op.getParams().length + l.size();
	}
	
	public static boolean isForwardDeclaration(Operation op){
		return !op.hasBody() && !op.isNative();
	}
}
