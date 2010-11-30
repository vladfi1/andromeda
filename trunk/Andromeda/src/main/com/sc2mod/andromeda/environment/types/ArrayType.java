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

/**
 * An array type.
 * @author J. 'gex' Finis
 */
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.syntaxNodes.ArrayTypeNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.IntObject;

public class ArrayType extends UnscopedType {

	private IType wrappedType;
	private ArrayTypeNode expressionProvider;

	ArrayType(IType wrappedType2, ArrayTypeNode expressionProvider,
			TypeProvider t) {
		super(t);
		this.wrappedType = wrappedType2;
		this.expressionProvider = expressionProvider;
	}

	@Override
	public int getRuntimeType() {
		return RuntimeType.ARRAY;
	}
	
	
	public int getDimension() {
		ExprNode dimExpr = expressionProvider.getDimension();
		DataObject val = dimExpr.getValue();
		if(val != null && val instanceof IntObject){
			return val.getIntValue();
		}
		throw new InternalProgramError("Trying to get the dimension of an array that has not yet its dimension resolved or has dimension of the wrong type");
	}

	@Override
	public TypeCategory getCategory() {
		return TypeCategory.ARRAY;
	}

	@Override
	public String getDescription() {
		return "array type";
	}

	@Override
	public String getUid() {
		return wrappedType.getUid().concat("[]");
	}

	/**
	 * Arrays cannot be passed as parameter or returned
	 */
	@Override
	public boolean isValidAsParameter() {
		return false;
	}
	
	@Override
	public IType getWrappedType() {
		return wrappedType;
	}
	
	@Override
	public String getGeneratedName() {
		StringBuilder nameBuilder = new StringBuilder(32);
		nameBuilder.append(wrappedType.getGeneratedName()).append("[").append(getDimension()).append("]");
		return nameBuilder.toString();
	}
	
	@Override
	public int getByteSize() {
		//XPilot: changed from wrappedType.getByteSize()
		return wrappedType.getMemberByteSize()*getDimension();
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }

	public ArrayTypeNode getExpressionProvider() {
		return expressionProvider; 
	}

}
