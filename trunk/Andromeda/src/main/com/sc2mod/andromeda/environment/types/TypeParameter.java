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

//XPilot: what is this? :)
//import javax.management.relation.RoleUnresolved;

import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.TypeParamNode;

/**
 * A generic type parameter.
 * @author J. 'gex' Finis
 *
 */
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class TypeParameter extends Type {

	private TypeParamNode decl;
	private String name;
	private Class forClass;
	
	public TypeParameter(Class forClass, TypeParamNode elementAt) {
		super(forClass);
		decl = elementAt;
		this.forClass = forClass;
		name = elementAt.getName();
	}

	@Override
	public TypeCategory getCategory() {
		return TypeCategory.TYPE_PARAM;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	@Override
	public String getUid() {
		return name;
	}
	
	@Override
	public String getFullName() {
		return forClass.getUid() + "::" + name;
	}
	

	/**
	 * A type parameter always contains type parameters (itself)
	 */
	@Override
	public boolean containsTypeParams() {
		return true;
	}
	
	@Override
	public Type replaceTypeParameters(TypeParamMapping paramMap) {
		return paramMap.getReplacement(this);
	}
	
	@Override
	public String getGeneratedName() {
		return BasicType.INT.getGeneratedName();
	}
	
	@Override
	public boolean canExplicitCastTo(Type toType) {
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
