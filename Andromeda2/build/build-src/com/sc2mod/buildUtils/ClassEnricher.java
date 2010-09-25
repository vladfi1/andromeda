/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.buildUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassEnricher {

	private static Pattern header = Pattern.compile("(public (?:abstract )?class \\w+) (?:(?:extends (\\w+) )|(?:implements (\\w+) ))\\{");
	public static void enrich(File f) throws IOException{
		if(!f.getPath().endsWith(".java")) throw new IOException("Can only enrich java files!");
		BufferedReader b = new BufferedReader(new FileReader(f));
		File newFile = new File(f.getAbsolutePath()+".new");
		BufferedWriter w = new BufferedWriter(new FileWriter(newFile));
		String line;
	 
		while((line = b.readLine())!= null){ 
			Matcher m = header.matcher(line);
			if(m.matches()){
				if(m.group(2)==null && m.group(3).equals("SyntaxNode")){
					System.out.println("Enriching " + m.group(1));
					w.write(m.group(1) + " extends SyntaxNode {\r\n");
					continue;
				}
			} 
			
			w.write(line);
			w.write("\r\n");
		}
		
		b.close();
		w.close(); 
		
		f.delete();
		newFile.renameTo(f);
		
	}
	
	private static Pattern scanner = Pattern.compile("\\s*this\\.zzReader \\= in\\;");
	public static void enrichScanner(File f) throws IOException{
		if(!f.getPath().endsWith("Scanner.java")) throw new IOException("Can only enrich Scanner.java files!");
		BufferedReader b = new BufferedReader(new FileReader(f));
		File newFile = new File(f.getAbsolutePath()+".new");
		BufferedWriter w = new BufferedWriter(new FileWriter(newFile));
		String line;
	 
		while((line = b.readLine())!= null){ 
			Matcher m = scanner.matcher(line);
			//System.out.println(line);
			if(m.matches()){
				System.out.println("Enriching scanner");
				w.write("this.zzReader = (SourceReader)in;");
				w.write("\r\n");
				continue;
			} 
			
			w.write(line);
			w.write("\r\n");
		}
		
		b.close();
		w.close(); 
		
		f.delete();
		newFile.renameTo(f);
	}
}
