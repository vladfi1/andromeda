/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.util.visitors;

import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.syntaxNodes.util.VoidVisitorAdapter;

public class VoidVisitorErrorAdapater extends VoidVisitorAdapter{


	@Override
	public void visitDefault(SyntaxNode s) {
		throw new InternalProgramError(s,"Unimplemented method: Visitor " + this.getClass().getSimpleName() +
				" does not define visit for class " + s.getClass().getSimpleName());
	}
}
