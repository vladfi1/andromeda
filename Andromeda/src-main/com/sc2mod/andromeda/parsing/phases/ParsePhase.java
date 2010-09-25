package com.sc2mod.andromeda.parsing.phases;

import java.util.EnumMap;
import java.util.List;

import mopaqlib.MoPaQ;

import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.CompilationResult;
import com.sc2mod.andromeda.parsing.IParser;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.Language;
import com.sc2mod.andromeda.parsing.LanguageImpl;
import com.sc2mod.andromeda.parsing.Source;
import com.sc2mod.andromeda.parsing.TriggerExtractor;
import com.sc2mod.andromeda.parsing.Workflow;
import com.sc2mod.andromeda.program.FileCollector;
import com.sc2mod.andromeda.program.Program;
import com.sc2mod.andromeda.syntaxNodes.SourceFile;
import com.sc2mod.andromeda.util.StopWatch;

public class ParsePhase extends Phase {

	private LanguageImpl language;
	private IParser parser;
	private EnumMap<InclusionType, List<Source>> input;
	
	public ParsePhase(Language language) {
		super(PhaseRunPolicy.IF_NO_ERRORS,"Parsing source files",true);
		this.language = language.getImpl();
	}

	@Override
	public void execute(CompilationEnvironment env,
			Workflow workflow) {

		this.parser = language.createParser(env);
		this.input = env.getParserInput();
		SourceFile result = null;
		result = parseFilesOfType(result,InclusionType.NATIVE,"native library");
		result = parseFilesOfType(result,InclusionType.LANGUAGE,"andromeda system library");
		result = parseFilesOfType(result,InclusionType.MAIN,null);
		
		env.setSyntaxTree(result);
	}
	
	private SourceFile parseFile(InclusionType iType, Source file, SourceFile parsed, String typeName){
		System.out.print("Parsing " + typeName + " ["+ file.getName() +"]...");
		if(typeName == null) typeName = file.getTypeName();
		StopWatch timer = new StopWatch();
		timer.start();
		parsed = parser.parse(file,parsed, iType);
		System.out.println(" DONE (" + timer.getTime() + " ms)");
		return parsed;
	}
	
	private SourceFile parseFilesOfType(SourceFile parsed, InclusionType iType, String typeName){
		List<Source> srcs = input.get(iType);
		if(srcs == null) return parsed;
		for(Source s : srcs){
			parsed = parseFile(iType, s, parsed, typeName);
		}
		return parsed;
	}


}
