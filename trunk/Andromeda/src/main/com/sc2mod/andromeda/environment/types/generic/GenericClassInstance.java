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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.sc2mod.andromeda.classes.ClassNameProvider;
import com.sc2mod.andromeda.classes.VirtualCallTable;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.scopes.content.OperationSet;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.IRecordType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclNode;

public class GenericClassInstance extends GenericTypeInstance implements IClass {

	private IClass genericParent;
	private OperationSet constructors;
	
	
	
	public GenericClassInstance(IClass class1, Signature s) {
		super(class1, s);
	}


//
//	
//	/**
//	 * XPilot: now works for generic types
//	 */
//	@Override
//	public boolean isInstanceof(IClass c) {
//		if(this == c) return true;
//		if(c.isGeneric()) {
//			if(c instanceof GenericClassInstance) {
//				GenericClassInstance gci = (GenericClassInstance)c;
//				if(theType == gci.theType) {
//					return signature.fits(gci.signature);
//				}
//			} else {
//				//XPilot: why check if instanceof a base GenericClass (not an instance)?
//				//System.out.println(c.getFullName());
//				//System.out.println(getFullName());
//				//throw new Error();
//				return false;
//			}
//		}
//		if(superClass == null) return false;
//		return superClass.isInstanceof(c);
//	}
//	
//	public void generateGenericMembers() {
//		if(genericMembersResolved) return;
//		genericMembersResolved = true;
//		
////		System.out.println("RESOLVING " + this.getFullName());
//		
//		//XPilot: certain fields
//		if(theType.superClass == null) {
//			superClass = null;
//		} else {
//			superClass = (IClass) theType.superClass.replaceTypeParameters(paramMap);
//		}
//		topClass = theType.topClass;
//		
//		//Constructors
//		constructors = new LinkedHashMap<Signature, Constructor>();		
//		for(Entry<Signature, Constructor> e: theType.constructors.entrySet()) {
//			Signature s = alterSignature(e.getKey());
//			constructors.put(s, e.getValue());
//		}
//		
//		//Fields
//		fields = theType.fields.getAlteredFieldSet(paramMap);	
//		
//		//Methods
//		methods = theType.methods.getAlteredMethodSet(paramMap);
//		
//		//for(AbstractFunction f : methods.getMyMethods()) {
//		//	System.out.println(f.getSignature());
//		//}
//		
//		//XPilot: Destructor
//		destructor = theType.destructor;
//	}
	
//	private Signature alterSignature(Signature sig) {
//		if(sig.isEmpty()) return sig;
//		if(!sig.containsTypeParams()) return sig;
//		
//		IType[] types = sig.getTypeArrayCopy();
//		//int numTypeParams = theType.typeParams.length;
//		int sigSize = types.length;
//		for(int i=0;i<sigSize;i++) {
//			types[i] = types[i].replaceTypeParameters(paramMap);
//		}
//		return new Signature(types);
//	}
	
//	/**
//	 * A generic class instance contains type params when one of its type arguments
//	 * contains (or is) any.
//	 */
//	@Override
//	public boolean containsTypeParams() {
//		return signature.containsTypeParams();		
//	}
//	
//	@Override
//	public IType replaceTypeParameters(TypeParamMapping paramMap) {
//		if(!containsTypeParams()) return this;
//		return theType.getGenericInstance(signature.replaceTypeParameters(paramMap));
//	}
//	
//	/**
//	 * XPilot: Added so that the hierarchy can be properly traversed.
//	 */
//	@Override
//	public boolean isTopClass() {
//		return theType.isTopClass();
//	}
//	
//	@Override
//	public VirtualCallTable getVirtualCallTable() {
//		return theType.getVirtualCallTable();
//	}
//	
//	@Override
//	public void registerInstantiation() {
//		theType.registerInstantiation();
//	}
//
//
//	@Override
//	public IType getGeneratedType() {
//		return theType.getGeneratedType();
//	}
//
//	@Override
//	public int getMemberByteSize() {
//		return theType.getMemberByteSize();
//	}
//	
//	@Override
//	public boolean hasAnnotation(String name) {
//		return theType.hasAnnotation(name);
//	}
//
//	//***** XPilot: methods that should not be called? *****
//	
//	@Override
//	protected int calcByteSize() {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public boolean canConcatenateCastTo(IType toType) {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public boolean canExplicitCastTo(IType toType) {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public void generateImplementsTransClosure() {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public HashSet<String> getAllowedAnnotations() {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public int getClassIndex() {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public int getInstanceLimit() {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public IClass getTopClass() {
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public TypeParameter[] getTypeParams() {
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public boolean isImplicitReferenceType() {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public boolean isStatic() {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public boolean isUsed() {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public void setGeneratedName(String generatedName) {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public void setInstanceLimit(int instanceLimit) {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public void setMetaClassName(String name) {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public void setStatic() {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//
//	@Override
//	public void setVirtualCallTable(VirtualCallTable virtualCallTable) {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
	
	@Override
	public int calcByteSize() {
		return genericParent.calcByteSize();
	}

	@Override
	public LinkedList<IRecordType> getDescendants() {
		return genericParent.getDescendants();
	}
	
	@Override
	public ClassDeclNode getDefinition() {
		return genericParent.getDefinition();
	}


	public void setConstructors(OperationSet genericOperationSet) {
		constructors = genericOperationSet;
	}
	
	@Override
	public OperationSet getConstructors() {
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
	public int getClassIndex() {
		return genericParent.getClassIndex();
	}


	@Override
	public Destructor getDestructor() {
		return genericParent.getDestructor();
	}


	@Override
	public ArrayList<VarDecl> getHierarchyFields() {
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
	public boolean isTopClass() {
		return genericParent.isTopClass();
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
	public void setHierarchyFields(ArrayList<VarDecl> hierarchyFields) {
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
