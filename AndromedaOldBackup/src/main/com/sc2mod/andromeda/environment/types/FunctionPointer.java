/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;

public class FunctionPointer extends Type{

	private Signature sig;
	private Type returnType;
	private String uid;
	private LinkedHashSet<FuncPointerDecl> usages;
	
	public FunctionPointer(Signature sig2, Type returnType) {
		this.sig = sig2;
		this.returnType = returnType;
	}

	@Override
	public int getCategory() {
		return FUNCTION;
	}
	
	@Override
	public int getByteSize() {
		return 4;
	}

	@Override
	public String getDescription() {
		return "function";
	}

	@Override
	public int getRuntimeType() {
		return RuntimeType.FUNCTION;
	}

	@Override
	public String getUid() {
		if(uid == null) generateUid();
		return uid;
	}

	private void generateUid() {
		StringBuilder sb = new StringBuilder(64);
		sb.append("function<").append(returnType.getFullName()).append("(").append(sig.getFullName()).append(")>");
		uid = sb.toString();
	}

	public void registerUsage(FuncPointerDecl funcPointerDecl) {
		if(usages == null) usages = new LinkedHashSet<FuncPointerDecl>(8);
		usages.add(funcPointerDecl);
	}
	
	@Override
	public String getGeneratedDefinitionName() {
		return BasicType.INT.getGeneratedName();
	}
	
	@Override
	public String getGeneratedName() {
		return BasicType.INT.getGeneratedName();
	}

	public void calcIndices() {
		int index = 0;
		for(FuncPointerDecl fpd : usages){
			if(fpd.getNumReadAccesses() > 0)
				fpd.setIndex(index++);
		}
	}

}
