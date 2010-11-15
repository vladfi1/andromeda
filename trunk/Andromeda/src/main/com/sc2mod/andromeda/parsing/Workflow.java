package com.sc2mod.andromeda.parsing;

import java.io.File;
import java.util.List;

import com.sc2mod.andromeda.notifications.ErrorUtil;
import com.sc2mod.andromeda.notifications.UnrecoverableProblem;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.parsing.phases.Phase;
import com.sc2mod.andromeda.util.StopWatch;
import com.sc2mod.andromeda.util.logging.Log;
import com.sc2mod.andromeda.util.logging.LogFormat;
import com.sc2mod.andromeda.util.logging.LogLevel;

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
		ct.start();
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
					try{
						if(logPhases&&p.doLog())
							Log.print(LogLevel.PHASE,p.getDescription() + "... ");
						phaseTimer.start();
						p.execute(env,this);
						if(logPhases&&p.doLog()){
							Log.print(LogLevel.PHASE,LogFormat.ERROR," DONE (" + phaseTimer.getTime() + " ms)\n");
						}
					} catch(UnrecoverableProblem e) {
						throw e;
				 	} catch (Throwable e){
				 		throw ErrorUtil.raiseInternalProblem(e);
					}
				} catch (UnrecoverableProblem e) {
					Log.print(LogLevel.PHASE," ABORTED (" + phaseTimer.getTime() + " ms)\n");
					//if(logPhases&&p.doLog())
					//	Log.print(LogLevel.PHASE,LogFormat.ERROR,"--- Compilation aborted due to errors! ---\n\n");
				}
			}
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
	
	public long getOverallTime(){
		return overallTimer.getTime();
	}


//	
//	protected void reportError(Throwable e) {
//		e.printStackTrace();
//		System.err.println("--- Compilation unsuccessful, no code generated! ---");
//	}
	
}
