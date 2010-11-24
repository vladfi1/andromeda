package com.sc2mod.andromeda.parser.cup.factory;

import com.sc2mod.andromeda.parsing.Language;
import com.sc2mod.andromeda.parsing.framework.IParser;
import com.sc2mod.andromeda.parsing.framework.IParserInterface;
import com.sc2mod.andromeda.parsing.framework.ParserFactory;
import com.sc2mod.andromeda.problems.InternalProgramError;

public abstract class CupFactory implements ParserFactory {

	public static CupFactory getFactory(Language lang) {
		switch (lang) {
		case ANDROMEDA:
			return new CupFactory() {

				@Override
				public IParser createParser(IParserInterface pi) {
					return new CupParserAdapter(this, new AndromedaParser());
				}
			};
		case GALAXY:
			return new CupFactory() {

				@Override
				public IParser createParser(IParserInterface pi) {
					return new CupParserAdapter(this, new GalaxyParser());
				}
			};
		}
		throw new InternalProgramError("Unsupported language: " + lang.name());
	}

}
