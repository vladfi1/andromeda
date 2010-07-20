package com.sc2mod.andromeda.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.sc2mod.andromeda.notifications.Message;
import com.sc2mod.andromeda.parsing.FileSource;
import com.sc2mod.andromeda.parsing.ParseResult;
import com.sc2mod.andromeda.parsing.ParserLanguage;
import com.sc2mod.andromeda.parsing.Source;
import com.sc2mod.andromeda.program.ConfigHandler;
import com.sc2mod.andromeda.program.InvalidParameterException;
import com.sc2mod.andromeda.program.Options;
import com.sc2mod.andromeda.program.Parameters;
import com.sc2mod.andromeda.program.Program;

public class AndromedaTestRun {

	protected Options getDefaultOptions(){
		try {
			ConfigHandler config = new ConfigHandler(new File( new File("."),"andromeda.conf"), true);
			
			Options o = new Options(config, new Parameters(new String[0]));
			return o;
		} catch (Throwable e) {
			fail("Init failed");
			throw new Error("Init failed");
		}
		
	}
	
	protected void reset(){

		Program.appDirectory = new File(".");
	}
	
	/**
	 * Can be called after any callAndromeda() invocation to reparse the
	 * output with a galaxy parser to verify that the output has no errors in it.
	 *
	 * Will parse the output of the previous run with a galaxy parser and fail
	 * if there are any parse errors (warnings are ignored).
	 */
	protected void checkOutput(){
		reset();
		ArrayList<Source> input = new ArrayList<Source>();
		input.add(new FileSource(new File("out/test/Andromeda.galaxy").getAbsolutePath()));
		ParseResult result = null;
		try {
			result  = Program.invokeWorkflow(input,getDefaultOptions(), ParserLanguage.GALAXY);
			ArrayList<Message> errors = result.getErrors();
			if(!errors.isEmpty()){
				fail("The created code has compile errors!\n" + getListStr(errors));
			}
		} catch (Throwable t){
			fail("An exception has been thrown: " + t.getMessage());
		}

	}

	@SuppressWarnings({ "unchecked" })
	protected String getListStr(ArrayList a){
		StringBuffer b = new StringBuffer();
		for(Object o: a){
			b.append(String.valueOf(o)).append("\n");
		}
		return b.toString();
	}
}
