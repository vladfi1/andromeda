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
import java.util.Stack;
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.AbstractFunction;
import com.sc2mod.andromeda.environment.Constructor;
import com.sc2mod.andromeda.environment.Scope;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.StaticInit;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclaration;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclaration;
import com.sc2mod.andromeda.syntaxNodes.Expression;
import com.sc2mod.andromeda.syntaxNodes.ExpressionList;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclaration;
import com.sc2mod.andromeda.syntaxNodes.StructDeclaration;
import com.sc2mod.andromeda.syntaxNodes.TypeAlias;
import com.sc2mod.andromeda.syntaxNodes.TypeCategory;
import com.sc2mod.andromeda.syntaxNodes.TypeExtension;
import com.sc2mod.andromeda.syntaxNodes.TypeList;
import com.sc2mod.andromeda.syntaxNodes.TypeParam;
import com.sc2mod.andromeda.vm.data.DataObject;

public class TypeProvider {

	
	private static final String FUNC_NAME = "funcName";
	private ArrayList<RecordType> rootTypes = new ArrayList<RecordType>();
	private ArrayList<RecordType> recordTypes = new ArrayList<RecordType>();
	private ArrayList<Class> classes = new ArrayList<Class>();
	private ArrayList<StaticInit> typeInitializers = new ArrayList<StaticInit>();
	private ArrayList<ArrayList<Type>> overriddenTypes = new ArrayList<ArrayList<Type>>();
	private LinkedHashMap<Signature,LinkedHashMap<Type,FunctionPointer>> funcPointers = new LinkedHashMap<Signature, LinkedHashMap<Type,FunctionPointer>>();
	
	private LinkedHashMap<String,Type> types = new LinkedHashMap<String,Type>();
	private Class metaClass;

	public Class getClass(String name){
		Type t = types.get(name);
		if(t == null) return null;
		if(!(t instanceof Class)){
			throw new CompilationError("The internal system class \"" + t.getUid() + "\" was not defined as class!");
		}
		return (Class)t;
	}

	private HashMap<Type,Type> pointerTypes = new HashMap<Type,Type>();
	private HashMap<Type,HashMap<Integer,Type>> arrayTypes = new HashMap<Type,HashMap<Integer,Type>>();
	private HashMap<Type,EnrichmentSet> enrichments = new HashMap<Type,EnrichmentSet>();
	private int curClassIndex = 1;
	private int curInterfaceIndex = 0;
	private Class objectClass;
	
	
	
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
			throw new CompilationError("Duplicate type definition!\nFirst Definition:\n" + tt.getDescription() + "\nSecond Definition:\n" + t.getDescription() );
		}
	}
	
	private void registerRecordType(RecordType r){
		recordTypes.add(r);
		registerSimpleType(r);
	}
	
	public void registerStruct(StructDeclaration d, Scope scope) {
		registerRecordType(new Struct(d,scope));
	}

	public void registerClass(ClassDeclaration d, Scope scope) {
		Class c;		
		if(d.getTypeParams()!=null){
			c = new GenericClass(d,scope);
		} else {
			c = new Class(d,scope);
		}
		registerRecordType(c);
		classes.add(c);
	}

	public void registerInterface(InterfaceDeclaration d, Scope scope) {
		registerRecordType(new Interface(d,scope));
	}
	
	public Type resolveTypeName(String name){
		return types.get(name);
		
	}

	public Type resolveType(com.sc2mod.andromeda.syntaxNodes.Type type){
		String name = null;
		int cat = type.getCategory();
		Type result = null;
		TypeList args = null;
		switch(cat){
			case TypeCategory.SIMPLE:
				args = type.getTypeArguments();
			case TypeCategory.BASIC:
				name = type.getName();
				result = types.get(name);
				if(result == null){
					break;
				}
				//Generics
				if(result.isGeneric()){
					if(args == null) throw new CompilationError(type,"The generic type " + result.getUid() + " must have type arguments.");
					//We have a generic type, so resolve the type arguments
					int size  = args.size();
					Type[] typeArgs = new Type[size];
					for(int i=0;i<size;i++){
						Type t = resolveType(args.elementAt(i));
						typeArgs[i]=t;				
					}
					return ((RecordType)result).getGenericInstance(new Signature(typeArgs));
				} else{
					if(args != null) throw new CompilationError(args,"The non-generic type " + result.getUid() + " cannot have type arguments.");
				}
				break;
			case TypeCategory.QUALIFIED:
				throw new Error("Qualified type names not possible yet!");
			case TypeCategory.POINTER:
				result = getPointerType(resolveType(type.getWrappedType()));
				break;
			case TypeCategory.ARRAY:
				result = getArrayType(resolveType(type.getWrappedType()),type.getDimensions());
				break;
			case TypeCategory.FUNCTION:
				result = getFunctionPointerType(type);
		}

		
		if(result == null) throw new CompilationError(type, "Could not resolve the type " + name + ".");
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
			com.sc2mod.andromeda.syntaxNodes.Type type) {
		TypeList tl = type.getTypeArguments();
		int size = tl.size();
		Type[] types = new Type[size];
		for(int i=0; i<size ; i++){
			types[i] = resolveType(tl.elementAt(i));
		}
		Type t = resolveType(type.getReturnType());
		Signature sig = new Signature(types);
		
	
		
		return getFunctionPointerType(sig, t);
		
		
		
	}

	private Type getSingleArrayType(Type wrappedType,Expression dimension){
		Type ty = dimension.getInferedType();
		if(ty == null){
			throw new CompilationError(dimension,"Could not determine the type of an array dimension.");
		}
		
		if(ty!=BasicType.INT)
			throw new CompilationError(dimension,"The dimension of an array must be of type int, but it is of type" + ty.getUid());
		
		
		DataObject value = dimension.getValue();
		if(value == null)
			throw new CompilationError(dimension,"Could not determine the dimensions of an array. The expression must be constant. If it contains constant variables, these must be defined BEFORE the array definition.");
		int dim = value.getIntValue();
		if(dim < 0)
			throw new CompilationError(dimension,"Array dimension is less than zero (" + dim + ").");
		
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
	private Type getArrayType(Type wrappedType, ExpressionList dimensions) {
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
	private ArrayList<TypeAlias> typeAliases = new ArrayList<TypeAlias>();
	public void addEnrichment(EnrichDeclaration enrichDeclaration, Scope scope) {
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

	public void registerObjectClass() {
		Class c = (Class)types.get("Object");
		if(c == null) throw new CompilationError("Missing system class 'Object'!");
		objectClass = c;
	}
	
	public Class getObjectClass(){
		return objectClass;
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

	public void registerTypeAlias(TypeAlias typeAlias) {
		typeAliases.add(typeAlias);
		Type t = resolveType(typeAlias.getEnrichedType());
		Type old = types.put(typeAlias.getName(), t);
		if(old != null){
			throw new CompilationError(typeAlias,"Duplicate type '" + typeAlias.getName() + "");
		}
	}

	public void registerTypeExtension(TypeExtension typeExtension) {
		Type t = resolveType(typeExtension.getEnrichedType());
		Extension e = new Extension(typeExtension,t);
		Type old = types.put(e.getName(), e);
		if(old != null){
			throw new CompilationError(typeExtension,"Duplicate type '" + e.getName() + "");
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
	
	private Type funcNameType;
	public Type getFuncNameType() {
		if(funcNameType == null){
			funcNameType = resolveTypeName(FUNC_NAME);
			if(funcNameType == null){
				throw new CompilationError("System type 'funcName' not found. Was FuncName.a in package a.lang modified or deleted?");
			}
		}
		return funcNameType;
	}
}
