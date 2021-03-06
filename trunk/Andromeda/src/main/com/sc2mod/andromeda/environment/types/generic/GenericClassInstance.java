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

import java.util.ArrayList;
import java.util.HashSet;

import com.sc2mod.andromeda.classes.ClassNameProvider;
import com.sc2mod.andromeda.classes.VirtualCallTable;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.content.MethodSet;
import com.sc2mod.andromeda.environment.scopes.content.OperationSet;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.impl.ClassImpl;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;

public class GenericClassInstance extends GenericTypeInstance implements IClass {

	private IClass genericParent;
	private OperationSet constructors;
	private boolean constructorsCopiedDown;
	
	//TODO: Super interfaces generic
	
	
	
	
	public GenericClassInstance(ClassImpl class1, Signature s, TypeProvider t) {
		super(class1, s, t);
		this.genericParent = class1;
		constructors = new MethodSet(this, "<init>");
		
	}
	

	@Override
	public IClass getGenericParent() {
		return genericParent;
	}
	
	@Override
	public int calcByteSize() {
		return genericParent.calcByteSize();
	}

	@Override
	public ClassDeclNode getDefinition() {
		return genericParent.getDefinition();
	}
	
	@Override
	public OperationSet getConstructors() {
		if(!constructorsCopiedDown){
			constructorsCopiedDown = true;
			Signature typeArgs = this.getTypeArguments();
			for(Operation op : this.getGenericParent().getConstructors()){
				constructors.add(GenericUtil.getGenericOperation(tprov, op, typeArgs));
			}
		}
		return constructors;
	}

	@Override
	public HashSet<IInterface> getInterfaces() {
		return genericParent.getInterfaces();
	}


	@Override
	public void addConstructor(Constructor c) {
		throw new Error("Not allowed!");
	}


	@Override
	public void generateImplementsTransClosure() {
		throw new Error("Not allowed!");
	}


	@Override
	public Destructor getDestructor() {
		return genericParent.getDestructor();
	}


	@Override
	public ArrayList<Variable> getHierarchyFields() {
		return genericParent.getHierarchyFields();
	}


	@Override
	public int getInstanceLimit() {
		return genericParent.getInstanceLimit();
	}


	@Override
	public String getMetaClassName() {
		return genericParent.getMetaClassName();
	}


	@Override
	public ClassNameProvider getNameProvider() {
		return genericParent.getNameProvider();
	}


	@Override
	public IClass getSuperClass() {
		return genericParent.getSuperClass();
	}


	@Override
	public IClass getTopClass() {
		return genericParent.getTopClass();
	}


	@Override
	public VirtualCallTable getVirtualCallTable() {
		return genericParent.getVirtualCallTable();
	}


	@Override
	public boolean hasConstructors() {
		return genericParent.hasConstructors();
	}


	@Override
	public boolean isUsed() {
		return genericParent.isUsed();
	}


	@Override
	public void registerIndirectInstantiation() {
		genericParent.registerIndirectInstantiation();
	}


	@Override
	public void registerInstantiation() {
		genericParent.registerInstantiation();
	}


	@Override
	public void setDestructor(Destructor destructor) {
		throw new Error("Not allowed!");
	}


	@Override
	public void setHierarchyFields(ArrayList<Variable> hierarchyFields) {
		throw new Error("Not allowed!");
	}


	@Override
	public void setInstanceLimit(int instanceLimit) {
		throw new Error("Not allowed!");
	}


	@Override
	public void setMetaClassName(String name) {
		throw new Error("Not allowed!");
	}


	@Override
	public void setSuperClass(IClass type) {
		throw new Error("Not allowed!");
	}


	@Override
	public void setVirtualCallTable(VirtualCallTable virtualCallTable) {
		throw new Error("Not allowed!");
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
