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

import mopaqlib.MoPaQ;

import com.sc2mod.andromeda.parsing.framework.Source;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.problems.ErrorUtil;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.SourceLocationContent;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;
import com.sc2mod.andromeda.util.Pair;

/**
 * The Compilation Environment stores all data that arises during a compilation run
 * and needs to be saved for later use.
 * @author gex
 *
 */
public class SourceManager {

	private HashMap<Integer,SourceInfo> sources = new HashMap<Integer,SourceInfo>();
	private HashMap<String,SourceInfo> sourcesByPath = new HashMap<String,SourceInfo>();
	private List<String> lookupDirs = new ArrayList<String>();
	private List<String> libDirs = new ArrayList<String>();
	private File nativeDir;


	private MoPaQ map;
	private int count = 0;
	private long bytesRead = 0;
	private Configuration options;
	
	

	public SourceManager(Configuration options){
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
		return sources.get(id).getSource();
	}
	
	public synchronized SourceInfo getSourceInfoById(int id){
		return sources.get(id);
	}
	
	/**
	 * Gets a reader onto a specific source under a specific inclusion type
	 * or null if the resource has already been read. 
	 * The source is also added to the "known and read" sources.
	 * 
	 * Only one reader can be gotten for one source (to prevent double includes).
	 * Every subsequent call to the same source will return null.
	 * 
	 * If the source is null or does not exist, a FileNotFound exception is thrown.
	 * 
	 * @param f the source to be read.
	 * @param inclusionType the inclusion type.
	 * @return
	 */
	public synchronized SourceReader getReader(Source f, InclusionType inclusionType) throws FileNotFoundException{
		if(f == null || !f.exists()){
			throw new FileNotFoundException("The source " + f.getFullPath() + " does not exist");
		}
		if(sourcesByPath.containsKey(f.getFullPath())){
			//This resource was already included! Do not get a reader on it.
			return null;
		}
		int id = count++;
		SourceInfo info = new SourceInfo(f,id,inclusionType);
		SourceReader r;
		try {
			r = new SourceReader(this,f, inclusionType, id);
		} catch (IOException e) {
			throw ErrorUtil.raiseInternalProblem(e);
		}
		bytesRead += f.length();
		sources.put(r.getFileId(), info);
		sourcesByPath.put(f.getFullPath(), info);
		return r;
	}
	
	private Source checkPathsForFile(String filePath, String pkgName, List<String> lookupDirs){
		Source toRead = null;
		for(String base: lookupDirs){
			toRead = new FileSource(base + "/" + filePath, pkgName);
			if(!(filePath.endsWith(".galaxy")||filePath.endsWith(".a"))){
				toRead = new FileSource(base + "/" + filePath + ".a", pkgName);
				if(!toRead.exists()){
					toRead = new FileSource(base + "/" + filePath + ".galaxy", pkgName);
				}
			}
			if(toRead.exists()){
				break;
			}
		}
		return toRead;
	}
	
//	public synchronized boolean hasReadSource(Source s){
//		return sourcesByPath.containsKey(s.getFullPath());
//	}
	
	private Pair<Source,InclusionType> resolveFile(String filePath, InclusionType includeType){
		Source toRead = null;
		String pkgName = filePathToPkgName(filePath);
		
		//If this is a library include we only check the lib folders...
		if(includeType == InclusionType.NATIVE){		
			if(!filePath.endsWith(".galaxy")) filePath = filePath.concat(".galaxy");
			toRead = new FileSource(nativeDir.getAbsolutePath() + "/" + filePath, pkgName);
			if(toRead == null)
				return null;
			return new Pair<Source, InclusionType>(toRead, InclusionType.NATIVE);
		} else if(includeType != InclusionType.LIBRARY){
			
			//First we look in the map file (if one is specified)
			if(map != null){
			
			}
			
			//Next we look in the lookup paths
			toRead = checkPathsForFile(filePath, pkgName, this.lookupDirs);
			if(toRead != null) return new Pair<Source, InclusionType>(toRead, InclusionType.INCLUDE);
		} 
		
		//Finally the lib paths
		toRead = checkPathsForFile(filePath, pkgName, libDirs);
		
		//Not found :(
		if(toRead == null)
			return null;
		
		return new Pair<Source, InclusionType>(toRead, InclusionType.LIBRARY);
	}
	
	private String filePathToPkgName(String filePath) {
		// FIXME Implement correctly
		return filePath;
	}

	public synchronized Pair<Source, InclusionType> resolveInclude(String fileName, SyntaxNode where){
		int fileId = (where.getLeftPos()&0xFF000000)>>24;
		if(!sources.containsKey(fileId)) throw new InternalError("Unknown file id!");
		SourceInfo f = sources.get(fileId);
		Pair<Source, InclusionType> toRead = resolveFile(fileName, f.getType());
		if(toRead==null||!toRead._1.exists())
			return null;
		
		return toRead;
	}
	
//	public SourceReader getReaderFromInclude(String fileName,SyntaxNode where) throws FileNotFoundException {
//		Pair<Source, InclusionType> toRead = resolveInclude(fileName, where);
//		return getReader(toRead._1,toRead._2);
//	}


	public String getSourceInformation(SyntaxNode node){
		return getSourceInformation(node.getLeftPos(), node.getRightPos());
	}
	
	public String getSourceInformation(int left, int right){
		//Retrieve the file
		int fileId = (left&0xFF000000)>>24;
		int pos = (left&0x00FFFFFF);
		int length = (right&0x00FFFFFF)-pos;
		String result = "";
		if(!sources.containsKey(fileId)) return "Unknown file id!";
		try {
			result = new SourceLocationContent(sources.get(fileId).getSource(), pos, length).toString();
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
