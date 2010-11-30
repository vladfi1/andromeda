package com.sc2mod.andromeda.environment.types.casting;

import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeUtil;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.types.basic.BasicTypeSet;
import com.sc2mod.andromeda.environment.types.basic.TypeBool;
import com.sc2mod.andromeda.environment.types.basic.TypeByte;
import com.sc2mod.andromeda.environment.types.basic.TypeChar;
import com.sc2mod.andromeda.environment.types.basic.TypeFixed;
import com.sc2mod.andromeda.environment.types.basic.TypeInt;
import com.sc2mod.andromeda.environment.types.basic.TypeString;
import com.sc2mod.andromeda.environment.types.generic.GenericClassInstance;
import com.sc2mod.andromeda.environment.types.generic.GenericExtensionInstance;
import com.sc2mod.andromeda.environment.types.generic.GenericInterfaceInstance;
import com.sc2mod.andromeda.environment.types.generic.TypeParameter;
import com.sc2mod.andromeda.environment.types.impl.ClassImpl;
import com.sc2mod.andromeda.environment.types.impl.ExtensionImpl;
import com.sc2mod.andromeda.environment.types.impl.InterfaceImpl;
import com.sc2mod.andromeda.environment.visitors.ParameterSemanticsVisitorAdapter;

class CastDecisionVisitorExplicit extends ParameterSemanticsVisitorAdapter<IType, Boolean>{

	private final boolean unchecked;
	public CastDecisionVisitorExplicit(boolean unchecked){
		this.unchecked = unchecked;
	}
	
	
	@Override
	public Boolean visit(BasicType from, IType to) {
		switch(to.getCategory()){
		case BASIC: return to == from; 
		case EXTENSION:
			IType toBase = to.getBaseType();
			return unchecked && toBase == from;
		}
		return false;
	}
	
	@Override
	public Boolean visit(TypeBool from, IType to) {
		switch(to.getCategory()){
		case BASIC: return to == from; 
		case EXTENSION:
			IType toBase = to.getBaseType();
			return unchecked && toBase == from;
		}
		return false;
	}
	
	@Override
	public Boolean visit(TypeByte from, IType to) {
		switch(to.getCategory()){
		case BASIC: return isNumeric(to); 
		case EXTENSION:
			IType toBase = to.getBaseType();
			return unchecked && toBase == from;
		}
		return false;
	}
	
	@Override
	public Boolean visit(TypeChar from, IType to) {
		throw new Error("Not implemented!");
	}
	
	@Override
	public Boolean visit(TypeFixed from, IType to) {
		switch(to.getCategory()){
		case BASIC: return isNumeric(to); 
		case EXTENSION:
			IType toBase = to.getBaseType();
			return unchecked && toBase == from;
		}
		return false;
	}
	
	@Override
	public Boolean visit(TypeInt from, IType to) {
		switch(to.getCategory()){
		case CLASS: return unchecked;
		case TYPE_PARAM: return unchecked;
		case FUNCTION: return unchecked;
		case BASIC: return isNumeric(to); 
		case EXTENSION:
			IType toBase = to.getBaseType();
			return unchecked && toBase == from;
		}
		return false;
	}
	
	private boolean isNumeric(IType t){
		BasicTypeSet bt = t.getTypeProvider().BASIC;
		return t == bt.INT || t == bt.BYTE || t == bt.FLOAT; 
	}
	
	@Override
	public Boolean visit(TypeString from, IType to) {
		switch(to.getCategory()){
		case BASIC: return to == this || to == from.getTypeProvider().BASIC.TEXT;
		case EXTENSION:
			IType toBase = to.getBaseType();
			return unchecked && toBase == this;
		}
		return false;
	}
	
	@Override
	public Boolean visit(TypeParameter from, IType to) {
		if(to==from) return true;
		if(to==from.getTypeProvider().BASIC.INT) return true;
		if(unchecked){
			return intBasedUncheckedCast(to);
		}
		return false;
	}
	
	private boolean intBasedUncheckedCast(IType to){
		switch(to.getCategory()){
		case TYPE_PARAM:
		case CLASS:
		case INTERFACE:
		case FUNCTION:
			return true;
		case BASIC:
		case EXTENSION:
			return to.getBaseType() == to.getTypeProvider().BASIC.INT;
		}
		return false;
	}
	
	@Override
	public Boolean visit(ExtensionImpl from, IType to) {
		if(from.isSubtypeOf(to)){
			return true;
		}
		if(unchecked){
			if(from.getBaseType() == from.getTypeProvider().BASIC.INT){
				return intBasedUncheckedCast(to);
			} else {
				return from.getBaseType() == to.getBaseType();
			}
		} else {
			if(from.getBaseType() == to){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Boolean visit(GenericExtensionInstance from,	IType to) {
		if(from.isSubtypeOf(to)){
			return true;
		}
		BasicType INT = from.getTypeProvider().BASIC.INT;
		if(to==INT) return true;
		if(unchecked){
			if(from.getBaseType() == INT){
				return intBasedUncheckedCast(to);
			} else {
				return from.getBaseType() == to.getBaseType();
			}
		} else {
			if(from.getBaseType() == to){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Boolean visit(ClassImpl from, IType to) {
		if(TypeUtil.isHierarchyShared(from, to)){
			return true;
		}
		if(to==from.getTypeProvider().BASIC.INT) return true;
		if(unchecked){
			return intBasedUncheckedCast(to);
		}
		return false;
	}
	
	@Override
	public Boolean visit(InterfaceImpl from, IType to) {
		if(TypeUtil.isHierarchyShared(from, to)){
			return true;
		}
		if(to==from.getTypeProvider().BASIC.INT) return true;
		if(unchecked){
			return intBasedUncheckedCast(to);
		}
		return false;
	}
	
	@Override
	public Boolean visit(GenericClassInstance from, IType to) {
		if(TypeUtil.isHierarchyShared(from, to)){
			return true;
		}
		if(to==from.getTypeProvider().BASIC.INT) return true;
		if(unchecked){
			return intBasedUncheckedCast(to);
		}
		return false;
	}
	
	@Override
	public Boolean visit(GenericInterfaceInstance from,	IType to) {
		if(TypeUtil.isHierarchyShared(from, to)){
			return true;
		}
		if(to==from.getTypeProvider().BASIC.INT) return true;
		if(unchecked){
			return intBasedUncheckedCast(to);
		} 
		return false;
	}
	

	
	
	
	
	
	@Override
	public Boolean visitDefault(SemanticsElement semanticsElement, IType to) {
		IType from = (IType) semanticsElement;
		
		TypeUtil.isHierarchyShared(from, to);
		return false;
	}
}
