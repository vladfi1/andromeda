package com.sc2mod.andromeda.parsing;

public class CompilerThread extends Thread {

	public final CompilationEnvironment compilationEnvironment;
	
	CompilerThread(CompilationEnvironment se){
		this.compilationEnvironment = se;
	}
}
