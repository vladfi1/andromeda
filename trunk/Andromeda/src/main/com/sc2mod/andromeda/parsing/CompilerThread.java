package com.sc2mod.andromeda.parsing;

import java.util.HashSet;

import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class CompilerThread extends Thread {

	public final CompilationEnvironment compilationEnvironment;
	private HashSet<Integer> uniqueEvent = new HashSet<Integer>();
	
	CompilerThread(CompilationEnvironment se){
		this.compilationEnvironment = se;
	}
	
	public static String getSourceInfo(SyntaxNode sn){
		CompilerThread t = (CompilerThread)Thread.currentThread();
		String info = t.compilationEnvironment.getSourceManager().getSourceInformation(sn);
		return info;
	}
	
	public static CompilationEnvironment getEnvironment(){
		CompilerThread t = (CompilerThread)Thread.currentThread();
		return t.compilationEnvironment;
	}

	public static boolean registerUniqueEvent(int raiseNumber) {
		CompilerThread t = (CompilerThread)Thread.currentThread();
		return t.uniqueEvent.add(raiseNumber);
		
	}
}
