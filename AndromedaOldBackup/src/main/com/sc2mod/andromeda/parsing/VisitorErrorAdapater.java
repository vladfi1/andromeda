/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parsing;

import com.sc2mod.andromeda.syntaxNodes.VisitorAdaptor;

public class VisitorErrorAdapater extends VisitorAdaptor{


	@Override
	public void visit() {
		throw new Error("Unimplemented call!");
	}
}
