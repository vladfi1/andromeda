package com.sc2mod.andromeda.parsing;

import java.io.FileNotFoundException;

import com.sc2mod.andromeda.notifications.ErrorUtil;
import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.notifications.UnrecoverableProblem;
import com.sc2mod.andromeda.parser.AndromedaScanner;
import com.sc2mod.andromeda.parser.Symbol;
import com.sc2mod.andromeda.syntaxNodes.ImportListNode;
import com.sc2mod.andromeda.syntaxNodes.ImportNode;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.PackageDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.util.StopWatch;
import com.sc2mod.andromeda.util.ThreadUtil;
import com.sc2mod.andromeda.util.logging.Log;
import com.sc2mod.andromeda.util.logging.LogLevel;

public class ParserThread extends CompilerThread {

	private ParserScheduler scheduler;
	private boolean idle = true;
	private boolean interrupted;
	private IParser parser;
	private ParserInput input;
	private String typeName;
	private int numParsed;
	boolean packageDeclParsed;
	
	ParserThread(int num, ParserScheduler ps, CompilationEnvironment se, IParser parser) {
		super(se,"Parser Thread " + num);
		scheduler = ps;
		this.parser = parser;
	}
	
	public void run(){
		while(true){
			synchronized(this){
				while(idle && !interrupted)
					ThreadUtil.wait(this);
				if(interrupted)
					break;
			}
			try{
				doParseFile(parser,input,typeName);
			} catch (Throwable t){
				scheduler.registerException(this, t);
			}
			input = null;
			typeName = null;
			idle = true;
			scheduler.registerParseFinished(this);
		}
	}
	
	public void parseFile(IParser parser,  ParserInput input , String typeName){
		if(!idle){
			throw ErrorUtil.defaultInternalError();
		}
		//this.parser = parser;
		this.input = input;
		this.typeName = typeName;
		packageDeclParsed = false;

		synchronized(this){
			idle = false;
			this.notify();
		}
	}
		
	
	public void doParseFile(IParser parser,  ParserInput input , String typeName){
		//if(typeName == null) typeName = input.getSource().getTypeName();
		StopWatch timer = new StopWatch();
		timer.start();
		parse(parser,input);
		if(Log.log(LogLevel.DETAIL))
			Log.println(LogLevel.DETAIL,"    => Parsed " + typeName + " ["+ input.getSource().getName() +"] " + " (" + timer.getTime() + " ms) (" + ++numParsed + ")");
	}
	
	private SourceFileNode parse(IParser parser, ParserInput input)  {
		SourceReader a;
		try {
			a = compilationEnvironment.getSourceManager().getReader(input.getSource(), input.getInclusionType());
		} catch (FileNotFoundException e1) {
			if(input.getImportedBy() != null){
				throw Problem.ofType(ProblemId.INCLUDED_FILE_NOT_FOUND).at(input.getImportedBy()).details(input.getSource().getName())
					.raiseUnrecoverable();
			} else {
				throw Problem.ofType(ProblemId.INPUT_FILE_NOT_FOUND).details(input.getSource().getName())
					.raiseUnrecoverable();
			}
		}
		if (a == null)
			return null;
		parser.setScanner(new AndromedaScanner(a));
		Symbol sym;
		try {
			sym = parser.parse();
		} catch (UnrecoverableProblem e){
			throw e;
		} catch (Exception e) {
			throw new InternalProgramError(e);
		}
		SourceFileNode fi = ((SourceFileNode) sym.value);
		SourceInfo fileInfo = compilationEnvironment.getSourceManager().getSourceInfoById(a.getFileId());
		if(fileInfo == null)
			throw new InternalProgramError("File has no file info");
		fi.setSourceInfo(fileInfo);
		input.connect(fi);
		return fi;
	}

	
	public static void importRead(ImportNode sn){
		ParserThread t = (ParserThread)Thread.currentThread();
		t.scheduler.registerImport(sn);
	}
	
	public static void includeRead(IncludeNode pd){
		ParserThread t = (ParserThread)Thread.currentThread();
		t.scheduler.registerInclude(pd);
	}
	
	public static void packageRead(PackageDeclNode pd){
		ParserThread t = (ParserThread)Thread.currentThread();
		t.packageDeclParsed = true;
		t.scheduler.registerPackageDecl(pd);

	}

	public synchronized void setInterrupted() {
		interrupted = true;
		this.notifyAll();
	}
	
	@Override
	public String toString() {
		return "Parser Thread" + getId();
	}
	

}
