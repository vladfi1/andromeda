/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.variables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sc2mod.andromeda.environment.IDefined;
import com.sc2mod.andromeda.environment.IIdentifiable;
import com.sc2mod.andromeda.environment.IModifiable;
import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.ModifierUtil;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.TypeNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;
import com.sc2mod.andromeda.vm.data.DataObject;

/**
 * Semantics representation of any declared variable
 * (Global, Field, Accessor, Local, Parameter)
 * @author J. 'gex' Finis
 *
 */
public abstract class VarDecl extends Variable{

	protected Visibility visibility = Visibility.DEFAULT;	
	private IdentifierNode declaration;
	private TypeNode typeNode;
	protected IType type;
	private String generatedName;
	private String name;
	private ModifierListNode mods;
	private DataObject value;
	protected LocalVarDecl overrides;
	private IScope scope;
	private int numReads;
	private int numWrites;
	private int numInlines;
	private boolean createCode = true;
	private List<StmtNode> initCode;
	
	private VarDecl(ModifierListNode mods, IdentifierNode def, IScope scope){
		this.declaration = def;
		this.name = def.getId();
		this.mods = mods;
		this.scope = scope;
		def.setSemantics(this);
		ModifierUtil.processModifiers(this, mods);
	}
	
	public VarDecl(ModifierListNode mods,IType type,IdentifierNode def, IScope scope) {
		this(mods,def,scope);
		setResolvedType(type);
	}
	
	public VarDecl(ModifierListNode mods,TypeNode type,IdentifierNode def, IScope scope) {
		this(mods,def,scope);
		this.typeNode = type;
	}
	
	@Override
	public IdentifierNode getDefinition() {
		return declaration;
	}
	
	public TypeNode getTypeNode() {
		return typeNode;
	}
	
	@Override
	public IScope getScope() {
		return scope;
	}
	
	public List<StmtNode> getInitCode() {
		return initCode;
	}

	public void addInitCode(Collection<StmtNode> initCode) {
		if(this.initCode==null){
			this.initCode = new ArrayList<StmtNode>(initCode.size());
		}
		this.initCode.addAll(initCode);
	}

	public boolean isCreateCode() {
		return createCode;
	}

	public void setCreateCode(boolean createCode) {
		this.createCode = createCode;
	}

	//public abstract int getDeclType();
	
	/**
	 * Called by the resolve and check visitor when
	 * the type is resolved to entry it.
	 * @param t
	 */
	public void setResolvedType(IType t){
		this.type = t;
		if(declaration != null)
			declaration.setInferedType(t);
	}

	public String getGeneratedName() {
		return generatedName==null?getUid():generatedName;
	}

	public void setGeneratedName(String generatedName) {
		this.generatedName = generatedName;
	}
	
	@Override
	public String getUid() {
		return name;
	}

	@Override
	public IType getType(){
		return type;
	}
	
	public void override(LocalVarDecl overridden){
		if(overridden.overrides!=null) overrides = overridden.overrides;
		else overrides = overridden;
	}
	
	public LocalVarDecl getOverride(){
		return overrides;
	}
	
	public boolean doesOverride(){
		return overrides!=null;
	}
	

	@Override
	public Visibility getVisibility() {return visibility; }
	@Override
	public boolean isAbstract() {return false;}
	@Override
	public boolean isConst() {return false;}
	@Override
	public boolean isFinal() {return false;}
	@Override
	public boolean isNative() {return false;}
	@Override
	public boolean isOverride() {return false;}
	@Override
	public boolean isStatic() {return false;}
	
	@Override
	public void setAbstract() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(mods)
				.details("This definition","abstract")
				.raiseUnrecoverable();
	}
	@Override
	public void setConst() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(mods)
		.details("This definition","const")
		.raiseUnrecoverable();
}
	@Override
	public void setFinal() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(mods)
		.details("This definition","final")
		.raiseUnrecoverable();
}
	@Override
	public void setNative() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(mods)
		.details("Variables","native")
		.raiseUnrecoverable();
}
	@Override
	public void setOverride(){
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(mods)
		.details("Variables","override")
		.raiseUnrecoverable();
	}
	@Override
	public void setStatic() {
		throw Problem.ofType(ProblemId.INVALID_MODIFIER).at(mods)
		.details("This definition","static")
		.raiseUnrecoverable();
	}
	@Override
	public void setVisibility(Visibility visibility){
		throw Problem.ofType(ProblemId.INVALID_VISIBILITY_MODIFIER).at(mods)
		.details("This definition")
		.raiseUnrecoverable();
	}
	
	public DataObject getValue() {
		return this.value;
	}
	
	public void setValue(DataObject value) {
		this.value = value;
	}
	
	public int getDeclarationIndex(){
		return -1;
	}

	public void registerAccess(boolean write) {
		if(write){
			numWrites++;
		} else {
			numReads++;
		}
	}
	
	public void registerInline(){
		numInlines++;
	}
	
	public int getNumReadAccesses(){
		return numReads;
	}
	
	public int getNumWriteAccesses(){
		return numWrites;
	}
	
	public int getNumInlines(){
		return numInlines;
	}
	
	public boolean isGlobalField(){
		return false;
	}

	/**
	 * The type to which this variable belongs, if it is a field or accessor,
	 * or null for global variables and locals.
	 * 
	 * Any type is possible, not only record types, since an enrichment can
	 * even make a native type contain accessors.
	 * @return the containing type
	 */
	public IType getContainingType(){
		return null;
	}
	
	public abstract boolean isInitedInDecl();

	public VarDeclNode getDeclarator() {
		return null;
	}

	public boolean isMember() {
		return false;
	}
	
	@Override
	public String getElementTypeName() {
		return "field";
	}
}
