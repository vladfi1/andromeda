package com.sc2mod.andromeda.environment.types;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.Signature;

public class AndromedaSystemTypes extends SystemTypes{

	private static String[] buildLangName(String name){
		return new String[]{"a","lang",name};
	}
	
	public static final String T_CLASS = "Class";
	public static final String T_OBJECT = "Object";
	public static final String T_SYSTEM = "System";
	public static final String T_FUNC_NAME = "funcName";
	public static final String M_ERROR = "error";
	public static final String CONS_CLASS = "cons_class";
	
	public AndromedaSystemTypes(Environment env) {
		super(env);
	}

	@Override
	protected void onResolveSystemTypes() {
		IClass clazz = resolveSystemClass(T_CLASS, buildLangName(T_CLASS));
		resolveSystemClass(T_OBJECT, buildLangName(T_OBJECT));
		IClass system = resolveSystemClass(T_SYSTEM, buildLangName(T_SYSTEM));
		resolveSystemType(T_FUNC_NAME,buildLangName(T_FUNC_NAME));
		resolveSystemMethod(M_ERROR, system, "error", new Signature(BasicType.STRING),true);
		resolveSystemConstructor(CONS_CLASS, clazz, new Signature(BasicType.INT,BasicType.STRING));
	}

}
