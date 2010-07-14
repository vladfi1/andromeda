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
public class AndromedaWorkflow {
	  
	private long lastTime = 0;
	private Options options;
	private List<AndromedaSource> files;
	private MoPaQ map;
	
	public AndromedaWorkflow(List<AndromedaSource> files, Options o){
		this.options = o;
		this.files = files;
	}
	
	private long getTime(){
		long result = System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		return result;
	}
	
	private AndromedaParser createParser(){
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
	
	private AndromedaFile parseAllFiles(AndromedaParser p) throws Exception{
		AndromedaFile af = null;
		
		//Assemble file lists
		List<AndromedaSource> natives = FileCollector.getFilesFromList(options.nativeLibFolder,options.nativeList);
		List<AndromedaSource> langFiles = FileCollector.getFiles(new File(options.libFolder.getAbsolutePath() + "/a/lang"));
		List<AndromedaSource> triggers = null;
		
		
		int numFiles = files.size();
		if(options.mapIn != null){
			if(numFiles > 0){
				Program.log.caption("+++ Compiling map " + options.mapIn.getName() + " and " + numFiles + " additional script files +++");
			} else {
				Program.log.caption("+++ Compiling map " + options.mapIn.getName() + " +++");
			}
			System.out.print("Extracting code from map file...");
			map = new MoPaQ(options.mapIn);		
			triggers = new TriggerExtractor().extractTriggers(map);
			System.out.println(" DONE (" + getTime() + " ms)");
		} else {
			if(options.triggersIn != null){
				triggers = new TriggerExtractor().extractTriggers(options.triggersIn);
			}
			Program.log.caption("+++ Compiling " + numFiles + " script files +++");
			if(numFiles > 0){
				
			} else {
				Program.log.caption("+++ Compiling map " + options.mapIn.getName() + " +++");
			}
		}
		
		//Natives
		for (AndromedaSource f: natives) {
			System.out.print("Parsing native library ["+f.getName()+"]...");
			af = p.parse(f,af,	AndromedaFileInfo.TYPE_NATIVE);
			System.out.println(" DONE (" + getTime() + " ms)");
		}
		
		//Language files
		for (AndromedaSource f: langFiles) {
			System.out.print("Parsing andromeda system library ["+f.getName()+"]...");
			af = p.parse(f,af,	AndromedaFileInfo.TYPE_LANGUAGE);
			System.out.println(" DONE (" + getTime() + " ms)");
		}
		
		//Normal files
		for(AndromedaSource f: files){
			System.out.print("Parsing " + f.getTypeName() + " ["+f.getName()+"]...");
			af = p.parse(f,af,	AndromedaFileInfo.TYPE_MAIN);
			System.out.println(" DONE (" + getTime() + " ms)");
		}
		
		//Triggers in the input map if one was specified
		if(triggers != null){
			for(AndromedaSource f: triggers){
				System.out.print("Parsing " + f.getTypeName() + " ["+f.getName()+"]...");
				af = p.parse(f,af,	AndromedaFileInfo.TYPE_MAIN);
				System.out.println(" DONE (" + getTime() + " ms)");
			}
		}
		
		return af;
	}
	
	private String writeCode(CodeGenVisitor generator) throws IOException, MoPaQException, MapFormatException{
		String outputName = options.outDir.getPath() + "/Andromeda.galaxy";
		StringBufferWriter w = new StringBufferWriter(); 
		generator.flushOutCode(w);
		String code = w.toString();

		//Create out directory
		options.outDir.mkdirs();
		
		//Write to external file
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(options.outDir,"Andromeda.galaxy")));
		bw.write(code);
		bw.close();
		
		//If we have an input map, manipulate its mapScript
		if(map != null|| options.mapScriptIn != null){
			
			//Write map script to output folder
			String mapScript;
			if(map != null){
				mapScript = ScriptInjector.getManipulatedMapScript(map);
			} else {
				mapScript = ScriptInjector.getManipulatedMapScript(options.mapScriptIn);
			}
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(options.outDir,"MapScript.galaxy")));
			bw2.write(mapScript);
			bw2.close();
			
			//Write to map file if specified
			if(options.mapOut != null){
				ScriptInjector.injectAndromeda(map, mapScript, code);
				map.save(options.mapOut);
				outputName = options.mapOut.getName();
			}
		}
		
		
		return outputName;
	}
	
	private void reportError(Throwable e) {
		e.printStackTrace();
		System.err.println("--- Compilation unsuccessful, no code generated! ---");
	}
	
	public boolean compile() {
		
		long bytesOut = 0;
	

		lastTime = System.currentTimeMillis();
		long time = lastTime;

		boolean abort = false;
		AndromedaParser p = null;
		try {
			p = createParser();
			
			//*** Do the parsing ***
			AndromedaFile af = parseAllFiles(p);
			

			//Do semantics analysis
			System.out.print("No syntax errors. Checking semantics...");
			Environment env = SemanticAnalysis.analyze(af,options);

			System.out.println(" DONE (" + getTime() + " ms)");
			
			String outputName = "-NO OUTPUT-";
			OutputStats outputStats = null;
			if(!options.noCodegen){
				//Transform code
				System.out.print("No semantic errors. Doing code transformations...");
				CodeTransformationVisitor trans = new CodeTransformationVisitor(options,env.typeProvider,env.nameResolver);
				trans.visit(af);
				System.out.println(" DONE (" + getTime() + " ms)");
				
				//Call hierarchy check
				System.out.print("Checking call hierarchy...");
				CallHierarchyVisitor chv = new CallHierarchyVisitor(options,env.nameResolver);
				chv.visit(af);
				UnusedFinder.process(options, env);
				new VirtualCallResolver(env).tryResolve();
				VirtualCallTable.generateVCTs(env);
				MetaClassInit.init(new SyntaxGenerator(options,env.nameResolver),env);
				System.out.println(" DONE (" + getTime() + " ms)");
				
				//Names
				System.out.print("Generating identifiers...");
				NameGenerationVisitor snv = new NameGenerationVisitor(options);
				ClassFieldCalculator cfc = new IndexClassFieldCalculator(env.typeProvider,snv.getNameProvider());
				cfc.calcFields();
				env.typeProvider.calcFuncPointerIndices();
				cfc.generateClassNames();
				snv.prepareNameGeneration();
				snv.visit(af);
				System.out.println(" DONE (" + getTime() + " ms)");			
				
				//Generate code
				System.out.print("Generating code...");
				CodeGenVisitor c = new CodeGenVisitor(env, options,snv.getNameProvider());
				ClassGenerator cg = new IndexClassGenerator(env,c,snv.getNameProvider(), options);
				cg.generateClasses(env.typeProvider.getClasses());
				c.generateCode(snv,af);
				c.writeInit();
				outputStats = c.getOutputStatistics();
				System.out.println(" DONE (" + getTime() + " ms)");
				
				//Output the code to file / map
				System.out.print("Writing code...");
				outputName = writeCode(c);
				bytesOut = c.getBytesOut();
				System.out.println(" DONE (" + getTime() + " ms)");
			}
			
			//Output structure to xml if desired to
			if(options.xmlStructure != null){
				System.out.print("Writing structure to XML...");
				new StructureXMLVisitor(options).genXml(p.getSourceEnvironment(), options.xmlStructure, af);
				System.out.println(" DONE (" + getTime() + " ms)");
			}	
			
			
			
			
			
			
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
			if(outputStats!=null){
				System.out.println("Generated code memory usage:");
				System.out.println("=> Global variables: " + outputStats.globalsBytes / 1000f + " KB" );
				System.out.println("=> String literals: " + outputStats.stringLiteralBytes / 1000f + " KB" );
				System.out.println(">>> Total memory usage (without code): " + outputStats.getBytes() / 1000f 
						+ " KB (" + (((int)(outputStats.getBytes()/(float)(1<<21)*10000))/100f) + "%)"  );
			}
			Program.log.caption("+++ Compilation successful" + (options.noCodegen?"":", code written to " + outputName) + " +++");
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
			abort = true;
		}
		
		//Write parse result to xml if desired to
		List<Message> messages = Program.log.flushMessages();
		if(options.xmlErrors != null){
			new ResultXMLWriter().genXML(messages, options.xmlErrors);
		}
		
		//If an error occurred abort here
		if (abort) return false;
		
		//If desired, run the map
		if(options.runMap){
			Program.log.println("Running created map...");
			try {
				new MapRunner(Program.platform,options,Program.config).test(options.mapOut);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("--- Map run unsuccessful! ---");
			}
		
		}
		return true;
		

	}
}
