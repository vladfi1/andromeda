package com.sc2mod.andromeda.parsing;

import com.sc2mod.andromeda.syntaxNodes.AndromedaFile;

public interface IParser {

	AndromedaFile parse(Source f, AndromedaFile af, int typeNative) throws Exception;

	SourceEnvironment getSourceEnvironment();

}
