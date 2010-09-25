/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package compilerGen.copy;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
public class CopyrightGeneratorAntTask extends Task{

	private String path;
	private CopyrightGenerator cg;
	private String basePath;
	
	private boolean recursive;
	
	public void setBasePath(String path){
		this.basePath = path;
	}
	
	public void setPath(String path){
		this.path = path;
	}
	
	public void setRecursive(String isRecursive){
		this.recursive = Boolean.parseBoolean(isRecursive);
	}

	private void enrich(File dir) throws IOException{
		if(!dir.exists()) throw new BuildException("Path does not exist!");
		if(!dir.isDirectory()) throw new BuildException("Path is no directory!");
		File[] files = dir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					if(pathname.isDirectory()) return true;
					return pathname.getAbsolutePath().endsWith(".java");
				}
				
				
			});

		for (File f : files) {
			if(f.isDirectory()){
				if(recursive)enrich(f);
			}
			else cg.enrich(f);
		}

	}
	
	@Override
	public void execute() throws BuildException {
		File dir = new File(path); 

		try {
			cg = new CopyrightGenerator(basePath);
			enrich(dir);
		} catch (Exception e) {
			throw new BuildException(e);
		}
		System.out.println("Generated copyright for " + cg.getCount() + " files.");
	}
	
	public static void main(String[] args){
		
	}
	
}
