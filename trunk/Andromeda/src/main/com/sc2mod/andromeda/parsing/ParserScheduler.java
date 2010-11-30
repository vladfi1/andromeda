package com.sc2mod.andromeda.parsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.sc2mod.andromeda.parsing.framework.ParserFactory;
import com.sc2mod.andromeda.parsing.framework.Source;
import com.sc2mod.andromeda.problems.InternalProgramError;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.problems.UnrecoverableProblem;
import com.sc2mod.andromeda.syntaxNodes.ImportNode;
import com.sc2mod.andromeda.syntaxNodes.IncludeNode;
import com.sc2mod.andromeda.syntaxNodes.PackageDeclNode;
import com.sc2mod.andromeda.syntaxNodes.SourceFileNode;
import com.sc2mod.andromeda.syntaxNodes.SourceListNode;
import com.sc2mod.andromeda.util.ArrayStack;
import com.sc2mod.andromeda.util.Pair;
import com.sc2mod.andromeda.util.ThreadUtil;

public class ParserScheduler {

	private List<ParserThreadInput> inputSources;

	private LinkedList<ParserThreadInput> importQueue = new LinkedList<ParserThreadInput>();
	private List<SourceFileNode> collectedSources;
	private int remainingInputFiles;
	private int filesToParse;
	private ArrayStack<ParserThread> idleWorkerThreads = new ArrayStack<ParserThread>();
	private int numThreads;
	private ParserThread[] workerThreads;
	private CompilationEnvironment env;
	private Language language;
	private ImportResolver importResolver;
	private Throwable threadExeption = null;
	private ParserFactory parserFactory;
	
	private HashMap<String,PackageDeclNode> readCompilationUnits = new HashMap<String, PackageDeclNode>();
	
	public ParserScheduler(int numThreads, CompilationEnvironment env, List<Pair<Source,InclusionType>> inputSources, Language language) {
		collectedSources = Collections.synchronizedList(new ArrayList<SourceFileNode>(inputSources.size()*3));
		 
		List<ParserThreadInput> srcs = this.inputSources = new ArrayList<ParserThreadInput>(inputSources.size());
		for(Pair<Source, InclusionType> s : inputSources){
			srcs.add(ParserInputFactory.create(collectedSources, s._1,s._2,null,null));
		}
		remainingInputFiles = inputSources.size();
		filesToParse = remainingInputFiles;
		this.numThreads = numThreads;
		workerThreads = new ParserThread[numThreads];
		this.env = env;
		this.language = language;
		parserFactory = language.getImpl().getParserFactory();
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
		String importStr = ImportResolver.getImportString(sn);
		
		//add to import queue
		importQueue.add(ParserInputFactory.create(collectedSources, importStr, sn, importResolver));
		
		//wake up threads so somebody handles this
		this.notifyAll();
		
	}
	
	//FIXME: Duplicate compilation unit tests
	//FIXME: Test cases for include and import
	public synchronized void registerInclude(IncludeNode sn){
		
		Pair<Source, InclusionType> src = importResolver.resolveInclude(sn);
		
		
		//Includes are always added to queue
		importQueue.add(ParserInputFactory.create(sn, src._1, src._2,null));
		
		//wake up threads so somebody handles this
		this.notifyAll();
	}
	
	private void decrementRemainingInputFiles(){
		if(remainingInputFiles > 0){
			remainingInputFiles--;
			if(remainingInputFiles == 0){
				//wakes up the thread, so it can parse the imported files
				this.notifyAll();
			}
		}
	}
	
	public synchronized String registerPackageDecl(PackageDeclNode packageDecl){
		String pkg = null;
		if(packageDecl != null){
			pkg = importResolver.resolvePackage(packageDecl);
			if(pkg == null){
				Problem.ofType(ProblemId.UNIT_NAME_MISSING).at(packageDecl)
				.raise();
			} else {
				System.out.println(pkg);
				if(readCompilationUnits.containsKey(pkg)){
					throw Problem.ofType(ProblemId.DUPLICATE_COMPILATION_UNIT).at(readCompilationUnits.get(pkg),packageDecl)
						.raiseUnrecoverable();
				} else {
					readCompilationUnits.put(pkg, packageDecl);
				}
			}
		}
		
		decrementRemainingInputFiles();
		
		return pkg;
		
	}
	
	private synchronized void registerIdleThread(ParserThread thread){
		idleWorkerThreads.push(thread);
	}
	
	public synchronized void registerParseFinished(ParserThread parserThread){
		registerIdleThread(parserThread);
		this.notifyAll();
	}
	
	
	private SourceListNode constructSourceList(List<SourceFileNode> srcs){
		
		//Input is not sorted in galaxy parse runs
		if(language != Language.GALAXY)
			srcs = new InputSorter().sort(srcs);
		
		SourceListNode result = new SourceListNode();
		
		for(SourceFileNode f : srcs){
			result.add(f);
		}
		return result;
	}

	public SourceListNode parse() {
		
		//Create and start parser threads
		for(int i = 0; i < numThreads; i++){
			ParserThread p = new ParserThread(i,this, env, parserFactory.createParser(env.getParserInterface()));
			p.start();
			idleWorkerThreads.add(p);
			workerThreads[i] = p;
		}
		
		//Create result
		
		
		
		//parse input sources
		for(ParserThreadInput s: inputSources){
			ParserThread t = getThread();
			t.parseFile(null,s, "abc");
		}
		
		
		//wait until all input files have parsed their package decl
		synchronized (this) {
			while(remainingInputFiles > 0)
				ThreadUtil.wait(this);
		}

		
		outer: while(true){
			ParserThreadInput nextInclude;
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
		
		//If there were exceptions, throw them.
		if(threadExeption != null){
			if(threadExeption instanceof UnrecoverableProblem){
				throw (UnrecoverableProblem)threadExeption;
			} else {
				throw new InternalProgramError(threadExeption);
			}
		}
		
		return constructSourceList(collectedSources);
		
	}


	public synchronized void registerException(ParserThread pt, Throwable t) {
		//If the thread couldn't parse its package decl before an exception occurred,
		//we must decrement the remaining input files (which is normally done after a package decl
		//was parsed).
		if(!pt.packageDeclParsed){
			decrementRemainingInputFiles();
		}
		threadExeption = t;
		
	}


}

