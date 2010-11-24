package com.sc2mod.andromeda.parser.cup.factory;

import com.sc2mod.andromeda.parser.cup.Scanner;
import com.sc2mod.andromeda.parser.cup.Symbol;
import com.sc2mod.andromeda.parsing.framework.IParserHook;
import com.sc2mod.andromeda.parsing.framework.ParserInput;

interface ICupParser {

	Scanner createScanner(ParserInput input);

	void setScanner(Scanner scanner);

	Symbol parse() throws Exception;

	void setHook(IParserHook hook);
}
