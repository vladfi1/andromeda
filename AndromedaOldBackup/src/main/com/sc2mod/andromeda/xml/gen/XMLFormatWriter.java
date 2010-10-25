/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.xml.gen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class XMLFormatWriter extends Writer{

	private Writer encapsulatedWriter;
	private int indent;
	
	private int formatMode = XMLFormat.INDENT;
	private int curIndent = 0;
	private char lastChar;
	private boolean start = true;
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
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"
	};

	
	public XMLFormatWriter(BufferedWriter bufferedWriter) {
		encapsulatedWriter = bufferedWriter;
	}

	@Override
	public void close() throws IOException {
		encapsulatedWriter.close();
	}

	@Override
	public void flush() throws IOException {
		encapsulatedWriter.flush();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		int end = off+len;
		int lastStart = off;
		char lastChar = this.lastChar;
		for(int i=off;i<end;i++){
			char curChar = cbuf[i];
			switch(cbuf[i]){
			case '<':
				encapsulatedWriter.write(cbuf,lastStart,i-lastStart);
				lastStart = i;
				boolean isEnd = cbuf[i+1]!='/';
				
				if(start){
					start = false;
					break;
				}
				
				if(!isEnd)
					indent--;
				
				newLine();
				if(isEnd)
					indent++;
				break;
			case '>':
				if(lastChar == '/'){
					indent-=1;
				}
				break;
//			case '/':
//				if(lastChar == '<')
//					indent-=2;
//				break;
			}
			lastChar = curChar;
		}
		this.lastChar = lastChar;
		encapsulatedWriter.write(cbuf,lastStart,end-lastStart);
		
	}
	
	

	private void newLine() throws IOException {
		encapsulatedWriter.write("\r\n");
		if(formatMode==XMLFormat.INDENT)
			encapsulatedWriter.write(indentStrs[indent<19?indent:18]);
	}

}
