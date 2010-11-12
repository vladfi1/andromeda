package com.sc2mod.andromeda.parsing.phases;

import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.OutputMemoryStats;
import com.sc2mod.andromeda.parsing.Workflow;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.program.Program;
import com.sc2mod.andromeda.util.logging.Log;
import com.sc2mod.andromeda.util.logging.LogFormat;
import com.sc2mod.andromeda.util.logging.LogLevel;

public class PrintResultsPhase extends Phase {

	public PrintResultsPhase() {
		super(PhaseRunPolicy.ALWAYS, "Printing results", false);
	}

	@Override
	public void execute(CompilationEnvironment env, Workflow workflow) {
		if(env.getResult().isSuccessful()){
			writeSuccessful(env,workflow);
		} else {
			writeFailure();
		}
		
	}

	private void writeFailure() {
		Log.print(LogLevel.PHASE,LogFormat.ERROR,"--- Compilation aborted due to errors! ---\n\n");
	}

	private void writeSuccessful(CompilationEnvironment env, Workflow workflow) {
		LogLevel ll = LogLevel.PHASE;
		long bytesIn = env.getSourceManager().getBytesRead();
		int fileCount = env.getSourceManager().getFileCount();
		boolean codeGenerated = !env.getConfig().getParamBool(Parameter.MISC_NO_CODE_GEN);
		OutputMemoryStats outputStats = env.getTransientData().getOutputStats();
		long time = workflow.getOverallTime();
		int bytesOut = 0;
		String outputName = "N/A";
		
		Log.print(ll, "+++++++++++ Compilation Report +++++++++++\n");
		
		Log.print(ll, 
				"  => Successfully compiled "
				+ fileCount
				+ " files ("
				+ bytesIn
				+ " bytes).\n"
			);
		
		if(codeGenerated){
			bytesOut = outputStats.bytesOut;
			outputName = env.getTransientData().getOutputName();
			Log.print(ll, 
					"  => Produced code: " + bytesOut	+ " bytes.\n"
				);
		}
		
		Log.print(ll, 
				"  => Time: "
				+ time
				+ " ms ("
				+ (int) (((bytesOut + bytesIn) / (double) (1 << 10)) / (time / 1000.0))
				+ " KB/s)\n"
			);
				
		Log.print(ll, "  => Memory Usage: " + ((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1<<20)) + " MB\n");
		
		if(outputStats!=null) {
			Log.print(ll, "  => Generated code memory usage:\n");
			Log.print(ll, "    => Global variables: " + outputStats.globalsBytes / 1000f + " KB\n" );
			Log.print(ll, "    => String literals: " + outputStats.stringLiteralBytes / 1000f + " KB\n" );
			Log.print(ll, " >>> Total in-game memory usage (without op-code): " + outputStats.getRuntimeMemoryUsage() / 1000f 
					+ " KB (" + (((int)(outputStats.getRuntimeMemoryUsage()/(float)(1<<21)*10000))/100f) + "% of the galaxy hard limit (blame Blizzard for it))\n"  );
		}
		
		Log.print(LogLevel.CAPTION,	"+++ Compilation successful" + (!codeGenerated?"":", code written to " + outputName) + " +++\n");
	}

}
