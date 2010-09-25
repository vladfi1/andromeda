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

import java.awt.Color;
import java.io.PrintStream;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.sc2mod.andromeda.gui.forms.AutoscrollPane;
import com.sc2mod.andromeda.parsing.SourceEnvironment;
import com.sc2mod.andromeda.program.CollectingLog;
import com.sc2mod.andromeda.program.Log;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class GuiLog extends CollectingLog{
	
	private static final SimpleAttributeSet STYLE_NORMAL = new SimpleAttributeSet();
	public static final SimpleAttributeSet STYLE_ERROR = new SimpleAttributeSet();
	public static final SimpleAttributeSet STYLE_WARNING = new SimpleAttributeSet();
	private static final SimpleAttributeSet STYLE_ECHO = new SimpleAttributeSet();
	private static final SimpleAttributeSet STYLE_CAPTION = new SimpleAttributeSet();
	private static final SimpleAttributeSet STYLE_TEST = new SimpleAttributeSet();
	private static final SimpleAttributeSet STYLE_HIGHLIGHT = new SimpleAttributeSet();
	private AutoscrollPane logPane;
	
	public GuiLog(AutoscrollPane logPane){
		this.logPane = logPane;
		
		//System.out redirect
		System.setOut(new PrintStream(new UIOutputStream(logPane, STYLE_NORMAL)));
		
		//System.err redirect
		System.setErr(new PrintStream(new UIOutputStream(logPane, STYLE_ERROR)));
	}
	
	static{
		StyleConstants.setForeground(STYLE_NORMAL,Color.white);
		StyleConstants.setBold(STYLE_CAPTION, true);
		StyleConstants.setItalic(STYLE_CAPTION, true);
		StyleConstants.setForeground(STYLE_CAPTION,Color.decode("0xc0ff00"));
		
		StyleConstants.setForeground(STYLE_WARNING,Color.yellow);		
		StyleConstants.setForeground(STYLE_ERROR,Color.red);
		StyleConstants.setBold(STYLE_WARNING, true);
		StyleConstants.setBold(STYLE_ERROR, false);
		StyleConstants.setForeground(STYLE_ECHO,Color.white);
		StyleConstants.setForeground(STYLE_HIGHLIGHT,Color.decode("0xFF8888"));
		StyleConstants.setBold(STYLE_HIGHLIGHT, true);
		StyleConstants.setForeground(STYLE_TEST,Color.orange);
		
	}
	
	@Override
	public void print(String message) {
		logPane.append(message, STYLE_NORMAL);
	}
	
	@Override
	public void println(String message) {
		print(message);
		print("\n");
	}
	
	@Override
	public void warning(SyntaxNode where, String message) {	
		super.warning(where, message);
		logPane.append("\nWARNING: " +message + "\nat: " + SourceEnvironment.getLastEnvironment().getSourceInformation(where) + "\n", STYLE_WARNING);
	}

	@Override
	public void caption(String message) {
		logPane.append(message + "\n", STYLE_CAPTION);
	}
}
