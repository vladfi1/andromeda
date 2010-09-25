/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.compilerGen;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
public class ClassEnricherAntTask extends Task{

	private String path;
	private String scannerPath;
	
	public void setPath(String path){
		this.path = path;
	}
	
	public void setScanner(String path){
		this.scannerPath = path;
	}
	
	@Override
	public void execute() throws BuildException {
		if(path != null){
			File dir = new File(path); 
			if(!dir.exists()) throw new BuildException("Path does not exist!");
			if(!dir.isDirectory()) throw new BuildException("Path is no directory!");
			 
			File[] files = dir.listFiles(new FilenameFilter(){
	
				@Override
				public boolean accept(File arg0, String arg1) {
					return arg1.endsWith(".java");
				}
				 
			});
			for(File f: files){
				try {
					ClassEnricher.enrich(f);
				} catch (IOException e) {
					throw new BuildException(e);
				}
			}
		}
		if(scannerPath!=null){

			try {
				ClassEnricher.enrichScanner(new File(scannerPath));
			} catch (IOException e) {
				throw new BuildException(e);
			}
		}
	}
	
	public static void main(String[] args){
		
	}
	
}
