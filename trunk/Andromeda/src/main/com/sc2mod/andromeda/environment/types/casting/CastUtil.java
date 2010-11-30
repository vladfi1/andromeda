package com.sc2mod.andromeda.environment.types.casting;

import com.sc2mod.andromeda.environment.types.IType;
import com.sc2mod.andromeda.environment.types.basic.BasicTypeSet;

public final class CastUtil {

	private CastUtil(){}
	
	private static CastDecisionVisitorExplicit checkedExplicitChecker = new CastDecisionVisitorExplicit(false);
	private static CastDecisionVisitorExplicit uncheckedExplicitChecker = new CastDecisionVisitorExplicit(true);

	/**
	 * Returns true, if this type can be explicitly cast to the given type (with an explicit cast expression)
	 * The basic implementation allows this if one of the types is a subtype of the other one or the types are the same.
	 */
	public static boolean canExplicitCast(IType from, IType to, boolean unchecked){
		if(unchecked){
			return from.accept(uncheckedExplicitChecker, to);
		} else {
			return from.accept(checkedExplicitChecker, to);
		}
	}
	
	/**
	 * Returns iff a type can be concatenate cast to another type.
	 * 
	 * The basic implementation allows this if the type can be cast implicitly
	 */
	public static boolean canConcatenateCast(IType from, IType to){
		BasicTypeSet bt = from.getTypeProvider().BASIC;
		if(from.getBaseType() == bt.TEXT)
			return to == bt.TEXT;
		
		if(to == bt.STRING || to == bt.TEXT)
			return true;
		
		return CastUtil.canImplicitCast(from, to);
	}
	
	/**
	 * Returns iff a type can be cast implicitly to antoher type (i.e. without an explicit type cast).
	 * 
	 * The basic implementation for types returns true iff the
	 * type to be cast to is this type or if this type is a 
	 * subtype of the type.
	 */
	public static boolean canImplicitCast(IType from, IType to){
		return from.canImplicitCastTo(to);
	}
	
	
	
}
