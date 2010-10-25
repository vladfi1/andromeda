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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author gex
 *
 */
public class CopyrightGenerator {
	private Pattern header = Pattern.compile("^(.)*?package ",Pattern.DOTALL);
	private Pattern clazz = Pattern.compile("\\s*(\\/\\*.*?\\*\\/)?\\s+(public|private)?\\s+(abstract)?\\s+(class|interface)");
	
	String headerReplacement;
	String classReplacement;
	int count;
	public CopyrightGenerator(String basePath) throws IOException{
		headerReplacement = loadFile(new File(basePath,"genUtils/data/header.txt")) + "package ";
		classReplacement = loadFile(new File(basePath,"genUtils/data/class.txt"));
		
	}
	
	private String loadFile(File file) throws IOException {
		if(!file.exists()) throw new FileNotFoundException(file.getAbsolutePath() + " doesn't exist!");
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder result = new StringBuilder((int)file.length()+10);
		String line;
	 

		while((line = br.readLine())!= null){ 
			result.append(line);
			result.append("\r\n");
		}
		
		br.close();
		return result.toString();
	}

	public void enrich(File f) throws IOException{
		if(!f.getPath().endsWith(".java")) throw new IOException("Can only enrich java files!: " + f.getAbsolutePath());
		BufferedReader b = new BufferedReader(new FileReader(f));
		String line;
		StringBuilder file = new StringBuilder(2048);
		System.out.println("Enriching " + f.getName());
		
		while((line = b.readLine())!= null){ 
			file.append(line);
			file.append("\r\n");
		}
		
		String fileStr = file.toString();
		Matcher m = header.matcher(fileStr);
		
		if(!m.find()) throw new IOException("Header not found in " + f.getAbsolutePath() + "!");
		fileStr = m.replaceFirst(headerReplacement);
		
				
		b.close();
		

		BufferedWriter w = new BufferedWriter(new FileWriter(f));
		w.write(fileStr);
		w.close();
		count++;
	}
	
	public int getCount() {
		return count;
	}
	
	public static void main(String[] args) throws IOException {
		new CopyrightGenerator(".").enrich(new File("generated/com/sc2mod/andromeda/parser/Scanner.java"));
	}
}
