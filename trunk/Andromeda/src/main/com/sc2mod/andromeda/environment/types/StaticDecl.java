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

import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

/**
 * No longer used. Was used by the old name resolver to denote a static prefix.
 * @author gex
 *
 */
@Deprecated
public class StaticDecl extends SpecialType {

	private Type wrappedType;
	public StaticDecl(Type wrappedType) {
		super("type");
		this.wrappedType = wrappedType;
		
	}
	
	@Override
	public Type getWrappedType() {
		return wrappedType;
	}
	
	@Override
	public String getUid() {
		return "Type<" + wrappedType.getUid() + ">";
	}
	
	@Override
	public String getDescription() {
		return "Type Keyword";
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
