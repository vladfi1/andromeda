package com.sc2mod.andromeda.parsing;

import java.util.List;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.types.SystemTypes;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.parsing.framework.ParserFactory;
import com.sc2mod.andromeda.parsing.framework.Source;

public abstract class LanguageImpl {
	
	public abstract ParserFactory getParserFactory();
	//public abstract IParser createParser(CompilationEnvironment env);
	
	public abstract List<Source> getLanguageSources(CompilationEnvironment env);

	public abstract SystemTypes getSystemTypes(Environment env, TypeProvider tprov);


}
