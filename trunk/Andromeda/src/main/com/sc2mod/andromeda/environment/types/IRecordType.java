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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.sc2mod.andromeda.environment.IAnnotatable;
import com.sc2mod.andromeda.environment.IGlobal;
import com.sc2mod.andromeda.environment.IModifiable;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.Util;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.scopes.Visibility;
import com.sc2mod.andromeda.environment.types.generic.TypeParameter;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.AnnotationNode;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureNode;

/**
 * A class or interface.
 * @author J. 'gex' Finis
 */
public interface IRecordType extends INamedType, IModifiable, IGlobal, IAnnotatable {
	
	@Override
	abstract GlobalStructureNode getDefinition();

	LinkedList<IRecordType> getDescendants();
	
	int getNumNonStatics();

	int getNumStatics();

	LinkedList<IRecordType> getDecendants();
	
	TypeParameter[] getTypeParams();
	
	boolean isInstanceof(IClass curClass);
	
	int calcByteSize();
}
