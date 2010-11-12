package com.sc2mod.andromeda.parsing;

import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.syntaxNodes.ImportNode;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.PackageDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.SourceListNode;
import com.sc2mod.andromeda.util.ArrayStack;
import com.sc2mod.andromeda.util.Pair;
import com.sc2mod.andromeda.util.StopWatch;
import com.sc2mod.andromeda.util.ThreadUtil;

public class ParserScheduler {

	private List<ParserInput> inputSources;
	private LinkedList<ParserInput> importQueue = new LinkedList<ParserInput>();
	private List<SourceFileNode> collectedSources;
	private int remainingInputFiles;
	private int filesToParse;
	private ArrayStack<ParserThread> idleWorkerThreads = new ArrayStack<ParserThread>();
	private int numThreads;
	private ParserThread[] workerThreads;
	private CompilationEnvironment env;
	private LanguageImpl language;
	private ImportResolver importResolver;
	private HashMap<String,PackageDeclNode> readCompilationUnits = new HashMap<String, PackageDeclNode>();
	
	public ParserScheduler(int numThreads, CompilationEnvironment env, List<Pair<Source,InclusionType>> inputSources, LanguageImpl language) {
		collectedSources = Collections.synchronizedList(new ArrayList<SourceFileNode>(inputSources.size()*3));
		 
		List<ParserInput> srcs = this.inputSources = new ArrayList<ParserInput>(inputSources.size());
		for(Pair<Source, InclusionType> s : inputSources){
			srcs.add(ParserInputFactory.create(collectedSources, s._1,s._2,null,null));
		}
		remainingInputFiles = inputSources.size();
		filesToParse = remainingInputFiles;
		this.numThreads = numThreads;
		workerThreads = new ParserThread[numThreads];
		this.env = env;
		this.language = language;
		importResolver = new ImportResolver(env.getSourceManager());
	}
	
	
	/**
	 * Gets a parser thread to parse a file.
	 * May block if no thread is currently idle
	 * @return
	 */
	private synchronized ParserThread getThread(){
		while(true){
			if(!idleWorkerThreads.isEmpty()){
				return idleWorkerThreads.pop();
			} else {
				ThreadUtil.wait(this);
			}
		}
	}
	
	

	
	public synchronized void registerImport(ImportNode sn){
		Pair<Source, InclusionType> src = importResolver.resolveImport(sn);
		
		String importStr = importResolver.getImportString(sn);
		
		
		//add to import queue
		importQueue.add(ParserInputFactory.create(collectedSources, src._1, src._2, importStr, sn));
		
	}
	
	//FIXME: Test cases for include and import
	public synchronized void registerInclude(IncludeNode sn){
		
		Pair<Source, InclusionType> src = importResolver.resolveInclude(sn);
		
		
		//Includes are always added to queue
		importQueue.add(ParserInputFactory.create(sn, src._1, src._2,null));
		
		//wake up threads so somebody handles this
		this.notifyAll();
	}
	
	public synchronized void registerPackageDecl(PackageDeclNode packageDecl){
		if(packageDecl != null){
			String pkg = importResolver.resolvePackage(packageDecl);
			if(pkg == null){
				Problem.ofType(ProblemId.UNIT_NAME_MISSING).at(packageDecl)
				.raise();
			} else {
				if(readCompilationUnits.containsKey(pkg)){
					throw Problem.ofType(ProblemId.DUPLICATE_COMPILATION_UNIT).at(readCompilationUnits.get(pkg),packageDecl)
						.raiseUnrecoverable();
				} else {
					readCompilationUnits.put(pkg, packageDecl);
				}
			}
		}
		
		if(remainingInputFiles > 0){
			remainingInputFiles--;
			if(remainingInputFiles == 0){
				//wakes up the thread, so it can parse the imported files
				this.notifyAll();
			}
		}
		
	}
	
	private synchronized void registerIdleThread(ParserThread thread){
		idleWorkerThreads.push(thread);
	}
	
	public synchronized void registerParseFinished(ParserThread parserThread){
		registerIdleThread(parserThread);
		this.notifyAll();
	}
	
	
	private SourceListNode constructSourceList(List<SourceFileNode> srcs){
		SourceListNode result = new SourceListNode();
		
		for(SourceFileNode f : srcs){
			result.append(f);
		}
		return result;
	}

	public SourceListNode parse() {
		
		//Create and start parser threads
		for(int i = 0; i < numThreads; i++){
			ParserThread p = new ParserThread(this, env, language.createParser(env));
			p.start();
			idleWorkerThreads.add(p);
			workerThreads[i] = p;
		}
		
		//Create result
		
		
		
		//parse input sources
		for(ParserInput s: inputSources){
			ParserThread t = getThread();
			t.parseFile(null,s, "abc");
		}
		
		
		//wait until all input files have parsed their package decl
		synchronized (this) {
			while(remainingInputFiles > 0)
				ThreadUtil.wait(this);
		}

		
		outer: while(true){
			ParserInput nextInclude;
			synchronized (this){
				while(importQueue.isEmpty()){
					if(idleWorkerThreads.size() == numThreads){
						//if all threads are idle and the queue is empty,
						//then we are finished
						break outer;
					} else {
						ThreadUtil.wait(this);
					}
				}
				nextInclude = importQueue.removeLast();
				
				String importName = nextInclude.getImportName();
				if(importName != null){
					if(readCompilationUnits.containsKey(importName)){
						//This compilation unit was already processed, try next
						continue;
					}
				}
				
			}
			ParserThread t = getThread();
			t.parseFile(null, nextInclude, "abc");
		}
		
		//kill worker threads
		for(ParserThread thread : workerThreads){
			thread.setInterrupted();
		}
		
		return constructSourceList(collectedSources);
		
	}

//	private Source getSourceFromInclude(IncludeNode nextInclude) {
//		// TODO Auto-generated method stub
//		throw new Error("Not implemented!");
//	}
//	
//	  private Symbol processInclude(InclusionType inclusionType, boolean isImport){
//		  	if(curInclusionType == InclusionType.NATIVE){
//		  		inclusionType = InclusionType.NATIVE;
//		  	}
//		  	int includeToken;
//		  	String s;
//		  	if(isImport){
//				includeToken = IMPORT_START; 	
//				s = yytext();
//		  	} else {
//		  		s = null;
//		  		includeToken = INCLUDE_START;
//		  	}
//		  	SourceReader ar = this.zzReader.getSourceEnvironment().getReaderFromInclude(yytext(), yychar|curFile, (yylength()+yychar)|curFile,inclusionType,isImport);
//		  	if(ar == null) return null;
//		  	yypushStream(ar);
//		  	return symbol(includeToken,new SourceFileInfo(curFile,inclusionType,s));
//		  }

	


}
