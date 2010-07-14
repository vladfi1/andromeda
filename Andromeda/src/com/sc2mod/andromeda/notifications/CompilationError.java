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

import com.sc2mod.andromeda.parsing.AndromedaReader;
import com.sc2mod.andromeda.parsing.SourceEnvironment;
import com.sc2mod.andromeda.syntaxNodes.EmptyStatement;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

/**
 * Base class for any kind of compilation error that could occur
 * @author J. 'gex' Finis
 *
 */
public class CompilationError extends RuntimeException implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	private SyntaxNode[] where;
	private int type;
	private SourceEnvironment env;

	public void setEnvironment(SourceEnvironment env) {
		this.env = env;
	}

	private String description;
	
	public int getType() {
		return type;
	}

	public static final int CODE_NO_CONSTRUCTOR_WITH_THAT_SIGNATURE = 1;
	public static final int CODE_ONLY_IMPLICIT_CONSTRUCTOR_AVAILABLE = 2;
	public static final int IMPLICIT_CONSTRUCTOR_INVOCATION_IMPOSSIBLE = 3;
	
	public CompilationError(int position, int end, String description){
		super("\nat: " + SourceEnvironment.getLastEnvironment().getSourceInformation(position, end) + "\n" + description);
		SyntaxNode where = new EmptyStatement();
		where.setPos(position,end);
		this.where = new SyntaxNode[]{where};
		this.description = description;
	}
	
	public CompilationError(SyntaxNode s, String description, Throwable arg1) {
		super("\nat: " + SourceEnvironment.getLastEnvironment().getSourceInformation(s) + "\n" + description , arg1);
		where = new SyntaxNode[]{s};
		this.description = description;
	}

	public CompilationError(SyntaxNode s, String description) {
		super("\nat: " + SourceEnvironment.getLastEnvironment().getSourceInformation(s) + "\n" + description);
		where = new SyntaxNode[]{s};
		this.description = description;
	}
	
	public CompilationError(SyntaxNode s, SyntaxNode s2, String description, String secondNodeDescription) {
		super("\nat: " + SourceEnvironment.getLastEnvironment().getSourceInformation(s) + "\n" + description + "\n" 
				+ secondNodeDescription + ":\n" + SourceEnvironment.getLastEnvironment().getSourceInformation(s2));
		where = new SyntaxNode[]{s,s2};
		this.description = description;
	}
	
	public CompilationError(int type, SyntaxNode s, String description) {
		super("\nat: " + SourceEnvironment.getLastEnvironment().getSourceInformation(s) + "\n" + description);
		where = new SyntaxNode[]{s};
		this.type = type;
		this.description = description;
	}
	
	public CompilationError(int type, SyntaxNode s, SyntaxNode s2, String description, String secondNodeDescription) {
		super("\nat: " + SourceEnvironment.getLastEnvironment().getSourceInformation(s) + "\n" + description + "\n" 
				+ secondNodeDescription + ":\n" + SourceEnvironment.getLastEnvironment().getSourceInformation(s2));
		where = new SyntaxNode[]{s,s2};

		this.type = type;
		this.description = description;
	}
	
	public CompilationError(SyntaxNode s, SyntaxNode s2, SyntaxNode s3, String description, String secondNodeDescription, String thirdNodeDescription) {
		super("\nat: " + SourceEnvironment.getLastEnvironment().getSourceInformation(s) + "\n" + description + "\n" 
				+ secondNodeDescription + ":\n" + SourceEnvironment.getLastEnvironment().getSourceInformation(s2)
				+ thirdNodeDescription + ":\n" +  SourceEnvironment.getLastEnvironment().getSourceInformation(s3));
		where = new SyntaxNode[]{s,s2,s3};

		this.description = description;
	}

	public CompilationError(String string) {
		super(string);
		this.description = string;
		where = new SyntaxNode[]{};
	}

	@Override
	public SourcePosition[] getPositions() {
		if(env == null) throw new Error("Source environment not set, cannot create error positions!");
		SourcePosition[] sp = new SourcePosition[where.length];
		for(int i=0;i<where.length;i++){
			sp[i] = new LazySourcePosition(where[i],env);
		}
		return sp;		
	}

	@Override
	public int getSeverity() {
		return MessageSeverity.ERROR;
	}

	@Override
	public String getText() {
		return description;
	}

	@Override
	public int getCode() {
		// TODO Auto-generated method stub
		throw new Error("Not implemented!");
	}

	
}
