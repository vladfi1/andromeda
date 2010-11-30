package com.sc2mod.andromeda.parsing.galaxy;

import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.types.NoSystemTypes;
import com.sc2mod.andromeda.environment.types.SystemTypes;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.parser.cup.factory.CupFactory;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.Language;
import com.sc2mod.andromeda.parsing.LanguageImpl;
import com.sc2mod.andromeda.parsing.framework.ParserFactory;
import com.sc2mod.andromeda.parsing.framework.Source;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.semAnalysis.GalaxyAnalysisVisitor;

public class Galaxy extends LanguageImpl{


	@Override
	public List<Source> getLanguageSources(CompilationEnvironment env) {
		return new ArrayList<Source>(0);
	}

	@Override
	public ParserFactory getParserFactory() {
		return CupFactory.getFactory(Language.GALAXY);
	}
	
	@Override
	public SystemTypes getSystemTypes(Environment env, TypeProvider tprov) {
		return new NoSystemTypes(env, tprov);
	}
	
	@Override	
	public GalaxyAnalysisVisitor getAdditionalAnalyser(Configuration options) {
		return new GalaxyAnalysisVisitor(options);
	}
}
