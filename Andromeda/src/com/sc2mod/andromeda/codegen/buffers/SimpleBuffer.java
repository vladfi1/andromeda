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

/**
 * 
 *	Common buffer used for most actions.
 * 
 * @author J. 'gex' Finis
 */
public class SimpleBuffer extends CodeBuffer {
	
	public static final String LINE_SEPERATOR = "\r\n";
	private static final int MAX_LINE_LENGTH = 2046;
	private static final String[] indentStrs = 
	{
		"",
		"\t",
		"\t\t",
		"\t\t\t",
		"\t\t\t\t",
		"\t\t\t\t\t",
		"\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t"
	};
	protected StringBuffer buffer;
	protected int charSinceNewLine;
	protected boolean containsNewLine;
	
	public SimpleBuffer(int initialSize) {
		buffer = new StringBuffer(initialSize);
	}

	/**
	 * Appends a string to the buffer.
	 * Do not use new line symbols in this string.
	 * Call newLine() to append a new line symbol.
	 * @param s a string to append without newline symbols.
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
				newLine();
				buffer.append(s.substring(pos+1));
				charSinceNewLine = s.length()-(pos+1);
			} else {
				newLine();
				charSinceNewLine = s.length();
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
	
	public SimpleBuffer append(int i){
		append(String.valueOf(i),false);
		return this;
	}
	
	public SimpleBuffer appendStringLiteral(String s, CodeGenerator cg){
		append("\"").append(s).append("\"");
		cg.addStringLiteral(s.length());
		return this;
	}

	public void newLine(int indent){
		buffer.append(LINE_SEPERATOR);
		containsNewLine = true;
		if(indent>0){
			if(indent > 14) indent = 14;
			buffer.append(indentStrs[indent]);
		}
		charSinceNewLine = indent;
	}
	
	public void newLine(){
		newLine(0);
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
