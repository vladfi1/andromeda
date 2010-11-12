package com.sc2mod.andromeda.parsing.galaxy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.SourceManager;
import com.sc2mod.andromeda.parsing.IParser;
import com.sc2mod.andromeda.parsing.LanguageImpl;
import com.sc2mod.andromeda.parsing.Source;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;

public class Galaxy extends LanguageImpl{


	@Override
	public IParser createParser(CompilationEnvironment env) {
		//Create parser
		SourceManager fileManager = env.getSourceManager();
		GalaxyParser p = new GalaxyParser(fileManager);
		
		
		//Assemble lookup paths
		File nativeFolder = env.getConfig().getParamFile(Parameter.FILES_NATIVE_LIB_FOLDER);
		fileManager.addLookupDir(nativeFolder);
		fileManager.setNativeDir(nativeFolder);
	
		return p;
	}

	@Override
	public List<Source> getLanguageSources(CompilationEnvironment env) {
		return new ArrayList<Source>(0);
	}

}
