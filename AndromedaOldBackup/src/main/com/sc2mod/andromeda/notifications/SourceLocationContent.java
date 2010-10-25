/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.notifications;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.sc2mod.andromeda.parsing.Source;

public class SourceLocationContent {

	public final int line;
	public final int column;
	public final int tabsInLine;
	public final int lineStartOffset;
	public final int offset;
	public final int length;
	
	private String lineContent;
	
	private Source source;
	private int startUnderline;
	private int endUnderline;
	
	public SourceLocationContent(Source f, int pos, int length) throws IOException{
		try {
			BufferedReader b = new BufferedReader(f.createReader());
			int lineNr = 1;
			int lastStart = 0;
			int offset = 0;
			int tabsInLine = 0;
			char[] buffer = new char[2048];
			boolean nextNewline = false;
			while(true){
				int siz = b.read(buffer);
				int max = Math.min(siz, Math.min(2048,pos-offset));
				
				for(int i=0;i<max;i++){
					switch(buffer[i]){
					case '\r':
						nextNewline = true;
						lineNr++;
						tabsInLine = 0;
						lastStart = i+1+offset;
						break;
					case '\n':
						if(!nextNewline){
							lineNr++;
							tabsInLine = 0;
							lastStart = i+1+offset;
						} else {
							lastStart = i+1+offset;
							nextNewline = false;
						}
						break;
					case '\t':
						tabsInLine++;
						
					default:
						nextNewline = false;
					}				
				}
				if(offset+max>=pos){
					break;
				}
				offset+= 2048;
				//Todo end of file handling
			}
			b.close();
			
			this.lineStartOffset = lastStart;
			this.line = lineNr;
			this.column = pos - lastStart;
			this.tabsInLine = tabsInLine;
			this.source = f;
			this.offset = pos;
			this.length = length;
		} catch (FileNotFoundException e) {
			throw new IOException("Source file not found", e);
		} catch (IOException e) {
			throw new IOException("Error reading source file",e);
		} 
		
	}
	
	public String getLineContent(){
		if(lineContent != null) return lineContent;
		
		try {
			BufferedReader b = new BufferedReader(source.createReader());
			b.skip(this.lineStartOffset);
			lineContent = b.readLine();
			startUnderline = column;
			endUnderline = Math.min(lineContent.length(),startUnderline + length);
			
		} catch (IOException e) {
			lineContent = "-- LINE CONTENT COULDN'T BE READ --";
		}
		return lineContent;
	}
	
	public String toString(){
		String lineContent = getLineContent();
		return source.getTypeName() + " " + source.getName() + ", line " + line + ":\n" + lineContent + "\n" + generateUnderline(startUnderline,endUnderline,tabsInLine);
	}

	private static String generateUnderline(int startUnderline, int endUnderline, int tabsInLine) {
		char[] chars = new char[endUnderline];
		
		int i;
		
		//Tabs
		for(i=0;i<tabsInLine;i++){
			chars[i] = '\t';
		}
		//Whitespaces
		for(;i<startUnderline;i++){
			chars[i] = ' ';
		}
		//Marker
		for(;i<endUnderline;i++){
			chars[i] = '^';
		}

		return String.valueOf(chars);
	}
}
