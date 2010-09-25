package com.sc2mod.andromeda.parsing;

import com.sc2mod.andromeda.syntaxNodes.SourceFile;

public interface IParser {

	SourceFile parse(Source f, SourceFile af, InclusionType inclusionType);

	CompilationFileManager getSourceEnvironment();

}
