/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.gui.forms;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class ScriptInfoParser {

	private HashMap<String,InfoParseResult> cache = new HashMap<String,InfoParseResult>();
	
	static SimpleAttributeSet capt = new SimpleAttributeSet();
	private static SimpleAttributeSet key = new SimpleAttributeSet();
	private static SimpleAttributeSet error = new SimpleAttributeSet();
	private static SimpleAttributeSet vers = new SimpleAttributeSet();
	
	public void clearCache(){
		cache = new HashMap<String,InfoParseResult>();
	}

	public static class InfoParseResult{
		Document 	result;
		boolean		allowExecution = false;
		boolean		allowViewSource = false;
		File		file;
		
		public InfoParseResult(Document result,boolean exec, boolean view, File file){
			this.result = result;
			this.allowExecution = exec;
			this.allowViewSource = view;
			this.file = file;
		}
	}
	
	static{
		StyleConstants.setBold(capt, true);
		StyleConstants.setUnderline(capt, true);
		StyleConstants.setBold(key, true);
		StyleConstants.setBold(error, true);
		StyleConstants.setForeground(error, Color.RED);
		StyleConstants.setFontSize(vers, 11);
		StyleConstants.setItalic(vers, true);
		StyleConstants.setBold(vers, true);
		StyleConstants.setAlignment(vers,StyleConstants.ALIGN_RIGHT);
	}

	private static class ScriptInformation{
		String infoText = null;
		String author = "unknown";
		String version = null;
		String date = null;
		boolean isLibrary = false;
		String type = null;
		LinkedList<String> params = new LinkedList<String>();
		
		public void appendToDocument(Document d){
			try {
				d.insertString(d.getLength(),"Author: ", key );
				d.insertString(d.getLength(), author, null);
				if(type != null){
					d.insertString(d.getLength(),"\nType: ", key );
					d.insertString(d.getLength(), type, null);
				}
				if(version != null){
					d.insertString(d.getLength(),"\nv " + version, vers );
				}
				d.insertString(d.getLength(), "\n\n" + infoText, null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static Pattern keyword = Pattern.compile("\\A\\@(\\w+)\\s+(.*)");
	
	private void handleKeyword(String keyword,String value,ScriptInformation writeTo){
		value = value.trim();
		if(keyword == null){
			writeTo.infoText = value;
		} else if(keyword.equals("author")){
			writeTo.author = value;
		} else if(keyword.equals("date")){
			writeTo.date = value;
		} else if(keyword.equals("param")){
			writeTo.params.add(value);
		} else if(keyword.equals("version")){
			writeTo.version = value;
		} else if(keyword.equals("type")){
			if(value.equalsIgnoreCase("Library")) writeTo.isLibrary = true;
			writeTo.type = value;
		}
	}
	
	private ScriptInformation doParse(File f) throws IOException{
		BufferedReader b = new BufferedReader(new FileReader(f));
		
		//Does the map have a valid header comment?
		String line;
		boolean valid = false;
		while((line = b.readLine())!=null){
			line = line.trim();
			if(line.equals(""))continue;
			if(line.startsWith("/*")){
				valid = true;
				break;
			}
			break;
		}
		if(!valid) return null;
		
		//Lines holen
		LinkedList<String> lines = new LinkedList<String>();
		while((line = b.readLine())!= null){
			line = line.trim();
			int end = line.indexOf("*/");
			if(end!= -1){
				line = line.substring(0,end);
				lines.add(line);
				break;
			}
			lines.add(line);
						
		}
		
		b.close();
		
		//Lines parsen
		ScriptInformation result = new ScriptInformation();
		String currentKeyword = null;
		StringBuilder currentValue = new StringBuilder();
		for(String lin: lines){
			//Remove startline stars
			if(lin.startsWith("*")){
				lin = lin.substring(1).trim();
			}
			
			//Handle keywords
			Matcher m = keyword.matcher(lin);
			if(m.matches()){
				handleKeyword(currentKeyword,currentValue.toString(),result);
				currentKeyword = m.group(1);
				currentValue = new StringBuilder();
				currentValue.append(m.group(2));
				currentValue.append("\n");
			}
			
			//Handle no new keyword
			else {
				currentValue.append(lin);
				currentValue.append("\n");
			}
			
		}
		
		//letztes keyword verarbeiten
		handleKeyword(currentKeyword,currentValue.toString(),result);
				
		return result;
	}
	public InfoParseResult parseFile(File f){
		try {
			if(cache.containsKey(f.getAbsolutePath())){
				return cache.get(f.getAbsolutePath());
			}
			DefaultStyledDocument result = new DefaultStyledDocument();
			InfoParseResult ret = new InfoParseResult(result,false,false,f);
			result.insertString(0, "Script info for " + f.getName() + ":\n\n", capt);
			try {
				ScriptInformation info = doParse(f);
				if(info == null){
					result.insertString(result.getLength(), "- no information available for this script file -", null);
					ret.allowExecution = true;
					ret.allowViewSource = true;
				} else {
					info.appendToDocument(result);
					ret.allowViewSource = true;
					ret.allowExecution = !info.isLibrary;
				}
				
				cache.put(f.getAbsolutePath(), ret);
				return ret;
			} catch (IOException e) {
				result.insertString(result.getLength(), "- the file could not be read! -", error);
				return ret;
			}
		} catch (BadLocationException e) {
			throw new Error(e);
		}
	}
}
