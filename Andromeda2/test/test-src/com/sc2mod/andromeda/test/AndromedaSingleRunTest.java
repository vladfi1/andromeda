package com.sc2mod.andromeda.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.ListIterator;

import com.sc2mod.andromeda.notifications.Message;
import com.sc2mod.andromeda.parsing.CompilationResult;
import com.sc2mod.andromeda.parsing.FileSource;
import com.sc2mod.andromeda.parsing.Language;
import com.sc2mod.andromeda.parsing.Source;
import com.sc2mod.andromeda.parsing.options.Configuration;
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
	private CompilationResult result;
	
	@Override
	protected void reset() {
		super.reset();
		errors = new ArrayList<Message>();
		warnings = new ArrayList<Message>();
	}

	private String packageToString(Package p){
		String result = "test/" + p.getName().replace('.', '/');
		return result;
	}
	
	protected void callAndromeda(String sourceFile){

		ArrayList<Source> input = new ArrayList<Source>();
		input.add(new FileSource(new File(packageToString(this.getClass().getPackage()),sourceFile).getAbsolutePath()));
		Configuration o = getDefaultOptions();
		o.outDir = new File("out/test");
		try {
			result = Program.invokeWorkflow(input,o, Language.ANDROMEDA);
			
		} catch (Throwable t){
			fail("An exception has been thrown: " + t.getMessage());
		}
		errors = result.getErrors();
		warnings = result.getWarnings();
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
	 * Asserts that a compilation error has occurred during the last call of Andromeda. Fails otherwise.
	 * Removes the error from the list.
	 **/
	protected void assertCompilationError(int errorId){
		for(ListIterator<Message> l = errors.listIterator();l.hasNext();){
			Message e = l.next();
			if(e.getId() != errorId) continue;
			if((e instanceof CompilationError)){
				l.remove();
				return;
			}
		}
		fail("No compilation error with the id " + errorId + " was thrown although asserted!");
	}
	
	/**
	 * Asserts that a compilation error has occurred during the last call of Andromeda. Fails otherwise.
	 * Removes the error from the list.
	 **/
	protected void assertWarning(int warningId){
		for(ListIterator<Message> l = errors.listIterator();l.hasNext();){
			Message e = l.next();
			if(e.getId() != warningId) continue;
			if((e instanceof CompilationError)){
				l.remove();
				return;
			}
		}
		fail("No warning with the id " + warningId + " was thrown although asserted!");
	}
	
	/**
	 * Asserts that no warnings or errors that were not asserted before were raised during the last call of Andromeda. Fails otherwise
	 */
	protected void assertNoMoreErrorsAndWarnings(){
		if(!errors.isEmpty()){
			fail("Errors were raised although not asserted!\n" + getListStr(errors));
		}
		if(!warnings.isEmpty()){
			fail("Warnings were raised although not asserted!\n" + getListStr(warnings));
		}
	}
	
	protected void assertNoMoreErrors(){
		if(!errors.isEmpty()){
			fail("Errors were raised although not asserted!\n" + getListStr(errors));
		}
	}
	

}
