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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.parsing.FileSource;
import com.sc2mod.andromeda.parsing.Source;
import com.sc2mod.andromeda.util.Files;

public class CommandLineOptions {
	
	private static HashMap<String, ICLOption> registeredParams = new HashMap<String, ICLOption>(); 
	
	private static void registerParam(ICLOption param){
		String name = param.getCLParamString();
		
		//This is no param if its name is null (maybe only a aconfig file options)
		if(name == null) return;
		
		if(name.startsWith("--")){
			if(param.getType() != ParamType.FLAG) throw new InternalProgramError("Long parameters cannot contain other values than flags!");
		} else if(name.startsWith("-")){
		
		} else {
			throw new InternalProgramError("clParam does not start with -");
		}
		
		ICLOption old = registeredParams.put(name, param);
		if(old != null) throw new InternalProgramError("Duplicate parameter registration: " + name);
	}
	
	static{
		for(ICLOption p : Parameter.values()) registerParam(p);
		for(ICLOption p : CLOption.values()) registerParam(p);
	}

	private HashMap<String,Object> paramTable = new HashMap<String,Object>(64);
	private String[] params;
	private List<Source> files = new ArrayList<Source>();
	
	public Object getParam(ICLOption p) {
		String name = p.getCLParamString();
		if(name == null) return null;
		if(!paramTable.containsKey(name)) return null;
		
		//We need no checks here, since the param was already checked when it was entried
		return paramTable.get(name);
	}

	public boolean getParamFlag(ICLOption o){
		return paramTable.containsKey(o.getCLParamString());
	}

	public List<Source> getFiles() {
		return files;
	}

	public CommandLineOptions(String[] args) throws InvalidParameterException{
		params = args;
		parseParams();
	}
	
	private ICLOption getCLOption(String name) throws InvalidParameterException {
		ICLOption opt = registeredParams.get(name);
		if(opt == null) throw new InvalidParameterException("Unknown command line option '" + name + "'");
		return opt;
	}

	private void longParam(String name) throws InvalidParameterException{
		getCLOption(name);
		paramTable.put(name, true);
	}
	

	private void shortParam(String command,String argument) throws InvalidParameterException{
		ICLOption option = getCLOption(command);
		Object value = ParamUtil.parseString(option, argument);
		paramTable.put(command, value);
	}
	
	private void parseParams() throws InvalidParameterException{
		for (String s : params) {
			if(s.startsWith("--")){
				if (s.length() == 2)
					throw new Error("Invalid parameter '--'");
				longParam(s);
			} else if (s.startsWith("-")) {
				if (s.length() == 1)
					throw new Error("Invalid parameter '-'");
				String command = s.substring(0,2);
				String param = s.substring(2);
				shortParam(command, param);
			} else {
				File f = new File(s);
				Files.checkForFile(f);
				files.add(new FileSource(f));
			}
		}
	}


	
	
}
