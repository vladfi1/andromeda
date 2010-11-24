/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.codegen.buffers;

import com.sc2mod.andromeda.codegen.CodeGenerator;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.problems.InternalProgramError;

/**
 * 
 *	Common buffer used for most actions.
 * 
 * @author J. 'gex' Finis
 */
public class SimpleBuffer extends CodeBuffer {
	
	public static final String LINE_SEPERATOR = "\r\n";
	private static final int MAX_LINE_LENGTH = 2046;
	private static int MAX_INDENTS = 14;
	private static final String[] indentStrs;
	
	static {
		indentStrs = new String[MAX_INDENTS + 1];
		indentStrs[0] = "";
		for(int i = 1; i <= MAX_INDENTS; i++) {
			indentStrs[i] = indentStrs[i-1] + "\t";
		}
	}
	
	protected StringBuilder buffer;
	protected int charSinceNewLine;
	protected boolean containsNewLine;
	protected int indentLvl = 0;
	
	private boolean newLines;
	private boolean indent;
	
	public SimpleBuffer(int initialSize, Configuration conf) {
		buffer = new StringBuilder(initialSize);
		newLines = conf.getParamBool(Parameter.CODEGEN_NEW_LINES);
		indent = conf.getParamBool(Parameter.CODEGEN_USE_INDENT);
	}

	/**
	 * Appends a string to the buffer.
	 * Do not use new line symbols in this string.
	 * Call newLine() to append a new line symbol.
	 * @param s a string to append without newline symbols.
	 * @param csnl the number of characters since the last new line.
	 */
	private void append(String s, int csnl, boolean containsNewLine) {
		if(containsNewLine){
			charSinceNewLine = 0;
		}
		charSinceNewLine += csnl;
		
		//Max length exceeded
		if(charSinceNewLine > MAX_LINE_LENGTH){
			//Search whitespace to insert a linebreak
			int pos = s.length() - (charSinceNewLine - MAX_LINE_LENGTH);
			outer: while(pos>=0){
				char c = s.charAt(pos);
				switch (c){
				case ' ':
				case '\t':
					break outer;
				}
				pos--;
			}
			
			//Insert
			if(pos>=0){
				buffer.append(s.substring(0, pos));
				nl();
				buffer.append(s.substring(pos+1));
				charSinceNewLine = indentLvl+s.length()-(pos+1);
			} else {
				nl();
				charSinceNewLine = indentLvl+s.length();
				buffer.append(s);
			}
			return;
		}
		
		//No length exceed, just append
		buffer.append(s);
	}
	
	public void append(String s, boolean checkForNewLine){
		if(checkForNewLine){
			append(s,s.length(),false);
		} else {
			charSinceNewLine += s.length();
			buffer.append(s);
		}
	}
	
	public SimpleBuffer append(String s){
		append(s,false);
		return this;
	}
	
	//XPilot: added for string re-escapes
	public SimpleBuffer append(char c) {
		charSinceNewLine++;
		buffer.append(c);
		return this;
	}
	
	public SimpleBuffer append(int i){
		append(String.valueOf(i),false);
		return this;
	}
	
	public SimpleBuffer appendStringLiteral(String s, CodeGenerator cg){
		append("\"").append(s).append("\"");
		cg.addStringLiteral(s.length());
		return this;
	}

	public SimpleBuffer nl(){
		if(!newLines)
			return this;
		buffer.append(LINE_SEPERATOR);
		if(indent){
			containsNewLine = true;
			if(indentLvl>0){
				if(indentLvl > MAX_INDENTS) indentLvl = MAX_INDENTS;
				buffer.append(indentStrs[indentLvl]);
			} else {
				if(indentLvl<0)
					throw new InternalProgramError("negative indent level!");
			}
			charSinceNewLine = indentLvl;
		} else {
			containsNewLine = true;
			charSinceNewLine = 0;
		}
		return this;
	}
	
	public SimpleBuffer indent(){
		if(indent)
			indentLvl++;
		return this;
	}
	
	public SimpleBuffer unindent(){
		if(indent)
			indentLvl--;
		return this;
	}
	
	public boolean isEmpty(){
		return buffer.length()==0;
	}

	@Override
	public String flush() {
		String result = buffer.toString();
		buffer.setLength(0);
		charSinceNewLine = 0;
		containsNewLine = false;
		return result;
	}
	
	public void flushTo(SimpleBuffer toBuffer, boolean checkLength){
		if(isEmpty()) return;
		int csnl = charSinceNewLine;
		boolean cnl = containsNewLine;
		toBuffer.append(flush(),csnl,cnl);
	}

	public void appendTo(SimpleBuffer toBuffer, boolean checkLength){
		if(isEmpty()) return;
		int csnl = charSinceNewLine;
		boolean cnl = containsNewLine;
		toBuffer.append(buffer.toString(),csnl,cnl);
	}
	
	
	@Override
	public String toString() {
		return buffer.toString();
	}
	
	public int length(){
		return buffer.length();
	}
	
}
