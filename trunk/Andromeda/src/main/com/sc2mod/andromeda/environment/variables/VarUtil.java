package com.sc2mod.andromeda.environment.variables;

import com.sc2mod.andromeda.environment.types.IType;

public final class VarUtil {

	//Util
	private VarUtil(){}
	
	
	public static boolean isGlobalField(Variable vd){
		return vd.getVarType() == VarType.GLOBAL;
	}
	
	public static String getNameAndType(Variable vd){
		return new StringBuilder().append(vd.getType().getFullName()).append(" ").append(vd.getUid()).toString();
	}
	
	public static String getNameAndTypeAndPrefix(Variable vd){
		IType c = vd.getContainingType();
		if(c == null){
			return getNameAndType(vd);
		}
		return new StringBuilder().append(vd.getType().getFullName()).append(" ").append(c.getUid()).append(".").append(vd.getUid()).toString();
	}
}
