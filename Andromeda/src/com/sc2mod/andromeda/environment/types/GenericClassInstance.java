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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.sc2mod.andromeda.classes.ClassNameProvider;
import com.sc2mod.andromeda.classes.VirtualCallTable;
import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Constructor;
import com.sc2mod.andromeda.environment.Destructor;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.variables.FieldSet;
import com.sc2mod.andromeda.notifications.CompilationError;

public class GenericClassInstance extends GenericClass{

	private GenericClass theType;
	private Signature signature;
	private boolean genericMembersResolved;
	private TypeParamMapping paramMap;
	
	public GenericClassInstance(GenericClass class1, Signature s) {
		super(class1);
		theType = class1;
		this.signature = s;
		paramMap = new TypeParamMapping(theType.typeParams,signature);
		class1.genericInstances.put(s, this);
		//If the generic class has already resolved its members, we can copy it from there
		if(class1.membersResolved){
			generateGenericMembers();
		}
		
		//XPilot: need to pretend to be the original GenericClass
		//otherwise, a null pointer exception is thrown in checkCallHierarchy
		//this should be called in generateGenericMembers?
		//but descendants should be registered to the original GenericClass, not this?
		descendants = theType.descendants;
		minInstanceofIndex = theType.minInstanceofIndex;
		classIndex = theType.classIndex;
		//interfaces = theType.interfaces;
	}
	
	@Override
	public String getAllocatorName() {
		return theType.getAllocatorName();
	}
	
	@Override
	public String getDeallocatorName() {
		return theType.getDeallocatorName();
	}
	
	@Override
	public String getMetaClassName() {
		return theType.getMetaClassName();
	}
	
	@Override
	public String getGeneratedName() {
		return theType.getGeneratedName();
	}
	
	@Override
	public ClassNameProvider getNameProvider() {
		return theType.getNameProvider();
	}
	


	@Override
	public int getCategory() {
		return GENERIC_CLASS;
	}
	
	@Override
	public Type getWrappedType() {
		return theType;
	}

	@Override
	public String getDescription() {
		return "generic class";
	}
	
	@Override
	public String getFullName() {
		return new StringBuilder().append(theType.getUid()).append("<").append(signature.getFullName()).append(">").toString();
	}
		
	@Override
	public Signature getSignature() {
		return signature;
	}
	
	
	@Override
	public String getUid() {
		return theType.getUid();
	}
	
	/**
	 * XPilot: now works for generic types
	 */
	@Override
	public boolean isInstanceof(Class c) {
		if(this == c) return true;
		if(c.isGeneric()) {
			GenericClassInstance gci = (GenericClassInstance)c;
			if(theType == gci.theType) {
				return signature.fits(gci.signature);
			}
		}
		if(superClass == null) return false;
		return superClass.isInstanceof(c);
	}
	
	/* XPilot: Not needed, can use Class.canImplicitCastTo
	@Override
	public boolean canImplicitCastTo(Type toType) {
		if(toType == this) return true;
		//XPilot: Can now cast to a non-generic class
		if(toType.getCategory() == CLASS) {
			return theType.canImplicitCastTo(toType);
		}
		if(toType.getCategory() != GENERIC_CLASS) return false;
		
		//We can cast if the signature is okay and the non-generic prefix can be cast implicitly
		if(!theType.canImplicitCastTo(toType.getWrappedType())) return false;
		return signature.equals(((GenericClassInstance)toType).getSignature());
	}
	*/
	
	public void generateGenericMembers(){
		if(genericMembersResolved) return;
		genericMembersResolved = true;
		
		System.out.println("RSOLVING " + this.getFullName());
		
		//XPilot: certain fields
		superClass = theType.superClass;
		//if(superClass != null) {
		//	System.out.println(this.getFullName());
		//	System.out.println(theType.getFullName());
		//	System.out.println(superClass.getFullName());
		//}
		topClass = theType.topClass;
		
		//Constructors
		constructors = new LinkedHashMap<Signature, Constructor>();		
		for(Entry<Signature, Constructor> e: theType.constructors.entrySet()){
			Signature s = alterSignature(e.getKey());
			constructors.put(s, e.getValue());
		}
		
		//Fields
		fields = theType.fields.getAlteredFieldSet(paramMap);	
		
		//Methods
		methods = theType.methods.getAlteredMethodSet(paramMap);
		
		//for(AbstractFunction f : methods.getMyMethods()) {
		//	System.out.println(f.getSignature());
		//}
		
		//XPilot: Destructor
		destructor = theType.destructor;
	}

	//XPilot: added
	@Override
	public String getGeneratedDefinitionName() {
		return theType.getGeneratedDefinitionName();
	}
	
	private Signature alterSignature(Signature sig){
		if(sig.isEmpty()) return sig;
		if(!sig.containsTypeParams()) return sig;
		
		Type[] types = sig.getTypeArrayCopy();
		int numTypeParams = theType.typeParams.length;
		int sigSize = types.length;
		for(int i=0;i<sigSize;i++){
			types[i] = types[i].replaceTypeParameters(paramMap);
		}
		return new Signature(types);
	}
	
	/**
	 * A generic class instance contains type params when one of its type arguments
	 * contains (or is) any.
	 */
	@Override
	public boolean containsTypeParams() {
		return signature.containsTypeParams();		
	}
	
	@Override
	public Type replaceTypeParameters(TypeParamMapping paramMap) {
		if(!containsTypeParams()) return this;
		return theType.getGenericInstance(signature.replaceTypeParameters(paramMap));
	}
	
	@Override
	public boolean isGenericInstance() {
		return true;
	}

	@Override
	void resolveMembers(TypeProvider t) {
		//XPilot: should not happen for GenericClassInstances? The base GenericClass does this for us
		//System.out.println("GenericClass.resolveMembers(TypeProvider) called on a GenericClassInstance: " + getFullName());
		throw new CompilationError("GenericClass.resolveMembers(TypeProvider) called on a GenericClassInstance: " + getFullName());
	}
	
	@Override
	protected void checkForHierarchyCircle(TypeProvider typeProvider,HashSet<RecordType> marked) {
		//XPilot: should not happen for GenericClassInstances? The base GenericClass does this for us
		//throw new CompilationError("GenericClass.checkForHierarchyCircle(TypeProvider, HashSet<Record>) called on a GenericClassInstance: " + getFullName());
		theType.checkForHierarchyCircle(typeProvider, marked);
	}
	
	/**
	 * XPilot: Added so that the hierarchy can be properly traversed.
	 */
	@Override
	public boolean isTopClass() {
		return theType.isTopClass();
	}
	
	@Override
	public VirtualCallTable getVirtualCallTable() {
		return theType.getVirtualCallTable();
	}
}
