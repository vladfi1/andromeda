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
import java.util.LinkedList;
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Constructor;
import com.sc2mod.andromeda.environment.Scope;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.StaticInit;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclNode;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeAliasDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeCategorySE;
import com.sc2mod.andromeda.syntaxNodes.TypeExtensionDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeListNode;
import com.sc2mod.andromeda.vm.data.DataObject;

public class TypeProvider {

	
	private ArrayList<RecordType> rootTypes = new ArrayList<RecordType>();
	private ArrayList<RecordType> recordTypes = new ArrayList<RecordType>();
	private ArrayList<Class> classes = new ArrayList<Class>();
	private ArrayList<StaticInit> typeInitializers = new ArrayList<StaticInit>();
	private ArrayList<ArrayList<Type>> overriddenTypes = new ArrayList<ArrayList<Type>>();
	private LinkedHashMap<Signature,LinkedHashMap<Type,FunctionPointer>> funcPointers = new LinkedHashMap<Signature, LinkedHashMap<Type,FunctionPointer>>();
	
	private LinkedHashMap<String,Type> types = new LinkedHashMap<String,Type>();
	
	private SystemTypes systemTypes = new AndromedaSystemTypes(this);
	
	private HashMap<Type,Type> pointerTypes = new HashMap<Type,Type>();
	private HashMap<Type,HashMap<Integer,Type>> arrayTypes = new HashMap<Type,HashMap<Integer,Type>>();
	private HashMap<Type,EnrichmentSet> enrichments = new HashMap<Type,EnrichmentSet>();
	private int curClassIndex = 1;
	private int curInterfaceIndex = 0;
	
	
	
	public ArrayList<RecordType> getRecordTypes() {
		return recordTypes;
	}

	public int getNextClassIndex(){
		return curClassIndex++;
	}
	
	public int getCurInstanceofIndex(){
		return curClassIndex;
	}
	
	public int getNextInterfaceIndex(){
		return curInterfaceIndex++;
	}
	
	public int getCurInterfaceIndex(){
		return curInterfaceIndex;
	}
	
	public ArrayList<Class> getClasses() {
		return classes;
	}
	
	public TypeProvider(){
		BasicType.registerBasicTypes(this);
	}
	
	void registerSimpleType(SimpleType t){
		Type tt = types.put(t.getName(), t);
		if(tt != null){
			throw Problem.ofType(ProblemId.DUPLICATE_TYPE_DEFINITION).at(tt.getDefinitionIfExists(),t.getDefinitionIfExists())
				.details(t.getName())	
				.raiseUnrecoverable();
		}
	}
	
	private void registerRecordType(RecordType r){
		recordTypes.add(r);
		registerSimpleType(r);
	}
	
	public void registerStruct(StructDeclNode d, Scope scope) {
		registerRecordType(new Struct(d,scope));
	}

	public void registerClass(ClassDeclNode d, Scope scope) {
		Class c;		
		if(d.getTypeParams()!=null){
			c = new GenericClass(d,scope);
		} else {
			c = new Class(d,scope);
		}
		registerRecordType(c);
		classes.add(c);
	}

	public void registerInterface(InterfaceDeclNode d, Scope scope) {
		registerRecordType(new Interface(d,scope));
	}
	
	public Type resolveTypeName(String name){
		return types.get(name);
		
	}

	public Type resolveType(com.sc2mod.andromeda.syntaxNodes.TypeNode type){
		String name = null;
		int cat = type.getCategory();
		Type result = null;
		TypeListNode args = null;
		switch(cat){
			case TypeCategorySE.SIMPLE:
				args = type.getTypeArguments();
			case TypeCategorySE.BASIC:
				name = type.getName();
				result = types.get(name);
				if(result == null){
					break;
				}
				//Generics
				if(result.isGeneric()){
					if(args == null)
						throw Problem.ofType(ProblemId.GENERIC_TYPE_MISSING_TYPE_ARGUMENTS).at(type)
									.details(result.getFullName())
									.raiseUnrecoverable();
					//We have a generic type, so resolve the type arguments
					int size  = args.size();
					Type[] typeArgs = new Type[size];
					for(int i=0;i<size;i++){
						Type t = resolveType(args.elementAt(i));
						typeArgs[i]=t;				
					}
					return ((RecordType)result).getGenericInstance(new Signature(typeArgs));
				} else{
					if(args != null)
						throw Problem.ofType(ProblemId.NON_GENERIC_TYPE_HAS_TYPE_ARGUMENTS).at(args)
										.details(result.getFullName())
										.raiseUnrecoverable();
				}
				break;
			case TypeCategorySE.QUALIFIED:
				throw new Error("Qualified type names not possible yet!");
			case TypeCategorySE.POINTER:
				result = getPointerType(resolveType(type.getWrappedType()));
				break;
			case TypeCategorySE.ARRAY:
				result = getArrayType(resolveType(type.getWrappedType()),type.getDimensions());
				break;
			case TypeCategorySE.FUNCTION:
				result = getFunctionPointerType(type);
		}

		
		if(result == null)
			throw Problem.ofType(ProblemId.UNKNOWN_TYPE).at(type)
							.details(name)
							.raiseUnrecoverable();
		return result;
	}
	

	private FunctionPointer getFunctionPointerType(Signature params, Type returnType){
		LinkedHashMap<Type, FunctionPointer> funcs = funcPointers.get(params);
		if(funcs == null){
			funcs = new LinkedHashMap<Type, FunctionPointer>();
			funcPointers.put(params, funcs);
		}
		
		FunctionPointer fp = funcs.get(returnType);
		if(fp == null){
			fp = new FunctionPointer(params,returnType);
			funcs.put(returnType, fp);
		}
		return fp;
	}
	
	private Type getFunctionPointerType(
			com.sc2mod.andromeda.syntaxNodes.TypeNode type) {
		TypeListNode tl = type.getTypeArguments();
		int size = tl.size();
		Type[] types = new Type[size];
		for(int i=0; i<size ; i++){
			types[i] = resolveType(tl.elementAt(i));
		}
		Type t = resolveType(type.getReturnType());
		Signature sig = new Signature(types);
		
	
		
		return getFunctionPointerType(sig, t);
		
		
		
	}

	private Type getSingleArrayType(Type wrappedType,ExprNode dimension){
		Type ty = dimension.getInferedType();
		if(ty == null)
			throw Problem.ofType(ProblemId.UNKNOWN_ARRAY_DIMENSION_TYPE).at(dimension)
						.raiseUnrecoverable();
		
		if(ty.getReachableBaseType()!=BasicType.INT)
			throw Problem.ofType(ProblemId.INVALID_ARRAY_DIMENSION_TYPE).at(dimension)
						.details(ty.getUid())
						.raiseUnrecoverable();
		
		DataObject value = dimension.getValue();
		if(value == null)
			throw Problem.ofType(ProblemId.NON_CONSTANT_ARRAY_DIMENSION).at(dimension)
						.raiseUnrecoverable();
		int dim = value.getIntValue();
		if(dim < 0)
			throw Problem.ofType(ProblemId.NEGATIVE_ARRAY_DIMENSION).at(dimension)
						.details(dim)
						.raiseUnrecoverable();
		
		HashMap<Integer, Type> t = arrayTypes.get(wrappedType);
		if(t == null){
			arrayTypes.put(wrappedType, t = new HashMap<Integer, Type>());
		}
		
		Type type = t.get(dim);
		if(type == null){
			t.put(dim,type = new ArrayType(wrappedType, dim));
		}
		return type;
	}
	private Type getArrayType(Type wrappedType, ExprListNode dimensions) {
		int size= dimensions.size();
		for(int i=0;i<size;i++){
			wrappedType = getSingleArrayType(wrappedType, dimensions.elementAt(i));
		}
		return wrappedType;
	}

	public Type getPointerType(Type pointsTo){
		Type result = pointerTypes.get(pointsTo);
		//Type does not exist yet? Create and register
		if(result == null){
			pointerTypes.put(pointsTo, result = new PointerType(pointsTo));
		}
		return result;
	}
	


	public void resolveInheritance() {
		ArrayList<RecordType> recordTypes = new ArrayList<RecordType>();
		for(String s: types.keySet()){
			Type t = types.get(s);
			if(t instanceof RecordType) {
				recordTypes.add((RecordType)t);
			}
		}
		//XPilot: avoids concurrent modification exception, as type parameters will be pushed
		for(RecordType type : recordTypes) {
			type.resolveInheritance(this);
		}
	}
	
	public void checkHierarchy(){
		for(String s: types.keySet()){
			Type t = types.get(s);
			if(t instanceof RecordType){
				((RecordType)t).checkHierarchy(this);
			}
		}
	}

	public void resolveMemberTypes() {
		LinkedList<RecordType> typesToResolve = new LinkedList<RecordType>();
		typesToResolve.addAll(enrichmentsToResolve);
		typesToResolve.addAll(rootTypes);
		while(!typesToResolve.isEmpty()){
			RecordType r = typesToResolve.poll();
			typesToResolve.addAll(r.descendants);
			r.resolveMembers(this);
		}

		enrichmentsToResolve.clear();
	}

	public void addRootRecord(RecordType class1) {
		rootTypes.add(class1);
	}

	public void generateMethodIndex() {
		for(String s: types.keySet()){
			Type t = types.get(s);
			if(t instanceof RecordType){
				RecordType r = (RecordType)t;
				r.getMethods().generateMethodIndex();
				
				//Constructors
				int i = 0;
				for(Entry<Signature, Constructor> e: r.constructors.entrySet()){
						e.getValue().setIndex(i++);			
				}
			}
		}
		for(Entry<Type, EnrichmentSet> e: enrichments.entrySet()){
			e.getValue().getMethods().generateFunctionIndex();
		}
	}

	private ArrayList<Enrichment> enrichmentsToResolve = new ArrayList<Enrichment>();
	private ArrayList<TypeAliasDeclNode> typeAliases = new ArrayList<TypeAliasDeclNode>();
	public void addEnrichment(EnrichDeclNode enrichDeclaration, Scope scope) {
		Enrichment er = new Enrichment(enrichDeclaration, scope);
		recordTypes.add(er);
		enrichmentsToResolve.add(er);
	}
	
	public void resolveEnrichments(){
		for(Enrichment e : enrichmentsToResolve){
			e.resolveType(this);
			Type t = e.getEnrichedType();
			EnrichmentSet es = enrichments.get(t);
			if(es == null) enrichments.put(t, es = new EnrichmentSet(t));
			es.addEnrichment(e);
		}
	}
	
	public void resolveEnrichmentMethods(){
		for(Entry<Type, EnrichmentSet> entry: enrichments.entrySet()){
			entry.getValue().resolveEnrichmentMethods();
		}
	}

	public EnrichmentSet getEnrichmentSetForType(Type t) {
		return enrichments.get(t);
	}

	public void generateClassAndInterfaceIndex() {
		for(RecordType r: rootTypes){
			int category = r.getCategory();
			switch(category){
			case Type.CLASS:
			case Type.GENERIC_CLASS://XPilot: added
				Class c = (Class)r;
				c.generateClassIndex(this);
				c.generateImplementsTransClosure();
				break;
			case Type.INTERFACE:
				((Interface)r).generateInterfaceIndex(this);
				break;
			}
		}
		
	
	}

	public void checkImplicitConstructors() {
		for(Class c: classes){
			c.checkImplicitConstructor();
		}
	}

	public void addStaticInit(StaticInit s) {
		typeInitializers.add(s);
	}

	public  ArrayList<StaticInit> getTypeInits() {
		return typeInitializers;
	}


	public void pushTypeParams(TypeParameter[] typeParams) {
		ArrayList<Type> overridden = null;
		for(TypeParameter p: typeParams){
			Type typeBefore = types.put(p.getUid(), p);
			if(typeBefore != null){
				if(overridden == null)overridden = new ArrayList<Type>(2);
				overridden.add(typeBefore);
			}
		}
		overriddenTypes.add(overridden);
	}

	public void popTypeParams(TypeParameter[] typeParams) {
		//Remove them
		for(TypeParameter t: typeParams){
			types.remove(t.getUid());
		}
		
		//Restore old if they overrode other types
		ArrayList<Type> overridden = overriddenTypes.remove(overriddenTypes.size()-1);
		if(overridden != null){
			for(Type t: overridden){
				types.put(t.getUid(), t);
			}
		}
	}

	public void registerTypeAlias(TypeAliasDeclNode typeAlias) {
		typeAliases.add(typeAlias);
		Type t = resolveType(typeAlias.getEnrichedType());
		Type old = types.put(typeAlias.getName(), t);
		if(old != null){
			throw Problem.ofType(ProblemId.DUPLICATE_TYPE_DEFINITION).at(typeAlias,old.getDefinitionIfExists())
						.details(typeAlias.getName())
						.raiseUnrecoverable();
		}
	}

	public void registerTypeExtension(TypeExtensionDeclNode typeExtension) {
		Type t = resolveType(typeExtension.getEnrichedType());
		Extension e = new Extension(typeExtension,t);
		Type old = types.put(e.getName(), e);
		if(old != null){
			throw Problem.ofType(ProblemId.DUPLICATE_TYPE_DEFINITION).at(typeExtension,old.getDefinitionIfExists())
				.details(e.getName())
				.raiseUnrecoverable();
		}
	}

	/**
	 * Registers when a function is used as a func pointer
	 * @param funcPointerDecl
	 * @return
	 */
	public FunctionPointer registerFunctionPointerUsage(FuncPointerDecl funcPointerDecl) {
		AbstractFunction af = funcPointerDecl.getFunction();
		FunctionPointer fpt = getFunctionPointerType(af.getSignature(),af.getReturnType());
		fpt.registerUsage(funcPointerDecl);
		return fpt;
	}

	public void calcFuncPointerIndices() {
		for(Entry<Signature, LinkedHashMap<Type, FunctionPointer>> fps: funcPointers.entrySet()){
			for(Entry<Type, FunctionPointer> fp : fps.getValue().entrySet()){
				fp.getValue().calcIndices();
			}
		}
	}
	

	/*
	 * System types stuff. Only a facade, gets forwarded to systemTypes class.
	 */
	
	public void resolveSystemTypes() {
		systemTypes.resolveSystemTypes();
	}

	public Type getSystemType(String id) {
		return systemTypes.getSystemType(id);
	}
	
	public Class getSystemClass(String id){
		return systemTypes.getSystemClass(id);
	}
	
	public AbstractFunction getSystemFunction(String id){
		return systemTypes.getSystemFunction(id);
	}
}
