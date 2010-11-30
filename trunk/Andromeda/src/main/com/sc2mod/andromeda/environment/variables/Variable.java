package com.sc2mod.andromeda.environment.variables;

import java.util.Collection;
import java.util.List;

import com.sc2mod.andromeda.environment.IDefined;
import com.sc2mod.andromeda.environment.IIdentifiable;
import com.sc2mod.andromeda.environment.IModifiable;
import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.scopes.IScopedElement;
import com.sc2mod.andromeda.environment.scopes.ScopedElementType;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.syntaxNodes.IdentifierNode;
import com.sc2mod.andromeda.syntaxNodes.StmtNode;
import com.sc2mod.andromeda.syntaxNodes.VarDeclNode;
import com.sc2mod.andromeda.vm.data.DataObject;

public abstract class Variable implements SemanticsElement, IScopedElement, IIdentifiable, IDefined, IModifiable{

	public abstract IType getType();
	
	@Override
	public ScopedElementType getElementType(){
		return ScopedElementType.VAR;
	}
	
	@Override
	public abstract IdentifierNode getDefinition();

	public abstract boolean isInitedInDecl();
	
	public abstract String getGeneratedName();

	public abstract void setGeneratedName(String generatedName);
	
	public abstract VarType getVarType();
	
	//TODO: Refactor to access stats?
	public abstract void registerAccess(boolean write);
	public abstract void registerInline();
	public abstract int getNumReadAccesses();
	public abstract int getNumWriteAccesses();
	public abstract int getNumInlines();
	
	public abstract VarDeclNode getDeclarator();

	
	public abstract List<StmtNode> getInitCode();
	public abstract void addInitCode(Collection<StmtNode> initCode);
	
	//TODO: Rename
	public abstract boolean doesOverride();
	
	public abstract DataObject getValue();
	public abstract void setValue(DataObject value);

	public abstract String getDescription();
	
	public abstract int getDeclarationIndex();
	
}
