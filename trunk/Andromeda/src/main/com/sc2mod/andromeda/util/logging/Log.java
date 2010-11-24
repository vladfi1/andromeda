package com.sc2mod.andromeda.util.logging;

import java.util.List;

import com.sc2mod.andromeda.problems.Message;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.program.Program;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import static com.sc2mod.andromeda.util.logging.LogFormat.*;

public class Log {

	private static Logger getCurrentLogger(){
		return Program.log;
	}
	
	public static void print(LogLevel ll, LogFormat lf, String message){
		getCurrentLogger().print(ll,lf,message);
	}
	
	public static void print(LogLevel ll, String message){
		getCurrentLogger().print(ll,ll.getFormat(),message);
	}
	

	public static void println(LogLevel ll, String message){
		getCurrentLogger().println(ll,ll.getFormat(),message);
	}
	
	public static boolean log(LogLevel ll){
		return getCurrentLogger().log(ll);
	}

	public static void printProblem(Problem problem, boolean printStackTraces) {
		getCurrentLogger().printProblem(problem, printStackTraces);
	}
	
//	public void caption(String message);
//	
//	public void println(String message);
//
//	public void warning(SyntaxNode where, String message);
//	
//	public List<Message> getMessages();
//	
//	public List<Message> flushMessages();
//	
//	public void addMessage(Message m);
}
