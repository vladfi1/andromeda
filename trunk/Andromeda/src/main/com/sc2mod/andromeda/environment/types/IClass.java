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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import com.sc2mod.andromeda.classes.ClassNameProvider;
import com.sc2mod.andromeda.classes.VirtualCallTable;
import com.sc2mod.andromeda.classes.indexSys.IndexClassNameProvider;
import com.sc2mod.andromeda.environment.Annotations;
import com.sc2mod.andromeda.environment.IIdentifiable;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.parsing.CompilationFileManager;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.TypeListNode;
import com.sc2mod.andromeda.syntaxNodes.TypeParamListNode;

import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Deallocator;
import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.scopes.content.MethodSet;
import com.sc2mod.andromeda.environment.scopes.content.OperationSet;
import com.sc2mod.andromeda.environment.types.generic.GenericClassInstance;
import com.sc2mod.andromeda.environment.types.generic.TypeParameter;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

import com.sc2mod.andromeda.environment.visitors.SemanticsVisitorNode;

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

	public int getClassIndex();
	
	public ArrayList<VarDecl> getHierarchyFields();

	public void setHierarchyFields(ArrayList<VarDecl> hierarchyFields);

	public boolean isTopClass();

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
