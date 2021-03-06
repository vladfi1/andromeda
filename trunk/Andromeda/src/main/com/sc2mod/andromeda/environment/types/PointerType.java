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

import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;

public class PointerType extends UnscopedType {

	private IType pointsTo;
	private String uid;
	public PointerType(IType child, TypeProvider t) {
		super(t);
		pointsTo = child;
		uid = child.getUid() + "*";
	}

	@Override
	public TypeCategory getCategory() {
		return TypeCategory.POINTER;
	}

	@Override
	public String getDescription() {
		return "Pointer Type";
	}

	@Override
	public String getUid() {
		return uid;
	}
	
	@Override
	public String getGeneratedName() {
		return pointsTo.getGeneratedName().concat("*");
	}
	
	public IType pointsTo(){
		return pointsTo;
	}

	@Override
	public IType getWrappedType() {
		return pointsTo;
	}
	
	@Override
	public int getRuntimeType() {
		return RuntimeType.OTHER;
	}
	
	@Override
	public int getByteSize() {
		return 4;
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
