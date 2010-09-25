/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.variables.implicit;

import com.sc2mod.andromeda.environment.types.AndromedaSystemTypes;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;
import com.sc2mod.andromeda.environment.variables.ImplicitFieldDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.FuncNameObject;
import com.sc2mod.andromeda.vm.data.FunctionObject;
import com.sc2mod.andromeda.vm.data.StringObject;

public class FuncPointerNameDecl extends ImplicitFieldDecl{

	private Type pointerType;
	private Expression leftSide;
	public FuncPointerNameDecl(Expression leftSide, TypeProvider typeProvider) {
		this.pointerType = leftSide.getInferedType();
		this.leftSide = leftSide;
		type = typeProvider.getSystemType(AndromedaSystemTypes.T_FUNC_NAME);
	}
	
	@Override
	public String getGeneratedName() {
		throw new Error("Cannot yet get .name of a function pointer variable (only function constants are allowed at the moment)");
	}
	
	@Override
	public boolean isConst() {
		return leftSide.getConstant();
	}
	
	@Override
	public DataObject getValue() {
		VarDecl dataobject = (VarDecl) leftSide.getSemantics();
		if(dataobject.getDeclType() == VarDecl.TYPE_FUNCTION_POINTER){
			return new FuncNameObject(((FuncPointerDecl)dataobject).getFunction());
		}
		return null;
	}

}
