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

import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.TypeExtensionDeclNode;
import com.sc2mod.andromeda.vm.data.DataObject;
import com.sc2mod.andromeda.vm.data.IntObject;
import com.sc2mod.andromeda.vm.data.StringObject;

import com.sc2mod.andromeda.environment.IDefined;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.types.generic.GenericExtensionInstance;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public interface IExtension extends INamedType{
	
	@Override
	public BasicType getBaseType();
	
	public void setResolvedExtendedType(IType extendedType2,
			BasicType extendedBaseType2, int hierarchyLevel2);


	public boolean isDistinct();

	public boolean isKey();
	
	@Override
	public TypeExtensionDeclNode getDefinition();
	
	public int getExtensionHierachryLevel();


}
