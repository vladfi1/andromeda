package com.sc2mod.andromeda.problems;

import java.util.ArrayList;

import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.CompilerThread;
import com.sc2mod.andromeda.syntaxNodes.ModifierListNode;
import com.sc2mod.andromeda.syntaxNodes.SyntaxNode;

public class Problem{



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected final ProblemId problemId;
	public final CompilationEnvironment environment;
	private ProblemSeverity severity;
	protected Object[] messageTokens;
	protected StackTraceElement[] stackTrace;
	protected final ArrayList<SourceLocation> locations = new ArrayList<SourceLocation>(1); 

	
	/**
	 * This constructor is only to be called by Problems.createProblem or by subclasses!
	 * @param problemId
	 * @param message
	 * @param srcEnv
	 * @param severity
	 */
	protected Problem(ProblemId problemId, CompilationEnvironment srcEnv, ProblemSeverity severity){
		this.problemId = problemId;
		this.environment = srcEnv;
		this.severity = severity;
	}
	
	//================ BUILD METHODS: Assemble problems ================
	
	public static Problem ofType(ProblemId problemId, CompilationEnvironment srcEnv){
		return problemId.createProblem(srcEnv);
	}
	
	public static Problem ofType(ProblemId problemId){
		Thread t = Thread.currentThread();
		if(!(t instanceof CompilerThread)) throw new Error("Calling Problem.ofType from a non-Compiler Thread");
		return problemId.createProblem(((CompilerThread)t).compilationEnvironment);
	}
	
	public Problem details(Object ... messageTokens){
		this.messageTokens = messageTokens;
		return this;
	}

	
	/**
	 * Adds a location to this problem.
	 * @param sl The location to be added
	 * @return this
	 */
	public Problem at(SourceLocation sl){
		locations.add(sl);
		return this;
	}
	
	/**
	 * Adds a location to this problem
	 * @param node the node that caused the problem.
	 * @return this
	 */
	public Problem at(SyntaxNode node){
		if (node==null) return this;
		at(new LazySourceLocation(node, environment.getSourceManager()));
		return this;
	}
	
	/**
	 * Adds locations to this problem.
	 * @param nodes locations to be added
	 * @return this
	 */
	public Problem at(SyntaxNode ... nodes){
		for(SyntaxNode sn : nodes){
			at(sn);
		}
		return this;
	}
	
	/**
	 * Adds a location to this problem
	 * @param left the left-code, as returned from the parser
	 * @param right the right-code, as returned from the parser
	 * @return this
	 */
	public Problem at(int left, int right){
		at(new LazySourceLocation(left,right, environment.getSourceManager()));
		return this;
	}
	
	//================ RAISE METHODS: Register problem after it is built ================
	
	/**
	 * Raises (registers) this problem without interrupting the compilation run.
	 * Should be used in cases where the compiler can recover from this problem.
	 */
	public Problem raise(){
		environment.getResult().registerProblem(this);
		return this;
	}
	
	
	/**
	 * Throws this problem by wrapping it into an unrecoverable
	 * problem and then throwing that. Should be used in situations where
	 * the Problem cannot be handled properly (i.e. the compiler
	 * cannot recover from it). This will interrupt the compilation
	 * run immediately by throwing this problem as exception.
	 * 
	 * This method is of return type UnrecoverableProblem, even if it never
	 * returns something but always throws an exception. However,
	 * you can use this to throw the problem in your code, which
	 * will notify the compiler that the control flow ands here.
	 * (Then you do not need fake returns after the call of this
	 * 	method)
	 */
	public UnrecoverableProblem raiseUnrecoverable(){
		severity = ProblemSeverity.FATAL_ERROR;
		raise();
		throw new UnrecoverableProblem(this);
	}

	public void raiseOnce(int raiseNumber){
		if(CompilerThread.registerUniqueEvent(raiseNumber))
			raise();
	}
	
	/**
	 * Returns a defensive copy of the source locations of this problem
	 * @return the locations of this problem
	 */
	public SourceLocation[] getLocations(){
		return locations.toArray(new SourceLocation[locations.size()]);
	}
	
	public String getMessage(){
		return problemId.createMessage(messageTokens);
	}

	/**
	 * Returns true, if the error producing code should be removed.
	 * Only used for certain errors for which the "REMOVE" policy may
	 * be specified in the config file
	 * @return
	 */
	public boolean wantRemove(){
		return false;
	}
	

	public ProblemSeverity getSeverity() {
		return severity;
	}

	public void setStackTrace(StackTraceElement[] stackTrace) {
		this.stackTrace = stackTrace;
	}

	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}

	public ProblemId getProblemId() {
		return problemId;
	}
	
	@Override
	public String toString() {
		return problemId.name() + ": " + getMessage(); 
	}

	public static void raiseSingleTimeDeprecation(ModifierListNode mods, int uniqueId, String string) {
		if(!CompilerThread.registerUniqueEvent(uniqueId))
			return;
		Problem.ofType(ProblemId.DEPRECATION).at(mods)
			.details(string)
			.raise();
	}
	
}
