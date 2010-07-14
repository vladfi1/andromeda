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



import java.util.HashSet;

import com.sc2mod.andromeda.environment.Scope;
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclaration;

/**
 * Enrichments are no real type, but they nevertheless extend
 * Record type because they share much stuff with it.
 * (Having methods, members, modifiers,...)
 * @author gex
 *
 */
public class Enrichment extends RecordType {

	private Type enrichedType;
	private EnrichDeclaration decl;
	public Enrichment(EnrichDeclaration decl, Scope scope) {
		super(decl,scope);
		this.decl = decl;
		fields = new EnrichmentFieldSet();
	}
	
	public void resolveType(TypeProvider t){
		this.enrichedType = t.resolveType(decl.getEnrichedType());
	}

	//Not important for enrichments
	protected void checkForHierarchyCircle(TypeProvider typeProvider,HashSet<RecordType> marked) {}
	public void checkHierarchy(TypeProvider typeProvider) {}
	protected void resolveExtends(TypeProvider t) {}
	protected void resolveImplements(TypeProvider t) {}
	
	@Override
	void resolveMembers(TypeProvider t) {
		super.resolveMembers(t);
		if(fields.numNonStaticFields()!=0){
			throw new CompilationError(fields.getFieldByName(fields.getFieldNames().iterator().next()).getDefinition(),
					"Enrichments cannot contain fields");
			}
	}

	@Override
	public int getCategory() {
		return ENRICH;
	}

	@Override
	public String getDescription() {
		return "Enrichment";
	}
	
	public Type getEnrichedType() {
		return enrichedType;
	}
	
	@Override
	public String getGeneratedName() {
		return enrichedType.getGeneratedName();
	}
	
	@Override
	public String getName() {
		return enrichedType.getUid();
	}
	
	@Override
	public String getUid() {
		return enrichedType.getUid();
	}
	
	

}
