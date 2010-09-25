package com.sc2mod.andromeda.parsing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

import mopaqlib.MoPaQ;
import mopaqlib.MoPaQException;

import com.sc2mod.andromeda.classes.ClassFieldCalculator;
import com.sc2mod.andromeda.classes.ClassGenerator;
import com.sc2mod.andromeda.classes.MetaClassInit;
import com.sc2mod.andromeda.classes.VirtualCallTable;
import com.sc2mod.andromeda.classes.indexSys.IndexClassFieldCalculator;
import com.sc2mod.andromeda.classes.indexSys.IndexClassGenerator;
import com.sc2mod.andromeda.codegen.CodeGenVisitor;
import com.sc2mod.andromeda.codegen.NameGenerationVisitor;
import com.sc2mod.andromeda.codetransform.CallHierarchyVisitor;
import com.sc2mod.andromeda.codetransform.CodeTransformationVisitor;
import com.sc2mod.andromeda.codetransform.SyntaxGenerator;
import com.sc2mod.andromeda.codetransform.UnusedFinder;
import com.sc2mod.andromeda.codetransform.VirtualCallResolver;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.notifications.Message;
import com.sc2mod.andromeda.notifications.UnlocatedErrorMessage;
import com.sc2mod.andromeda.notifications.UnrecoverableProblem;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.parsing.phases.Phase;
import com.sc2mod.andromeda.program.FileCollector;
import com.sc2mod.andromeda.program.MapFormatException;
import com.sc2mod.andromeda.program.MapRunner;
import com.sc2mod.andromeda.program.Program;
import com.sc2mod.andromeda.program.ScriptInjector;
import com.sc2mod.andromeda.semAnalysis.SemanticAnalysis;
import com.sc2mod.andromeda.syntaxNodes.SourceFile;
import com.sc2mod.andromeda.util.StopWatch;
import com.sc2mod.andromeda.util.logging.Log;
import com.sc2mod.andromeda.util.logging.LogFormat;
import com.sc2mod.andromeda.util.logging.LogLevel;
import com.sc2mod.andromeda.xml.gen.ResultXMLWriter;
import com.sc2mod.andromeda.xml.gen.StructureXMLVisitor;

public class Workflow {
	
	private StopWatch overallTimer = new StopWatch();
	private StopWatch phaseTimer = new StopWatch();
	protected CompilationEnvironment env;
	
	private final Phase[] phases;

	public Workflow(List<Source> files, Configuration o, Phase... phases) {
		this.phases = phases;
		this.env = new CompilationEnvironment(o);
		this.env.addParserInput(InclusionType.MAIN, files);
	}
	
	public CompilationEnvironment execute(){
		CompilerThread ct = new CompilerThread(env){
			@Override
			public void run() {
				Workflow.this.doExecute();
			}
		};
		ct.run();
		try { ct.join(); } catch (InterruptedException e) {}
		return env;
	}
	
	/**
	 * Executes the phases.
	 */
	private void doExecute(){
		overallTimer.start();
		if(Log.log(LogLevel.CAPTION)) printCaption();
		boolean logPhases = (Log.log(LogLevel.PHASE));
		for(int i = 0;i < phases.length;i++){
			Phase p = phases[i];
			if(p.canExecute(env)){
				try {
					if(logPhases&&p.doLog())
						Log.print(LogLevel.PHASE,p.getDescription() + "... ");
					phaseTimer.start();
					p.execute(env,this);
					if(logPhases&&p.doLog()){
						Log.print(LogLevel.PHASE,LogFormat.ERROR," DONE (" + phaseTimer.getTime() + " ms)");
					}
				} catch (UnrecoverableProblem e) {
					if(logPhases&&p.doLog())
						Log.print(LogLevel.PHASE,LogFormat.ERROR,"ABORTED");
					
				}
			}
		}
		printResult();
	}
	
	private void printResult() {
		if(env.getResult().isSuccessful()){
			
		} else {
			
		}
	}

	private void printCaption() {
		Configuration config = env.getConfig();
		File mapIn = config.getParamFile(Parameter.FILES_MAP_IN);
		int numFiles = env.getParserInput().get(InclusionType.MAIN).size() + (config.getParamFile(Parameter.FILES_MAP_TRIGGERS_IN)==null?0:1);
		if(mapIn != null) {
			if(numFiles > 0) {
				Log.println(LogLevel.CAPTION,"+++ Compiling map " + mapIn.getName() + " and " + numFiles + " additional script files +++");
			} else {
				Log.println(LogLevel.CAPTION,"+++ Compiling map " + mapIn.getName() + " +++");
			}
		} else {
			Log.println(LogLevel.CAPTION,"+++ Compiling " + numFiles + " script files +++");
		}
	}


	
	protected void reportError(Throwable e) {
		e.printStackTrace();
		System.err.println("--- Compilation unsuccessful, no code generated! ---");
	}
	
}
