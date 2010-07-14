/**
 *  Andromeda, a galaxy extension language.
 *  Copyright (C) 2010 J. 'gex' Finis  (gekko_tgh@gmx.de, sc2mod.com)
 * 
 *  Because of possible Plagiarism, Andromeda is not yet
 *	Open Source. You are not allowed to redistribute the sources
 *	in any form without my permission.
 *  
 */
package com.sc2mod.andromeda.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mopaqlib.MoPaQ;

import com.sc2mod.andromeda.notifications.CompilationError;
import com.sc2mod.andromeda.notifications.LocationFetcher;
import com.sc2mod.andromeda.notifications.SourceLocation;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class SourceEnvironment {

	private HashMap<Integer,AndromedaSource> sources = new HashMap<Integer,AndromedaSource>();
	private HashMap<String,AndromedaSource> sourcesByPath = new HashMap<String,AndromedaSource>();
	private List<String> lookupDirs = new ArrayList<String>();
	private List<String> libDirs = new ArrayList<String>();
	private File nativeDir;


	private MoPaQ map;
	private int count = 0;
	private long bytesRead = 0;
	private static SourceEnvironment lastEnvironment;
	
	public SourceEnvironment(){
		lastEnvironment = this;
	}
	
	public static SourceEnvironment getLastEnvironment(){
		return lastEnvironment;
	}
	
	public void setNativeDir(File nativeLibFolder) {
		this.nativeDir = nativeLibFolder;
	}
	
	public void addLookupDir(File dir){
		lookupDirs.add(dir.getAbsolutePath());
	}
	
	public void addLibDir(File dir){
		libDirs.add(dir.getAbsolutePath());
	}
	
	public int getFileCount() {
		return count;
	}


	public long getBytesRead() {
		return bytesRead;
	}
	
	public AndromedaSource getSourceById(int id){
		return sources.get(id);
	}
	
	public AndromedaReader getReader(AndromedaSource f, int inclusionType){
		if(sourcesByPath.containsKey(f.getFullPath())){
			//This resource was already included! Do not get a reader on it.
			return null;
		}
		AndromedaReader r;
		try {
			r = new AndromedaReader(this,f, inclusionType, count++);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		bytesRead += f.length();
		sources.put(r.getFileId(), f);
		sourcesByPath.put(f.getFullPath(), f);
		return r;
	}
	
	private AndromedaSource checkPathsForFile(String filePath, List<String> lookupDirs){
		AndromedaSource toRead = null;
		for(String base: lookupDirs){
			toRead = new FileSource(base + "/" + filePath);
			if(!(filePath.endsWith(".galaxy")||filePath.endsWith(".a"))){
				toRead = new FileSource(base + "/" + filePath + ".a");
				if(!toRead.exists()){
					toRead = new FileSource(base + "/" + filePath + ".galaxy");
				}
			}
			if(toRead.exists()){
				break;
			}
		}
		return toRead;
	}
	
	public AndromedaSource resolveFile(String filePath, int includeType){
		AndromedaSource toRead = null;
		
		//If this is a library include we only check the lib folders...
		if(includeType == AndromedaFileInfo.TYPE_NATIVE){		
			if(!filePath.endsWith(".galaxy")) filePath = filePath.concat(".galaxy");
			toRead = new FileSource(nativeDir.getAbsolutePath() + "/" + filePath);
					
			return toRead;
		} else if(includeType != AndromedaFileInfo.TYPE_LIBRARY){
			
			//First we look in the map file (if one is specified)
			if(map != null){
			
			}
			
			//Next we look in the lookup paths
			toRead = checkPathsForFile(filePath,this.lookupDirs);
			if(toRead != null) return toRead;
		} 
		
		//Finally the lib paths
		toRead = checkPathsForFile(filePath, libDirs);
		
		return toRead;
	}

	private static Pattern includePattern = Pattern.compile("include\\s*\\\"(.*)\\\"");
	private static Pattern importPattern = Pattern.compile("import\\s*(.*)\\;");
	public AndromedaReader getReaderFromInclude(String s,int left, int right, int includeType, boolean importSyntax) {
		int fileId = (left&0xFF000000);
		if(!sources.containsKey(fileId)) throw new CompilationError("Unknown file id!");
		AndromedaSource f = sources.get(fileId);
		Matcher m;
		if(importSyntax){
			s = s.replace('.', '/');
			m = importPattern.matcher(s);
			if(!m.matches())
				throw new CompilationError("Malformed import directive:\n" + s);	
		} else {
			m = includePattern.matcher(s);
			if(!m.matches())
				throw new CompilationError("Malformed include directive:\n" + s);			
		}
				
		AndromedaSource toRead = resolveFile(m.group(1), includeType);
		
		try{
			if(toRead==null||!toRead.exists()) throw new FileNotFoundException();
			return getReader(toRead,includeType);
		}catch(FileNotFoundException e){
			throw new CompilationError("The included file '" + m.group(1) + "' was not found. Include source:\n" + getSourceInformation(left, right));
		}
	}
	
	public String getSourceInformation(SyntaxNode node){
		return getSourceInformation(node.getLeftPos(), node.getRightPos());
	}
	
	public String getSourceInformation(int left, int right){
		//Retrieve the file
		int fileId = (left&0xFF000000);
		int pos = (left&0x00FFFFFF);
		int length = (right&0x00FFFFFF)-pos;
		String result = "";
		if(!sources.containsKey(fileId)) return "Unknown file id!";
		try {
			result = new SourceLocation(sources.get(fileId), pos, length).toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "-- SOURCE INFORMATION COULDN'T BE READ --";
		}
		
		return result;
	}
	
}
