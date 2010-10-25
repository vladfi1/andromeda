package com.sc2mod.andromeda.environment.types;

import com.sc2mod.andromeda.environment.Signature;

public class AndromedaSystemTypes extends SystemTypes{

	public static final String T_CLASS = "Class";
	public static final String T_OBJECT = "Object";
	public static final String T_SYSTEM = "System";
	public static final String T_FUNC_NAME = "funcName";
	public static final String M_ERROR = "error";
	public static final String CONS_CLASS = "cons_class";
	
	public AndromedaSystemTypes(TypeProvider t) {
		super(t);
	}

	@Override
	protected void onResolveSystemTypes() {
		Class clazz = resolveSystemClass(T_CLASS, T_CLASS);
		resolveSystemClass(T_OBJECT, T_OBJECT);
		Class system = resolveSystemClass(T_SYSTEM, T_SYSTEM);
		resolveSystemType(T_FUNC_NAME,T_FUNC_NAME);
		resolveSystemMethod(M_ERROR, system, "error", new Signature(BasicType.STRING));
		resolveSystemConstructor(CONS_CLASS, clazz, new Signature(BasicType.INT,BasicType.STRING));
	}

}
