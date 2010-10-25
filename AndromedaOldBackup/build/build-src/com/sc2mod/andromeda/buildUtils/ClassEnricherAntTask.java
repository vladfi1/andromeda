/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.buildUtils;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
public class ClassEnricherAntTask extends Task{

	private String path;
	private String scannerPath;
	private File semanticsFile;
	
	public void setSemanticsFile(String file){
		this.semanticsFile = new File(file);
	}
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
			try {ClassEnricher cle = new ClassEnricher(semanticsFile);
				for(File f: files){
						cle.enrich(f);
				}
			} catch (IOException e) {
				throw new BuildException(e);
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
