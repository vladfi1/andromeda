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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassEnricher {
	
	private HashMap<String, String> semanticMapping = new HashMap<String, String>();

	public ClassEnricher(File semanticsDecl) throws IOException{
		BufferedReader r = new BufferedReader(new FileReader(semanticsDecl));
		String line;
		String prefix = "";
		while((line = r.readLine()) != null){
			line = line.trim();
			if("".equals(line)||line.startsWith("//")) continue;
			if(line.startsWith("package ")){
				prefix = line.substring(8);
				continue;
			}
			String[] sides = line.split(":");
			if(sides.length!= 2) throw new IOException("Malformed line:\n" + line);
			semanticMapping.put(sides[0].trim(), (prefix!=""?prefix+".":"") + sides[1].trim());
		}
	}
	
	private String getSimpleName(String qualifiedName){
		String[] ss = qualifiedName.split("\\.");
		if(ss.length<=1) return qualifiedName;
		return ss[ss.length-1];
	}
	
	
	private static Pattern header = Pattern.compile("(public (?:abstract )?class \\w+) (?:(?:extends (\\w+) )|(?:implements (\\w+) ))\\{");
	private static Pattern childrenAcceptPattern = Pattern.compile("\\s*public void childrenAccept.*");
	public void enrich(File f) throws IOException{
		if(!f.getPath().endsWith(".java")) throw new IOException("Can only enrich java files!");
		BufferedReader b = new BufferedReader(new FileReader(f));
		File newFile = new File(f.getAbsolutePath()+".new");
		BufferedWriter w = new BufferedWriter(new FileWriter(newFile));
		String line;
		String mapTo = semanticMapping.get(f.getName().substring(0,f.getName().length()-5));
		String pkg = null;
		if(mapTo != null){
			pkg = mapTo;
			mapTo = getSimpleName(mapTo);
		}
		while((line = b.readLine())!= null){ 
			Matcher m = header.matcher(line);
			if(m.matches()){
				if(mapTo!=null){
					System.out.println("Adding semantics to " + m.group(1));
					w.write("import com.sc2mod.andromeda.problems.InternalProgramError;\n");
					w.write("import com.sc2mod.andromeda.environment.SemanticsElement;\n");
					if(pkg != null){
						w.write("import " + pkg + ";\n\n");
					}
					
				}
				if(m.group(2)==null && m.group(3).equals("SyntaxNode")){
					System.out.println("Enriching " + m.group(1));
					w.write(m.group(1) + " extends SyntaxNode {\r\n");
					continue;
				}
				
			} 
			if(mapTo != null){
				m = childrenAcceptPattern.matcher(line);
				if(m.matches()){

					writeSemanticsGetSet(w,mapTo);
					mapTo = null;
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
	
	private void writeSemanticsGetSet(BufferedWriter writer, String mapTo) throws IOException {
		//Attr
		writer.write("  private " + mapTo + " semantics;\n");
		
		//Set
		writer.write("  @Override\n");
		writer.write("  public void setSemantics(SemanticsElement semantics){\n");
		writer.write("  \tif(!(semantics instanceof " + mapTo + ")) throw new InternalProgramError(this,\"Trying to assign semantics of type \"\n");
		writer.write("  \t\t\t\t+ semantics.getClass().getSimpleName() + \" to node \" + this.getClass().getSimpleName());\n");
		writer.write("  \tthis.semantics = (" + mapTo + ")semantics;\n");
		writer.write("  }\n");
		
		//Get
		writer.write("  @Override\n");
		writer.write("  public " + mapTo + " getSemantics(){\n");
		writer.write("  \treturn semantics;\n");
		writer.write("  }\n\n");
		
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
