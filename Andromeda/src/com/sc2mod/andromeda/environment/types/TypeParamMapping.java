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

import java.util.Map.Entry;

import com.sc2mod.andromeda.environment.Signature;

public class TypeParamMapping {

	private final ParamMapEntry[] entries;
	
	private static class ParamMapEntry{
		public final Type from;
		public final Type to;
		public ParamMapEntry(Type from, Type to) {
			super();
			this.from = from;
			this.to = to;
		}
	}
	
	public TypeParamMapping(Type[] from, Signature to){
		int size = from.length;
		entries = new ParamMapEntry[size];
		for(int i=0;i<size;i++){
			entries[i] = new ParamMapEntry(from[i], to.get(i));
		}
	}
	
	public int size(){
		return entries.length;
	}

	public Type getReplacement(Type typeParameter) {
		int size = entries.length;
		for(int i=0;i < size; i++){
			if(entries[i].from == typeParameter){
				//Hit, replace
				return entries[i].to;
			}
		}
		//No hit, do not replace
		return typeParameter;
	}
}
