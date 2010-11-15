package com.sc2mod.andromeda.semAnalysis;

import java.util.ArrayList;
import java.util.LinkedList;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.operations.Deallocator;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.IRecordType;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.generic.GenericHierachyGenerationVisitor;
import com.sc2mod.andromeda.environment.types.impl.ClassImpl;
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
	private Environment env;
	//private GenericHierachyGenerationVisitor genericsVisitor;
	
	public CopyDownVisitor(Environment env) {
		this.tprov = env.typeProvider;
		this.env = env;
		//this.genericsVisitor = new GenericMemberGenerationVisitor(tprov);
	}
	
	/**
	 * Executes this visitor onto all class hierarchies of
	 * an environment.
	 * @param env
	 */
	public void execute(){
		//Do it just for top classes, all others will be called
		//recursively by the visit method
		for(IClass c : tprov.getClasses()){
			if(c.isTopClass())
				c.accept(this);
		}
		
	}
	
	@Override
	public void visit(ClassImpl class1) {
		copyDownInheritedMembers(class1);
		setDestructor(class1);
		class1.setCopiedDownContent();
		
		//TODO Cleanup
//		//Copy generic members for all generic instances that are already present at this point
//		if(class1.isGenericDecl()){
//			//we copy to array list since this operation might create new generic members (concurrent modification)
//			for(INamedType genericInstance : new ArrayList<INamedType>(tprov.getGenericInstances(class1))){
//				copyGenericMembers(genericInstance);
//			}
//		}
		
		
		//Recursive call for subclasses
		LinkedList<IRecordType> decendants = class1.getDescendants();
		for(IRecordType subclass : decendants){
			subclass.accept(this);
		}
	}
	
//	private void copyGenericMembers(INamedType genericInstance) {
//		genericInstance.accept(genericsVisitor);
//	}

	/**
	 * Sets the destructor for this class appropriately, if it has
	 * no own destructor. If it has one, add an override for
	 * the super destructor.
	 * @param class1
	 */
	private void setDestructor(IClass class1) {
		IClass superClass = class1.getSuperClass();
		Destructor destructor = class1.getDestructor();
		
		if(superClass!=null){
			if(destructor == null)
			{
				class1.setDestructor(superClass.getDestructor());
			} else {
				superClass.getDestructor().getOverrideInformation().addOverride(destructor);
			}
		} else {
			if(destructor == null){
				//If we are in a top class and have no destructor, the deallocator is
				//the destructor
				class1.setDestructor(Deallocator.createDeallocator(env,class1));		
			}
			
		}
	}

	/**
	 * Adds non-private members from superclasses to this class
	 * @param class1
	 */
	private void copyDownInheritedMembers(IClass class1){
		IClass superClass = class1.getSuperClass();
		for(IInterface i : class1.getInterfaces()){
			class1.addInheritedContent(i);
		}
		if(superClass != null){
			System.out.println("Adding from " + superClass.getFullName() + " to " + class1.getFullName());
			class1.addInheritedContent(superClass);
		}
	}
	

}
