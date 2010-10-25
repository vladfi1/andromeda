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

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import com.sc2mod.andromeda.codegen.CodeGenVisitor;
import com.sc2mod.andromeda.codegen.CodeWriter;

/**
 * Final buffer for writing the different generated sections into a file. 
 * @author J. 'gex' Finis
 *
 */
public class FileBuffer {

	public SimpleBuffer forwardDeclarations = new SimpleBuffer(16536);
	public SimpleBuffer variables = new SimpleBuffer(16536);
	public SimpleBuffer functions = new SimpleBuffer(32000);
	public SimpleBuffer types = new SimpleBuffer(8192);
	public SimpleBuffer typedefs = new SimpleBuffer(512);
	
	
	public StringBuffer flush(CodeGenVisitor cgf, File map, File backup) throws IOException{
		int bufferSize = (typedefs.length()+types.length()+forwardDeclarations.length()+
		variables.length()+cgf.classInitBuffer.length()+functions.length())*6/5;
		StringBuffer b = new StringBuffer(bufferSize);
		b.append("//Andromeda generated code @ " + Calendar.getInstance().getTime().toString());
		b.append(SimpleBuffer.LINE_SEPERATOR);
		b.append(typedefs.flush());
		b.append(SimpleBuffer.LINE_SEPERATOR);
		b.append(types.flush());
		b.append(SimpleBuffer.LINE_SEPERATOR);
		b.append(forwardDeclarations.flush());		
		b.append(SimpleBuffer.LINE_SEPERATOR);
		b.append(variables.flush());
		b.append(SimpleBuffer.LINE_SEPERATOR).append(SimpleBuffer.LINE_SEPERATOR);
		b.append(cgf.classInitBuffer.flush());
		b.append(SimpleBuffer.LINE_SEPERATOR);
		b.append(functions.flush());
		return b;
	}


	public void flush(CodeGenVisitor cgf, CodeWriter b) throws IOException {
		int bufferSize = (typedefs.length()+types.length()+forwardDeclarations.length()+
		variables.length()+cgf.classInitBuffer.length()+functions.length())*6/5;
		b.setCapacity(bufferSize);
		b.write("//Andromeda generated code @ " + Calendar.getInstance().getTime().toString());
		b.write(SimpleBuffer.LINE_SEPERATOR);
		b.write(typedefs.flush());
		b.write(SimpleBuffer.LINE_SEPERATOR);
		b.write(types.flush());
		b.write(SimpleBuffer.LINE_SEPERATOR);
		b.write(forwardDeclarations.flush());		
		b.write(SimpleBuffer.LINE_SEPERATOR);
		b.write(variables.flush());
		b.write(SimpleBuffer.LINE_SEPERATOR);
		b.write(SimpleBuffer.LINE_SEPERATOR);
		b.write(cgf.classInitBuffer.flush());
		b.write(SimpleBuffer.LINE_SEPERATOR);
		b.write(functions.flush());
		b.close();
	}





}
