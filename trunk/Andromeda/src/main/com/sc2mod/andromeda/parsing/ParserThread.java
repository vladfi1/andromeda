package com.sc2mod.andromeda.parsing;

import java.io.FileNotFoundException;

import com.sc2mod.andromeda.parsing.framework.IParser;
import com.sc2mod.andromeda.parsing.framework.IParserHook;
import com.sc2mod.andromeda.parsing.framework.ParserInput;
import com.sc2mod.andromeda.problems.ErrorUtil;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
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
	private ParserThreadInput input;
	private String typeName;
	private int numParsed;
	boolean packageDeclParsed;
	private String qualifiedCUName;
	
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
	
	public void parseFile(IParser parser,  ParserThreadInput input , String typeName){
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
		
	
	public void doParseFile(IParser parser,  ParserThreadInput input , String typeName){
		//if(typeName == null) typeName = input.getSource().getTypeName();
		StopWatch timer = new StopWatch();
		timer.start();
		parse(parser,input);
		if(Log.log(LogLevel.DETAIL))
			Log.println(LogLevel.DETAIL,"    => Parsed " + typeName + " ["+ input.getSource().getName() +"] " + " (" + timer.getTime() + " ms) (" + ++numParsed + ")");
	}
	
	private SourceFileNode parse(IParser parser, ParserThreadInput input)  {
		//Get a reader for the source
		SourceReader a;
		try {
			a = compilationEnvironment.getSourceManager().getReader(input.getSource(), input.getInclusionType());
		} catch (FileNotFoundException e1) {
			if(input.getImportedBy() != null){
				throw Problem.ofType(ProblemId.INCLUDED_FILE_NOT_FOUND).at(input.getImportedBy()).details(input.getSource().getName())
					.raiseUnrecoverable();
			} else {
				throw Problem.ofType(ProblemId.INPUT_FILE_NOT_FOUND).details(input.getSource().getName(),input.getSource().getFullPath())
					.raiseUnrecoverable();
			}
		}
		
		//If no reader, then this source has already been parsed, so do nothing and return null
		if (a == null)
			return null;
		
		//Set parser hook so the thread gets informed about package decls, imports and includes
		parser.setParserHook(THREAD_HOOK);
		
		//Do the parsing
		SourceFileNode fi = (SourceFileNode) parser.parse(new ParserInput(a, a.getFileId()));
		
		//Set file info for the parsed source file node
		SourceInfo fileInfo = compilationEnvironment.getSourceManager().getSourceInfoById(a.getFileId());
		if(fileInfo == null)
			throw new InternalProgramError("File has no file info");
		fileInfo.setQualifiedName(qualifiedCUName);
		fi.setSourceInfo(fileInfo);
		
		//Connect to input.
		//For includes, this directly adds this file to the include
		//For imports and main files, the file is added to the global file list
		input.connect(fi);
		return fi;
	}

	private static ParserThreadHook THREAD_HOOK = new ParserThreadHook();
	
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
		t.qualifiedCUName = t.scheduler.registerPackageDecl(pd);
		
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

class ParserThreadHook implements IParserHook{

	@Override
	public void importRead(ImportNode i) {
		ParserThread.importRead(i);
	}

	@Override
	public void includeRead(IncludeNode in) {
		ParserThread.includeRead(in);
	}

	@Override
	public void packageDeclRead(PackageDeclNode p) {
		ParserThread.packageRead(p);
	}
	
}
