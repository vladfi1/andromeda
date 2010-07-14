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
import com.sc2mod.andromeda.parsing.AndromedaParser;
import com.sc2mod.andromeda.parsing.AndromedaReader;
import com.sc2mod.andromeda.parsing.AndromedaSource;
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

	public static void main(String[] args) throws URISyntaxException {
				
		//Get application directory
		if(appDirectory==null){
			appDirectory = new File(ClassLoader.getSystemResource(".").toURI());
			isPackaged = true;
		}
		
		//Do platform dependent stuff
		platform = new Platform();
		
		//Parse arguments
		Options o;
		try {
			params = new Parameters(args);
	
			
			//Load config
			try {			
				config = new ConfigHandler(new File(appDirectory,"andromeda.conf"), true);
			} catch (FileNotFoundException e) {
				System.err.println("Config file (andromeda.conf) not found!");
				e.printStackTrace();
				return;
			} catch (IOException e) {
				System.err.println("Error reading config file (andromeda.conf)!");
				e.printStackTrace();
				return;
			}
		
			//Assemble options
			o = new Options(config, params);
		} catch (InvalidParameterException e1) {
			System.err.println("Invalid usage!:\n" + e1.getMessage());
			return;
		}
		
		//If no input was specified, start the GUI
		List<AndromedaSource> files = params.getFiles();
		if (files.isEmpty()&&o.mapIn==null&&o.triggersIn==null) {
			guiController = new GUIController();
			return;
		} 
		
		//We have input so do parsing
		if(new AndromedaWorkflow(files, o).compile()){
			System.exit(0);
		} else {
			System.exit(-1);
		}

		
	}

}
