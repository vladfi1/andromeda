package com.sc2mod.andromeda.parsing.phases;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

import mopaqlib.MoPaQ;
import mopaqlib.MoPaQException;

import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.CompilationResult;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.Language;
import com.sc2mod.andromeda.parsing.LanguageImpl;
import com.sc2mod.andromeda.parsing.Source;
import com.sc2mod.andromeda.parsing.TriggerExtractor;
import com.sc2mod.andromeda.parsing.Workflow;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.program.FileCollector;
import com.sc2mod.andromeda.program.Program;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.util.StopWatch;
import com.sc2mod.andromeda.util.logging.Log;
import com.sc2mod.andromeda.util.logging.LogLevel;

public class InputCollectionPhase extends Phase {

	private final LanguageImpl language;
	public InputCollectionPhase(Language l){
		super(PhaseRunPolicy.IF_NO_ERRORS, "Collecting input sources", true);
		this.language = l.getImpl();
	}
	
	
	@Override
	public void execute(CompilationEnvironment env,
			Workflow workflow) {
		SourceFileNode af = null;
		Configuration cfg = env.getConfig();
		File mapIn = cfg.getParamFile(Parameter.FILES_MAP_IN);
		File triggersIn = cfg.getParamFile(Parameter.FILES_MAP_TRIGGERS_IN);
		
		//Assemble file lists
		List<Source> natives = FileCollector.getFilesFromList(cfg.getParamFile(Parameter.FILES_NATIVE_LIB_FOLDER),cfg.getParamCommaList(Parameter.FILES_NATIVE_LIST));
		List<Source> langFiles = language.getLanguageSources(env);
		List<Source> triggers = null;
		
		try {
			if(mapIn != null) {
				StopWatch timer = new StopWatch();
				timer.start();
				if(Log.log(LogLevel.DETAIL))
					Log.println(LogLevel.DETAIL, "Extracting code from map file...");
				
				System.out.print("Extracting code from map file...");
				MoPaQ map = new MoPaQ(mapIn);		
				triggers = new TriggerExtractor().extractTriggers(map);
				System.out.println(" DONE (" + timer.getTime() + " ms)");
			} else {
				if(triggersIn != null) {
					triggers = new TriggerExtractor().extractTriggers(triggersIn);
				}
			}
		} catch (IOException e) {
			throw Problem.ofType(ProblemId.EXTERNAL_IO_EXCEPTION)
					.details(e.getMessage())
					.raiseUnrecoverable();
		} catch (MoPaQException e) {
			throw Problem.ofType(ProblemId.EXTERNAL_MOPAQ_ERROR)
			.details(e.getMessage())
			.raiseUnrecoverable();
		} catch (DataFormatException e) {
			throw Problem.ofType(ProblemId.EXTERNAL_MOPAQ_ERROR)
			.details(e.getMessage())
			.raiseUnrecoverable();
		}
		
		env.addParserInput(InclusionType.NATIVE, natives);
		env.addParserInput(InclusionType.LANGUAGE, langFiles);
		env.addParserInput(InclusionType.MAIN, triggers);
		
	
	}

}
