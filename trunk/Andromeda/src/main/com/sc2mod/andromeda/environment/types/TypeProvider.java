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
import java.util.List;
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.StaticInit;
import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.GlobalScope;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.content.NameResolver;
import com.sc2mod.andromeda.environment.variables.FuncPointerDecl;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.program.ToDo;
import com.sc2mod.andromeda.syntaxNodes.ClassDeclNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.ExprNode;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;
import com.sc2mod.andromeda.syntaxNodes.InterfaceDeclNode;
import com.sc2mod.andromeda.syntaxNodes.StructDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeAliasDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeExtensionDeclNode;
import com.sc2mod.andromeda.syntaxNodes.TypeListNode;
import com.sc2mod.andromeda.util.Pair;
import com.sc2mod.andromeda.vm.data.DataObject;

public class TypeProvider {

	//*** TYPE COLLECTIONS***
	//private LinkedHashMap<String,Type> types = new LinkedHashMap<String,Type>();
	
	private ArrayList<RecordTypeImpl> rootRecordTypes = new ArrayList<RecordTypeImpl>();
	private ArrayList<RecordTypeImpl> recordTypes = new ArrayList<RecordTypeImpl>();
	private ArrayList<IClass> classes = new ArrayList<IClass>();
	private LinkedHashMap<Signature,LinkedHashMap<IType,FunctionPointer>> funcPointers = new LinkedHashMap<Signature, LinkedHashMap<IType,FunctionPointer>>();
	private ArrayList<Pair<TypeAliasDeclNode, IScope>> typeAliases = new ArrayList<Pair<TypeAliasDeclNode,IScope>>();
	
	
	private SystemTypes systemTypes;
	
	private HashMap<IType,IType> pointerTypes = new HashMap<IType,IType>();
	private HashMap<IType,HashMap<Integer,IType>> arrayTypes = new HashMap<IType,HashMap<Integer,IType>>();
	
	private GlobalScope globalScope;
	private ArrayList<Extension> extensions = new ArrayList<Extension>();
	
	private TypeResolver resolver = new TypeResolver(this);
	
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
	
	public ArrayList<RecordTypeImpl> getRecordTypes() {
		return recordTypes;
	}
	
	public ArrayList<IClass> getClasses() {
		return classes;
	}
	
	public ArrayList<Extension> getExtensionType() {
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
	
	void registerBasicType(BasicType t){
		globalScope.addContent(t.getName(), t);
	}
	
	private void registerRecordType(RecordTypeImpl r){
		recordTypes.add(r);
		registerSimpleType(r);
	}
	
	public void registerStruct(StructDeclNode d, IScope scope) {
		registerRecordType(new IStruct(d,scope));
	}

	public void registerClass(ClassDeclNode d, IScope scope) {
		IClass c;		
		if(d.getTypeParams()!=null){
			c = new GenericClass(d,scope);
		} else {
			c = new IClass(d,scope);
		}
		registerRecordType(c);
		classes.add(c);
	}

	public void registerInterface(InterfaceDeclNode d, IScope scope) {
		registerRecordType(new IInterface(d,scope));
	}
	
	public void registerRootRecord(RecordTypeImpl class1) {
		rootRecordTypes.add(class1);
	}
	
	public void registerTypeAlias(TypeAliasDeclNode typeAlias, IScope scope) {
		//Only added to the alias list, the rest is done later, since we cannot
		//resolve the aliased type at this point (since it could be defined below the alias)
		typeAliases.add(new Pair<TypeAliasDeclNode, IScope>(typeAlias,scope));
	}

	public void registerTypeExtension(TypeExtensionDeclNode typeExtension, IScope scope) {
		
		//Create it
		Extension e = new Extension(typeExtension,scope);
		
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
	public FunctionPointer registerFunctionPointerUsage(FuncPointerDecl funcPointerDecl) {
		Operation af = funcPointerDecl.getFunction();
		FunctionPointer fpt = getFunctionPointerType(af.getSignature(),af.getReturnType());
		fpt.registerUsage(funcPointerDecl);
		return fpt;
	}

	//==========================================
	//			TYPE RESOLVING			
	//==========================================

	public IType resolveType(com.sc2mod.andromeda.syntaxNodes.TypeNode type, IScope scope){
		return type.accept(resolver,scope);
	}
	

	private FunctionPointer getFunctionPointerType(Signature params, IType returnType){
		LinkedHashMap<IType, FunctionPointer> funcs = funcPointers.get(params);
		if(funcs == null){
			funcs = new LinkedHashMap<IType, FunctionPointer>();
			funcPointers.put(params, funcs);
		}
		
		FunctionPointer fp = funcs.get(returnType);
		if(fp == null){
			fp = new FunctionPointer(params,returnType);
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

	private IType getSingleArrayType(IType wrappedType,ExprNode dimension){
		ToDo.println("Array dims check");
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
	
	//==========================================
	//			GARBAGE: DOES NOT BELONG HERE, MOVE OR DELETE			
	//==========================================
	private int curClassIndex = 1;
	private int curInterfaceIndex = 0;
	
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




	public void generateClassAndInterfaceIndex() {
		for(RecordTypeImpl r: rootRecordTypes){
			TypeCategory category = r.getCategory();
			switch(category){
			case CLASS:
				IClass c = (IClass)r;
				c.generateClassIndex(this);
				c.generateImplementsTransClosure();
				break;
			case INTERFACE:
				((IInterface)r).generateInterfaceIndex(this);
				break;
			}
		}
		
	
	}




	public void calcFuncPointerIndices() {
		for(Entry<Signature, LinkedHashMap<IType, FunctionPointer>> fps: funcPointers.entrySet()){
			for(Entry<IType, FunctionPointer> fp : fps.getValue().entrySet()){
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
