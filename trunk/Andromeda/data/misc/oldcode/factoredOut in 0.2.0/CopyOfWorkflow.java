package com.sc2mod.andromeda.parsing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
import com.sc2mod.andromeda.codetransform.SimplificationStmtVisitor;
import com.sc2mod.andromeda.codetransform.SyntaxGenerator;
import com.sc2mod.andromeda.codetransform.UnusedFinder;
import com.sc2mod.andromeda.codetransform.VirtualCallResolver;
import com.sc2mod.andromeda.environment.Environment;
import com.sc2mod.andromeda.notifications.Message;
import com.sc2mod.andromeda.notifications.UnlocatedErrorMessage;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.program.FileCollector;
import com.sc2mod.andromeda.program.MapFormatException;
import com.sc2mod.andromeda.program.MapRunner;
import com.sc2mod.andromeda.program.Program;
import com.sc2mod.andromeda.program.ScriptInjector;
import com.sc2mod.andromeda.semAnalysis.SemanticAnalysisWorkflow;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.xml.gen.ResultXMLWriter;
import com.sc2mod.andromeda.xml.gen.StructureXMLVisitor;

public abstract class CopyOfWorkflow {
	
	/**
	 * XPilot: for debugging
	 */
	private static boolean parseNatives = true;
	
	private long lastTime = 0;
	protected Configuration options;
	protected List<Source> files;
	private MoPaQ map;
	private CompilationResult result = new CompilationResult();
	protected CompilationFileManager env;
	
	public CopyOfWorkflow(List<Source> files, Configuration o) {
		this.options = o;
		this.files = files;
		this.env = new CompilationFileManager(o);
	}
	
	protected long getTime() {
		long result = System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		return result;
	}
	
	protected abstract IParser createParser();
	
	protected abstract List<Source> getLanguageFiles();
	
	protected SourceFileNode parseAllFiles(IParser p) throws Exception{
		SourceFileNode af = null;
		
		//Assemble file lists
		List<Source> natives = FileCollector.getFilesFromList(options.nativeLibFolder,options.nativeList);
		List<Source> langFiles = getLanguageFiles();
		List<Source> triggers = null;
		
		int numFiles = files.size();
		if(options.mapIn != null) {
			if(numFiles > 0) {
				Program.log.caption("+++ Compiling map " + options.mapIn.getName() + " and " + numFiles + " additional script files +++");
			} else {
				Program.log.caption("+++ Compiling map " + options.mapIn.getName() + " +++");
			}
			System.out.print("Extracting code from map file...");
			map = new MoPaQ(options.mapIn);		
			triggers = new TriggerExtractor().extractTriggers(map);
			System.out.println(" DONE (" + getTime() + " ms)");
		} else {
			if(options.triggersIn != null) {
				triggers = new TriggerExtractor().extractTriggers(options.triggersIn);
			}
			Program.log.caption("+++ Compiling " + numFiles + " script files +++");
			if(numFiles > 0) {
				
			} else {
				Program.log.caption("+++ Compiling map " + options.mapIn.getName() + " +++");
			}
		}
		
		//Natives
		if(parseNatives)
		for (Source f: natives) {
			System.out.print("Parsing native library ["+f.getName()+"]...");
			af = p.parse(f,af,	InclusionType.NATIVE);
			System.out.println(" DONE (" + getTime() + " ms)");
		}
		
		//Language files
		for (Source f: langFiles) {
			System.out.print("Parsing andromeda system library ["+f.getName()+"]...");
			af = p.parse(f,af,	InclusionType.LANGUAGE);
			System.out.println(" DONE (" + getTime() + " ms)");
		}
		
		//Normal files
		for(Source f: files) {
			System.out.print("Parsing " + f.getTypeName() + " ["+f.getName()+"]...");
			af = p.parse(f,af,	InclusionType.MAIN);
			System.out.println(" DONE (" + getTime() + " ms)");
		}
		
		//Triggers in the input map if one was specified
		if(triggers != null) {
			for(Source f: triggers) {
				System.out.print("Parsing " + f.getTypeName() + " ["+f.getName()+"]...");
				af = p.parse(f,af,	InclusionType.MAIN);
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
		if(map != null|| options.mapScriptIn != null) {
			
			//Write map script to output folder
			String mapScript;
			if(map != null) {
				mapScript = ScriptInjector.getManipulatedMapScript(map);
			} else {
				mapScript = ScriptInjector.getManipulatedMapScript(options.mapScriptIn);
			}
			BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(options.outDir,"MapScript.galaxy")));
			bw2.write(mapScript);
			bw2.close();
			
			//Write to map file if specified
			if(options.mapOut != null) {
				ScriptInjector.injectAndromeda(map, mapScript, code);
				map.save(options.mapOut);
				outputName = options.mapOut.getName();
			}
		}
		
		
		return outputName;
	}
	
	protected void reportError(Throwable e) {
		e.printStackTrace();
		System.err.println("--- Compilation unsuccessful, no code generated! ---");
	}
	
	protected long resetTime() {
		lastTime = System.currentTimeMillis();
		return lastTime;
	}
	
	public CompilationResult compile() {
		
		long bytesOut = 0;
	
		long time = resetTime();

		boolean abort = false;
		IParser p = null;
		try {
			p = createParser();
			
			//*** Do the parsing ***
			SourceFileNode af = parseAllFiles(p);
			

			//Do semantics analysis
			System.out.print("No syntax errors. Checking semantics...");
			Environment env = SemanticAnalysisWorkflow.analyze(af,options);

			System.out.println(" DONE (" + getTime() + " ms)");
			
			String outputName = "-NO OUTPUT-";
			OutputMemoryStats outputStats = null;
			if(!options.noCodegen) {
				//Transform code
				System.out.print("No semantic errors. Doing code transformations...");
				SimplificationStmtVisitor trans = new SimplificationStmtVisitor(options,env.typeProvider,env.nameResolver);
				trans.visit(af);
				System.out.println(" DONE (" + getTime() + " ms)");
				
				//Call hierarchy check
				System.out.print("Checking call hierarchy...");
				
				CallHierarchyVisitor chv = new CallHierarchyVisitor(options,env.nameResolver);
				chv.visit(af);
				
				VirtualCallResolver vcr = new VirtualCallResolver(env, chv);
				vcr.tryResolve();
				
				//XPilot: completes hierarchy analysis
				//chv.visitVirtualInvocations(env.getVirtualInvocations());
				
				UnusedFinder.process(options, env);
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
				if(!env.typeProvider.getClasses().isEmpty()) {
					ClassGenerator cg = new IndexClassGenerator(env,c,snv.getNameProvider(), options);
					cg.generateClasses(env.typeProvider.getClasses());
				}
				c.generateCode(snv,af);
				c.writeInit();
				System.out.println(" DONE (" + getTime() + " ms)");
				
				//Output the code to file / map
				System.out.print("Writing code...");
				outputName = writeCode(c);
				outputStats = c.getOutputStatistics();
				System.out.println(" DONE (" + getTime() + " ms)");
			}
			
			//Output structure to xml if desired to
			if(options.xmlStructure != null) {
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
			if(outputStats!=null) {
				System.out.println("Generated code memory usage:");
				System.out.println("=> Global variables: " + outputStats.globalsBytes / 1000f + " KB" );
				System.out.println("=> String literals: " + outputStats.stringLiteralBytes / 1000f + " KB" );
				System.out.println(">>> Total memory usage (without code): " + outputStats.getRuntimeMemoryUsage() / 1000f 
						+ " KB (" + (((int)(outputStats.getRuntimeMemoryUsage()/(float)(1<<21)*10000))/100f) + "%)"  );
			}
			Program.log.caption("+++ Compilation successful" + (options.noCodegen?"":", code written to " + outputName) + " +++");
		} catch (Throwable e) {
			reportError(e);
			if(e instanceof CompilationError) {
				((CompilationError)e).setEnvironment(p.getSourceEnvironment());
			}
			if(e instanceof Message) {
				Program.log.addMessage((Message) e);
			} else {
				Program.log.addMessage(new UnlocatedErrorMessage(e.getMessage()));
			}
			abort = true;
		}
		
		//Write parse result to xml if desired to and add messages to result
		List<Message> messages = Program.log.flushMessages();
		getResult().addMessages(messages);
		if(options.xmlErrors != null) {
			new ResultXMLWriter().genXML(messages, options.xmlErrors);
		}
		
		//If an error occurred abort here
		if (abort) return getResult();
		
		//If desired, run the map
		if(options.runMap) {
			Program.log.println("Running created map...");
			try {
				new MapRunner(Program.platform,options,Program.config).test(options.mapOut);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("--- Map run unsuccessful! ---");
			}
		
		}
		return getResult();
		

	}

	public CompilationResult getResult() {
		return result;
	}
}
