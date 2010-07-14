/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.codegen;

import java.util.ArrayList;
import java.util.Hashtable;

import com.sc2mod.andromeda.environment.Function;
import com.sc2mod.andromeda.environment.Method;
import com.sc2mod.andromeda.environment.types.RecordType;
import com.sc2mod.andromeda.environment.types.Struct;
import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.variables.FieldDecl;
import com.sc2mod.andromeda.environment.variables.FieldOrAccessorDecl;
import com.sc2mod.andromeda.environment.variables.FieldSet;
import com.sc2mod.andromeda.environment.variables.VarDecl;

/**
 * Implementation of the NameProvider interface which
 * tries to create human-readable names that resemble the 
 * names in the Andromeda code.
 * @author J. 'gex' Finis
 *
 */
public class LongNameProvider implements INameProvider {

	private Object token = new Object();
	private Hashtable<String, Object> localNames = new Hashtable<String, Object>();
	
	@Override
	public void assignLocalNamesForMethod(Function function) {
		
		VarDecl[] vars = function.getParams();
		for(int i=0;i<vars.length;i++){
			VarDecl v = vars[i];
			v.setGeneratedName(v.getUid());
		}
		vars = function.getLocals();
		

		localNames.clear();
		
		if(vars!=null){
			for(int i=0;i<vars.length;i++){
				VarDecl v = vars[i];
				if(v.doesOverride()) continue;
				
				//We need to check local vars for duplicate names because the override mechanisms can
				//create duplicates
				String name = v.getUid();
				if(localNames.put(name, token)==token){
					int index = 2;
					String newName;
					do{
						newName = name + index++;
					} while(localNames.put(newName, token)==token);
					name = newName;
				}
						
				v.setGeneratedName(name);
			}
		}
		
	}

	private StringBuilder builder = new StringBuilder(32);
	private int initIndex = 1;
	
	@Override
	public String getFunctionName(Function function) {
		//Reset builder
		builder.setLength(0);
		
		boolean writeIndex = false;
		switch(function.getFunctionType()){
		case Function.TYPE_NATIVE:
			return function.getName();
		case Function.TYPE_STATIC_METHOD:
			builder.append("s_");
		case Function.TYPE_METHOD:
			builder.append(function.getContainingType().getName()).append("__");
		case Function.TYPE_FUNCTION:
			builder.append(function.getName());
			writeIndex = true;
			break;
		case Function.TYPE_CONSTRUCTOR:
			builder.append("new__");
			builder.append(function.getContainingType().getName());
			writeIndex = true;
			break;
		case Function.TYPE_DESTRUCTOR:
			builder.append("delete__");
			builder.append(function.getContainingType().getName());
			break;
		case Function.TYPE_STATIC_INIT:
			builder.append("init___");
			RecordType r = function.getContainingType();
			if(r != null){
				builder.append(function.getContainingType().getName()).append("__");
			} else {
				builder.append("global");
			}
			builder.append(initIndex++);
			
			break;
		default:
			throw new Error("Unknown function type!");
		}
		
		if(writeIndex){
			int index = function.getIndex();
			if(index != 0){
				builder.append("__").append(index);
			}
		}
		return builder.toString();
	}

	@Override
	public String getGlobalName(VarDecl decl) {
		return decl.getUid();
	}


	@Override
	public String getTypeName(Type type) {
		return type.getUid();
	}

	@Override
	public void assignFieldNames(Struct struct) {
		FieldSet f = struct.getFields();
		for(String name: f.getFieldNames()){
			VarDecl field = f.getFieldByName(name);
			field.setGeneratedName(field.getUid());
		}
	}

	@Override
	public String getFieldName(FieldDecl decl, Type clazz) {
		StringBuilder b = new StringBuilder(24);
		if(decl.isStatic()){
			b.append("s_");
			b.append(clazz.getUid());		
			b.append("__");
			b.append(decl.getUid());
		} else {
			ArrayList<FieldDecl> fieldsUsed = decl.getUsedByFields();
			b.append(clazz.getUid());		
			b.append("__");
			b.append(decl.getUid());
			if(fieldsUsed!=null){
				b.append("_mult");
			}
		}

		return b.toString();
	}

	@Override
	public String getGlobalNameRaw(String name) {
		return "A__".concat(name);
	}

	@Override
	public String getLocalNameRaw(String name, int index) {
		return name;
	}

	@Override
	public String getGlobalNameRawNoPrefix(String name) {
		return name;
	}



	
}
