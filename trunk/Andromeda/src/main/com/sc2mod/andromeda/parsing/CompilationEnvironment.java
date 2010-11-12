package com.sc2mod.andromeda.parsing;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.SourceListNode;

public class CompilationEnvironment {

	private final Configuration config;
	private final SourceManager fileManager;
	private final CompilationResult result;
	private Environment semanticEnvironment;
	private SourceListNode syntaxTree;
	private TransientCompilationData transientData = new TransientCompilationData();
	private final EnumMap<InclusionType,List<Source>> parserInput = new EnumMap<InclusionType, List<Source>>(InclusionType.class);
	
	
	
	public TransientCompilationData getTransientData() {
		return transientData;
	}

	public SourceListNode getSyntaxTree() {
		return syntaxTree;
	}

	public void setSyntaxTree(SourceListNode syntaxTree) {
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

	public SourceManager getSourceManager() {
		return fileManager;
	}

	public CompilationResult getResult() {
		return result;
	}
	
	public CompilationEnvironment(Configuration config){
		this.config = config;
		fileManager = new SourceManager(config);
		result = new CompilationResult(config.getParamBool(Parameter.DEBUG_PRINT_ERROR_STACK_TRACE));
	}

	public void setSemanticEnvironment(Environment semanticEnvironment) {
		this.semanticEnvironment = semanticEnvironment;
	}

	public Environment getSemanticEnvironment() {
		return semanticEnvironment;
	}
}
