/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import com.sc2mod.andromeda.gui.jobs.JobHandler;
import com.sc2mod.andromeda.gui.misc.GUIController;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.Language;
import com.sc2mod.andromeda.parsing.WorkflowFactory;
import com.sc2mod.andromeda.parsing.framework.Source;
import com.sc2mod.andromeda.parsing.options.CLOption;
import com.sc2mod.andromeda.parsing.options.CommandLineOptions;
import com.sc2mod.andromeda.parsing.options.ConfigFile;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.InvalidParameterException;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.util.Files;
import com.sc2mod.andromeda.util.logging.ConsoleLog;
import com.sc2mod.andromeda.util.logging.Logger;

public class Program {

	public static String name = "Andromeda";
	public static final String VERSION = getVersion();
	public static Platform platform = new Platform();
	public static CommandLineOptions clOptions;
	public static ConfigFile config;
	
	public static File appDirectory;
	
	public static GUIController guiController;
	public static JobHandler jobHandler = new JobHandler();
	public static Logger log = new ConsoleLog();
	private static boolean isPackaged;
	
	public static URL getSystemResource(String path){
//		if(!isPackaged){
//			path = "./../" + path;
//		}
		URL u = ClassLoader.getSystemResource(path);
		return u;
	}
	
	private static String getVersion(){
		InputStream stream = Program.class.getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
		Manifest manifest;
		try {
			manifest = new Manifest(stream);
		} catch (IOException e) {
			return "-- unversioned --";
		}
		Attributes attributes = manifest.getMainAttributes();
		return attributes.getValue("Implementation-Version");

	}
	
	static class InitializationError extends Exception{

		public InitializationError(Throwable cause) {
			super(cause);
		}

		public InitializationError(String string) {
			super(string);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
	private static Configuration setupParamsAndOptions(String args[]) throws InvalidParameterException, InitializationError{
		
		//Parse arguments
		Configuration o;

		clOptions = new CommandLineOptions(args);

		
		//Load config
		File confFile = (File)clOptions.getParam(CLOption.SET_CONFIG);
		try {			
			
			config = new ConfigFile(confFile, true);
		} catch (FileNotFoundException e) {
			throw new InitializationError("Config file (" + Files.getCleanPath(confFile) + ") not found!");
		} catch (IOException e) {
			throw new InitializationError("Error reading config file (" + Files.getCleanPath(confFile) + ")!");
		}
	
		//Assemble options
		o = new Configuration(config, clOptions);
	
		
		return o;
	}
	
	/**
	 * Method used by test cases. Starts andromeda just like the main method but does not do other program initialization.
	 * @param args
	 * @throws InitializationError 
	 */
	public static CompilationEnvironment invokeWorkflow(List<Source> sources, Configuration options, Language language) {
		return new WorkflowFactory(language).createWorkflow(sources,options).execute();
	}

	public static void main(String[] args) throws URISyntaxException {
				

		//Get application directory
		if(appDirectory==null){
			appDirectory = new File(ClassLoader.getSystemResource(".").toURI());
			isPackaged = true;
		}
		
		//Params params, assemble options

		Configuration o;
		try {
			o = setupParamsAndOptions(args);
		} catch (InvalidParameterException e2) {
			System.err.println("Invalid usage!:\n" + e2.getMessage());
			return;
		} catch (InitializationError e2) {
			System.err.println("Andromeda could not be initialized!:\n" + e2.getMessage());
			return;
		}
		
		//Display help if desired and return.
		if(clOptions.getParamFlag(CLOption.DISPLAY_HELP)){
			displayHelp();
			return;
		}

		
				
		//If no input was specified, start the GUI
		List<Source> files = clOptions.getFiles();
		if (files.isEmpty()&&o.isParamNull(Parameter.FILES_MAP_IN)&&o.isParamNull(Parameter.FILES_MAP_TRIGGERS_IN)) {
			guiController = new GUIController();
			return;
		} 
		
		
		if(invokeWorkflow(files, o, Language.ANDROMEDA).getResult().isSuccessful()){

//			//again
//			CompilationEnvironment cr = invokeWorkflow(files, o, Language.ANDROMEDA);
//			//again
//			 cr = invokeWorkflow(files, o, Language.ANDROMEDA);
//			//again
//			 cr = invokeWorkflow(files, o, Language.ANDROMEDA);
//			
			System.exit(0);
		} else {
			System.exit(-1);
		}

		
	}

	private static void displayHelp() {
		BufferedReader is = Files.getReaderFromResource("usage.txt");
		String line;
		try {
			while((line = is.readLine()) != null){
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
