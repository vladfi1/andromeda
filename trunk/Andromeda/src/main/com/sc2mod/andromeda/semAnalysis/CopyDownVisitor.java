package com.sc2mod.andromeda.semAnalysis;

import java.util.LinkedList;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.operations.Deallocator;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.Interface;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitorAdapter;

/**
 * This visitor walks through the class hierarchy from top
 * to bottom and copies down the non-private members and performs
 * other actions and checks that relate to copying down members
 * @author gex
 *
 */
public class CopyDownVisitor extends VoidSemanticsVisitorAdapter{

	private TypeProvider tprov;
	
	public CopyDownVisitor(Environment env) {
		this.tprov = env.typeProvider;
	}
	
	/**
	 * Executes this visitor onto all class hierarchies of
	 * an environment.
	 * @param env
	 */
	public void execute(){
		//Do it just for top classes, all others will be called
		//recursively by the visit method
		for(Class c : tprov.getClasses()){
			if(c.isTopClass())
				c.accept(this);
		}
		
	}
	
	@Override
	public void visit(Class class1) {
		copyDownInheritedMembers(class1);
		
		setDestructor(class1);
		
		//Recursive call for subclasses
		LinkedList<RecordType> decendants = class1.getDecendants();
		for(RecordType subclass : decendants){
			subclass.accept(this);
		}
	}
	
	/**
	 * Sets the destructor for this class appropriately, if it has
	 * no own destructor. If it has one, add an override for
	 * the super destructor.
	 * @param class1
	 */
	private void setDestructor(Class class1) {
		Class superClass = class1.getSuperClass();
		Destructor destructor = class1.getDestructor();
		
		if(superClass!=null){
			if(destructor == null)
			{
				class1.setDestructor(superClass.getDestructor());
			} else {
				superClass.getDestructor().addOverride(destructor);
			}
		} else {
			if(destructor == null){
				//If we are in a top class and have no destructor, the deallocator is
				//the destructor
				class1.setDestructor(Deallocator.createDeallocator(tprov,class1));		
			}
			
		}
	}

	/**
	 * Adds non-private members from superclasses to this class
	 * @param class1
	 */
	private void copyDownInheritedMembers(Class class1){
		Class superClass = class1.getSuperClass();
		for(Interface i : class1.getInterfaces()){
			class1.addInheritedContent(i);
		}
		if(superClass != null){
			class1.addInheritedContent(superClass);
		}
	}
	

}
