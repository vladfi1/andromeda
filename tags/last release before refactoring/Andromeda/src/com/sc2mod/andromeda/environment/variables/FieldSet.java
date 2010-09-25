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
import java.util.Set;
import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Visibility;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeParamMapping;
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.parsing.AndromedaReader;

public class FieldSet implements Cloneable{
	
	private LinkedHashMap<String, VarDecl> fields = new LinkedHashMap<String, VarDecl>();
	private int numNonStaticFields;
	private int numInits;
	private ArrayList<FieldDecl> myNonStaticFields;
	private ArrayList<FieldDecl> myStaticFields;
	private int numStaticFields;
	
	public FieldSet(){
		myNonStaticFields = new ArrayList<FieldDecl>();
		myStaticFields = new ArrayList<FieldDecl>();
	}
	
	public FieldSet getAlteredFieldSet(TypeParamMapping replace){
		FieldSet result = null;
		try {
			result = (FieldSet)this.clone();
		} catch (CloneNotSupportedException e) {}
		
		result.fields = new LinkedHashMap<String, VarDecl>();
		for(Entry<String, VarDecl> e: fields.entrySet()){
			VarDecl vd = e.getValue();
			Type t = vd.getType();
			Type t2 = t.replaceTypeParameters(replace);
			
			if(t != t2){
				//Type has changed we need a generic proxy
				result.fields.put(e.getKey(), new GenericVarProxy(vd,t2));
			} else {
				//Type has not changed, just use the old field
				result.fields.put(e.getKey(), vd);
			}
		}
		return result;
		
	}
	
	public ArrayList<FieldDecl> getNonStaticClassFields(){
		return myNonStaticFields;
	}
	
	public ArrayList<FieldDecl> getStaticClassFields(){
		return myStaticFields;
	}
	
	public Set<String> getFieldNames(){
		return fields.keySet();
	}

	public void addField(VarDecl f){
		addField(f, true);
	}
	
	protected void addField(VarDecl f, boolean addToMyFields) {
		String uid = f.getUid();
		VarDecl old = fields.put(uid, f);

		if(!f.isAccessor()&&addToMyFields){
			if(f.isStatic()){
				numStaticFields++;
				myStaticFields.add((FieldDecl) f);
			} else {
				if(f.isInitDecl())
					numInits++;
				numNonStaticFields++;
				myNonStaticFields.add((FieldDecl) f);
			}
		}
		
		// No duplicate field? Everything fine!
		if (old == null)
			return;
		
		// Both fields defined in the same type? Fail!
		if (old.getContainingType() == f.getContainingType()) 
			throw new CompilationError(f.getDefinition(),old
					.getDefinition(),
					"Duplicate field!","First Definition");		
		
		
		//Accessor overriding non-accessor? Not allowed
		if((old.isAccessor()&&!f.isAccessor())||(!old.isAccessor()&&f.isAccessor()))
			throw new CompilationError(f.getDefinition(),old
					.getDefinition(),
					"Accessors cannot override real fields and vice versa.","Overridden");	
		
		//Shadowing permitted, however, maybe we have to exchange the fields
		if(!addToMyFields)
			fields.put(uid, old);
	}

	public void addInheritedFields(FieldSet m) {
		for (String fieldName : m.fields.keySet()) {
			VarDecl f = m.fields.get(fieldName);
			
			//Private fields are not inherited
			if(f.getVisibility()==Visibility.PRIVATE) continue;
			addField(f,false);
		}
	}
	
	public VarDecl getFieldByName(String name){
		return fields.get(name);
	}

	public int size() {
		return fields.size();
	}
	
	public int numNonStaticFields(){
		return numNonStaticFields;
	}
	
	public int numNonStaticInits(){
		return numInits;
	}

	public int numStaticFields() {
		return numStaticFields;
	}
}
