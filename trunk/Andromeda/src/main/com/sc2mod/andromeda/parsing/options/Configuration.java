/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parsing.options;

import java.io.File;
import java.util.EnumMap;

public class Configuration {
	
	private EnumMap<Parameter, Object> params = new EnumMap<Parameter, Object>(Parameter.class);
	
	private ConfigFile confFile;
	private CommandLineOptions clOptions;
	private String runConf;
	
	private ConfigurationChecker checker = null;
	
	public Object getParam(Parameter p) {
		return params.get(p);
	}
	
	public boolean isParamNull(Parameter p){
		return params.get(p) == null;
	}
	
	public Object setParam(Parameter p, Object value) throws InvalidParameterException{
		//Validate the value (this can also change the value)
		value = ParamUtil.checkValue(p,value);
		
		//Store
		return params.put(p, value);
	}
	
	
	public String getParamString(Parameter p){
		return params.get(p).toString();
	}
	
	public boolean getParamBool(Parameter p){
		return (Boolean)params.get(p);
	}
	
	public File getParamFile(Parameter p){
		return (File)params.get(p);
	}
	
	public File[] getParamFiles(Parameter p){
		return (File[])params.get(p);
	}
	
	public String[] getParamCommaList(Parameter p){
		return (String[])params.get(p);
	}
	
	
	
	public Configuration(ConfigFile configHandler) throws InvalidParameterException{
		
	}
	
	public Configuration(ConfigFile configHandler, CommandLineOptions clOptions) throws InvalidParameterException{
		this(configHandler,clOptions,null);
	}
	
	
	public Configuration(ConfigFile configHandler, CommandLineOptions clOptions, String forceRunConf) throws InvalidParameterException {
		//Set checker
		checker = new ConfigurationChecker();
		
		//Set values
		this.confFile = configHandler;
		this.clOptions = clOptions;
		
		//Determine and check run configuration
		runConf = lookupParam(Parameter.RUN_CONFIG).toString();
		if(forceRunConf != null){
			runConf = forceRunConf;
		}
		String[] runConfs = configHandler.getPropertyCommaString("GENERAL", "runConfigs", "debug");
		boolean exists = false;
		for(String s: runConfs){
			if(runConf.equals(s)) exists = true;
		}
		if(!exists){
			throw new InvalidParameterException("The run configuration '" + runConf + "' does not exist.");
		}
		runConf = "RC_" + runConf.toUpperCase();
		
		//Read parameters
		for(Parameter p : Parameter.values()){
			Object value = lookupParam(p);
			
			//Store the param, we do not call setParam() here because the parameter is already checked
			//by the config / commandlineoptions class
			params.put(p, value);
		}
	}
	
	private Object lookupParam(Parameter p) throws InvalidParameterException{
		Object value = clOptions.getParam(p);
		if(value == null) 
			value = confFile.getProperty(p,runConf);
		return value;
	}
	

	public void check() throws ConfigurationException{
		if(checker != null) checker.check(this);
	}

}
