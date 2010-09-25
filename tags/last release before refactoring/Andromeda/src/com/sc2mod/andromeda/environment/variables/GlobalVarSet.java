/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment.variables;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.sc2mod.andromeda.environment.Scope;
import com.sc2mod.andromeda.environment.Visibility;
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.parsing.AndromedaReader;
import com.sc2mod.andromeda.syntaxNodes.GlobalVarDeclaration;
import com.sc2mod.andromeda.syntaxNodes.VariableDeclarators;

public class GlobalVarSet {

	private LinkedHashMap<String, ArrayList<GlobalVarDecl>> variables = new LinkedHashMap<String, ArrayList<GlobalVarDecl>>();

	public void add(GlobalVarDeclaration globalVarDeclaration, Scope scope) {
		VariableDeclarators vars = globalVarDeclaration.getFieldDecl().getDeclaredVariables();
		int size = vars.size();
		for(int i=0;i<size;i++){
			GlobalVarDecl v = new GlobalVarDecl(globalVarDeclaration.getFieldDecl(),i,scope);
			ArrayList<GlobalVarDecl> list = variables.get(v.getUid());
			if(list==null){
				list = new ArrayList<GlobalVarDecl>();
				list.add(v);
				variables.put(v.getUid(), list);
			} else {
				for(GlobalVarDecl v2: list){
					if(v2.getScope().equals(v.getScope())||(v.getVisibility()!=Visibility.PRIVATE&&v2.getVisibility()!=Visibility.PRIVATE)){
						
						throw new CompilationError(vars.elementAt(i).getName(),v2.getDefinition(), "Duplicate global variable " + v.getUid(),"First Definition");
					}
				}
				list.add(v);
			}
			
		}
	}

	public GlobalVarDecl getVarByName(String name, Scope scope){
		ArrayList<GlobalVarDecl> l = variables.get(name);
		if(l == null) return null;
		GlobalVarDecl candidate = null;
		for(GlobalVarDecl g: l){
			//Private vars of the same scope override non private vars
			if(g.getVisibility()!=Visibility.PRIVATE)candidate = g;
			else if(g.getScope().equals(scope)) return g;
		}
		return candidate;
	}

	public LinkedHashMap<String, ArrayList<GlobalVarDecl>> getVarSet() {
		return variables;
	}


	
	
}
