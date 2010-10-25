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
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.syntaxNodes.EmptyStatement;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

/**
 * Base class for any kind of compilation error that could occur
 * @author J. 'gex' Finis
 *
 */
public class CompilationErrorOld extends Problem {

	
	
	public CompilationError(int problemId, CompilationEnvironment srcEnv) {
		super(problemId, srcEnv, ProblemSeverity.ERROR);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

	private SyntaxNode[] where;
	private int type;

	private String description;
	
	public int getId() {
		return type;
	}

	public static final int CODE_NO_CONSTRUCTOR_WITH_THAT_SIGNATURE = 1;
	public static final int CODE_ONLY_IMPLICIT_CONSTRUCTOR_AVAILABLE = 2;
	public static final int IMPLICIT_CONSTRUCTOR_INVOCATION_IMPOSSIBLE = 3;
	
	public CompilationError(int position, int end, String description){
		super("\nat: " + CompilationEnvironment.getLastEnvironment().getSourceInformation(position, end) + "\n" + description);
		SyntaxNode where = new EmptyStatement();
		where.setPos(position,end);
		this.where = new SyntaxNode[]{where};
		this.description = description;
	}
	
	public CompilationError(SyntaxNode s, String description, Throwable arg1) {
		super("\nat: " + CompilationEnvironment.getLastEnvironment().getSourceInformation(s) + "\n" + description , arg1);
		where = new SyntaxNode[]{s};
		this.description = description;
	}

	public CompilationError(SyntaxNode s, String description) {
		super("\nat: " + CompilationEnvironment.getLastEnvironment().getSourceInformation(s) + "\n" + description);
		where = new SyntaxNode[]{s};
		this.description = description;
	}
	
	public CompilationError(SyntaxNode s, SyntaxNode s2, String description, String secondNodeDescription) {
		super("\nat: " + CompilationEnvironment.getLastEnvironment().getSourceInformation(s) + "\n" + description + "\n" 
				+ secondNodeDescription + ":\n" + CompilationEnvironment.getLastEnvironment().getSourceInformation(s2));
		where = new SyntaxNode[]{s,s2};
		this.description = description;
	}
	
	public CompilationError(int type, SyntaxNode s, String description) {
		super("\nat: " + CompilationEnvironment.getLastEnvironment().getSourceInformation(s) + "\n" + description);
		where = new SyntaxNode[]{s};
		this.type = type;
		this.description = description;
	}
	
	public CompilationError(int type, SyntaxNode s, SyntaxNode s2, String description, String secondNodeDescription) {
		super("\nat: " + CompilationEnvironment.getLastEnvironment().getSourceInformation(s) + "\n" + description + "\n" 
				+ secondNodeDescription + ":\n" + CompilationEnvironment.getLastEnvironment().getSourceInformation(s2));
		where = new SyntaxNode[]{s,s2};

		this.type = type;
		this.description = description;
	}
	
	public CompilationError(SyntaxNode s, SyntaxNode s2, SyntaxNode s3, String description, String secondNodeDescription, String thirdNodeDescription) {
		super("\nat: " + CompilationEnvironment.getLastEnvironment().getSourceInformation(s) + "\n" + description + "\n" 
				+ secondNodeDescription + ":\n" + CompilationEnvironment.getLastEnvironment().getSourceInformation(s2)
				+ thirdNodeDescription + ":\n" +  CompilationEnvironment.getLastEnvironment().getSourceInformation(s3));
		where = new SyntaxNode[]{s,s2,s3};

		this.description = description;
	}

	public CompilationError(String string) {
		super(string);
		this.description = string;
		where = new SyntaxNode[]{};
	}

	@Override
	public SourceLocation[] getPositions() {
		if(env == null) throw new Error("Source environment not set, cannot create error positions!");
		SourceLocation[] sp = new SourceLocation[where.length];
		for(int i=0;i<where.length;i++){
			sp[i] = new LazySourceLocation(where[i],env);
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

	/**
	 * Raises this compilation error.
	 * Use this method to "throw" errors from which the compiler can recover.
	 * If you have an error from which the compiler cannot recover, throw it instead.
	 * 
	 * This method must be called in a CompilerThread (because this thread is used to
	 * get the correct Source Environment to attach this error to).
	 */
	public void raise(){
		
	}
	
}
