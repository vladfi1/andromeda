/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.gui.misc;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.text.AttributeSet;

public class UIOutputStream extends OutputStream {

	private LogWriter writer;
	private AttributeSet attrs;
	public UIOutputStream(LogWriter w, AttributeSet attrs){
		writer = w;
		this.attrs = attrs;
	}
	
	@Override
	public void write(int b) throws IOException {
		writer.append("" + (char)b,attrs);		
	}
	
	@Override
	public void write(byte[] bs, int offset, int length) throws IOException{
		writer.append(new String(bs,offset,length),attrs);	
	}
	
	


}
