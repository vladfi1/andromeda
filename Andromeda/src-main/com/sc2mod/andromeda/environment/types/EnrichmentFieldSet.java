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

import com.sc2mod.andromeda.environment.variables.FieldOrAccessorDecl;
import com.sc2mod.andromeda.environment.variables.FieldSet;
import com.sc2mod.andromeda.environment.variables.VarDecl;

public class EnrichmentFieldSet extends FieldSet {

	private EnrichmentSet enrichmentSet;

	public void setEnrichmentSet(EnrichmentSet enrichmentSet) {
		this.enrichmentSet = enrichmentSet;
	}


	@Override
	protected void addField(VarDecl f, boolean addToMyFields){
		super.addField(f,addToMyFields);
		enrichmentSet.addField(f);
	}
	
	public void addField(VarDecl f){
		addField(f, true);
	}
}
