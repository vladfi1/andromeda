package com.sc2mod.andromeda.parsing.phases;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import mopaqlib.MoPaQ;

import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.CompilationResult;
import com.sc2mod.andromeda.parsing.IParser;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.Language;
import com.sc2mod.andromeda.parsing.LanguageImpl;
import com.sc2mod.andromeda.parsing.ParserScheduler;
import com.sc2mod.andromeda.parsing.Source;
import com.sc2mod.andromeda.parsing.TriggerExtractor;
import com.sc2mod.andromeda.parsing.Workflow;
import com.sc2mod.andromeda.program.FileCollector;
import com.sc2mod.andromeda.program.Program;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.SourceListNode;
import com.sc2mod.andromeda.util.Pair;
import com.sc2mod.andromeda.util.StopWatch;

public class ParsePhase extends Phase {

	private LanguageImpl language;
	private IParser parser;
	private EnumMap<InclusionType, List<Source>> input;
	LinkedList<Pair<Source,InclusionType>> startQueue;
	
	public ParsePhase(Language language) {
		super(PhaseRunPolicy.IF_NO_ERRORS,"Parsing source files",true);
		this.language = language.getImpl();
	}

	@Override
	public void execute(CompilationEnvironment env,
			Workflow workflow) {

		this.parser = language.createParser(env);
		this.input = env.getParserInput();
		startQueue = new LinkedList<Pair<Source,InclusionType>>();
		addFilesOfType(InclusionType.NATIVE,"native library");
		addFilesOfType(InclusionType.LANGUAGE,"andromeda system library");
		addFilesOfType(InclusionType.MAIN,null);
		
	
		SourceListNode result = new ParserScheduler(16,env,startQueue,language).parse();
		
		env.setSyntaxTree(result);
	}
	
	private void addFilesOfType(InclusionType iType, String string) {
		List<Source> srcs = input.get(iType);
		if(srcs == null) return;
		for(Source s : srcs){
			startQueue.add(new Pair<Source, InclusionType>(s, iType));
		}
	}

	/*private SourceFileNode parseFile(InclusionType iType, Source file, SourceFileNode parsed, String typeName){
		System.out.print("    => Parsing " + typeName + " ["+ file.getName() +"]...");
		if(typeName == null) typeName = file.getTypeName();
		StopWatch timer = new StopWatch();
		timer.start();
		parsed = parser.parse(file,parsed, iType);
		System.out.println(" DONE (" + timer.getTime() + " ms)");
		return parsed;
	}
	
	private SourceFileNode parseFilesOfType(SourceFileNode parsed, InclusionType iType, String typeName){
		List<Source> srcs = input.get(iType);
		if(srcs == null) return parsed;
		for(Source s : srcs){
			parsed = parseFile(iType, s, parsed, typeName);
		}
		return parsed;
	}*/


}
