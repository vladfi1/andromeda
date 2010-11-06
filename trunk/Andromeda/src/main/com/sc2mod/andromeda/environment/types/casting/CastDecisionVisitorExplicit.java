package com.sc2mod.andromeda.environment.types.casting;

import com.sc2mod.andromeda.environment.SemanticsElement;
import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.TypeCategory;
import com.sc2mod.andromeda.environment.types.TypeUtil;
import com.sc2mod.andromeda.environment.types.basic.BasicType;
import com.sc2mod.andromeda.environment.types.basic.TypeBool;
import com.sc2mod.andromeda.environment.types.basic.TypeByte;
import com.sc2mod.andromeda.environment.types.basic.TypeChar;
import com.sc2mod.andromeda.environment.types.basic.TypeFixed;
import com.sc2mod.andromeda.environment.types.basic.TypeInt;
import com.sc2mod.andromeda.environment.types.basic.TypeString;
import com.sc2mod.andromeda.environment.types.basic.TypeText;
import com.sc2mod.andromeda.environment.types.generic.GenericClassInstance;
import com.sc2mod.andromeda.environment.types.generic.GenericExtensionInstance;
import com.sc2mod.andromeda.environment.types.generic.GenericInterfaceInstance;
import com.sc2mod.andromeda.environment.types.generic.GenericStructInstance;
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
		return t == BasicType.INT || t == BasicType.BYTE || t == BasicType.FLOAT; 
	}
	
	@Override
	public Boolean visit(TypeString from, IType to) {
		switch(to.getCategory()){
		case BASIC: return to == this || to == BasicType.TEXT;
		case EXTENSION:
			IType toBase = to.getBaseType();
			return unchecked && toBase == this;
		}
		return false;
	}
	
	@Override
	public Boolean visit(ExtensionImpl extensionImpl, IType state) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}
	
	@Override
	public Boolean visit(ClassImpl classImpl, IType state) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}
	
	@Override
	public Boolean visit(InterfaceImpl interfaceImpl, IType state) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}
	
	@Override
	public Boolean visit(GenericClassInstance genericClassInstance, IType state) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}
	
	@Override
	public Boolean visit(GenericInterfaceInstance genericInterfaceInstance,
			IType state) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}
	
	@Override
	public Boolean visit(GenericExtensionInstance genericExtensionInstance,
			IType state) {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}
	
	
	
	
	
	@Override
	public Boolean visitDefault(SemanticsElement semanticsElement, IType to) {
		IType from = (IType) semanticsElement;
		
		TypeUtil.isHierarchyShared(from, to);
		return false;
	}
}
