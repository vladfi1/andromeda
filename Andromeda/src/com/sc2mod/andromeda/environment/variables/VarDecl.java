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
import com.sc2mod.andromeda.environment.Util;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.syntaxNodes.Modifiers;
import com.sc2mod.andromeda.syntaxNodes.Statement;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclarator;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclaratorId;
import com.sc2mod.andromeda.vm.data.DataObject;

/**
 * Semantics representation of any declared variable
 * (Global, Field, Accessor, Local, Parameter)
 * @author J. 'gex' Finis
 *
 */
public abstract class VarDecl extends SemanticsElement implements IIdentifiable, IDefined, IModifiable{

	public static final int TYPE_LOCAL = 1;
	public static final int TYPE_FIELD = 2;
	public static final int TYPE_GLOBAL = 3;
	public static final int TYPE_STATIC_FIELD = 4;
	public static final int TYPE_PARAMETER = 5;
	public static final int TYPE_ACCESSOR = 6;
	public static final int TYPE_STATIC_ACCESSOR = 7;
	public static final int TYPE_STATIC = 8;
	public static final int TYPE_FUNCTION_POINTER = 9;
	public static final int TYPE_IMPLICIT = 10;
	
	private VariableDeclaratorId declaration;
	private com.sc2mod.andromeda.syntaxNodes.Type syntaxType;
	protected Type type;
	private String generatedName;
	private String name;
	private Modifiers mods;
	private DataObject value;
	protected LocalVarDecl overrides;
	private int numReads;
	private int numWrites;
	private int numInlines;
	private boolean marked;
	private boolean createCode = true;
	private List<Statement> initCode;
	
	
	
	
	public List<Statement> getInitCode() {
		return initCode;
	}

	public void addInitCode(Collection<Statement> initCode) {
		if(this.initCode==null){
			this.initCode = new ArrayList<Statement>(initCode.size());
		}
		this.initCode.addAll(initCode);
	}

	public boolean isCreateCode() {
		return createCode;
	}

	public void setCreateCode(boolean createCode) {
		this.createCode = createCode;
	}

	public abstract int getDeclType();
	
	public void resolveType(TypeProvider t){
		this.type = t.resolveType(syntaxType);
		declaration.setInferedType(this.type);
	}
	
	public Modifiers getModifiers(){
		return mods;
	}

	public String getGeneratedName() {
		return generatedName==null?getUid():generatedName;
	}

	public void setGeneratedName(String generatedName) {
		this.generatedName = generatedName;
	}
	
	public VarDecl(Modifiers mods,Type type,VariableDeclaratorId def) {
		this.declaration = def;
		this.name = def.getName();
		this.mods = mods;
		this.type = type;
		declaration.setInferedType(type);
		def.setSemantics(this);
		Util.processModifiers(this, mods);
	}
	
	public VarDecl(Modifiers mods,com.sc2mod.andromeda.syntaxNodes.Type type,VariableDeclaratorId def) {
		this.declaration = def;
		this.name = def.getName();
		this.mods = mods;
		this.syntaxType = type;
		def.setSemantics(this);
		Util.processModifiers(this, mods);
	}
	
	public VarDecl() {
		//Constructor only for special var decls used by the name resolver
	}

	@Override
	public String getUid() {
		return name;
	}

	@Override
	public SyntaxNode getDefinition() {
		return declaration;
	}
	
	public Type getType(){
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
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
	

	public boolean isAccessor(){
		return false;
	}

	@Override
	public int getVisibility() {return 0;}
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
	public void setAbstract() {throw new CompilationError(mods,"This definition cannot be declared 'abstract'");}
	@Override
	public void setConst() {throw new CompilationError(mods,"This definition cannot be declared 'const'");}
	@Override
	public void setFinal() {throw new CompilationError(mods,"This definition cannot be declared 'final'");}
	@Override
	public void setNative() {throw new CompilationError(mods,"Variables cannot be declared 'override'");}
	@Override
	public void setOverride() {throw new CompilationError(mods,"Variables cannot be declared 'override'");}
	@Override
	public void setStatic() {throw new CompilationError(mods,"This definition cannot be declared 'static'");}
	@Override
	public void setVisibility(int visibility) {throw new CompilationError(mods,"This variable declaration cannot have a visibility modifier");}
	
	public DataObject getValue() {
		return this.value;
	}
	
	public void setValue(DataObject value) {
		this.value = value;
	}
	
	public int getIndex(){
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
	
	public boolean isMarked() {
		return marked;
	}

	public void mark() {
		this.marked = true;
	}

	public RecordType getContainingType(){
		return null;
	}
	
	public abstract boolean isInitDecl();

	public VariableDeclarator getDeclarator() {
		return null;
	}

	public boolean isMember() {
		return false;
	}
}
