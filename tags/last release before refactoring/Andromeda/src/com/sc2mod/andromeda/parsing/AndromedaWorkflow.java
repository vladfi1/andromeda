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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.text.NumberFormatter;

import mopaqlib.MoPaQ;
import mopaqlib.MoPaQException;

import com.sc2mod.andromeda.classes.ClassFieldCalculator;
import com.sc2mod.andromeda.classes.ClassGenerator;
import com.sc2mod.andromeda.classes.MetaClassInit;
import com.sc2mod.andromeda.classes.VirtualCallTable;
import com.sc2mod.andromeda.classes.indexSys.IndexClassFieldCalculator;
import com.sc2mod.andromeda.classes.indexSys.IndexClassGenerator;
import com.sc2mod.andromeda.codegen.CodeGenVisitor;
import com.sc2mod.andromeda.codegen.NameGenerationVisitor;
import com.sc2mod.andromeda.codetransform.CallHierarchyVisitor;
import com.sc2mod.andromeda.codetransform.CodeTransformationVisitor;
import com.sc2mod.andromeda.codetransform.SyntaxGenerator;
import com.sc2mod.andromeda.codetransform.UnusedFinder;
import com.sc2mod.andromeda.codetransform.VirtualCallResolver;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.notifications.Message;
import com.sc2mod.andromeda.notifications.UnlocatedErrorMessage;
import com.sc2mod.andromeda.program.FileCollector;
import com.sc2mod.andromeda.program.MapFormatException;
import com.sc2mod.andromeda.program.MapRunner;
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.program.Program;
import com.sc2mod.andromeda.program.ScriptInjector;
import com.sc2mod.andromeda.semAnalysis.SemanticAnalysis;
import com.sc2mod.andromeda.syntaxNodes.AndromedaFile;
import com.sc2mod.andromeda.xml.gen.ResultXMLWriter;
import com.sc2mod.andromeda.xml.gen.StructureXMLVisitor;

/**
 * 
 * @author gex
 *
 */
public class AndromedaWorkflow extends Workflow{

	public AndromedaWorkflow(List<Source> files, Options o) {
		super(files, o);
	}
	
	protected IParser createParser(){
		//Create parser
		AndromedaParser p = new AndromedaParser();
		
		//Assemble lookup paths
		SourceEnvironment env = p.getSourceEnvironment();		
		env.addLookupDir(options.nativeLibFolder);
		
		if(options.mapIn!=null){
			//In map as lookup dir
			env.addLookupDir(options.mapIn.getAbsoluteFile().getParentFile());
		} else if(!files.isEmpty()) {
			//Folder of first file lookup dir
			env.addLookupDir(((FileSource)files.get(0)).getFile().getAbsoluteFile().getParentFile());
		}
		env.addLibDir(options.libFolder);
		env.setNativeDir(options.nativeLibFolder);
		return p;
		
	}

	@Override
	protected List<Source> getLanguageFiles() {
		return FileCollector.getFiles(new File(options.libFolder.getAbsolutePath() + "/a/lang"));
	}
	  

}
