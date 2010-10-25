package com.sc2mod.andromeda.parsing.options;

public interface IParam {


	ParamType getType();

	boolean mayBeNull();

	void doAdditionalChecks(Object value) throws InvalidParameterException;
	

	public Object getDefaultValue();
}
