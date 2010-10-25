/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.operations;

import java.util.ArrayList;


import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class ConstructorInvocation extends Invocation {

	private boolean implicit;
	private ArrayList<Class> wrappedFieldInits;
	private Class classToAlloc;
	
	public ArrayList<Class> getWrappedFieldInits() {
		return wrappedFieldInits;
	}

	public Class getClassToAlloc() {
		return classToAlloc==null?(Class)getWhichFunction().getContainingType():classToAlloc;
	}

	public ConstructorInvocation(Function whichFunction, boolean implicit, ArrayList<Class> wrappedFieldInits){
		super(whichFunction,InvocationType.METHOD);
		this.implicit = implicit;
		this.wrappedFieldInits = wrappedFieldInits;
	}
	
	public ConstructorInvocation(Class classToAlloc, boolean implicit, ArrayList<Class> wrappedFieldInits){
		super(null,InvocationType.METHOD);
		this.implicit = implicit;
		this.classToAlloc = classToAlloc;
		this.wrappedFieldInits = wrappedFieldInits;
	}

	public boolean isImplicit() {
		return implicit;
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
