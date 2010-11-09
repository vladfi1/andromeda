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

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.parsing.CompilerThread;

public class Invocation implements SemanticsElement {


	private Operation whichFunction;
	private InvocationType invocationType;
	private boolean isUsed;
	
	public void use() {
		isUsed = true;
	}
	
	public boolean isUsed() {
		return isUsed;
	}

	public InvocationType getInvocationType() {
		return invocationType;
	}

	public Operation getWhichFunction() {
		return whichFunction;
	}
	
	public Invocation( Operation meth, InvocationType native1) {
		this.whichFunction = meth;
		this.invocationType = native1;
		if(invocationType == InvocationType.VIRTUAL){
			CompilerThread.getEnvironment().getTransientData().getVirtualInvocations().add(this);
		}
	}
	
	public IType getReturnType(){
		return whichFunction.getReturnType();
	}

	public void setInvocationType(InvocationType at) {
		invocationType = at;		
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
