/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.classes;

import java.util.ArrayList;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.OverrideInformation;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IDeclaredType;
import com.sc2mod.andromeda.environment.types.TypeUtil;
import com.sc2mod.andromeda.parsing.TransientCompilationData;

/**
 * A Virtual Call Table (VCT) contains information for calling virtual methods.
 * 
 * @author J. 'gex' Finis
 *
 */
public class VirtualCallTable {
	
	/**
	 * The actual virtual call table.
	 * The information is contained in the methods in the table.
	 */
	public ArrayList<Operation> table = new ArrayList<Operation>();
	
	/**
	 * The VCT of the superclass, if the class to which this VCT
	 * belongs has one.
	 */
	VirtualCallTable superTable;
	
	public IClass clazz;
	
	/**
	 * VCTs of direct descendant classes.
	 */
	public ArrayList<VirtualCallTable> subTables = new ArrayList<VirtualCallTable>(4);

	/**
	 * Generates a VCT for each class in an environment and links it
	 * to the respective class
	 * @param env the environment for whose classes to generate VCTs for
	 */
	public static void generateVCTs(TransientCompilationData transientData, Environment env) {
		for(IClass clazz : env.typeProvider.getClasses()) {
			//Create a class table for all top classes, these will
			//then recursively create VCTs for child classes.
			if(clazz.isTopType()) {
//				System.out.println("Generating VCT for " + clazz.getFullName());
				new VirtualCallTable(clazz, env, transientData);
			}
		}
	}
	

	
	/**
	 * Constructor. Creates the call table and the call tables for child classes
	 * @param clazz the class for which to create the table
	 * @param env the environment
	 * @param transientData 
	 */
	private VirtualCallTable(IClass clazz, Environment env, TransientCompilationData transientData) {
		this.clazz = clazz;
		
//		System.out.println("Generating VCT for " + clazz.getFullName());

		//Set this as table of the class
		clazz.setVirtualCallTable(this);
		
		//Add the table from the superclass
		IClass superClass = clazz.getSuperClass();
		if(superClass != null) {
			//XPilot: make sure we are not using the GenericClassInstance
//			superClass = (Class) superClass.getWrappedType();
//			System.out.println("Super class = " + superClass);
			superTable = superClass.getVirtualCallTable();
			superTable.subTables.add(this);
			table.addAll(superTable.table);
		}
		
		Iterable<Operation> methods = TypeUtil.getMethods(clazz, false);
		
		//First we process all methods
		for(Operation op : methods){
			processMethod(op, transientData);
		}
		
		//Lastly the destructor, if it was defined in this class and not copied down
		Destructor m = clazz.getDestructor();
		if(m != null && m.getContainingType() == clazz) processMethod(m,transientData);
		
		//Generate tables for subclasses
		for(IDeclaredType r : clazz.getDescendants()) {
			new VirtualCallTable((IClass) r, env, transientData);
		}
	}

	private void processMethod(Operation op, TransientCompilationData env){
		OverrideInformation info = op.getOverrideInformation();
		//Methods without body and methods that are not called virtually are ignored
		if(!info.isCalledVirtually()) {
//			System.out.println(m + " virtual = false.");
			return;
		}
		
//		System.out.println(m + " virtual = true.");
		
		Operation overrides = info.getOverridenMethod();
		
//		System.out.println("Overrides: " + overrides);
		
		int callIndex;
		int tableIndex;
		if(overrides != null && overrides.getOverrideInformation().isCalledVirtually()) {
			OverrideInformation overridesInfo = overrides.getOverrideInformation();
			
			//This method is already in the call table!
			tableIndex = overridesInfo.getVirtualTableIndex();
			if(op.getModifiers().isAbstract()) {
				callIndex = overridesInfo.getCurVirtualCallChildIndex();
			} else {
				callIndex = superTable.incCallIndex(tableIndex);
			}
			
			//Set in table
			table.set(tableIndex, op);
		} else {
			//This method is new in the call table!
			tableIndex = table.size();
			if(op.getModifiers().isAbstract()) {					
				callIndex = -1;
			} else {
				callIndex = 0;
			}
			
			//Add to table
			table.add(op);	
			
//			System.out.println("Adding " + m);
			
			env.registerMaxVCTSize(table.size());
		}
		//Set the indices for the method
		info.setVirtualCallIndex(callIndex);
		info.setVirtualTableIndex(tableIndex);
	}
	
	/**
	 * Gets the next call index and increments the
	 * call index for all super methods.
	 * @param tableIndex for which index in the table to increment the call index
	 * @return the incremented call index
	 */
	private int incCallIndex(int tableIndex) {
//		System.out.println("incCallIndex(" + tableIndex + ") on " + clazz);
		int superTableIndex = -1;
		if(superTable != null) {
			superTableIndex = superTable.incCallIndex(tableIndex);
		}
		if(table.size()<=tableIndex) {
//			System.out.println("Returning -1");
			return -1;
		}
		Operation m = table.get(tableIndex);
		
		//If the method is identical to the method in the top table, we do not increment the index
		//(since we have done it already in the top table in the recursive call)
		//XPilot: added check for superTable not containing the tableIndex
		if(superTableIndex >= 0 && superTable.table.get(tableIndex)==m) return m.getOverrideInformation().getCurVirtualCallChildIndex();
		return table.get(tableIndex).getOverrideInformation().getNextVirtualCallChildIndex();
	}

	/**
	 * Retrieves the actual table
	 * @return the vct table
	 */
	public ArrayList<Operation> getTable() {
		return table;
	}
}
