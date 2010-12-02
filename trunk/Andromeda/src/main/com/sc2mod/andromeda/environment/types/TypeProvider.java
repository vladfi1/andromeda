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
import java.util.Collection;
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
import com.sc2mod.andromeda.environment.types.basic.BasicTypeSet;
import com.sc2mod.andromeda.environment.types.generic.GenericTypeInstance;
import com.sc2mod.andromeda.environment.types.generic.TypeParamInstanciationVisitor;
import com.sc2mod.andromeda.environment.types.impl.ClassImpl;
import com.sc2mod.andromeda.environment.types.impl.ExtensionImpl;
import com.sc2mod.andromeda.environment.types.impl.InterfaceImpl;
import com.sc2mod.andromeda.environment.types.impl.StructImpl;
import com.sc2mod.andromeda.parsing.Language;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.syntaxNodes.ArrayTypeNode;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclNode;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeAliasDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeExtensionDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeListNode;
import com.sc2mod.andromeda.util.Pair;

public class TypeProvider {

	//*** TYPE COLLECTIONS***
	//private LinkedHashMap<String,Type> types = new LinkedHashMap<String,Type>();
	private Environment env;
	
	private ArrayList<IDeclaredType> declaredTypes = new ArrayList<IDeclaredType>();
	private ArrayList<IClass> classes = new ArrayList<IClass>();
	private LinkedHashMap<Signature,LinkedHashMap<IType,ClosureType>> funcPointers = new LinkedHashMap<Signature, LinkedHashMap<IType,ClosureType>>();
	private ArrayList<Pair<TypeAliasDeclNode, IScope>> typeAliases = new ArrayList<Pair<TypeAliasDeclNode,IScope>>();
	
	
	private SystemTypes systemTypes;

	private HashMap<IType,IType> pointerTypes = new HashMap<IType,IType>();
	private HashMap<IType,HashMap<Integer,ArrayType>> arrayTypes = new HashMap<IType,HashMap<Integer,ArrayType>>();
	private HashMap<INamedType,HashMap<Signature,INamedType>> genericInstances = new HashMap<INamedType, HashMap<Signature,INamedType>>();
	
	private GlobalScope globalScope;
	private ArrayList<IExtension> extensions = new ArrayList<IExtension>();
	
	private TypeResolver resolver = new TypeResolver(this);
	private TypeParamInstanciationVisitor paramInstanciator = new TypeParamInstanciationVisitor(this);
	
	public final BasicTypeSet BASIC;
	
	private int nextTypeIndex = 1;
	public int getNextTypeIndex(){
		return nextTypeIndex++;
	}
	
	public TypeProvider(Environment env, Language language){
		this.env = env;
		globalScope = env.getTheGlobalScope();
		BASIC = new BasicTypeSet(this);
		systemTypes = language.getImpl().getSystemTypes(env, this);
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
	
	public ArrayList<IDeclaredType> getDeclaredTypes() {
		return declaredTypes;
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
	
	private void registerDeclaredType(IDeclaredType r){
		declaredTypes.add(r);
		registerSimpleType(r);
	}
	
	public void registerStruct(StructDeclNode d, IScope scope) {
		registerDeclaredType(new StructImpl(d,scope,env));
	}

	public void registerClass(ClassDeclNode d, IScope scope) {
		IClass c;		
		c = new ClassImpl(d,scope,env);
		registerDeclaredType(c);
		classes.add(c);
	}

	public void registerInterface(InterfaceDeclNode d, IScope scope) {
		registerDeclaredType(new InterfaceImpl(d,scope,env));
	}
	
	
	public void registerTypeAlias(TypeAliasDeclNode typeAlias, IScope scope) {
		//Only added to the alias list, the rest is done later, since we cannot
		//resolve the aliased type at this point (since it could be defined below the alias)
		typeAliases.add(new Pair<TypeAliasDeclNode, IScope>(typeAlias,scope));
	}

	public void registerTypeExtension(TypeExtensionDeclNode typeExtension, IScope scope) {
		
		//Create it
		IExtension e = new ExtensionImpl(typeExtension,scope,env);
		
		//Entry into scope
		registerDeclaredType(e);
		
		//Entry into extension list
		extensions.add(e);
	}

	//==========================================
	//			TYPE RESOLVING			
	//==========================================

	public IType resolveType(com.sc2mod.andromeda.syntaxNodes.TypeNode type, IScope scope, boolean staticContext){
		resolver.setStaticContect(staticContext);
		return type.accept(resolver,scope);
	}
	
	public IType getClosureType(OperationAccess opAccess) {
		Operation op = opAccess.getAccessedElement();
		ClosureType ct = getClosureType(op.getSignature(), op.getReturnType());
		ct.registerUsage(opAccess);
		return ct;
	}

	private ClosureType getClosureType(Signature params, IType returnType){
		LinkedHashMap<IType, ClosureType> funcs = funcPointers.get(params);
		if(funcs == null){
			funcs = new LinkedHashMap<IType, ClosureType>();
			funcPointers.put(params, funcs);
		}
		
		ClosureType fp = funcs.get(returnType);
		if(fp == null){
			fp = new ClosureType(params,returnType,this);
			funcs.put(returnType, fp);
		}
		return fp;
	}
	
	IType getFunctionPointerType(com.sc2mod.andromeda.syntaxNodes.TypeNode type, IScope scope, boolean staticContext) {
		//TODO check where this is used
		if(true)throw new Error("What is this?");
		TypeListNode tl = type.getTypeArguments();
		int size = tl.size();
		IType[] types = new IType[size];
		for(int i=0; i<size ; i++){
			types[i] = resolveType(tl.get(i), scope, staticContext);
		}
		IType t = resolveType(type.getReturnType(), scope, staticContext);
		Signature sig = new Signature(types);

		return getClosureType(sig, t);
		
	}
	
	public ArrayType createArrayType(IType wrappedType, ArrayTypeNode expressionProvider) {
		return new ArrayType(wrappedType, expressionProvider, this);
	}
	
//	public ArrayType getArrayType(IType wrappedType, int dim){
//		HashMap<Integer, ArrayType> t = arrayTypes.get(wrappedType);
//		if(t == null){
//			arrayTypes.put(wrappedType, t = new HashMap<Integer, ArrayType>());
//		}
//		
//		ArrayType type = t.get(dim);
//		if(type == null){
//			t.put(dim,type = new ArrayType(wrappedType, dim,this));
//		}
//		return type;
//	}
//
//	/**
//	 * Creates a new array type.
//	 * Currently ignores the dimension
//	 * @param wrappedType
//	 * @param dimension
//	 * @return
//	 */
//	IType getArrayType(IType wrappedType, ExprNode dimension) {
//		return getArrayType(wrappedType, -1);
//	}

	public IType getPointerType(IType pointsTo){
		IType result = pointerTypes.get(pointsTo);
		//Type does not exist yet? Create and register
		if(result == null){
			pointerTypes.put(pointsTo, result = new PointerType(pointsTo,this));
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
			if(!t.isGenericInstance()){
				throw new InternalProgramError("Trying to create a generic instance of the non generic type " + t.getFullName());
			} else {
				t = ((GenericTypeInstance)t).getGenericParent();
			}
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
			instances.put(s, g);
		}
		return g;
	}

	
	private static Collection<INamedType> EMPTY = new ArrayList<INamedType>(0);
	
	public Collection<INamedType> getGenericInstances(INamedType t){
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
	//=========================================


	public void calcFuncPointerIndices() {
		for(Entry<Signature, LinkedHashMap<IType, ClosureType>> fps: funcPointers.entrySet()){
			for(Entry<IType, ClosureType> fp : fps.getValue().entrySet()){
				fp.getValue().calcIndices();
			}
		}
	}









}
