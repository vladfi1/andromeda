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

import java.util.Collection;
import java.util.List;

import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;
import com.sc2mod.andromeda.vm.data.DataObject;

import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class GenericVarProxy extends VarDecl {

	private VarDecl wrappedDecl;
	private Type type;

	public GenericVarProxy(VarDecl vd, Type type) {
		this.wrappedDecl = vd;
		this.type = type;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public int getDeclType() {
		return wrappedDecl.getDeclType();
	}

	@Override
	public boolean isInitDecl() {
		return wrappedDecl.isInitDecl();
	}

	@Override
	public void addInitCode(Collection<StmtNode> initCode) {
		wrappedDecl.addInitCode(initCode);
	}

	@Override
	public boolean doesOverride() {
		return wrappedDecl.doesOverride();
	}

	@Override
	public Type getContainingType() {
		return wrappedDecl.getContainingType();
	}

	@Override
	public VarDeclNode getDeclarator() {
		return wrappedDecl.getDeclarator();
	}

	@Override
	public SyntaxNode getDefinition() {
		return wrappedDecl.getDefinition();
	}

	@Override
	public String getGeneratedName() {
		return wrappedDecl.getGeneratedName();
	}

	@Override
	public int getIndex() {
		return wrappedDecl.getIndex();
	}

	@Override
	public List<StmtNode> getInitCode() {
		return wrappedDecl.getInitCode();
	}

	@Override
	public ModifierListNode getModifiers() {
		return wrappedDecl.getModifiers();
	}

	@Override
	public int getNumInlines() {
		return wrappedDecl.getNumInlines();
	}

	@Override
	public int getNumReadAccesses() {
		return wrappedDecl.getNumReadAccesses();
	}

	@Override
	public int getNumWriteAccesses() {
		return wrappedDecl.getNumWriteAccesses();
	}

	@Override
	public LocalVarDecl getOverride() {
		return wrappedDecl.getOverride();
	}

	@Override
	public String getUid() {
		return wrappedDecl.getUid();
	}

	@Override
	public DataObject getValue() {
		return wrappedDecl.getValue();
	}

	@Override
	public Visibility getVisibility() {
		return wrappedDecl.getVisibility();
	}

	@Override
	public boolean isAbstract() {
		return wrappedDecl.isAbstract();
	}

	@Override
	public boolean isAccessor() {
		return wrappedDecl.isAccessor();
	}

	@Override
	public boolean isConst() {
		return wrappedDecl.isConst();
	}

	@Override
	public boolean isCreateCode() {
		return wrappedDecl.isCreateCode();
	}

	@Override
	public boolean isFinal() {
		return wrappedDecl.isFinal();
	}

	@Override
	public boolean isGlobalField() {
		return wrappedDecl.isGlobalField();
	}

	@Override
	public boolean isMember() {
		return wrappedDecl.isMember();
	}

	@Override
	public boolean isNative() {
		return wrappedDecl.isNative();
	}

	@Override
	public boolean isOverride() {
		return wrappedDecl.isOverride();
	}

	@Override
	public boolean isStatic() {
		return wrappedDecl.isStatic();
	}

	@Override
	public void override(LocalVarDecl overridden) {
		wrappedDecl.override(overridden);
	}

	@Override
	public void registerAccess(boolean write) {
		wrappedDecl.registerAccess(write);
	}

	@Override
	public void registerInline() {
		wrappedDecl.registerInline();
	}

	@Override
	public void setCreateCode(boolean createCode) {
		wrappedDecl.setCreateCode(createCode);
	}

	@Override
	public void setGeneratedName(String generatedName) {
		wrappedDecl.setGeneratedName(generatedName);
	}

	@Override
	public void setValue(DataObject value) {
		wrappedDecl.setValue(value);
	}

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
