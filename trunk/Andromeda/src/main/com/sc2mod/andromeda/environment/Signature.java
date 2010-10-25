/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.environment;

import com.sc2mod.andromeda.environment.types.Type;
import com.sc2mod.andromeda.environment.types.TypeParamMapping;
import com.sc2mod.andromeda.environment.types.TypeParameter;
import com.sc2mod.andromeda.environment.variables.ParamDecl;
import com.sc2mod.andromeda.syntaxNodes.ExprListNode;

public class Signature {

	public static final Signature EMPTY_SIGNATURE = new Signature(new ExprListNode());
	
	private Type[] sig;
	private int hashCode;
	private byte containsTypeParams;
	
	private void calcHashcode(){
		for(int i=sig.length-1;i>=0;i--){
			hashCode += sig[i].hashCode()<<(i<<3);
		}
	}
	
	
	public Signature(Type... sig) {
		this.sig = sig;
		calcHashcode();
	}
	
	public Signature(ExprListNode e){
		int size = e.size();
		sig = new Type[size];
		for(int i=size-1;i>=0;i--){
			sig[i] = e.elementAt(i).getInferedType();
		}
		calcHashcode();
	}
	
	/**
	 * Constructs a signature from a list of parameters
	 * @param params
	 */
	public Signature(ParamDecl[] params) {
		int size = params.length;
		sig = new Type[size];
		for(int i=size-1;i>=0;i--){
			sig[i] = params[i].getType();
		}
		calcHashcode();
	}


	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		//Same instance?
		if(obj==this) return true;
		if(obj instanceof Signature){
			//Same length?
			Signature s = (Signature)obj;
			Type[] otherSig = s.sig;
			if(otherSig.length!= sig.length) return false;
			
			//Parameters differ?
			for(int i=otherSig.length-1;i>=0;i--){
				if(sig[i]!=otherSig[i]) return false;
			}
			
			//Same sig
			return true;
		}
		return false;
	}
	
	public boolean canImplicitCastTo(Signature castTo){
		Type[] otherSig = castTo.sig;
		int size = otherSig.length;
		Type[] sig = this.sig;
		if(size!=sig.length) return false;
		
		for(int i=size-1;i>=0;i--){
			if(!sig[i].canImplicitCastTo(otherSig[i])) return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(64);
		for(int i=0;i<sig.length;i++){
			b.append(sig[i].getUid());
			if(i+1!=sig.length) b.append(",");
		}
		return b.toString();
	}

	public int size() {
		return sig.length;
	}

	public String getFullName() {
		StringBuilder b = new StringBuilder(64);
		for(int i=0;i<sig.length;i++){
			b.append(sig[i].getFullName());
			if(i+1!=sig.length) b.append(",");
		}
		return b.toString();
	}

	public Type[] getTypeArrayCopy() {
		Type[] result = new Type[sig.length];
		System.arraycopy(sig, 0, result, 0, sig.length);
		return result;
	}

	public Type get(int j) {
		return sig[j];
	}

	public boolean isEmpty() {
		return sig.length == 0;
	}

	public boolean containsTypeParams() {
		if(containsTypeParams>0){
			return containsTypeParams == 1;
		}
		int size = sig.length;
		for(int i=0;i<size;i++){
			if(sig[i].containsTypeParams()){
				containsTypeParams = 1;
				return true;
			}
		}
		containsTypeParams = 2;
		return false;
	}

	public Signature replaceTypeParameters(TypeParamMapping paramMap) {
		if(!containsTypeParams()) return this;
		Type[] result = getTypeArrayCopy();
		
		int size = size();
		for(int i=0;i<size;i++){
			Type t = result[i].replaceTypeParameters(paramMap);			
			result[i] = t;
		}
		Signature sig = new Signature(result);
		return sig;
	}
	
	/**
	 * XPilot: Added for type bounds (future)
	 */
	public boolean fits(Signature bounds) {
		//TODO: implement TypeParameter bounds!
		return equals(bounds);
	}
}
