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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.DataFormatException;

import javax.tools.JavaFileManager.Location;

import mopaqlib.MoPaQ;
import mopaqlib.MoPaQException;

import com.sc2mod.andromeda.gui.jobs.JobHandler;
import com.sc2mod.andromeda.gui.misc.GUIController;
import com.sc2mod.andromeda.parsing.ParseResult;
import com.sc2mod.andromeda.parsing.AndromedaParser;
import com.sc2mod.andromeda.parsing.AndromedaReader;
import com.sc2mod.andromeda.parsing.ParserFactory;
import com.sc2mod.andromeda.parsing.ParserLanguage;
import com.sc2mod.andromeda.parsing.Source;
import com.sc2mod.andromeda.parsing.AndromedaWorkflow;
import com.sc2mod.andromeda.parsing.FileSource;
import com.sc2mod.andromeda.parsing.SourceEnvironment;
import com.sc2mod.andromeda.parsing.TriggerExtractor;

public class Program {

	public static String name = "Andromeda";
	public static final String VERSION = getVersion();
	public static Platform platform;
	public static Parameters params;
	public static ConfigHandler config;
	
	public static File appDirectory;
	
	public static GUIController guiController;
	public static JobHandler jobHandler = new JobHandler();
	public static Log log = new ConsoleLog();
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
	
	private static Options setupParamsAndOptions(String args[]) throws InvalidParameterException, InitializationError{
		
		//Parse arguments
		Options o;

		params = new Parameters(args);

		
		//Load config
		try {			
			config = new ConfigHandler(new File(appDirectory,"andromeda.conf"), true);
		} catch (FileNotFoundException e) {
			throw new InitializationError("Config file (andromeda.conf) not found!");
		} catch (IOException e) {
			throw new InitializationError("Error reading config file (andromeda.conf)!");
		}
	
		//Assemble options
		o = new Options(config, params);
	
		
		return o;
	}
	
	/**
	 * Method used by test cases. Starts andromeda just like the main method but does not do other program initialization.
	 * @param args
	 * @throws InitializationError 
	 */
	public static ParseResult invokeWorkflow(List<Source> sources, Options options, ParserLanguage language) throws InitializationError{
		return new ParserFactory(language).createWorkflow(sources,options).compile();
	}

	public static void main(String[] args) throws URISyntaxException {
				
		//Get application directory
		if(appDirectory==null){
			String path = Program.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			System.out.println(path);
			appDirectory = new File(path);
			isPackaged = true;
		}
		
		//Do platform dependent stuff
		platform = new Platform();
		
		//Params params, assemble options

		Options o;
		try {
			o = setupParamsAndOptions(args);
		} catch (InvalidParameterException e2) {
			System.err.println("Invalid usage!:\n" + e2.getMessage());
			return;
		} catch (InitializationError e2) {
			System.err.println("Andromeda could not be initialized!:\n" + e2.getMessage());
			return;
		}
				
		//If no input was specified, start the GUI
		List<Source> files = params.getFiles();
		if (files.isEmpty()&&o.mapIn==null&&o.triggersIn==null) {
			guiController = new GUIController();
			return;
		} 
		
		//We have input so do parsing
		if(new AndromedaWorkflow(files, o).compile().isSuccessful()){
			System.exit(0);
		} else {
			System.exit(-1);
		}

		
	}

}
