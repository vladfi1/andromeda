/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.types.basic;

import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.RuntimeType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.impl.NamedTypeImpl;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class BasicType extends NamedTypeImpl {
	
	
	
	//private static ArrayList<BasicType> basicTypeList = new ArrayList<BasicType>(35);

	@Override
	public int getRuntimeType() {
		return RuntimeType.OTHER;
	}
	



	public BasicType(String name, TypeProvider t) {
		this(null,name, t); //Basic types have no scope
	}
	
	/**
	 * Constructor for types that do have a scope.
	 * @param scope
	 */
	protected BasicType(IScope scope, String name, TypeProvider t){
		super(scope,name, t);
		t.registerBasicType(this);
	}
	
	/**
	 * Constructor for generic instances of a type.
	 * @param genericParent the type for which to create a generic instance.
	 */
	protected BasicType(BasicType genericParent, Signature sig, TypeProvider t){
		super(genericParent,sig,t);
	}
	

	@Override
	public boolean canBeNull() {
		return true;
	}
	

	

	@Override
	public String getDescription() {
		return "native type";
	}

	@Override
	public TypeCategory getCategory() {
		return TypeCategory.BASIC;
	}
	
	@Override
	public int getByteSize() {
		return 4;
	}


	
	

	@Override
	public Visibility getVisibility() {
		return Visibility.PUBLIC;
	}

	@Override
	public SyntaxNode getDefinition() {
		return null;
	}


	@Override
	public INamedType createGenericInstance(Signature s) {
		throw new Error("Cannot create a generic instance of a basic type!");
	}


	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
