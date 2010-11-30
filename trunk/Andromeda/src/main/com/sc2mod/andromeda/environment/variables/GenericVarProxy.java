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

import com.sc2mod.andromeda.environment.ModifierSet;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;
import com.sc2mod.andromeda.vm.data.DataObject;

public class GenericVarProxy extends Variable {

	private Variable wrappedDecl;
	
	private IType type;

	public GenericVarProxy(Variable vd, IType type) {
		this.wrappedDecl = vd;
		this.type = type;
	}

	@Override
	public IType getType() {
		return type;
	}
	
	public void addInitCode(Collection<StmtNode> initCode) {
		wrappedDecl.addInitCode(initCode);
	}

	public boolean doesOverride() {
		return wrappedDecl.doesOverride();
	}

	public boolean equals(Object obj) {
		return wrappedDecl.equals(obj);
	}

	public IType getContainingType() {
		return wrappedDecl.getContainingType();
	}

	public VarDeclNode getDeclarator() {
		return wrappedDecl.getDeclarator();
	}

	public IdentifierNode getDefinition() {
		return wrappedDecl.getDefinition();
	}

	public ScopedElementType getElementType() {
		return wrappedDecl.getElementType();
	}

	public String getElementTypeName() {
		return wrappedDecl.getElementTypeName();
	}

	public String getGeneratedName() {
		return wrappedDecl.getGeneratedName();
	}

	public List<StmtNode> getInitCode() {
		return wrappedDecl.getInitCode();
	}

	public int getNumInlines() {
		return wrappedDecl.getNumInlines();
	}

	public int getNumReadAccesses() {
		return wrappedDecl.getNumReadAccesses();
	}

	public int getNumWriteAccesses() {
		return wrappedDecl.getNumWriteAccesses();
	}

	public IScope getScope() {
		return wrappedDecl.getScope();
	}

	public String getUid() {
		return wrappedDecl.getUid();
	}

	public VarType getVarType() {
		return wrappedDecl.getVarType();
	}

	public Visibility getVisibility() {
		return wrappedDecl.getVisibility();
	}

	public int hashCode() {
		return wrappedDecl.hashCode();
	}

	public boolean isInitedInDecl() {
		return wrappedDecl.isInitedInDecl();
	}

	public boolean isStaticElement() {
		return wrappedDecl.isStaticElement();
	}

	public void registerAccess(boolean write) {
		wrappedDecl.registerAccess(write);
	}

	public void registerInline() {
		wrappedDecl.registerInline();
	}

	public void setGeneratedName(String generatedName) {
		wrappedDecl.setGeneratedName(generatedName);
	}

	public String toString() {
		return wrappedDecl.toString();
	}
	
	@Override
	public DataObject getValue() {
		return wrappedDecl.getValue();
	}

	@Override
	public void setValue(DataObject value) {
		wrappedDecl.setValue(value);
	}
	
	@Override
	public String getDescription() {
		return wrappedDecl.getDescription();
	}
	
	/**
	 * For non-local variables, this returns an index which can
	 * be used to compare which declaration is above which one:
	 * A declaration is above another one if it has a smaller index.
	 */
	@Override
	public int getDeclarationIndex() {
		return wrappedDecl.getDeclarationIndex();
	}
	



	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }

	@Override
	public ModifierSet getModifiers() {
		return wrappedDecl.getModifiers();
	}
}
