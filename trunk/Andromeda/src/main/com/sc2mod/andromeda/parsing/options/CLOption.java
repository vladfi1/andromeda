package com.sc2mod.andromeda.parsing.options;

import static com.sc2mod.andromeda.parsing.options.ParamType.*;

import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.util.Files;

/**
 * This enumeration represents command line options which are not added
 * to the Configuration of the parser.
 * 
 * Parameters used by the parser should instead be specified in the Parameter enum.
 * 
 * @author gex
 *
 */
public enum CLOption implements ICLOption {

	DISPLAY_HELP(FLAG,false,"--help"),
	
	SET_CONFIG(APP_FILE,Files.getAppFile("andromeda.conf"),"-c");

	private String clParam;
	private ParamType type;
	private Object defaultValue;
	
	private CLOption(ParamType type, Object defaultValue, String clParam){
		this.clParam = clParam;
		this.type = type;
		this.defaultValue = defaultValue;
		
		//Check that the default value is correct
		try {
			ParamUtil.checkValue(this, defaultValue);
		} catch (InvalidParameterException e) {
			throw new InternalProgramError("The default value for parameter " + this + "is invalid!\n" + e.getMessage());
		}
	}

	public ParamType getType(){
		return type;
	}
	
	
	public String getCLParamString() {
		return clParam;
	}
	
	public void doAdditionalChecks(Object value) throws InvalidParameterException{}
	
	public boolean mayBeNull(){	return true; }

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}
}
