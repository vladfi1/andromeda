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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.sc2mod.andromeda.environment.MethodSet;
import com.sc2mod.andromeda.environment.Signature;
import com.sc2mod.andromeda.environment.operations.Operation;
import com.sc2mod.andromeda.environment.operations.Method;
import com.sc2mod.andromeda.environment.scopes.FileScope;
import com.sc2mod.andromeda.environment.variables.FieldOrAccessorDecl;
import com.sc2mod.andromeda.environment.variables.FieldSet;
import com.sc2mod.andromeda.environment.variables.VarDecl;
import com.sc2mod.andromeda.syntaxNodes.MethodInvocationExprNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

@Deprecated
public class EnrichmentSet {

	private ArrayList<Enrichment> enrichments = new ArrayList<Enrichment>(2);
	private EnrichmentMethodSet methods;
	private FieldSet accessors = new FieldSet();
	private Type forType;
	
	public EnrichmentSet(Type forType) {
		this.forType = forType;
		methods = new EnrichmentMethodSet(forType);
	}


	public void addEnrichment(Enrichment e) {
		enrichments.add(e);
		((EnrichmentFieldSet) e.fields).setEnrichmentSet(this);
	}
	
	public void addField(VarDecl f){
		if(!f.isAccessor())
			accessors.addField(f);
	}
	
	public void resolveEnrichmentMethods(){
		for(Enrichment e: enrichments){
			MethodSet m = e.getMethods();
			HashMap<String, LinkedHashMap<Signature, Operation>> mt = m.getMethodTable();
			for(String s : mt.keySet()){
				LinkedHashMap<Signature, Operation> meths = mt.get(s);
				for(Signature sig: meths.keySet()){
					Operation meth = meths.get(sig);
					methods.addMethod(meth);
				}
			}
			
			FieldSet f = e.getFields();
			for(String s: f.getFieldNames()){
				VarDecl fd = f.getFieldByName(s);
				if(fd.isAccessor())
					accessors.addField(f.getFieldByName(s));
			}
		}
	}

	
	public VarDecl getFieldByName(String name, RecordType containingType, FileScope scope){
		return accessors.getFieldByName(name);
	
	}
	
	public Operation resolveMethodInvocation(FileScope scope, SyntaxNode where, String name, Signature sig, RecordType containingType){
		return methods.resolveInvocation(scope, where, name, sig);
	}


	public EnrichmentMethodSet getMethods() {
		return methods;
	}

}
