package com.sc2mod.andromeda.parsing;

import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;

public interface IParser {

	SourceFileNode parse(Source f, SourceFileNode af, InclusionType inclusionType);

	CompilationFileManager getSourceEnvironment();

}
