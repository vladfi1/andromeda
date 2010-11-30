package com.sc2mod.andromeda.test.junit;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.ListIterator;

import com.sc2mod.andromeda.parsing.CompilationEnvironment;
import com.sc2mod.andromeda.parsing.FileSource;
import com.sc2mod.andromeda.parsing.Language;
import com.sc2mod.andromeda.parsing.framework.Source;
import com.sc2mod.andromeda.parsing.options.ConfigFile;
import com.sc2mod.andromeda.parsing.options.Configuration;
import com.sc2mod.andromeda.parsing.options.InvalidParameterException;
import com.sc2mod.andromeda.parsing.options.Parameter;
import com.sc2mod.andromeda.problems.Problem;
import com.sc2mod.andromeda.problems.ProblemId;
import com.sc2mod.andromeda.program.Program;
import com.sc2mod.andromeda.test.sc2tester.SC2TestResults;
import com.sc2mod.andromeda.test.sc2tester.SC2TestResultsFailDecider;
import com.sc2mod.andromeda.test.sc2tester.SC2Tester;
import com.sc2mod.andromeda.util.Files;

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
	
	private CompilationEnvironment env;
	private ArrayList<Problem> problems;
	
	@Override
	protected void reset() {
		super.reset();
	}


	
	protected void callAndromeda(Language lang, Configuration options,String... sourceFile){

		ArrayList<Source> input = new ArrayList<Source>();
		for(String s : sourceFile){
			input.add(new FileSource(getPackageFile(s),null));	
		}
		try {
			options.setParam(Parameter.FILES_OUT_DIR, new File("out/test"));
			env = Program.invokeWorkflow(input,options, lang);
			
		} catch (Throwable t){
			fail("An exception has been thrown: " + t.getMessage());
		}
		problems = env.getResult().getProblems();
	}
	
	protected void callAndromeda(Language lang, String... sourceFiles){
		callAndromeda(lang,getDefaultOptions(),sourceFiles);
	}
	
	protected void parseAndDoIngameTest(String testSuiteName, int timeout, String... sourceFiles){
		String[] srcs = new String[sourceFiles.length+1];
		System.arraycopy(sourceFiles, 0, srcs, 0, sourceFiles.length);
		//FIXME Do this properly...
		srcs[sourceFiles.length] = "sc2unitTest.a";
		Configuration o = getDefaultOptions();
		File outMap = new File("out/test/test.SC2Map");
		try {
			o.setParam(Parameter.FILES_MAP_IN, new File("test/code/empty.SC2Map"));
			o.setParam(Parameter.FILES_MAP_OUT, outMap);
		} catch (InvalidParameterException e) {
			fail(e.getStackTrace().toString());
		}
		
		callAndromeda(Language.ANDROMEDA,o,srcs);
		assertNoMoreProblems();
		checkOutput();
		
		ConfigFile confFile = getDefaultConfigFile();
		String params = o.getParamString(Parameter.TEST_SC2_PARAMS);
		String executablePath = confFile.getPropertyString("GENERAL", "sc2Executable", "");
		File sc2Exe = Files.getAppFile(executablePath);
		File bankFolder = new File("C:\\Users\\gex\\Documents\\StarCraft II\\Banks");
		SC2TestResults results = new SC2Tester(sc2Exe, params, bankFolder, timeout).execute(outMap, testSuiteName);
		
		System.out.println("SC2 Test results:");
		System.out.println(results.toString());
		SC2TestResultsFailDecider isFail = new SC2TestResultsFailDecider(results);
		if(isFail.isFail()){
			fail(isFail.getFailMessage());
		}
	}
	
	protected void callAndromeda(String... sourceFile){
		callAndromeda(Language.ANDROMEDA,sourceFile);
	}
	
	protected void callGalaxy(String... sourceFiles){
		callAndromeda(Language.GALAXY,sourceFiles);
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
