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
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Scope;
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.parsing.AndromedaReader;
import com.sc2mod.andromeda.parsing.SourceEnvironment;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclaration;
import com.sc2mod.andromeda.syntaxNodes.TypeList;

public class Interface extends RecordType {

	private InterfaceDeclaration declaration;

	private HashMap<String,Interface> interfaces = new HashMap<String,Interface>();
	
	private int index;
	private int tableIndex;
	private ArrayList<Class> implementingClasses = new ArrayList<Class>();
	
	/**
	 * The index is used to locate the bit for
	 * instanceof with this interface
	 * @return index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * The table index is used to locate the slot in the interface
	 * table.
	 * @return table index
	 */
	public int getTableIndex() {
		return tableIndex;
	}

	public Interface(InterfaceDeclaration declaration, Scope scope) {
		super(declaration,scope);
		super.setAbstract();
		this.declaration = declaration;
	}

	@Override
	public String getDescription() {
		return "Interface, defined at:\n" + SourceEnvironment.getLastEnvironment().getSourceInformation(declaration);
	}
	


	protected void resolveExtends(TypeProvider t) {
		TypeList tl = declaration.getInterfaces();
		int size = tl.size();
		for(int i=0;i<size;i++){
			Type in = t.resolveType(tl.elementAt(i));
			if(in.getCategory()!=INTERFACE){
				throw new CompilationError(declaration.getSuperClass(), "Interfaces may only extend interfaces!");
			}
			Type old = interfaces.put(in.getUid(), (Interface) in);
			if(old != null){
				throw new CompilationError(tl.elementAt(i),"Duplicate interface in extends clause!");
			}
		}	
	}
	
	@Override
	public boolean resolveInheritance(TypeProvider t) {
		if(!super.resolveInheritance(t))return false;

		resolveExtends(t);
		return true;
		
	}
	
	@Override
	public int getCategory() {
		return INTERFACE;
	}
	
	@Override
	protected void checkForHierarchyCircle(TypeProvider typeProvider,HashSet<RecordType> marked) {
		if(!hierarchyChecked){
			if(!interfaces.isEmpty()){
				for(Entry<String, Interface> e: interfaces.entrySet()){
					Interface i = e.getValue();
					i.decendants.add(this);
				}
			} else {
				typeProvider.addRootRecord(this);
			}
		}
		doHierarchyCheck(this, marked);
		for(String s:interfaces.keySet()){
			interfaces.get(s).checkForHierarchyCircle(typeProvider,marked);
		}
	}

	@Override
	public void checkHierarchy(TypeProvider typeProvider) {
		if(hierarchyChecked) return;
		checkForHierarchyCircle(typeProvider,new HashSet<RecordType>());
	}
	
	@Override
	void resolveMembers(TypeProvider t) {
		for(Entry<String, Interface> e: interfaces.entrySet()){
			Interface i = e.getValue();
			i.decendants.add(this);
			methods.addInheritedMethods(i.methods);
		}
		super.resolveMembers(t);
	}
	
	@Override
	public void setAbstract() {
		throw new CompilationError(this.declaration.getModifiers(), "Interfaces cannot be 'abstract'.");
	}
	
	@Override
	public boolean canHaveFields() {
		return false;
	}
	
	@Override
	public boolean isImplicitReferenceType() {
		return true;
	}
	
	@Override
	public boolean canHaveMethods() {
		return true;
	}
	
	void generateInterfaceIndex(TypeProvider tp){
		
		index = tp.getNextInterfaceIndex();
		
	}
	
	@Override
	public int getByteSize() {
		throw new Error("Getting byte size of interface impossible!");
	}
}
