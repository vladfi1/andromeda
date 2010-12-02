package com.sc2mod.andromeda.semAnalysis;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.operations.Constructor;
import com.sc2mod.andromeda.environment.operations.Destructor;
import com.sc2mod.andromeda.environment.operations.Function;
import com.sc2mod.andromeda.environment.operations.Method;
import com.sc2mod.andromeda.environment.operations.StaticInit;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.content.ScopeContentSet;
import com.sc2mod.andromeda.environment.types.Enrichment;
import com.sc2mod.andromeda.environment.types.IClass;
import com.sc2mod.andromeda.environment.types.IExtension;
import com.sc2mod.andromeda.environment.types.IInterface;
import com.sc2mod.andromeda.environment.types.INamedType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.types.TypeUtil;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.types.basic.BasicTypeSet;
import com.sc2mod.andromeda.environment.types.generic.TypeParameter;
import com.sc2mod.andromeda.environment.types.impl.ClassImpl;
import com.sc2mod.andromeda.environment.types.impl.ExtensionImpl;
import com.sc2mod.andromeda.environment.types.impl.InterfaceImpl;
import com.sc2mod.andromeda.environment.types.impl.StructImpl;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.GlobalVarDecl;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitorAdapter;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;
import com.sc2mod.andromeda.syntaxNodes.ParameterListNode;
import com.sc2mod.andromeda.syntaxNodes.ParameterNode;
import com.sc2mod.andromeda.syntaxNodes.TypeListNode;
import com.sc2mod.andromeda.syntaxNodes.TypeNode;
import com.sc2mod.andromeda.syntaxNodes.TypeParamListNode;
import com.sc2mod.andromeda.syntaxNodes.TypeParamNode;

/**
 * This semantics visitor resolves the types for a given semantics element.
 * 
 * For classes and interfaces, it resolves and checks the types in the extends/implements clause.
 * 
 * For method it resolves and checks parameters and return type.
 * 
 * Can raise a variety of problems that arise during the resolving and checking.
 * @author gex
 *
 */
public class ResolveAndCheckTypesVisitor extends VoidSemanticsVisitorAdapter {

	private TypeProvider tprov;
	private BasicTypeSet BASIC;
	private static ParamDecl[] NO_PARAMS = new ParamDecl[0];
	
	public ResolveAndCheckTypesVisitor(Environment env) {
		this.tprov = env.typeProvider;
		this.BASIC = tprov.BASIC;
	}
	
	
	//*************************************************************
	//***														***
	//***						VARIABLES						***
	//***														***
	//*************************************************************
	
	/**
	 * Resolves the type of a var decl
	 */
	private void resolveVarType(VarDecl varDecl) {
		TypeNode typeNode = varDecl.getTypeNode();
		
		/*
		 * If this var has no type node, we cannot resolve it.
		 * (This can happen if the var was created with an already resolved type
		 * for example at implicit decls)
		 */
		if(typeNode == null)
			return;
		
		IType t = tprov.resolveType(typeNode, varDecl.getScope(),varDecl.isStaticElement());
		varDecl.setResolvedType(t);
	}
	
	@Override
	public void visit(FieldDecl fieldDecl) {
		resolveVarType(fieldDecl);
	}

	
	@Override
	public void visit(GlobalVarDecl globalVarDecl) {
		resolveVarType(globalVarDecl);
	}
	
	
	
	//*************************************************************
	//***														***
	//***						OPERATIONS						***
	//***														***
	//*************************************************************
	
	
	private void resolveFuncParams(Function function){
		IScope scope = function.getScope();
		ParameterListNode paramList = function.getHeader().getParameters();
		int size = paramList.size();
		IType[] sig = new IType[size];
		ParamDecl[] params = new ParamDecl[size];

		for(int i=0;i<size;i++){
			ParameterNode param = paramList.get(i);
			IType type = tprov.resolveType(param.getType(),scope, function.isStaticElement());
			if(!type.isValidAsParameter()) 
				Problem.ofType(ProblemId.ARRAY_OR_STRUCT_AS_PARAMETER).at(param.getType())
						.raise();
			sig[i] = type;
			params[i] = new ParamDecl(null,type,param.getName());
	
		}
		function.setResolvedParameters(params);
	}
	
	private void resolveReturnType(Function function){
		IType returnType = tprov.resolveType(function.getHeader().getReturnType(),function.getScope(), function.isStaticElement());
		if(!returnType.isValidAsParameter()) 
			Problem.ofType(ProblemId.ARRAY_OR_STRUCT_RETURNED).at(function.getHeader().getReturnType())
						.raise();
		function.setReturnType(returnType);
	}
	
	@Override
	public void visit(Function function) {
		resolveFuncParams(function);
		resolveReturnType(function);
	}
	
	@Override
	public void visit(Method method) {
		resolveFuncParams(method);
		resolveReturnType(method);
	}
	
	@Override
	public void visit(Constructor constructor) {
		resolveFuncParams(constructor);
		constructor.setReturnType(BASIC.VOID);
	}
	
	@Override
	public void visit(Destructor destructor) {
		destructor.setResolvedParameters(NO_PARAMS);
		destructor.setReturnType(BASIC.VOID);
	}
	
	@Override
	public void visit(StaticInit staticInit) {
		staticInit.setReturnType(BASIC.VOID);
		staticInit.setResolvedParameters(NO_PARAMS);
	}

	//*************************************************************
	//***														***
	//***						TYPES & ENRICHMENTS				***
	//***														***
	//*************************************************************
	private void resolveTypeParams(INamedType type, TypeParamListNode paramList) {
		if(paramList == null) return;
		
		int size = paramList.size();
		TypeParameter[] params = new TypeParameter[size];
		ScopeContentSet content = type.getContent();
		for(int i = 0;i<size;i++){
			TypeParamNode paramNode = paramList.get(i);
			//TODO: Fully Implement type bounds
			TypeNode typeBound = paramNode.getTypeBound();
			IType resolvedTypeBound = null;
			if(typeBound != null){
				resolvedTypeBound = tprov.resolveType(typeBound, type,type.isStaticElement());
			}
			TypeParameter param = new TypeParameter(type, paramNode, i, resolvedTypeBound, tprov);
			params[i] = param;
			
			//Add to the type as scope content
			content.add(param.getUid(), param);
		}
		type.setTypeParameters(params);
	}
	
	@Override
	public void visit(StructImpl struct) {
		resolveTypeParams(struct, struct.getDefinition().getTypeParams());
	}
	
	//*************************************************************
	//***														***
	//***						INTERFACES						***
	//***														***
	//*************************************************************

	
	protected void resolveInterfaceExtends(IInterface interfac) {
		TypeListNode tl = interfac.getDefinition().getInterfaces();
		for(TypeNode typeNode: tl){
			IType in = tprov.resolveType(typeNode,interfac,false);
			if(in.getCategory()!=TypeCategory.INTERFACE){
				throw Problem.ofType(ProblemId.INTERFACE_EXTENDING_NON_INTERFACE).at(typeNode)
							.raiseUnrecoverable();
			}
			if(!interfac.getInterfaces().add((IInterface)in)){
				throw Problem.ofType(ProblemId.DUPLICATE_EXTENDS).at(typeNode)
						.raiseUnrecoverable();
			}
		}	
	}
	
	@Override
	public void visit(InterfaceImpl interface1) {
		resolveTypeParams(interface1, interface1.getDefinition().getTypeParams());
		resolveInterfaceExtends(interface1);
	}
	
	


	//*************************************************************
	//***														***
	//***						CLASSES							***
	//***														***
	//*************************************************************
	
	/**
	 * XPilot: enabled extending of generic classes.
	 */
	protected void resolveClassExtends(IClass clazz) {
		IType type = tprov.resolveType(clazz.getDefinition().getSuperClass(),clazz,false);
		if(!TypeUtil.isClass(type))
			throw Problem.ofType(ProblemId.CLASS_EXTENDS_NON_CLASS).at(clazz.getDefinition().getSuperClass())
							.raiseUnrecoverable();
		clazz.setSuperClass((IClass)type);
	}

	protected void resolveClassImplements(IClass clazz) {
		TypeListNode tl = clazz.getDefinition().getInterfaces();
	
		for(TypeNode typeNode : tl){
			IType in = tprov.resolveType(typeNode,clazz,false);
			if(in.getCategory()!=TypeCategory.INTERFACE){
				throw Problem.ofType(ProblemId.CLASS_IMPLEMENTS_NON_INTERFACE).at(typeNode)
						.raiseUnrecoverable();
			}
			if(!clazz.getInterfaces().add((IInterface)in)){
					throw Problem.ofType(ProblemId.DUPLICATE_IMPLEMENTS).at(typeNode)
						.raiseUnrecoverable();
			}
		}		
		if(clazz.getModifiers().isStatic())
			throw Problem.ofType(ProblemId.STATIC_CLASS_HAS_IMPLEMENTS).at(clazz.getDefinition().getInterfaces())
						.raiseUnrecoverable();
	}
	

	@Override
	public void visit(ClassImpl class1) {
		GlobalStructureNode decl = class1.getDefinition();
		//Type parameters
		resolveTypeParams(class1, decl.getTypeParams());
		
		TypeListNode in =decl.getInterfaces();
		if(in!=null&&!in.isEmpty())
			resolveClassImplements(class1);
		if(decl.getSuperClass() != null)
			resolveClassExtends(class1);
	}
	

	//*************************************************************
	//***														***
	//***						EXTENSIONS						***
	//***														***
	//*************************************************************
	
	@Override
	public void visit(ExtensionImpl extension) {
		IType extendedType = tprov.resolveType(extension.getDefinition().getEnrichedType(), extension.getScope(),false);
		BasicType extendedBaseType;
		int hierarchyLevel;
		switch(extendedType.getCategory()){
		case BASIC:
			extendedBaseType = (BasicType)extendedType;
			hierarchyLevel = 0;
			break;
		case EXTENSION:
			IExtension e = ((IExtension)extendedType);
			extendedBaseType = e.getBaseType();
			hierarchyLevel = e.getExtensionHierachryLevel()+1;
			if(extension.isDistinct())
				throw Problem.ofType(ProblemId.DISJOINT_EXTENSION_BASED_ON_EXTENSION).at(extension.getDefinition())
						.raiseUnrecoverable();
			break;
		default:
			throw Problem.ofType(ProblemId.EXTENSION_TYPE_INVALID).at(extension.getDefinition())
						.details(extendedType.getFullName())
						.raiseUnrecoverable();
		}		
				
		//Entry the results into the extension
		extension.setResolvedExtendedType(extendedType,extendedBaseType,hierarchyLevel);
		
		//Type parameters
		resolveTypeParams(extension, extension.getDefinition().getTypeParams());
		
	}
	
	//*************************************************************
	//***														***
	//***						ENRICHMENTS						***
	//***														***
	//*************************************************************

	@Override
	public void visit(Enrichment enrichment) {
		//Resolve type only if the enrichment does not yet have an enriched type.
		//(Enrichments attached to type extensions already have their type resolved)
		if(enrichment.getEnrichedType()==null){
			IType enrichedType = tprov.resolveType(enrichment.getDefinition().getEnrichedType(), enrichment.getParentScope(),false);
			enrichment.setResolvedEnrichedType(enrichedType);
		}
	}
}
