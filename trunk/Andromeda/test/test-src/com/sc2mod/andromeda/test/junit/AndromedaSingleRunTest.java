package com.sc2mod.andromeda.test.junit;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.ListIterator;

import com.sc2mod.andromeda.notifications.Message;
import com.sc2mod.andromeda.notifications.Problem;
import com.sc2mod.andromeda.notifications.ProblemId;
import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.CompilationResult;
import com.sc2mod.andromeda.parsing.FileSource;
import com.sc2mod.andromeda.parsing.Language;
import com.sc2mod.andromeda.parsing.Source;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.program.Program;

/**
 * Abstract super class for all tests that do a single parsing run in each
 * test method.
 * After each test, assertNoMoreErrorsAndWarnings is called implicitly.
 * 
 * Each test should invoke a callAndromeda(...) method exactly once.
 * If any errors or warnings are asserted, these must be checked (and thus removed from the list)
 * with the correct assert method after the run.
 * 
 * If a warning or error is not checked, the test will fail (since 
 * assertNoMoreErrorsAndWarnings is invoked after it)
 * 
 * @author J. 'gex' Finis
 *
 */
public abstract class AndromedaSingleRunTest extends AndromedaTestRun {
	
	private ArrayList<Message> errors;
	private ArrayList<Message> warnings;
	private CompilationEnvironment env;
	private ArrayList<Problem> problems;
	
	@Override
	protected void reset() {
		super.reset();
		errors = new ArrayList<Message>();
		warnings = new ArrayList<Message>();
	}

	private String packageToString(Package p){
		String result = "test/test-src/" + p.getName().replace('.', '/');
		return result;
	}
	
	protected void callAndromeda(String... sourceFile){

		ArrayList<Source> input = new ArrayList<Source>();
		for(String s : sourceFile){
			input.add(new FileSource(new File(packageToString(this.getClass().getPackage()),s).getAbsolutePath(),null));	
		}
		Configuration o = getDefaultOptions();
		try {
			o.setParam(Parameter.FILES_OUT_DIR, new File("out/test"));
			env = Program.invokeWorkflow(input,o, Language.ANDROMEDA);
			
		} catch (Throwable t){
			fail("An exception has been thrown: " + t.getMessage());
		}
		problems = env.getResult().getProblems();
	}
	
	/**
	 * Invokes andromeda onto the file that is in the same folder as the test class
	 * and has the same name as the test class (with .a extension instead of .java)
	 * 
	 * So test/Bla.java would call test/Bla.a
	 */
	protected void callAndromeda() {
		callAndromeda(this.getClass().getSimpleName() + ".a");
	}
	
	/**
	 * Invokes andromeda onto the file that is in the same folder as the test class
	 * and has the same name as the test class (with .a extension instead of .java)
	 * The file num is appended behind the file name.
	 * 
	 * So test/Bla.java with fileNum = 1 would call test/Bla1.a
	 */
	protected void callAndromeda(int fileNum){
		callAndromeda(this.getClass().getSimpleName() + fileNum + ".a");
	}
	

	
	/**
	 * Asserts that a problem with the specified id has occurred during the last call of Andromeda. Fails otherwise.
	 * Removes the error from the list.
	 **/
	protected void assertProblem(ProblemId id){
		for(ListIterator<Problem> l = problems.listIterator();l.hasNext();){
			Problem e = l.next();
			if(e.getProblemId() != id) continue;
			l.remove();
			return;
		}
		fail("No problem of type " + id.name() + " was raised although asserted!");
	}

	
	/**
	 * Asserts that no problems that were not asserted before were raised during the last call of Andromeda. Fails otherwise
	 */
	protected void assertNoMoreProblems(){
		if(!problems.isEmpty())
			fail("Problems were raised although not asserted!\n" + getListStr(problems));
		
	}
	
	protected void assertOnlyProblem(ProblemId id){
		assertProblem(id);
		assertNoMoreProblems();
	}
	
	
	

}
