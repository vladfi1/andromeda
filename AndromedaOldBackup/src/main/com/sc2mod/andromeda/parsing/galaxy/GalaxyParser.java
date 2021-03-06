/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parsing.galaxy;


import com.sc2mod.andromeda.notifications.InternalProgramError;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.notifications.UnrecoverableProblem;
import com.sc2mod.andromeda.parser.GalaxyGenParser;
import com.sc2mod.andromeda.parser.GalaxyScanner;
import com.sc2mod.andromeda.parser.Symbol;
import com.sc2mod.andromeda.parsing.CompilationFileManager;
import com.sc2mod.andromeda.parsing.IParser;
import com.sc2mod.andromeda.parsing.InclusionType;
import com.sc2mod.andromeda.parsing.Source;
import com.sc2mod.andromeda.parsing.SourceFileInfo;
import com.sc2mod.andromeda.parsing.SourceReader;
import com.sc2mod.andromeda.syntaxNodes.GlobalStructureListNode;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;

public class GalaxyParser extends GalaxyGenParser implements IParser {

	private CompilationFileManager sourceEnvironment;

	public CompilationFileManager getSourceEnvironment() {
		return sourceEnvironment;
	}

	public GalaxyParser(CompilationFileManager env) {
		sourceEnvironment = env;
	}
	
	private SourceFileNode parse(Source f, InclusionType inclusionType) {
		SourceReader a = sourceEnvironment.getReader(f, inclusionType);
		if(a == null) return null;
		this.setScanner(new GalaxyScanner(a));
		Symbol sym;
		try {
			sym = parse();
		} catch (UnrecoverableProblem e){
			throw e;
		} catch (Exception e) {
			throw new InternalProgramError(e);
		}
		GlobalStructureListNode topContent = new GlobalStructureListNode();
		SourceFileNode top = new SourceFileNode(null,null,topContent);
		top.setFileInfo(new SourceFileInfo(0, InclusionType.MAIN,null));
		SourceFileNode fi = ((SourceFileNode)sym.value);
		fi.setFileInfo(new SourceFileInfo(a.getFileId(), inclusionType,null));
		topContent.append(new IncludeNode(fi));
		return top;
	}
	
	

	public SourceFileNode parse(Source f, SourceFileNode fold, InclusionType inclusionType) {
		if(fold==null) return parse(f,inclusionType);
		SourceReader a = sourceEnvironment.getReader(f,inclusionType);
		if(a == null) return fold;
		this.setScanner(new GalaxyScanner(a));
		Symbol sym;
		try {
			sym = parse();
		} catch (UnrecoverableProblem e){
			throw e;
		} catch (Exception e) {
			throw new InternalProgramError(e);
		}
		SourceFileNode fi = ((SourceFileNode)sym.value);
		fi.setFileInfo(new SourceFileInfo(a.getFileId(), inclusionType,null));
		fold.getContent().append(new IncludeNode(fi));
		return fold;
	}
	
	private Problem createParserProblem(String message, Object info){
		if (info instanceof com.sc2mod.andromeda.parser.Symbol) {
			com.sc2mod.andromeda.parser.Symbol sym = (com.sc2mod.andromeda.parser.Symbol) info;
			return Problem.ofType(ProblemId.SYNTAX_UNEXPECTED_TOKEN).at(sym.left,sym.right).details(sym.toString());
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



}
