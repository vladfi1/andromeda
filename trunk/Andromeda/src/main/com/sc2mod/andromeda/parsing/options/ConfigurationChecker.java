package com.sc2mod.andromeda.parsing.options;

import static com.sc2mod.andromeda.parsing.options.Parameter.*;

public class ConfigurationChecker {

	
	protected void check(Configuration conf) throws ConfigurationException{
		
		if(!conf.getParamBool(CODEGEN_NEW_LINES)){
			conf.setParam(CODEGEN_USE_INDENT,false);
			conf.setParam(CODEGEN_DESCRIPTION_COMMENTS,false);
			conf.setParam(CODEGEN_OWN_LINE_FOR_OPEN_BRACES,false);
		}
		if(conf.isParamNull(FILES_MAP_IN)) { 
			if(!conf.isParamNull(FILES_MAP_OUT))
				throw new ConfigurationException("You may not specify an output map (-o) if no input map (-i) was specified.");
		} else {
			if(!conf.isParamNull(FILES_MAP_TRIGGERS_IN))
				throw new ConfigurationException("You may not specify an input map (-i) and an input trigger file (-g) together.");
			if(!conf.isParamNull(FILES_MAP_SCRIPT_IN))
				throw new ConfigurationException("You may not specify an input map (-i) and an input map scipt file (-m) together.");
			
	
		}
	}
}
