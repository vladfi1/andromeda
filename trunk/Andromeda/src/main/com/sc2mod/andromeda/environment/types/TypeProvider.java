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
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.access.OperationAccess;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.scopes.GlobalScope;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.types.generic.GenericMemberGenerationVisitor;
import com.sc2mod.andromeda.environment.types.generic.TypeParamInstanciationVisitor;
import com.sc2mod.andromeda.environment.types.impl.ClassImpl;
import com.sc2mod.andromeda.environment.types.impl.ExtensionImpl;
import com.sc2mod.andromeda.environment.types.impl.InterfaceImpl;
import com.sc2mod.andromeda.environment.types.impl.StructImpl;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclNode;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeAliasDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeExtensionDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeListNode;
import com.sc2mod.andromeda.util.Pair;

public class TypeProvider {

	//*** TYPE COLLECTIONS***
	//private LinkedHashMap<String,Type> types = new LinkedHashMap<String,Type>();
	
	private ArrayList<IRecordType> rootRecordTypes = new ArrayList<IRecordType>();
	private ArrayList<IRecordType> recordTypes = new ArrayList<IRecordType>();
	private ArrayList<IClass> classes = new ArrayList<IClass>();
	private LinkedHashMap<Signature,LinkedHashMap<IType,ClosureType>> funcPointers = new LinkedHashMap<Signature, LinkedHashMap<IType,ClosureType>>();
	private ArrayList<Pair<TypeAliasDeclNode, IScope>> typeAliases = new ArrayList<Pair<TypeAliasDeclNode,IScope>>();
	
	
	private SystemTypes systemTypes;
	
	private boolean resolveGenerics;
	private GenericMemberGenerationVisitor genericsResolver = new GenericMemberGenerationVisitor(this);

	private HashMap<IType,IType> pointerTypes = new HashMap<IType,IType>();
	private HashMap<IType,HashMap<Integer,IType>> arrayTypes = new HashMap<IType,HashMap<Integer,IType>>();
	private HashMap<INamedType,HashMap<Signature,INamedType>> genericInstances = new HashMap<INamedType, HashMap<Signature,INamedType>>();
	
	private GlobalScope globalScope;
	private ArrayList<IExtension> extensions = new ArrayList<IExtension>();
	
	private TypeResolver resolver = new TypeResolver(this);
	private TypeParamInstanciationVisitor paramInstanciator = new TypeParamInstanciationVisitor(this);
	
	public TypeProvider(Environment env){
		globalScope = env.getTheGlobalScope();
		BasicType.registerBasicTypes(this);
		systemTypes = new AndromedaSystemTypes(env);
	}
	
	/*
	 * System types stuff. Only a facade, gets forwarded to systemTypes class.
	 */
	
	public void resolveSystemTypes() {
		systemTypes.resolveSystemTypes();
	}

	public IType getSystemType(String id) {
		return systemTypes.getSystemType(id);
	}
	
	public IClass getSystemClass(String id){
		return systemTypes.getSystemClass(id);
	}
	
	public Operation getSystemFunction(String id){
		return systemTypes.getSystemFunction(id);
	}
	
	/*
	 * Getters: Allow accessing some of the gathered data
	 */
	
	public ArrayList<IRecordType> getRecordTypes() {
		return recordTypes;
	}

	
	public ArrayList<IClass> getClasses() {
		return classes;
	}
	
	public ArrayList<IExtension> getExtensionType() {
		return extensions ;
	}
	
	public ArrayList<Pair<TypeAliasDeclNode, IScope>> getTypeAliases(){
		return typeAliases;
	}
	
	public boolean doResolveGenerics() {
		return resolveGenerics;
	}

	public void setResolveGenerics(boolean resolveGenerics) {
		this.resolveGenerics = resolveGenerics;
	}

	
	//==========================================
	//			REGISTRATION METHODS 			
	//==========================================

	void registerSimpleType(INamedType t){
		//Add the type to all scopes to which it belongs
		t.getScope().addContent(t.getName(), t);
		
//		Type tt = types.put(t.getName(), t);
//		if(tt != null){
//			throw Problem.ofType(ProblemId.DUPLICATE_TYPE_DEFINITION).at(tt.getDefinitionIfExists(),t.getDefinitionIfExists())
//				.details(t.getName())	
//				.raiseUnrecoverable();
//		}
	}
	
	public void registerBasicType(BasicType t){
		globalScope.addContent(t.getName(), t);
	}
	
	private void registerRecordType(IRecordType r){
		recordTypes.add(r);
		registerSimpleType(r);
	}
	
	public void registerStruct(StructDeclNode d, IScope scope) {
		registerRecordType(new StructImpl(d,scope));
	}

	public void registerClass(ClassDeclNode d, IScope scope) {
		IClass c;		
		c = new ClassImpl(d,scope);
		registerRecordType(c);
		classes.add(c);
	}

	public void registerInterface(InterfaceDeclNode d, IScope scope) {
		registerRecordType(new InterfaceImpl(d,scope));
	}
	
	public void registerRootRecord(IRecordType class1) {
		rootRecordTypes.add(class1);
	}
	
	public void registerTypeAlias(TypeAliasDeclNode typeAlias, IScope scope) {
		//Only added to the alias list, the rest is done later, since we cannot
		//resolve the aliased type at this point (since it could be defined below the alias)
		typeAliases.add(new Pair<TypeAliasDeclNode, IScope>(typeAlias,scope));
	}

	public void registerTypeExtension(TypeExtensionDeclNode typeExtension, IScope scope) {
		
		//Create it
		IExtension e = new ExtensionImpl(typeExtension,scope);
		
		//Entry into scope
		registerSimpleType(e);
		
		//Entry into extension list
		extensions.add(e);
	}

	/**
	 * Registers when a function is used as a func pointer
	 * @param funcPointerDecl
	 * @return
	 */
	public ClosureType registerFunctionPointerUsage(OperationAccess funcPointerDecl) {
		Operation af = funcPointerDecl.getAccessedElement();
		ClosureType fpt = getFunctionPointerType(af.getSignature(),af.getReturnType());
		fpt.registerUsage(funcPointerDecl);
		return fpt;
	}

	//==========================================
	//			TYPE RESOLVING			
	//==========================================

	public IType resolveType(com.sc2mod.andromeda.syntaxNodes.TypeNode type, IScope scope){
		return type.accept(resolver,scope);
	}
	

	private ClosureType getFunctionPointerType(Signature params, IType returnType){
		LinkedHashMap<IType, ClosureType> funcs = funcPointers.get(params);
		if(funcs == null){
			funcs = new LinkedHashMap<IType, ClosureType>();
			funcPointers.put(params, funcs);
		}
		
		ClosureType fp = funcs.get(returnType);
		if(fp == null){
			fp = new ClosureType(params,returnType);
			funcs.put(returnType, fp);
		}
		return fp;
	}
	
	IType getFunctionPointerType(com.sc2mod.andromeda.syntaxNodes.TypeNode type, IScope scope) {
		if(true)throw new Error("What is this?");
		TypeListNode tl = type.getTypeArguments();
		int size = tl.size();
		IType[] types = new IType[size];
		for(int i=0; i<size ; i++){
			types[i] = resolveType(tl.elementAt(i), scope);
		}
		IType t = resolveType(type.getReturnType(), scope);
		Signature sig = new Signature(types);

		return getFunctionPointerType(sig, t);
		
	}
	
	public IType getArrayType(IType wrappedType, int dim){
		HashMap<Integer, IType> t = arrayTypes.get(wrappedType);
		if(t == null){
			arrayTypes.put(wrappedType, t = new HashMap<Integer, IType>());
		}
		
		IType type = t.get(dim);
		if(type == null){
			t.put(dim,type = new ArrayType(wrappedType, dim));
		}
		return type;
	}

	private IType getSingleArrayType(IType wrappedType,ExprNode dimension){
		//TODO: Check array dimensions, once they are resolved
//		Type ty = dimension.getInferedType();
//		if(ty == null)
//			throw Problem.ofType(ProblemId.UNKNOWN_ARRAY_DIMENSION_TYPE).at(dimension)
//						.raiseUnrecoverable();
//		
//		if(ty.getReachableBaseType()!=BasicType.INT)
//			throw Problem.ofType(ProblemId.INVALID_ARRAY_DIMENSION_TYPE).at(dimension)
//						.details(ty.getUid())
//						.raiseUnrecoverable();
//		
//		DataObject value = dimension.getValue();
//		if(value == null)
//			throw Problem.ofType(ProblemId.NON_CONSTANT_ARRAY_DIMENSION).at(dimension)
//						.raiseUnrecoverable();
//		int dim = value.getIntValue();
//		if(dim < 0)
//			throw Problem.ofType(ProblemId.NEGATIVE_ARRAY_DIMENSION).at(dimension)
//						.details(dim)
//						.raiseUnrecoverable();
		int dim = 0;
		
		return getArrayType(wrappedType, 0);
	}
	
	IType getArrayType(IType wrappedType, ExprListNode dimensions) {
		int size= dimensions.size();
		for(int i=0;i<size;i++){
			wrappedType = getSingleArrayType(wrappedType, dimensions.elementAt(i));
		}
		return wrappedType;
	}

	public IType getPointerType(IType pointsTo){
		IType result = pointerTypes.get(pointsTo);
		//Type does not exist yet? Create and register
		if(result == null){
			pointerTypes.put(pointsTo, result = new PointerType(pointsTo));
		}
		return result;
	}
	
	/**
	 * Gets a generic instance of a generic type. Tries to get a cached type before creating a new one.
	 * @param s the signature of the type values to replace the parameters.
	 * @return
	 */
	public INamedType getGenericInstance(INamedType t, Signature s){
		if(!t.isGenericDecl()){
			throw new InternalProgramError("Trying to create a generic instance of the non generic type " + t.getName());
		}
		
		if(s.size() != t.getTypeParameters().length){
			throw new InternalProgramError("Trying to create a generic instance with the wrong number of type values");
		}
		
		//Lazily instanciate generic instance map
		HashMap<Signature, INamedType> instances = genericInstances.get(t);
		if(instances == null){
			instances = new HashMap<Signature, INamedType>();
			genericInstances.put(t, instances);
		}
		
		//Get the generic instance from the map. If we haven't got it yet, then create and put into map.
		INamedType g = instances.get(s);
		if(g == null){
			g = t.createGenericInstance(s);
			//If immediate generic resolving is activated, then the generic instance members are copied down
			if(doResolveGenerics()){
				g.accept(genericsResolver);
			}
			
			instances.put(s, g);
		}
		return g;
	}
	
	private static Iterable<INamedType> EMPTY = new ArrayList<INamedType>(0);
	
	public Iterable<INamedType> getGenericInstances(INamedType t){
		HashMap<Signature, INamedType> instances = genericInstances.get(t);
		if(instances == null){
			return EMPTY;
		}
		return instances.values();
	}
	
	public IType insertTypeArgs(IType forType, Signature typeArguments){
		return forType.accept(paramInstanciator,typeArguments);
	}
	
	public Signature insertTypeArgsInSignature(Signature inSignature, Signature typeArguments){
		if(!inSignature.containsTypeParams()) return inSignature;
		IType[] result = inSignature.getTypeArrayCopy();
		
		int size = result.length;
		for(int i=0;i<size;i++){
			IType t = insertTypeArgs(result[i], typeArguments);			
			result[i] = t;
		}
		Signature sig = new Signature(result);
		return sig;
	}
	
	//==========================================
	//			GARBAGE: DOES NOT BELONG HERE, MOVE OR DELETE			
	//==========================================




	//TODO: Do this somewhere else
//	public void generateClassAndInterfaceIndex() {
//		for(IRecordType r: rootRecordTypes){
//			TypeCategory category = r.getCategory();
//			switch(category){
//			case CLASS:
//				IClass c = (IClass)r;
//				c.generateClassIndex(this);
//				c.generateImplementsTransClosure();
//				break;
//			case INTERFACE:
//				((IInterface)r).generateInterfaceIndex(this);
//				break;
//			}
//		}
//		
//	
//	}




	public void calcFuncPointerIndices() {
		for(Entry<Signature, LinkedHashMap<IType, ClosureType>> fps: funcPointers.entrySet()){
			for(Entry<IType, ClosureType> fp : fps.getValue().entrySet()){
				fp.getValue().calcIndices();
			}
		}
	}


	
	
	//now done by the name resolver
//	public Type resolveTypeName(String name){
//		return types.get(name);
//		
//	}
	


}
