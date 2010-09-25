package com.sc2mod.andromeda.parsing.options;

public class ParamUtil {
	
	public static Object parseString(IParam option, String str) throws InvalidParameterException{
		Object value = option.getType().parseString(str);
		return checkValue(option, value);
	}
	
	public static Object checkValue(IParam option, Object value) throws InvalidParameterException{
		if(value == null && !option.mayBeNull()) throw new InvalidParameterException("The parameter " + option.toString() + " may not be null.");
		try {
			value = option.getType().checkValue(value);
			option.doAdditionalChecks(value);
		} catch (InvalidParameterException e) {
			throw new InvalidParameterException("Invalid value for parameter " + option.toString() + ":\n" + e.getMessage());
		}
		
		return value;
	}
}
