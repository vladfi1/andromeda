package com.sc2mod.andromeda.parsing;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;

public class CompilationEnvironment {

	private final Configuration config;
	private final CompilationFileManager fileManager;
	private final CompilationResult result;
	private Environment semanticEnvironment;
	private SourceFileNode syntaxTree;
	private final EnumMap<InclusionType,List<Source>> parserInput = new EnumMap<InclusionType, List<Source>>(InclusionType.class);
	
	public SourceFileNode getSyntaxTree() {
		return syntaxTree;
	}

	public void setSyntaxTree(SourceFileNode syntaxTree) {
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

	public void setSemanticEnvironment(Environment semanticEnvironment) {
		this.semanticEnvironment = semanticEnvironment;
	}

	public Environment getSemanticEnvironment() {
		return semanticEnvironment;
	}
}
