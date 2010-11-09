package com.sc2mod.andromeda.environment.variables;

public final class VarUtil {

	//Util
	private VarUtil(){}
	
	
	public static boolean isGlobalField(Variable vd){
		return vd.getVarType() == VarType.GLOBAL;
	}
}
