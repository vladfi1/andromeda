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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.ModifierUtil;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IRecordType;
import com.sc2mod.andromeda.environment.types.RuntimeType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.generic.TypeParameter;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.AnnotationNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;

/**
 * A class or interface.
 * @author J. 'gex' Finis
 */
import com.sc2mod.andromeda.environment.visitors.SemanticsVisitorNode;

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
