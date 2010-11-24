package com.sc2mod.andromeda.parsing.phases;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import mopaqlib.MoPaQ;
import mopaqlib.MoPaQException;

import com.sc2mod.andromeda.codegen.CodeGenVisitor;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.OutputMemoryStats;
import com.sc2mod.andromeda.parsing.StringBufferWriter;
import com.sc2mod.andromeda.parsing.Workflow;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.problems.ErrorUtil;
import com.sc2mod.andromeda.program.MapFormatException;
import com.sc2mod.andromeda.program.ScriptInjector;

public class WriteCodePhase extends Phase {

	public WriteCodePhase() {
		super(PhaseRunPolicy.IF_NO_ERRORS, "Writing output code", true);
	}

	private String writeCode(CompilationEnvironment env, CodeGenVisitor generator) throws IOException, MoPaQException, MapFormatException{
		Configuration options = env.getConfig();
		File outDir = options.getParamFile(Parameter.FILES_OUT_DIR);
		File mapScriptIn = options.getParamFile(Parameter.FILES_MAP_SCRIPT_IN);
		File mapOut = options.getParamFile(Parameter.FILES_MAP_OUT);
		String outputName = outDir.getPath() + "/Andromeda.galaxy";
		StringBufferWriter w = new StringBufferWriter(); 
		generator.flushOutCode(w);
		
		OutputMemoryStats outputStats = generator.getOutputStatistics();
		env.getTransientData().setOutputStats(outputStats);
		
		String code = w.toString();
		MoPaQ map = env.getTransientData().getOpenedMap();

		//Create out directory
		outDir.mkdirs();
		
		//Write to external file
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outDir,"Andromeda.galaxy")));
		bw.write(code);
		bw.close();
		
		//If we have an input map, manipulate its mapScript
		if(map != null|| mapScriptIn != null) {
			
			//Write map script to output folder
			String mapScript;
			if(map != null) {
				mapScript = ScriptInjector.getManipulatedMapScript(map);
			} else {
				mapScript = ScriptInjector.getManipulatedMapScript(mapScriptIn);
			}
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(outDir,"MapScript.galaxy")));
			bw2.write(mapScript);
			bw2.close();
			
			//Write to map file if specified
			if(mapOut != null) {
				ScriptInjector.injectAndromeda(map, mapScript, code);
				map.save(mapOut);
				outputName = mapOut.getName();
			}
		}
		
		
		return outputName;
	}
	
	@Override
	public void execute(CompilationEnvironment env, Workflow workflow) {
		try {
			String outputName = writeCode(env,env.getTransientData().getCodeGenerator());
			env.getTransientData().setOutputName(outputName);
		} catch (IOException e) {
			ErrorUtil.raiseIOProblem(e, false);
		} catch (MoPaQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MapFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
