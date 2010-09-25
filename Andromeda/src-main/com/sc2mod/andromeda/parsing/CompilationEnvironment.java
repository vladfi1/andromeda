package com.sc2mod.andromeda.parsing;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.syntaxNodes.SourceFile;

public class CompilationEnvironment {

	private final Configuration config;
	private final CompilationFileManager fileManager;
	private final CompilationResult result;
	private SourceFile syntaxTree;
	private final EnumMap<InclusionType,List<Source>> parserInput = new EnumMap<InclusionType, List<Source>>(InclusionType.class);
	
	public SourceFile getSyntaxTree() {
		return syntaxTree;
	}

	public void setSyntaxTree(SourceFile syntaxTree) {
		this.syntaxTree = syntaxTree;
	}
	
	public EnumMap<InclusionType, List<Source>> getParserInput() {
		return parserInput;
	}

	public void addParserInput(InclusionType it, Source src){
		if(src == null) return;
		List<Source> sources = parserInput.get(it);
		if(sources == null){
			sources = new ArrayList<Source>();
			parserInput.put(it, sources);
		}
		sources.add(src);
	}
	
	public void addParserInput(InclusionType it, List<Source> src){
		if(src == null) return;
		List<Source> sources = parserInput.get(it);
		if(sources == null){
			sources = new ArrayList<Source>();
			parserInput.put(it, sources);
		}
		sources.addAll(src);
	}
	

	
	
	public Configuration getConfig() {
		return config;
	}

	public CompilationFileManager getFileManager() {
		return fileManager;
	}

	public CompilationResult getResult() {
		return result;
	}
	
	public CompilationEnvironment(Configuration config){
		this.config = config;
		fileManager = new CompilationFileManager(config);
		result = new CompilationResult();
	}
}
