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


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.sc2mod.andromeda.problems.ConfigurationError;
import com.sc2mod.andromeda.program.ConfigSubscriber;



/**
 * This class can read properties files in Microsoft .ini file style and provides
 * an interface to read string, integer and boolean values.  The .ini files has the
 * following structure:
 * <pre>; a comment
 * [section]
 * key=value</pre>
 * 
 */
public class ConfigFile {
	private boolean caseSensitive;
	private LinkedHashMap<String,LinkedHashMap<String,ConfigSubscriber>> subscribers = new  LinkedHashMap<String,LinkedHashMap<String,ConfigSubscriber>>();
	private LinkedHashMap<String,LinkedHashMap<String,String>> sections = new LinkedHashMap<String,LinkedHashMap<String,String>>();
	private HashMap<ConfigSubscriber,String[]> subsc = new HashMap<ConfigSubscriber,String[]>();
	public ConfigFile(File path, boolean caseSensitive) throws FileNotFoundException, IOException {
		this(new FileReader(path), caseSensitive);
	}
	
	public ConfigFile(InputStream input, boolean caseSensitive) throws FileNotFoundException, IOException {
		this(new InputStreamReader(input), caseSensitive);
	}

	public ConfigFile(Reader input, boolean caseSensitive) throws FileNotFoundException, IOException {
		initialize(new BufferedReader(input), caseSensitive);
	}

	private String caseSens(String input){
		if (caseSensitive) return input;
		return input.toLowerCase();
	}
	private void initialize(BufferedReader r, boolean caseSensitive) throws IOException {
		this.caseSensitive = caseSensitive;
		int lineNum = 0;
		String section = null, line;
		while ((line = r.readLine()) != null) {
			line = line.trim();
			lineNum++;
			if (line.startsWith("﻿")) line = line.substring(3);
			if (line.equals("") || line.startsWith(";") || line.startsWith("/")) {
				continue;
			}
			
			if (line.startsWith("[")) {
				if (!line.endsWith("]")) {
				    throw new IOException("] expected in section header on line " + lineNum);
				}
			    section = caseSens(line.substring(1, line.length() - 1));
			} else if (section == null) {
			    throw new IOException("[section] header expected on line " + lineNum);
			} else {
				int index = line.indexOf('=');
				if (index < 0) {
				    //Log.debug("INI-Read error: key/value pair without = on line " + lineNum);
				    continue;
				}
			    String key = caseSens(line.substring(0, index).trim());
			    String value = line.substring(index + 1).trim();
			    LinkedHashMap<String,String> map = sections.get(section);
				if (map == null) {
				    sections.put(section, (map = new LinkedHashMap<String,String>()));
				}
				map.put(key, value);
			}
		}
		r.close();
	}

	public String getPropertyString(String section, String key, String defaultValue) {
		LinkedHashMap<String,String> map = sections.get(caseSens(section));
		if (map == null) {
				sections.put(caseSens(section), new LinkedHashMap<String,String>());
				map = sections.get(caseSens(section));
		}
		
		String value = map.get(caseSens(key));
		if (value == null) {
			value = defaultValue;
			map.put(caseSens(key), defaultValue);
		}
	
		return value;
	}
	
	public String[] getPropertyCommaString(String section, String key, String defaultValue){
		return getPropertyString(section,key,defaultValue).split(",");
		
	}

	public int getPropertyInt(String section, String key, int defaultValue) {
		String s = getPropertyString(section, key, defaultValue + "");
		return Integer.parseInt(s);
	}

	public boolean getPropertyBool(String section, String key, boolean defaultValue) {
		String s = getPropertyString(section, key, defaultValue?"true":"false");
		return s.equalsIgnoreCase("true");
	}
	

	public int getPropertyEnum(String section, String key, String defaultValue, String[] possibilities){
		String result = getPropertyString(section, key, defaultValue);
		int i = 0;
		for(String s: possibilities){
			if(s.equalsIgnoreCase(result)) return i;
			i++;
		}
		//Assemble error string
		String error = "";
		for(String s: possibilities){
			error+=s + " ";
		}
		throw new ConfigurationError("Invalid option for property [" + section + "] " + key + "! Allowed values:\n" + error);
		
	}
	
	private void addSubscriber(String section, String key, ConfigSubscriber i){
		if(!subscribers.containsKey(caseSens(section))) subscribers.put(caseSens(section), new LinkedHashMap<String,ConfigSubscriber>());
		subscribers.get(caseSens(section)).put(caseSens(key),i);
		subsc.put(i, new String[]{section,key});
	}
	
	public String subscribePropertyString(ConfigSubscriber i,String section, String key, String defaultValue) {
		addSubscriber(section,key,i);
		return getPropertyString(section,key,defaultValue);
	}
	

	public int subscribePropertyInt(ConfigSubscriber i,String section, String key, int defaultValue) {
		addSubscriber(section,key,i);
		return getPropertyInt(section,key,defaultValue);
	}

	public boolean subscribePropertyBool(ConfigSubscriber i,String section, String key, boolean defaultValue) {
		addSubscriber(section,key,i);
		return getPropertyBool(section,key,defaultValue);
	}
	
	public void setPropertyString(String section, String key, String value) {
		if(value == null) throw new Error("value may not be null");
		LinkedHashMap<String,String> map = sections.get(caseSens(section));
		if (map == null) {
				map = sections.put(caseSens(section), new LinkedHashMap<String,String>());
		}
		map.put(caseSens(key), value);
	}

	public void setPropertyInt(String section, String key, int value) {
		setPropertyString(section, key, value + "");
	}

	public void setPropertyBool(String section, String key, boolean value) {
		setPropertyString(section, key, value?"true":"false");
	}
	
	public void toFile(File f) throws IOException{
		//updates von den subscribern
		for(String s1: subscribers.keySet()){
			LinkedHashMap<String,ConfigSubscriber> l1 = subscribers.get(s1);
			for(String s2: l1.keySet()){
				Object newVal = l1.get(s2).inform();
				
				if(newVal instanceof String){
					setPropertyString(s1,s2,(String)newVal);
				} else if(newVal instanceof Integer){
					setPropertyInt(s1,s2,(Integer)newVal);
				} else if(newVal instanceof Boolean){
					setPropertyBool(s1,s2,(Boolean)newVal);
				} else throw new Error("WTF ini!");
			}
			
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(f));
		for(String category: sections.keySet()){
			out.write("[" + category.toUpperCase() + "]\r\n");
			LinkedHashMap<String,String> curCat = sections.get(category);
			for(String entry: curCat.keySet()){
				String content = curCat.get(entry);
				out.write(entry.toLowerCase() + "=" + content + "\r\n");
			
			}
			out.write("\r\n");
		}
		
		out.close();
	}
	
	public void update(ConfigSubscriber i){
		String[] vals = subsc.get(i);

		Object newVal = i.inform();
				
		if(newVal instanceof String){
			setPropertyString(vals[0],vals[1],(String)newVal);
		} else if(newVal instanceof Integer){
			setPropertyInt(vals[0],vals[1],(Integer)newVal);
		} else if(newVal instanceof Boolean){
			setPropertyBool(vals[0],vals[1],(Boolean)newVal);
		} else throw new Error("WTF ini!");

	}
	
	public LinkedHashMap<String,LinkedHashMap<String,String>> getFileContent(){
		return sections;
	}

	public Object getProperty(IConfigFileEntry p, String runConfig) throws InvalidParameterException {
		String s = getPropertyString(p.getSection(runConfig), p.getKey(), null);
		if(s == null)return ParamUtil.checkValue(p,p.getDefaultValue());
		return ParamUtil.parseString(p, s);
	}




}
