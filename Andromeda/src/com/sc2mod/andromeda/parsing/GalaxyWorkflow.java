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
import java.util.ArrayList;
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
public class GalaxyWorkflow extends Workflow{

	public GalaxyWorkflow(List<Source> files, Options o) {
		super(files, o);
	}
	  
	protected IParser createParser(){
		//Create parser
		GalaxyParser p = new GalaxyParser();
		
		//Assemble lookup paths
		SourceEnvironment env = p.getSourceEnvironment();		
		env.addLookupDir(options.nativeLibFolder);
		env.setNativeDir(options.nativeLibFolder);
		return p;
		
	}

	@Override
	protected List<Source> getLanguageFiles() {
		return new ArrayList<Source>(0);
	}

	public ParseResult compile() {
		
		long bytesOut = 0;
	
		long time = resetTime();
		
		IParser p = null;
		try {
			p = createParser();
			
			//*** Do the parsing ***
			AndromedaFile af = parseAllFiles(p);
			

			//Do semantics analysis
			System.out.print("No syntax errors. Checking semantics...");
			Environment env = SemanticAnalysis.analyze(af,options);

			System.out.println(" DONE (" + getTime() + " ms)");
		
			
			
			
			time = (System.currentTimeMillis() - time);
			long bytesIn = p.getSourceEnvironment().getBytesRead();

			System.out
					.println("=> Successfully compiled "
							+ p.getSourceEnvironment().getFileCount()
							+ " files ("
							+ bytesIn
							+ " bytes).\n=> Produced code: "
							+ bytesOut
							+ " bytes.\n=> Time: "
							+ time
							+ " ms ("
							+ (int) (((bytesOut + bytesIn) / (double) (1 << 10)) / (time / 1000.0))
							+ " KB/s)");
			System.out.println("=> Memory Usage: " + ((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1<<20)) + " MB");
			Program.log.caption("+++ Compilation successful +++");
		} catch (Throwable e) {
			reportError(e);
			if(e instanceof CompilationError){
				((CompilationError)e).setEnvironment(p.getSourceEnvironment());
			}
			if(e instanceof Message){
				Program.log.addMessage((Message) e);
			} else {
				Program.log.addMessage(new UnlocatedErrorMessage(e.getMessage()));
			}
		}
				
		return getResult();
		

	}
}
