/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.program;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sc2mod.andromeda.parsing.AndromedaSource;
import com.sc2mod.andromeda.parsing.FileSource;

public class Parameters {
	
	public static final String STRING_RUN_CONF = "runConfig";
	public static final String FILE_MAP_IN = "mapIn";
	public static final String FILE_MAP_OUT = "mapOut";
	public static final String FLAG_NO_CODE = "noCode";
	public static final String FILE_XML_STRUCTURE = "xmlStructure";
	public static final String FILE_XML_ERRORS = "xmlErrors";
	public static final String FLAG_OUTPUT_NATIVES = "xmlStructureNatives";
	public static final String FLAG_RUN_MAP = "runMap";
	public static final String FILE_DIR_OUT = "dirOut";
	public static final String FILE_MAPSCRIPT_IN = "mapscriptIn";
	public static final String FILE_TRIGGERS_IN = "triggersIn";
	
	private String[] params;
	private HashMap<String,Object> paramTable = new HashMap<String,Object>(64);
	private List<AndromedaSource> files = new ArrayList<AndromedaSource>();
	private static HashMap<String,ParamHandler> longParamHandlers = new HashMap<String, ParamHandler>();
	
	static {
		longParamHandlers.put("help", 
				new ParamHandler(){
					public void handle(Parameters p, String argument) {
						BufferedReader is = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("usage.txt")));
						String line;
						try {
							while((line = is.readLine()) != null){
								System.out.println(line);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.exit(0);
					}			
				}
		);
		longParamHandlers.put("run", 
				new ParamHandler(){
					public void handle(Parameters p, String argument) {
						p.putParam(FLAG_RUN_MAP, new Object());
					}			
				}
		);
		longParamHandlers.put("xmlnatives", 
				new ParamHandler(){
					public void handle(Parameters p, String argument) {
						p.putParam(FLAG_OUTPUT_NATIVES, new Object());
					}			
				}
		);
		longParamHandlers.put("nocode", 
				new ParamHandler(){
					public void handle(Parameters p, String argument) {
						p.putParam(FLAG_NO_CODE,new Object());
					}			
				}
		);
		
		
		
	}


	public List<AndromedaSource> getFiles() {
		return files;
	}

	public Parameters(String[] args) throws InvalidParameterException{
	
		
		params = args;
		parseParams();
	}
	
	public Object getParam(String key){
		return paramTable.get(key);
	}
	
	public String getParamString(String key){
		return (String) paramTable.get(key);
	}
	
	public File getParamFile(String key){
		return (File) paramTable.get(key);
	}
	
	private void putParam(String key, Object value){
		paramTable.put(key, value);
	}

	
	private void longParam(String name){
		ParamHandler ph = longParamHandlers.get(name);
		if(ph == null){
			throw new Error("Unknown parameter '--" + name + "'");
		}		
		ph.handle(this, null);
	}
	
	private void shortParam(char command,String argument) throws InvalidParameterException{

		switch (command) {
		case 'r':
			putParam(STRING_RUN_CONF,argument);
			break;
		case 'i':
			File f;
			f = new File(argument);
			Util.checkForFile(f);
			putParam(FILE_MAP_IN,f);
			break;
		
		//Input files directly
		case 'm':
			f = new File(argument);
			Util.checkForFile(f,".galaxy");
			putParam(FILE_MAPSCRIPT_IN,f);
			break;
		case 't':
			f = new File(argument);
			Util.checkForFile(f);
			putParam(FILE_TRIGGERS_IN,f);
			break;
			
		case 'o':
			f = new File(argument);
			Util.checkForFile(f,false);
			putParam(FILE_MAP_OUT,f);			
			break;
		case 'd':
			f = new File(argument);
			Util.checkForDir(f, false);
			putParam(FILE_DIR_OUT,f);
			break;
		case 's':
			f = new File(argument);
			Util.checkForFile(f,false);
			putParam(FILE_XML_STRUCTURE,f);
			break;
		case 'e':
			f = new File(argument);
			Util.checkForFile(f,false);
			putParam(FILE_XML_ERRORS,f);
			break;
		default:
			throw new Error("Unknown parameter '-" + command + "'");
		}

	}
	
	
	
	private void parseParams() throws InvalidParameterException{
		for (String s : params) {
			if(s.startsWith("--")){
				if (s.length() == 2)
					throw new Error("Invalid parameter '--'");
				String param = s.substring(2);
				longParam(param);
			} else if (s.startsWith("-")) {
				if (s.length() == 1)
					throw new Error("Invalid parameter '-'");
				char command = s.charAt(1);
				String param = s.substring(2);
				shortParam(command, param);
			} else {
				File f = new File(s);
				Util.checkForFile(f);
				files .add(new FileSource(f));
			}
		}
	}

	public boolean getParamFlag(String flagNoCode) {
		return paramTable.containsKey(flagNoCode);
	}
	
	private static abstract class ParamHandler{
		abstract void handle(Parameters p, String argument);
	}
}
