package com.sc2mod.andromeda.parsing.framework;

import com.sc2mod.andromeda.parser.cup.Scanner;
import com.sc2mod.andromeda.parsing.SourceManager;
import com.sc2mod.andromeda.problems.SourceLocation;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public interface IParser {
	
	/**
	 * Parses an input source.
	 * 
	 * TODO May this method be called concurrently?
	 * @param source
	 * @return
	 */
	SyntaxNode parse(ParserInput source);
	
	/**
	 * Sets the parser hook for this parser.
	 * Parsers implementing this interface should call 
	 * the different hook methods when the respective
	 * events occur.
	 * 
	 * If the parser already has a hook, the old hook
	 * is replaced by the new one.
	 * @param hook
	 */
	void setParserHook(IParserHook hook);
	
	
	SourceLocation getLocation(SyntaxNode sn);
	
	
	ParserFactory getFactory();
	

}
