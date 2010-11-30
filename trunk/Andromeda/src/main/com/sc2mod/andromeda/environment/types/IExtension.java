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

import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.syntaxNodes.TypeExtensionDeclNode;

public interface IExtension extends IDeclaredType{
	
	@Override
	public BasicType getBaseType();
	
	public void setResolvedExtendedType(IType extendedType2,
			BasicType extendedBaseType2, int hierarchyLevel2);


	public boolean isDistinct();
	
	@Override
	public TypeExtensionDeclNode getDefinition();
	
	public int getExtensionHierachryLevel();


}
