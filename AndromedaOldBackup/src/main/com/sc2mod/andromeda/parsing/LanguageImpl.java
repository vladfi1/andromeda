package com.sc2mod.andromeda.parsing;

import java.util.List;

import com.sc2mod.andromeda.parsing.options.Configuration;

public abstract class LanguageImpl {
	
	
	public abstract IParser createParser(CompilationEnvironment env);
	
	public abstract List<Source> getLanguageSources(CompilationEnvironment env);




}
