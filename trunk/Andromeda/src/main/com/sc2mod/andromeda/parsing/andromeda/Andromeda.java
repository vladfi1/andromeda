package com.sc2mod.andromeda.parsing.andromeda;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.environment.types.AndromedaSystemTypes;
import com.sc2mod.andromeda.environment.types.SystemTypes;
import com.sc2mod.andromeda.environment.types.TypeProvider;
import com.sc2mod.andromeda.parser.cup.factory.CupFactory;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.Language;
import com.sc2mod.andromeda.parsing.LanguageImpl;
import com.sc2mod.andromeda.parsing.framework.ParserFactory;
import com.sc2mod.andromeda.parsing.framework.Source;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.program.FileCollector;
import com.sc2mod.andromeda.semAnalysis.Analyser;
import com.sc2mod.andromeda.util.Files;

public class Andromeda extends LanguageImpl{

	@Override
	public List<Source> getLanguageSources(CompilationEnvironment env) {
		List<Source> sources = new ArrayList<Source>(10);
		for(File libFolder : env.getConfig().getParamFiles(Parameter.FILES_LIB_FOLDERS)){
			sources.addAll(FileCollector.getFiles(Files.getAppFile(libFolder + "/a/lang")));
		}
		return sources;
		
	}

	@Override
	public ParserFactory getParserFactory() {
		return CupFactory.getFactory(Language.ANDROMEDA);
	}

	@Override
	public SystemTypes getSystemTypes(Environment env, TypeProvider tprov) {
		return new AndromedaSystemTypes(env, tprov);
	}
	
	@Override
	public Analyser getAdditionalAnalyser(Configuration options) {
		return null;
	}



}
