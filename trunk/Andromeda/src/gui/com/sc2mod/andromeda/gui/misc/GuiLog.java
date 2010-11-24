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
import java.util.EnumMap;
import java.util.Map;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.sc2mod.andromeda.gui.forms.AutoscrollPane;
import com.sc2mod.andromeda.parsing.SourceManager;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.util.logging.LogFormat;
import com.sc2mod.andromeda.util.logging.LogLevel;
import com.sc2mod.andromeda.util.logging.Logger;

public class GuiLog extends Logger{
	
	private static final SimpleAttributeSet STYLE_NORMAL = new SimpleAttributeSet();
	public static final SimpleAttributeSet STYLE_ERROR = new SimpleAttributeSet();
	public static final SimpleAttributeSet STYLE_WARNING = new SimpleAttributeSet();
	private static final SimpleAttributeSet STYLE_ECHO = new SimpleAttributeSet();
	private static final SimpleAttributeSet STYLE_CAPTION = new SimpleAttributeSet();
	private static final SimpleAttributeSet STYLE_TEST = new SimpleAttributeSet();
	private static final SimpleAttributeSet STYLE_HIGHLIGHT = new SimpleAttributeSet();
	private AutoscrollPane logPane;
	
	private static Map<LogFormat, SimpleAttributeSet> formats = new EnumMap<LogFormat, SimpleAttributeSet>(LogFormat.class);
	
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
		
		formats.put(LogFormat.NORMAL, STYLE_NORMAL);
		formats.put(LogFormat.CAPTION, STYLE_CAPTION);
		formats.put(LogFormat.ERROR, STYLE_ERROR);
		
	}

	@Override
	public void print(LogLevel logLevel, LogFormat logFormat, String message) {
		SimpleAttributeSet style = formats.get(logFormat);
		if(style == null)
			style = STYLE_NORMAL;
		logPane.append(message, style);
	}

	@Override
	public void printProblem(Problem problem, boolean printStackTraces) {
		SimpleAttributeSet style;
		switch(problem.getSeverity()){
		case WARNING:
			style = STYLE_WARNING;
			break;
		case INFO:
			style = STYLE_NORMAL;
			break;
		default:
			style = STYLE_ERROR;
		}
		logPane.append(getDefaultProblemString(problem, printStackTraces), style);
		 
	}
}
