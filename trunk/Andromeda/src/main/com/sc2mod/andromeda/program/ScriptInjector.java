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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mopaqlib.MoPaQ;

public class ScriptInjector {

	
	private static final String ANDROMEDA_IDENTIFIER = "//-- ANDROMEDA INJECTED --\n";
	private static final String ANDROMEDA_INCLUDE = "include \"Andromeda\"\n";
	private static final String ANDROMEDA_INIT = "initAndromeda();";
	
	private static final Pattern INCLUDE_PATTERN = Pattern.compile("include \\\"(TriggerLibs\\/.*?)\\\"");
	private static final Pattern INIT_MAP_PATTERN = Pattern.compile("void\\s+InitMap\\s*\\(\\s*\\)\\s*\\{");
	
	private static String inject(String mapScript) throws MapFormatException{
		if(mapScript.startsWith(ANDROMEDA_IDENTIFIER)) return mapScript;
		StringBuffer out = new StringBuffer(mapScript.length() + 50);
		out.append(ANDROMEDA_IDENTIFIER);
		
		Matcher m = INCLUDE_PATTERN.matcher(mapScript);
		if(!m.find()) throw new MapFormatException("No include directive for the native libraries in the provided MapScript.galaxy!");
		m.appendReplacement(out, "include \"$1\"\n");
		out.append(ANDROMEDA_INCLUDE);
		
		m.usePattern(INIT_MAP_PATTERN);
		if(!m.find()) throw new MapFormatException("No InitMap() function in the provided MapScript.galaxy!");
		m.appendReplacement(out, "void InitMap(){\n\t");
		out.append(ANDROMEDA_INIT);
		m.appendTail(out);
		return out.toString();
	}
	
	
	
	public static String getManipulatedMapScript(MoPaQ m) throws MapFormatException{
		String mapScript = ScriptInjector.inject(new String(m.returnFileByName("MapScript.galaxy")));
		return mapScript;
	}
	
	public static String getManipulatedMapScript(File scriptFile) throws IOException, MapFormatException{
		FileInputStream r = new FileInputStream(scriptFile);
		byte[] bytes = new byte[(int) scriptFile.length()];
		r.read(bytes);
		r.close();
		String mapScript = ScriptInjector.inject(new String(bytes));
		return mapScript;
	}
	
	public static void injectAndromeda(MoPaQ m, String mapScriptCode, String andromedaCode){
			m.writeFile("Andromeda.galaxy", andromedaCode.getBytes());
			m.writeFile("MapScript.galaxy", mapScriptCode.getBytes());
	}


}
