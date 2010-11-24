package com.sc2mod.andromeda.parser.cup.factory;

import com.sc2mod.andromeda.parser.cup.Symbol;
import com.sc2mod.andromeda.parsing.framework.IParser;
import com.sc2mod.andromeda.parsing.framework.IParserHook;
import com.sc2mod.andromeda.parsing.framework.ParserFactory;
import com.sc2mod.andromeda.parsing.framework.ParserInput;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.problems.SourceLocation;
import com.sc2mod.andromeda.problems.UnrecoverableProblem;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

class CupParserAdapter implements IParser {

	private ParserFactory factory;
	private ICupParser parser;

	public CupParserAdapter(CupFactory factory, ICupParser parser) {
		this.factory = factory;
		this.parser = parser;
	}

	@Override
	public ParserFactory getFactory() {
		return factory;
	}

	@Override
	public SourceLocation getLocation(SyntaxNode sn) {
		//FIXME hier weiter
	}

	@Override
	public SyntaxNode parse(ParserInput source) {
		try {
			parser.setScanner(parser.createScanner(source));
			Symbol sym = parser.parse();
			return (SyntaxNode) sym.value;
		} catch (UnrecoverableProblem e){
			throw e;
		} catch (Exception e) {
			throw new InternalProgramError(e);
		}
	}

	@Override
	public void setParserHook(IParserHook hook) {
		parser.setHook(hook);
	}

}
