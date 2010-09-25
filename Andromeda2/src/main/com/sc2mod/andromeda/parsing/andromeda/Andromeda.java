package com.sc2mod.andromeda.parsing.andromeda;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.CompilationFileManager;
import com.sc2mod.andromeda.parsing.FileSource;
import com.sc2mod.andromeda.parsing.IParser;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.LanguageImpl;
import com.sc2mod.andromeda.parsing.Source;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.program.FileCollector;
import com.sc2mod.andromeda.util.Files;
import com.sc2mod.andromeda.util.StopWatch;

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
	public IParser createParser(CompilationEnvironment env) {
		Configuration cfg = env.getConfig();
		CompilationFileManager fileManager = env.getFileManager();
		File mapIn = cfg.getParamFile(Parameter.FILES_MAP_IN);
		List<Source> filesIn = env.getParserInput().get(InclusionType.MAIN);
		
		//Create parser
		StopWatch sw = new StopWatch();
		AndromedaParser p = new AndromedaParser(fileManager);
		sw.printTime("sdfsdf");
		//Assemble lookup paths
		
		if(mapIn != null){
			//In map as lookup dir
			fileManager.addLookupDir(mapIn.getAbsoluteFile().getParentFile());
		} else if(!filesIn.isEmpty()) {
			//Folder of first file lookup dir
			Source src = env.getParserInput().get(InclusionType.MAIN).get(0);
			if(src instanceof FileSource)
				fileManager.addLookupDir(((FileSource)src).getFile().getAbsoluteFile().getParentFile());
		}
		File[] libDirs = cfg.getParamFiles(Parameter.FILES_LIB_FOLDERS);
		for(File f : libDirs){
			fileManager.addLibDir(f);
		}
		fileManager.setNativeDir(cfg.getParamFile(Parameter.FILES_NATIVE_LIB_FOLDER));
		return p;
		
	}

}
