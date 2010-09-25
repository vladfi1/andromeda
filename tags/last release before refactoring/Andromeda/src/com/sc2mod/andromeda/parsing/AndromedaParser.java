/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parsing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java_cup.runtime.Symbol;

import com.sc2mod.andromeda.classes.ClassFieldCalculator;
import com.sc2mod.andromeda.classes.ClassGenerator;
import com.sc2mod.andromeda.classes.MetaClassInit;
import com.sc2mod.andromeda.classes.VirtualCallTable;
import com.sc2mod.andromeda.classes.indexSys.IndexClassFieldCalculator;
import com.sc2mod.andromeda.classes.indexSys.IndexClassGenerator;
import com.sc2mod.andromeda.classes.pointerSys.PointerClassGenerator;
import com.sc2mod.andromeda.codegen.CodeGenVisitor;
import com.sc2mod.andromeda.codegen.NameGenerationVisitor;
import com.sc2mod.andromeda.codetransform.CallHierarchyVisitor;
import com.sc2mod.andromeda.codetransform.CodeTransformationVisitor;
import com.sc2mod.andromeda.codetransform.SyntaxGenerator;
import com.sc2mod.andromeda.codetransform.UnusedFinder;
import com.sc2mod.andromeda.codetransform.VirtualCallResolver;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.parser.Parser;
import com.sc2mod.andromeda.parser.Scanner;
import com.sc2mod.andromeda.program.FileCollector;
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.semAnalysis.SemanticAnalysis;
import com.sc2mod.andromeda.syntaxNodes.AndromedaFile;
import com.sc2mod.andromeda.syntaxNodes.FileContent;
import com.sc2mod.andromeda.syntaxNodes.IncludedFile;

public class AndromedaParser extends Parser implements IParser {

	private SourceEnvironment sourceEnvironment;

	public SourceEnvironment getSourceEnvironment() {
		return sourceEnvironment;
	}

	public AndromedaParser() {
		stack = new SymbolStack();
		sourceEnvironment = new SourceEnvironment();
	}
	
	private AndromedaFile parse(Source f, int inclusionType) throws Exception{
		AndromedaReader a = sourceEnvironment.getReader(f, inclusionType);
		if(a == null) return null;
		this.setScanner(new Scanner(a));
		Symbol sym = parse();
		FileContent topContent = new FileContent();
		AndromedaFile top = new AndromedaFile(null,null,topContent);
		top.setFileInfo(new AndromedaFileInfo(0, AndromedaFileInfo.TYPE_MAIN,null));
		AndromedaFile fi = ((AndromedaFile)sym.value);
		fi.setFileInfo(new AndromedaFileInfo(a.getFileId(), inclusionType,null));
		topContent.append(new IncludedFile(fi));
		return top;
	}
	
	public AndromedaFile parse(Source f, AndromedaFile fold, int inclusionType) throws Exception{
		if(fold==null) return parse(f,inclusionType);
		AndromedaReader a = sourceEnvironment.getReader(f,inclusionType);
		if(a == null) return fold;
		this.setScanner(new Scanner(a));
		Symbol sym = parse();
		AndromedaFile fi = ((AndromedaFile)sym.value);
		fi.setFileInfo(new AndromedaFileInfo(a.getFileId(), inclusionType,null));
		fold.getContent().append(new IncludedFile(fi));
		return fold;
	}
	
	public void report_error(String message, Object info) {
		if (info instanceof java_cup.runtime.Symbol) {
			java_cup.runtime.Symbol sym = (java_cup.runtime.Symbol) info;
			throw new CompilationError(sym.left,sym.right,"Syntax Error: Unexpected token: ");
		}

	}

	public void report_fatal_error(String message, Object info) {
		report_error(message, info);
		throw new RuntimeException("Fatal Syntax Error");
	}




}
