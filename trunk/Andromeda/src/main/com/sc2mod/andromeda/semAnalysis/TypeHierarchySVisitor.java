package com.sc2mod.andromeda.semAnalysis;

import java.util.HashSet;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IDeclaredType;
import com.sc2mod.andromeda.environment.types.IExtension;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.generic.GenericClassInstance;
import com.sc2mod.andromeda.environment.types.generic.GenericExtensionInstance;
import com.sc2mod.andromeda.environment.types.generic.GenericInterfaceInstance;
import com.sc2mod.andromeda.environment.types.impl.ClassImpl;
import com.sc2mod.andromeda.environment.types.impl.ExtensionImpl;
import com.sc2mod.andromeda.environment.types.impl.InterfaceImpl;
import com.sc2mod.andromeda.environment.types.impl.StructImpl;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitorAdapter;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;

public class TypeHierarchySVisitor extends VoidSemanticsVisitorAdapter{

	private TypeProvider tprov;
	private HashSet<IDeclaredType> alreadyChecked = new HashSet<IDeclaredType>();
	
	public TypeHierarchySVisitor(Environment env) {
		this.tprov = env.typeProvider;
	}
	
	
	public void execute(){
		for(IDeclaredType t: tprov.getDeclaredTypes()){
			t.accept(this);
		}
	}
	
	
	/**
	 * Checks for hierarchy circles by marking types that were already in the hierarchy.
	 * @param t type to be checked
	 * @param marked the set of types already in that hierarchy
	 */
	private void doCircleCheck(IDeclaredType t,HashSet<IDeclaredType> marked){
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
	public void visit(StructImpl struct) {
		//tprov.registerRootRecord(struct);
	}
	
	//****** Extensions ******
	
	protected void buildExtensionHierarchy(IExtension extension, TypeProvider typeProvider,HashSet<IDeclaredType> marked) {
		//If called for a generic instance, call for the declaration instead.
		if(extension.isGenericInstance()){
			buildExtensionHierarchy(((GenericExtensionInstance)extension).getGenericParent(), typeProvider, marked);
			return;
		}
		
		IType s = extension.getSuperType();
		IExtension superType = (IExtension) (s == null || (s.getCategory() != TypeCategory.EXTENSION) ? null : s);
		
		//Build hierarchy
		if(!alreadyChecked.contains(extension)){
			if(superType != null){
				//TODO Testcases for extensions with modifiers (static, final, ...)
				if(superType.getModifiers().isFinal())
					throw Problem.ofType(ProblemId.FINAL_TYPE_EXTENDED).at(extension.getDefinition())
								.details(superType.getName())
								.raiseUnrecoverable();
				//only add as descendant if this extension is not distinct
				if(!extension.isDistinct())
					superType.getDescendants().add(extension);
			}
		}
		
		//Do circle checking
		doCircleCheck(extension, marked);
		if(superType!=null){
			buildExtensionHierarchy(superType,typeProvider,marked);
		}
		
	}

	@Override
	public void visit(ExtensionImpl ext) {
		if(alreadyChecked.contains(ext)) return;
		buildExtensionHierarchy(ext,tprov,new HashSet<IDeclaredType>());
	}
	
	//****** Classes *******

	protected void buildClassHierarchy(IClass clazz, TypeProvider typeProvider,HashSet<IDeclaredType> marked) {
		//If called for a generic instance, call for the declaration instead.
		if(clazz.isGenericInstance()){
			buildClassHierarchy(((GenericClassInstance)clazz).getGenericParent(), typeProvider, marked);
			return;
		}
		
		IClass superClass = clazz.getSuperClass();
		
		//Build hierarchy
		if(!alreadyChecked.contains(clazz)){
			boolean hasParents = false;
			for(IInterface i : clazz.getInterfaces()){
				i.getDescendants().add(clazz);
				hasParents = true;
			}
			if(superClass != null){
				if(superClass.getModifiers().isFinal())
					throw Problem.ofType(ProblemId.FINAL_TYPE_EXTENDED).at(clazz.getDefinition().getSuperClass())
								.details(superClass.getName())
								.raiseUnrecoverable();
				if(clazz.getModifiers().isStatic())
					throw Problem.ofType(ProblemId.STATIC_CLASS_HAS_EXTENDS).at(clazz.getDefinition().getSuperClass())
								.raiseUnrecoverable();
				if(superClass.getModifiers().isStatic())
					throw Problem.ofType(ProblemId.STATIC_CLASS_EXTENDED).at(clazz.getDefinition().getSuperClass())
								.raiseUnrecoverable();
				superClass.getDescendants().add(clazz);
				hasParents = true;
			}
			//Register this class as root type
			//if(!hasParents) typeProvider.registerRootRecord(clazz);
		}
		
		//Do circle checking
		doCircleCheck(clazz, marked);
		for(IInterface i : clazz.getInterfaces()){
			buildInterfaceHierarchy(i,typeProvider,marked);
		}
		if(superClass!=null){
			buildClassHierarchy(superClass,typeProvider,marked);
		}
		
	}

	@Override
	public void visit(ClassImpl class1) {
		if(alreadyChecked.contains(class1)) return;
		buildClassHierarchy(class1,tprov,new HashSet<IDeclaredType>());
	}

	
	//****** Interfaces *******
	
	protected void buildInterfaceHierarchy(IInterface interfac, TypeProvider typeProvider,HashSet<IDeclaredType> marked) {
		//If called for a generic instance, call for the declaration instead.
		if(interfac.isGenericInstance()){
			buildInterfaceHierarchy(((GenericInterfaceInstance)interfac).getGenericParent(), typeProvider, marked);
			return;
		}
		
		HashSet<IInterface> interfaces = interfac.getInterfaces();
		if(!alreadyChecked.contains(interfac)){
			if(!interfaces.isEmpty()){
				for(IInterface i: interfaces){
					i.getDescendants().add(interfac);
				}
			} else {
				//tprov.registerRootRecord(interfac);
			}
		}
		doCircleCheck(interfac, marked);
		for(IInterface i : interfaces){
			buildInterfaceHierarchy(i,typeProvider,marked);
		}
	}

	@Override
	public void visit(InterfaceImpl interface1) {
		if(alreadyChecked.contains(interface1)) return;
		buildInterfaceHierarchy(interface1,tprov,new HashSet<IDeclaredType>());
	}
}
