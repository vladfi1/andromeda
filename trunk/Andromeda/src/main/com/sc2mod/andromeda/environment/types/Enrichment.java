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

import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.EnrichDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

/**
 * Enrichments are no real type, but they nevertheless extend
 * Record type because they share much stuff with it.
 * (Having methods, members, modifiers,...)
 * @author gex
 *
 */
import com.sc2mod.andromeda.environment.IDefined;
import com.sc2mod.andromeda.environment.scopes.BlockScope;
import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.scopes.IScope;
import com.sc2mod.andromeda.environment.visitors.VoidSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.NoResultSemanticsVisitor;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitor;

public class Enrichment extends BlockScope implements IDefined {

	private IType enrichedType;
	private EnrichDeclNode decl;
	public Enrichment(EnrichDeclNode decl, IScope scope) {
		super(scope);
		this.decl = decl;
	}
	
	@Override
	public EnrichDeclNode getDefinition() {
		return decl;
	}
	
	public void setResolvedEnrichedType(IType enricType){
		this.enrichedType = enricType;
	}
	
	
	public IType getEnrichedType() {
		return enrichedType;
	}
	
	

	public void accept(VoidSemanticsVisitor visitor) { visitor.visit(this); }
	public <P> void accept(NoResultSemanticsVisitor<P> visitor,P state) { visitor.visit(this,state); }
	public <P,R> R accept(ParameterSemanticsVisitor<P,R> visitor,P state) { return visitor.visit(this,state); }
}
