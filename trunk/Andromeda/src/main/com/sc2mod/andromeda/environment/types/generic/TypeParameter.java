/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.types.generic;

import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.BasicType;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.RuntimeType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeParamMapping;
import com.sc2mod.andromeda.environment.types.impl.TypeImpl;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.TypeParamNode;

public class TypeParameter extends TypeImpl {

	private TypeParamNode decl;
	private String name;
	private INamedType forType;
	private int index;
	private IType typeBound;
	
	public TypeParameter(INamedType forType, TypeParamNode node, int index, IType typeBound) {
		super(forType);
		//FIXME: Check that the type bound is a valid type bound (i.e. something based off int)
		
		this.index = index;
		this.typeBound = typeBound;
		decl = node;
		this.forType = forType;
		name = node.getName();
	}

	@Override
	public TypeCategory getCategory() {
		return TypeCategory.TYPE_PARAM;
	}

	@Override
	public String getDescription() {
		return "type parameter";
	}

	@Override
	public String getUid() {
		return name;
	}
	
	@Override
	public String getFullName() {
		return forType.getUid() + "::" + name;
	}
	

	/**
	 * A type parameter always contains type parameters (itself)
	 */
	@Override
	public boolean containsTypeParams() {
		return true;
	}
	
	@Override
	public IType replaceTypeParameters(TypeParamMapping paramMap) {
		return paramMap.getReplacement(this);
	}
	
	@Override
	public String getGeneratedName() {
		return BasicType.INT.getGeneratedName();
	}
	
	@Override
	public boolean canExplicitCastTo(IType toType) {
		if(toType==this) return true;
		if(toType.isTypeOrExtension(BasicType.INT)) return true; 
		if(toType.getCategory()==TypeCategory.TYPE_PARAM)return true;
		if(toType.getCategory()==TypeCategory.CLASS) return true;
		return false;
	}

	
	public int getRuntimeType() {
		return RuntimeType.OTHER;
	}
	
	@Override
	public String getDefaultValueStr() {
		return "0";
	}
	
	@Override
	public int getByteSize() {
		//type parameters are always ints (pointers)
		return 4;
	}
	
	/**
	 * Type parameters are always accessible in their class only
	 */
	@Override
	public Visibility getVisibility() {
		return Visibility.PROTECTED;
	}

	@Override
	public TypeParamNode getDefinition() {
		return decl;
	}
	
	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }


}
