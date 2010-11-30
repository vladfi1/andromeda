/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.types.impl;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.IRecordType;
import com.sc2mod.andromeda.environment.types.RuntimeType;
import com.sc2mod.andromeda.environment.visitors.SemanticsVisitorNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;

public abstract class RecordTypeImpl extends DeclaredTypeImpl implements IRecordType , SemanticsVisitorNode {

	
	private int byteSize = -1;
	private boolean copiedDown;
	
	public RecordTypeImpl(GlobalStructureNode g, IScope s, Environment env) {
		super(g,s,env);
	}
	



	
	@Override
	public int getRuntimeType() {
		return RuntimeType.RECORD;
	}
	
	@Override
	public String getDefaultValueStr() {
		return "0";
	}
	
	@Override
	public boolean canHaveFields() {
		return true;
	}
	
	
	@Override
	public int calcByteSize(){
		throw new Error("Cannot calculate record type bytesize");
	}
	
	@Override
	public int getByteSize() {
		if(byteSize==-1){
			byteSize = calcByteSize();
		}
		return byteSize;
	}
	
	@Override
	public boolean hasCopiedDownContent(){
		return copiedDown;
	}
	 
	@Override
	public void setCopiedDownContent(){
		copiedDown = true;
	}

}
