package com.sc2mod.andromeda.parsing;

import com.sc2mod.andromeda.parser.Scanner;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;

public interface IParser {

	
	SourceManager getSourceEnvironment();

	com.sc2mod.andromeda.parser.Symbol parse() throws Exception;

	void setScanner(Scanner andromedaScanner);

}
