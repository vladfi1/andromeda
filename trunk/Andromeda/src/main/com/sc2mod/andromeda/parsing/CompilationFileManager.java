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

import com.sc2mod.andromeda.notifications.ErrorUtil;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.notifications.SourceLocationContent;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

/**
 * The Compilation Environment stores all data that arises during a compilation run
 * and needs to be saved for later use.
 * @author gex
 *
 */
public class CompilationFileManager {

	private HashMap<Integer,Source> sources = new HashMap<Integer,Source>();
	private HashMap<String,Source> sourcesByPath = new HashMap<String,Source>();
	private List<String> lookupDirs = new ArrayList<String>();
	private List<String> libDirs = new ArrayList<String>();
	private File nativeDir;


	private MoPaQ map;
	private int count = 0;
	private long bytesRead = 0;
	private Configuration options;
	
	

	public CompilationFileManager(Configuration options){
		this.options = options;
	}

	public Configuration getOptions() {
		return options;
	}

	public void setOptions(Configuration options) {
		this.options = options;
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
	
	public Source getSourceById(int id){
		return sources.get(id);
	}
	
	public SourceReader getReader(Source f, InclusionType inclusionType){
		if(sourcesByPath.containsKey(f.getFullPath())){
			//This resource was already included! Do not get a reader on it.
			return null;
		}
		SourceReader r;
		try {
			r = new SourceReader(this,f, inclusionType, count++);
		} catch (IOException e) {
			throw ErrorUtil.raiseInternalProblem(e);
		}
		bytesRead += f.length();
		sources.put(r.getFileId(), f);
		sourcesByPath.put(f.getFullPath(), f);
		return r;
	}
	
	private Source checkPathsForFile(String filePath, List<String> lookupDirs){
		Source toRead = null;
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
	
	public Source resolveFile(String filePath, InclusionType includeType){
		Source toRead = null;
		
		//If this is a library include we only check the lib folders...
		if(includeType == InclusionType.NATIVE){		
			if(!filePath.endsWith(".galaxy")) filePath = filePath.concat(".galaxy");
			toRead = new FileSource(nativeDir.getAbsolutePath() + "/" + filePath);
					
			return toRead;
		} else if(includeType != InclusionType.LIBRARY){
			
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
	public SourceReader getReaderFromInclude(String s,int left, int right, InclusionType includeType, boolean importSyntax) {
		/*
		 * Note: Every error here is unrecoverable, because missing an included file
		 * normally generates many following mistakes (cause all content in it is not found)
		 */
		int fileId = (left&0xFF000000);
		if(!sources.containsKey(fileId)) throw new InternalError("Unknown file id!");
		Source f = sources.get(fileId);
		Matcher m;
		if(importSyntax){
			s = s.replace('.', '/');
			m = importPattern.matcher(s);
			if(!m.matches())
				throw Problem.ofType(ProblemId.MALFORMED_IMPORT).at(left,right).details(s)
					.raiseUnrecoverable();	
		} else {
			m = includePattern.matcher(s);
			if(!m.matches())
				throw Problem.ofType(ProblemId.MALFORMED_INCLUDE).at(left,right).details(s)
					.raiseUnrecoverable();		
		}
				
		Source toRead = resolveFile(m.group(1), includeType);
		
		try{
			if(toRead==null||!toRead.exists()) throw new FileNotFoundException();
			return getReader(toRead,includeType);
		}catch(FileNotFoundException e){
			throw Problem.ofType(ProblemId.INCLUDED_FILE_NOT_FOUND).at(left,right).details(m.group(1))
				.raiseUnrecoverable();
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
			result = new SourceLocationContent(sources.get(fileId), pos, length).toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "-- SOURCE INFORMATION COULDN'T BE READ --";
		}
		
		return result;
	}

	/**
	 * Adds a problem. Internal method called by Problem.raise().
	 * Not to be called from anywhere else!
	 * @param problem the problem that occurred
	 */
	public void registerProblem(Problem problem) {
		// TODO Auto-generated method stubs
	
		throw new Error("Not implemented!");
	}
	
}
