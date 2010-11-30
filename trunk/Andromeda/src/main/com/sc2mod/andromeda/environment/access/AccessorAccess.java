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



import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.UsageType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;


public class AccessorAccess extends NameAccess{
	
	private Operation getMethod;
	private Operation setMethod;
	private Operation mainMethod;
	private IType type;
	private UsageType usage;

	

	
	public AccessorAccess(UsageType ut, SyntaxNode where, Operation getMethod, Operation setMethod, IType typ) {
		this(ut,where,getMethod!=null?getMethod:setMethod, typ);
		this.getMethod = getMethod;
		this.setMethod = setMethod;
	}	
	
	private AccessorAccess(UsageType ut, SyntaxNode where, Operation mainMethod, IType typ) {
		where.setSemantics(this);
		this.usage = ut;
		this.mainMethod = mainMethod;
		this.type = typ;
	}
	
	public UsageType getUsageType(){
		return this.usage;
	}
	
	public Operation getGetMethod() {
		return getMethod;
	}

	public Operation getSetMethod() {
		return setMethod;
	}
	
	@Override
	public AccessType getAccessType() {
		return AccessType.ACCESSOR;
	}

	@Override
	public Operation getAccessedElement() {
		return mainMethod;
	}

	@Override
	public IScope getPrefixScope() {
		return type;
	}

	@Override
	public boolean isStatic() {
		return mainMethod.isStaticElement();
	}
	
	public IType getType(){
		return type;
	}



	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
