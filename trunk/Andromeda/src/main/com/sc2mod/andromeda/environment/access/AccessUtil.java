package com.sc2mod.andromeda.environment.access;

import com.sc2mod.andromeda.environment.variables.VarType;
import com.sc2mod.andromeda.environment.variables.Variable;



public final class AccessUtil {

	private AccessUtil(){}
	
	public static boolean isClassFieldAccess(NameAccess n){
		if(n.getAccessType() == AccessType.VAR){
			Variable var = (Variable)n.getAccessedElement();
			return var.getVarType() == VarType.FIELD;
		}
		return false;
	}
	
	public static Variable getVarIfVarAccess(NameAccess n){
		if(n.getAccessType() == AccessType.VAR){
			return (Variable)n.getAccessedElement();
		}
		return null;
	}
}
