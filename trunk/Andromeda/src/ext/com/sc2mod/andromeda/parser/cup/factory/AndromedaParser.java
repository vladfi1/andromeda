/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parser.cup.factory;


import com.sc2mod.andromeda.parser.cup.AndromedaGenParser;
import com.sc2mod.andromeda.parser.cup.AndromedaScanner;
import com.sc2mod.andromeda.parser.cup.Scanner;
import com.sc2mod.andromeda.parsing.framework.ParserInput;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;

class AndromedaParser extends AndromedaGenParser implements ICupParser {

	
	
//	public SourceFileNode parse(ParserInput input)  {
//		SourceReader a = sourceEnvironment.getReader(input.getSource(), input.getInclusionType());
//		if (a == null)
//			return null;
//		this.setScanner(new AndromedaScanner(a));
//		Symbol sym;
//		try {
//			sym = parse();
//		} catch (UnrecoverableProblem e){
//			throw e;
//		} catch (Exception e) {
//			throw new InternalProgramError(e);
//		}
//		SourceFileNode fi = ((SourceFileNode) sym.value);
//		input.connect(fi);
//		return fi;
//	}


	private Problem createParserProblem(String message, Object info) {
		if (info instanceof com.sc2mod.andromeda.parser.cup.Symbol) {
			com.sc2mod.andromeda.parser.cup.Symbol sym = (com.sc2mod.andromeda.parser.cup.Symbol) info;
			return Problem.ofType(ProblemId.SYNTAX_UNEXPECTED_TOKEN).at(
					sym.left, sym.right);
		} else {
			return Problem.ofType(ProblemId.SYNTAX_UNKNOWN).details(info);
		}

	}

	public void report_error(String message, Object info) {
		createParserProblem(message, info).raise();

	}

	public void report_fatal_error(String message, Object info) {
		createParserProblem(message, info).raiseUnrecoverable();
	}

	@Override
	public Scanner createScanner(ParserInput input) {
		return new AndromedaScanner(input.getInputNumber(),input.getReader());
	}

}
