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

import java.util.ArrayList;

import com.sc2mod.andromeda.classes.ClassNameProvider;
import com.sc2mod.andromeda.classes.VirtualCallTable;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.scopes.content.OperationSet;
import com.sc2mod.andromeda.environment.variables.Variable;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;

public interface IClass extends IReferentialType {
	
	
	@Override
	public ClassDeclNode getDefinition();
	
	public OperationSet getConstructors();
	
	public void addConstructor(Constructor c);
	
	public Destructor getDestructor();

	public void setDestructor(Destructor destructor);

	public VirtualCallTable getVirtualCallTable();

	public void setVirtualCallTable(VirtualCallTable virtualCallTable);

	public void setInstanceLimit(int instanceLimit);
	
	public int getInstanceLimit();
	
	public ArrayList<Variable> getHierarchyFields();

	public void setHierarchyFields(ArrayList<Variable> hierarchyFields);

	public IClass getTopClass();
	
	public ClassNameProvider getNameProvider();
	
	public IClass getSuperClass();
	public void setSuperClass(IClass type);
	
	public void registerInstantiation();

	void registerIndirectInstantiation();
	
	/**
	 * After call hierarchy analysis, this method can state if a class
	 * is EVER used (including use by subclassing). If this method returns false then,
	 * the class and all of its subclasses are never instantiated. 
	 * 
	 * Any code for them can be omitted and virtual calls might be resolvable at compile time
	 * @return whether this class is ever used (including subclassing)
	 */
	public boolean isUsed();

	public void setMetaClassName(String name);
	
	public String getMetaClassName();

	/**
	 * Generates the transitive closure for the implements relation which is needed for
	 * interface tables.
	 * A class interface pair is in this relation if either
	 * a) the class implements the interface directly
	 * b) the class extends a class which implements the interface
	 * c) the class implements an interface which extends the interface
	 * d) mixture of b) and c)
	 */
	public void generateImplementsTransClosure(); 
	
	public boolean hasConstructors();
}
