/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.access;

import java.util.ArrayList;


import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class ConstructorInvocation extends Invocation {

	private boolean implicit;
	private ArrayList<IClass> wrappedFieldInits;
	private IClass classToAlloc;
	
	public ArrayList<IClass> getWrappedFieldInits() {
		return wrappedFieldInits;
	}

	public IClass getClassToAlloc() {
		return classToAlloc==null?(IClass)getWhichFunction().getContainingType():classToAlloc;
	}

	public ConstructorInvocation(Operation whichFunction, boolean implicit, ArrayList<IClass> wrappedFieldInits){
		super(whichFunction,InvocationType.METHOD);
		this.implicit = implicit;
		this.wrappedFieldInits = wrappedFieldInits;
	}
	
	public ConstructorInvocation(IClass classToAlloc, boolean implicit, ArrayList<IClass> wrappedFieldInits){
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
