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

import java.util.LinkedHashSet;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.access.OperationAccess;
import com.sc2mod.andromeda.environment.synthetic.FuncNameField;
import com.sc2mod.andromeda.environment.variables.SyntheticFieldDecl;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierSE;

public class ClosureType extends UnscopedType{

	private Signature sig;
	private IType returnType;
	private String uid;
	private LinkedHashSet<OperationAccess> usages;
	
	public ClosureType(Signature sig2, IType returnType, TypeProvider t) {
		super(t);
		this.sig = sig2;
		this.returnType = returnType;
		SyntheticFieldDecl fd = new FuncNameField(t, this);
		addContent("name", fd);
	}

	public Signature getSignature(){
		return sig;
	}
	
	public IType getReturnType(){
		return returnType;
	}
	
	@Override
	public TypeCategory getCategory() {
		return TypeCategory.FUNCTION;
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

	public void registerUsage(OperationAccess funcPointerDecl) {
		if(usages == null) usages = new LinkedHashSet<OperationAccess>(8);
		usages.add(funcPointerDecl);
	}
	
	@Override
	public String getGeneratedDefinitionName() {
		return tprov.BASIC.INT.getGeneratedName();
	}
	
	@Override
	public String getGeneratedName() {
		return tprov.BASIC.INT.getGeneratedName();
	}

	public void calcIndices() {
		int index = 0;
		for(OperationAccess fpd : usages){
			//TODO Function pointer usage
			//if(((Variable) fpd.getAccessedElement()).getNumReadAccesses() > 0)
				fpd.setIndex(index++);
		}
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
