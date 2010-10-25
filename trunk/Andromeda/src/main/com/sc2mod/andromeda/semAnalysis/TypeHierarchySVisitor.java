package com.sc2mod.andromeda.semAnalysis;

import java.util.HashSet;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.types.Class;
import com.sc2mod.andromeda.environment.types.Interface;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Struct;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitorAdapter;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;

public class TypeHierarchySVisitor extends VoidSemanticsVisitorAdapter{

	private TypeProvider tprov;
	private HashSet<RecordType> alreadyChecked = new HashSet<RecordType>();
	
	public TypeHierarchySVisitor(Environment env) {
		this.tprov = env.typeProvider;
	}
	
	
	public void execute(){
		for(RecordType t: tprov.getRecordTypes()){
			t.accept(this);
		}
	}
	
	
	/**
	 * Checks for hierarchy circles by marking types that were already in the hierarchy.
	 * @param t type to be checked
	 * @param marked the set of types already in that hierarchy
	 */
	private void doCircleCheck(RecordType t,HashSet<RecordType> marked){
		alreadyChecked.add(t);
		if(marked.contains(t)){
			throw Problem.ofType(ProblemId.INHERITANCE_CYCLE).at(t.getDefinition())
						.details(t.getName())
						.raiseUnrecoverable();
		}
		marked.add(t);
	}
	
	//****** Structs *******
	
	/**
	 * Structs currently have no hierarchy, so just add them to the root types.
	 */
	@Override
	public void visit(Struct struct) {
		tprov.registerRootRecord(struct);
	}
	
	//****** Classes *******

	protected void buildClassHierarchy(Class clazz, TypeProvider typeProvider,HashSet<RecordType> marked) {
		Class superClass = clazz.getSuperClass();
		
		//Build hierarchy
		if(!alreadyChecked.contains(clazz)){
			boolean hasParents = false;
			for(Interface i : clazz.getInterfaces()){
				i.getDecendants().add(clazz);
				hasParents = true;
			}
			if(superClass != null){
				if(superClass.isFinal())
					throw Problem.ofType(ProblemId.FINAL_CLASS_EXTENDED).at(clazz.getDefinition().getSuperClass())
								.details(superClass.getName())
								.raiseUnrecoverable();
				if(clazz.isStatic())
					throw Problem.ofType(ProblemId.STATIC_CLASS_HAS_EXTENDS).at(clazz.getDefinition().getSuperClass())
								.raiseUnrecoverable();
				if(superClass.isStatic())
					throw Problem.ofType(ProblemId.STATIC_CLASS_EXTENDED).at(clazz.getDefinition().getSuperClass())
								.raiseUnrecoverable();
				superClass.getDescendants().add(clazz);
				hasParents = true;
			}
			//Register this class as root type
			if(!hasParents) typeProvider.registerRootRecord(clazz);
		}
		
		//Do circle checking
		doCircleCheck(clazz, marked);
		for(Interface i : clazz.getInterfaces()){
			buildInterfaceHierarchy(i,typeProvider,marked);
		}
		if(superClass!=null){
			buildClassHierarchy(superClass,typeProvider,marked);
		}
		
	}

	@Override
	public void visit(Class class1) {
		if(alreadyChecked.contains(class1)) return;
		buildClassHierarchy(class1,tprov,new HashSet<RecordType>());
	}

	
	//****** Interfaces *******
	
	protected void buildInterfaceHierarchy(Interface interfac, TypeProvider typeProvider,HashSet<RecordType> marked) {
		HashSet<Interface> interfaces = interfac.getInterfaces();
		if(!alreadyChecked.contains(interfac)){
			if(!interfaces.isEmpty()){
				for(Interface i: interfaces){
					i.getDecendants().add(interfac);
				}
			} else {
				tprov.registerRootRecord(interfac);
			}
		}
		doCircleCheck(interfac, marked);
		for(Interface i : interfaces){
			buildInterfaceHierarchy(i,typeProvider,marked);
		}
	}

	@Override
	public void visit(Interface interface1) {
		if(alreadyChecked.contains(interface1)) return;
		buildInterfaceHierarchy(interface1,tprov,new HashSet<RecordType>());
	}
}
