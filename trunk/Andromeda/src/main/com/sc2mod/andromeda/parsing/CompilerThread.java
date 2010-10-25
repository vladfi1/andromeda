package com.sc2mod.andromeda.parsing;

import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class CompilerThread extends Thread {

	public final CompilationEnvironment compilationEnvironment;
	
	CompilerThread(CompilationEnvironment se){
		this.compilationEnvironment = se;
	}
	
	public static String getSourceInfo(SyntaxNode sn){
		CompilerThread t = (CompilerThread)Thread.currentThread();
		String info = t.compilationEnvironment.getFileManager().getSourceInformation(sn);
		return info;
	}
	
	public static CompilationEnvironment getEnvironment(){
		CompilerThread t = (CompilerThread)Thread.currentThread();
		return t.compilationEnvironment;
	}
}
